package dungeonforge;

import dungeonforge.commands.*;
import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.*;
import dungeonforge.events.AchievementSystem;
import dungeonforge.events.EventBus;
import dungeonforge.events.QuestTracker;
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
        ctx = new GameContext(world, player, bus, new Combat(bus), new CommandHistory());
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

    // ---------- helpers ----------

    private Room findRoomWithLoot() {
        for (var level : ctx.getWorld().getLevels()) {
            for (Room r : level.getRooms()) {
                if (r.getChest() != null && r.getChest().getContents().size() >= 2) return r;
            }
        }
        return null;
    }

    private void assumeRoom(Room r) {
        assertNotNull(r,
                "the seeded world should contain at least one stocked chest");
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

    @Test
    void recordUndoEvidence() {
        Room room = ctx.getCurrentRoom();
        room.getMonsters().clear();

        Monster monster = new Monster("Fragile", 5, 1, 20);
        monster.setStrategy(
                new dungeonforge.behavior.AggressiveStrategy()
        );
        room.getMonsters().add(monster);

        System.out.println(
                "| Stage | HP | XP | Gold | Monster HP |"
        );
        System.out.println(
                "| --- | --- | --- | --- | --- |"
        );

        printEvidence("Before attack", monster);

        run("attack");
        printEvidence("After attack", monster);

        assertTrue(history.undoLast());
        printEvidence("After undo", monster);
    }

    private void printEvidence(String stage, Monster monster) {
        System.out.printf(
                "| %s | %d | %d | %d | %d |%n",
                stage,
                player.getHp(),
                player.getXp(),
                player.getGold(),
                monster.getHp()
        );
    }

    }
