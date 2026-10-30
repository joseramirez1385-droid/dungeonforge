package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.DungeonReport;
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
}
