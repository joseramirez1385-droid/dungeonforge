package dungeonforge;

import dungeonforge.commands.*;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.*;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.EventBus;
import dungeonforge.events.QuestTracker;
import dungeonforge.items.Hourglass;
import dungeonforge.items.Potion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 7 -- Sprint 5. The Command pattern. */
class CommandTest {

    private GameContext ctx;
    private CommandParser parser;
    private Player player;
    private CommandHistory history;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();

        player = new Player("Tester");
        GameWorld world = new GameWorld(player);
        EventBus bus = new EventBus();
        QuestTracker quests = new QuestTracker(bus);
        AchievementSystem achievements = new AchievementSystem(bus);
        history = new CommandHistory();
        ctx = new GameContext(world, player, bus, new Combat(bus), history);
        parser = new CommandParser(quests, achievements);
    }

    private void run(String input) {
        ctx.syncState();

        Command command = parser.parse(input, ctx);
        command.execute();
        history.push(command);

        ctx.syncState();
    }

    private Monster spawnHostile(String name, int maxHp, int attack, int xpReward) {
        Monster m = new Monster(name, maxHp, attack, xpReward);
        m.setStrategy(new dungeonforge.behavior.AggressiveStrategy());
        ctx.getCurrentRoom().getMonsters().add(m);
        ctx.syncState();
        return m;
    }

    // ---------- US-5.1: every action is an object ----------

    @Test
    void theParserAlwaysReturnsACommandNeverNull() {
        assertNotNull(parser.parse("north", ctx));
        assertNotNull(parser.parse("flibbertigibbet", ctx));
        assertNotNull(parser.parse("", ctx));
    }

    @Test
    void anUnknownVerbYieldsANullObjectRatherThanAnException() {
        Command c = parser.parse("flibbertigibbet", ctx);
        assertInstanceOf(NoCommand.class, c);
        assertDoesNotThrow(c::execute);
        assertFalse(c.isUndoable());
    }

    @Test
    void aliasesResolveToTheSameCommandType() {
        assertEquals(parser.parse("north", ctx).getClass(), parser.parse("n", ctx).getClass());
        assertEquals(parser.parse("attack", ctx).getClass(), parser.parse("kill", ctx).getClass());
        assertEquals(parser.parse("inventory", ctx).getClass(), parser.parse("i", ctx).getClass());
    }

    @Test
    void movingChangesTheCurrentRoom() {
        Room start = ctx.getCurrentRoom();
        run("north");
        assertNotSame(start, ctx.getCurrentRoom());
    }

    @Test
    void readOnlyCommandsAreNeverUndoable() {
        assertFalse(parser.parse("look", ctx).isUndoable());
        assertFalse(parser.parse("inventory", ctx).isUndoable());
        assertFalse(parser.parse("help", ctx).isUndoable());
    }

    // ---------- US-5.2: undo ----------

    @Test
    void undoRestoresThePreviousRoom() {
        Room start = ctx.getCurrentRoom();
        run("north");

        assertNotSame(start, ctx.getCurrentRoom());
        assertTrue(history.undoLast());
        assertSame(start, ctx.getCurrentRoom());
    }

    @Test
    void undoingAnAttackAlsoReversesTheCounterAttack() {
        Monster m = spawnHostile("Test Dummy", 40, 6, 9);
        int hpAtStart = player.getHp();
        run("attack");
        assertEquals(hpAtStart, player.getHp(),
                "an idle monster must not retaliate -- the first blow is free");
        assertEquals("hunting", m.getState().label(),
                "...but it notices you on the way out of that turn. If this says 'idle', the "
                        + "attack never ran at all -- look at the mode, not the state machine.");

        // Turn 2 -- now there is a real exchange, so there is a counter-attack to undo.
        int hpBefore = player.getHp();
        int monsterHpBefore = m.getHp();

        run("attack");

        assertTrue(player.getHp() < hpBefore, "the monster should have hit back");
        assertTrue(m.getHp() < monsterHpBefore);
        assertTrue(history.undoLast());
        assertEquals(hpBefore, player.getHp(), "undo must reverse the counter-attack too");
        assertEquals(monsterHpBefore, m.getHp());
    }

    @Test
    void undoRestoresXpAndGoldFromAKill() {
        Monster weak = spawnHostile("Fragile", 5, 1, 20);

        run("attack");
        assertTrue(player.getXp() > 0);
        assertTrue(player.getGold() > 0);

        history.undoLast();

        assertEquals(0, player.getXp());
        assertEquals(0, player.getGold());
        assertTrue(weak.isAlive(), "the dead should get back up when time reverses");

        assertEquals("idle", weak.getState().label(),
                "undo must put the monster's STATE back, not just its hit points");
    }

    @Test
    void undoOnAnEmptyStackReportsFailureRatherThanThrowing() {
        assertFalse(history.undoLast());
    }

    @Test
    void theUndoStackIsBoundedButTheReplayLogIsNot() {
        for (int i = 0; i < 60; i++) {
            run("look");
        }

        assertEquals(0, history.depth());
        assertEquals(60, history.replayLog().size());
    }

    // ---------- US-5.3: macro ----------

    @Test
    void lootBecomesOneMacroHoldingSeveralCommands() {
        Room room = findRoomWithLoot();
        assumeRoom(room);
        ctx.setCurrentRoom(room);
        room.getMonsters().clear();

        Command loot = parser.parse("loot", ctx);
        assertInstanceOf(MacroCommand.class, loot);
        assertTrue(((MacroCommand) loot).size() >= 2);
    }

    @Test
    void aMacroPicksUpEverythingAndUndoPutsItAllBack() {
        Room room = findRoomWithLoot();
        assumeRoom(room);
        ctx.setCurrentRoom(room);
        room.getMonsters().clear();

        int expected = room.getChest().getContents().size()
                + room.getFloorItems().size();

        run("loot");
        assertEquals(expected, player.getInventory().size());

        history.undoLast();

        assertEquals(
                0,
                player.getInventory().size(),
                "undoing a macro undoes all of it"
        );
    }

    @Test
    void lootInAnEmptyRoomIsANullObject() {
        Room bare = new Room("bare");
        ctx.setCurrentRoom(bare);

        assertInstanceOf(
                NoCommand.class,
                parser.parse("loot", ctx)
        );
    }

