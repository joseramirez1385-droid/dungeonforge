package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.DungeonReport;
import dungeonforge.core.Room;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.ThemeKit;
import dungeonforge.factory.ThemeRegistry;
import dungeonforge.generate.CaveGenerator;
import dungeonforge.generate.CorridorGenerator;
import dungeonforge.generate.LevelGenerator;
import dungeonforge.generate.RingGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** WEEK 10 -- Sprint 8. The Template Method pattern. */
class TemplateMethodTest {

    private ThemeKit theme;

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
        theme = new ThemeRegistry(new MonsterFactory()).forDepth(1);
    }

    private static List<LevelGenerator> allShapes() {
        return List.of(new CorridorGenerator(), new CaveGenerator(), new RingGenerator());
    }

    // ================= US-8.1 =================

    /**
     * Every shape satisfies every invariant.
     *
     * In the starter, the cave builder skipped the reachability
     * check and the ring builder populated the entrance.
     */
    @Test
    void everyShapeProducesALevelWithNoOrphanedRooms() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertEquals(0, DungeonReport.countOrphans(level), shape.shapeName()
                    + " left rooms the entrance cannot reach");
        }
    }

    @Test
    void everyShapeLeavesTheEntranceEmpty() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertTrue(level.getRooms().get(0).getMonsters().isEmpty(), shape.shapeName()
                    + " spawned monsters where the player materialises");
        }
    }

    /** A well-written shape wastes nothing. */
    /* TODO: This will only be valid after US-8.3 refactor.
    @Test
    void everyShapeKeepsAllTheRoomsItWasGiven() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);

            assertEquals(8, level.getRooms().size(), shape.shapeName()
            + " threw rooms away; enforceReachability is a net, not a bin");
        }
    }
    */

    @Test
    void everyShapeSetsFlavourOnTheEntrance() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertNotNull(level.getRooms().get(0).getFlavor(), shape.shapeName());
        }
    }

    /**
     * Keeping generate() final prevents subclasses from
     * reordering or skipping the template steps.
     */
    @Test
    void theTemplateMethodIsFinalSoNoSubclassCanReorderTheSteps()
            throws Exception {

        Method generate = LevelGenerator.class.getDeclaredMethod(
                "generate", int.class, ThemeKit.class, int.class, boolean.class);
        assertTrue(Modifier.isFinal(generate.getModifiers()),
                "generate() must be final -- that is the pattern, not a precaution");
    }

    /**
     * Fixed steps must not be visible to subclasses.
     */
    @Test
    void theInvariantStepIsPrivateToTheTemplate() {
        List<String> visible = new ArrayList<>();
        for (Method m : LevelGenerator.class.getDeclaredMethods()) {
            if (!Modifier.isPrivate(m.getModifiers())) visible.add(m.getName());
        }
        assertFalse(visible.contains("enforceReachability"),
                "a step a subclass can call is a step a subclass can call at the wrong time");
        assertFalse(visible.contains("populateRooms"));
        assertFalse(visible.contains("createRooms"));
    }

    @Test
    void everyShapeKeepsAllTheRoomsItWasGiven() {
        for (LevelGenerator shape : allShapes()) {
            DungeonLevel level = shape.generate(1, theme, 8, false);
            assertEquals(8, level.getRooms().size(), shape.shapeName() + " threw rooms away; "
                    + "enforceReachability is a net, not a bin"
            );
        }
    }

    // ================= US-8.2 =================

    @Test
    void connectingRoomsIsAnAbstractPrimitive() throws Exception {
        Method connect = LevelGenerator.class.getDeclaredMethod("connectRooms", DungeonLevel.class);
        assertTrue(Modifier.isAbstract(connect.getModifiers()), "every shape must supply its room connections");
    }

    @Test
    void hooksAreConcreteAndOptional() throws Exception {
        for (String hook : List.of("addShortcuts", "decorate")) {
            Method m = hook.equals("decorate") ?
                    LevelGenerator.class.getDeclaredMethod(hook, DungeonLevel.class, ThemeKit.class) :
                    LevelGenerator.class.getDeclaredMethod(hook, DungeonLevel.class);
            assertFalse(Modifier.isAbstract(m.getModifiers()), hook + " should be a hook, not a primitive");
        }
    }

    @Test
    void onlyOneShapeOverridesAHookAndTheRestInheritTheSilence() {
        int overrides = 0;
        for (LevelGenerator shape : allShapes()) {
            try {shape.getClass().getDeclaredMethod("addShortcuts", DungeonLevel.class);
                overrides++;
            } catch (NoSuchMethodException ignored) {
                // This shape inherits the empty hook.
            }
        }
        assertEquals(1, overrides, "only CaveGenerator should want extra edges");
    }

    @Test
    void theCaveHookAddsEdgesBeyondASpanningTree() {
        int treeEdges = 0;
        int caveEdges = 0;
        for (int seed = 1; seed <= 8; seed++) {
            RandomSource.getInstance().reseed(seed * 7919L);
            treeEdges += countDoors(new CorridorGenerator().generate(1, theme, 8, false));
            RandomSource.getInstance().reseed(seed * 7919L);
            caveEdges += countDoors(new CaveGenerator().generate(1, theme, 8, false));
        }

        assertTrue(caveEdges > treeEdges, "the cave hook should add doors beyond a spanning tree");
    }

    private static int countDoors(DungeonLevel level) {
        int doors = 0;
        for (Room room : level.getRooms()) {
            doors += room.getExits().size();
        }
        return doors;
    }
}
