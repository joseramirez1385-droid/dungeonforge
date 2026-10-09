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
        Command c = parser.parse(input, ctx);
        c.execute();
        history.push(c);
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
        Room room = ctx.getCurrentRoom();

        Monster monster = new Monster("Test Dummy", 40, 6, 9);
        monster.setStrategy(
                new dungeonforge.behavior.AggressiveStrategy()
        );
        room.getMonsters().add(monster);

        int hpBefore = player.getHp();
        int monsterHpBefore = monster.getHp();

        run("attack");

        assertTrue(
                player.getHp() < hpBefore,
                "The monster should have hit back."
        );
        assertTrue(monster.getHp() < monsterHpBefore);

        assertTrue(history.undoLast());

        assertEquals(
                hpBefore,
                player.getHp(),
                "Undo must reverse the counter-attack too."
        );
        assertEquals(monsterHpBefore, monster.getHp());
    }

    @Test
    void undoRestoresXpAndGoldFromAKill() {
        Room room = ctx.getCurrentRoom();

        Monster weak = new Monster("Fragile", 5, 1, 20);
        weak.setStrategy(
                new dungeonforge.behavior.AggressiveStrategy()
        );
        room.getMonsters().add(weak);

        int hpBefore = weak.getHp();
        int xpBefore = player.getXp();
        int goldBefore = player.getGold();

        run("attack");

        assertTrue(player.getXp() > xpBefore);
        assertTrue(player.getGold() > goldBefore);

        assertTrue(history.undoLast());

        assertEquals(xpBefore, player.getXp());
        assertEquals(goldBefore, player.getGold());
        assertEquals(hpBefore, weak.getHp());
        assertTrue(
                weak.isAlive(),
                "The monster should come back to life after undo."
        );
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