// ---------- US-5.4: the hourglass ----------

    @Test
    void theHourglassRewindsTheLastTurn() {
        player.addItem(new Hourglass(2));
        Room start = ctx.getCurrentRoom();

        run("north");
        assertNotSame(start, ctx.getCurrentRoom());

        run("use hourglass");
        assertSame(start, ctx.getCurrentRoom(), "the hourglass should undo the move");
    }

    @Test
    void aSpentHourglassRefuses() {
        player.addItem(new Hourglass(0));

        run("north");
        Room afterMove = ctx.getCurrentRoom();

        run("use hourglass");

        assertSame(
                afterMove,
                ctx.getCurrentRoom(),
                "a spent hourglass changes nothing"
        );
    }

    @Test
    void usingAPotionHealsAndConsumesIt() {
        player.setHp(10);
        player.addItem(new Potion("Test Draught", 0.2, 10, 25));

        run("use draught");

        assertTrue(player.getHp() > 10);
        assertNull(player.findItem("Test Draught"));
    }

// ---------- the free capabilities ----------

    @Test
    void theReplayLogRecordsWhatTheSessionActuallyDid() {
        run("look");
        run("north");
        run("status");

        assertEquals(
                java.util.List.of("look", "north", "status"),
                history.replayLog()
        );
    }


    // ---------- regression ----------

    @Test
    void earlierWeeksStillHold() {
        assertEquals(19, ctx.getWorld().getMonsterFactory().blueprintCount(), "Week 4");
        assertEquals("Crypt", ctx.getWorld().getLevels().get(0).getThemeName(), "Week 4 themes");

        RandomSource.getInstance().reseed(5L);
        int a = new GameWorld(new Player("A")).totalMonsters();
        RandomSource.getInstance().reseed(5L);
        int b = new GameWorld(new Player("B")).totalMonsters();
        assertEquals(a, b, "Week 3 determinism");
    }

    // ---------- helpers ----------

    private Room findRoomWithLoot() {
        for (var level : ctx.getWorld().getLevels()) {
            for (Room room : level.getRooms()) {
                if (room.getChest() != null && room.getChest().getContents().size() >= 2) return room;
                }
            }
        return null;
    }

    private void assumeRoom(Room room) {assertNotNull(room, "the seeded world should " +
            "contain at least one stocked chest"); }

}

