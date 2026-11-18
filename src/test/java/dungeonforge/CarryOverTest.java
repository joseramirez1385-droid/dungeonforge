package dungeonforge;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.core.Player;
import dungeonforge.events.DamageMeter;
import dungeonforge.events.EventBus;
import dungeonforge.events.EventType;
import dungeonforge.events.GameEvent;
import dungeonforge.items.BasicWeapon;
import dungeonforge.items.EnchantmentRegistry;
import dungeonforge.items.Weapon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CarryOverTest {

    @BeforeEach
    void setUp() {
        GameConfig.resetForTests();
        RandomSource.resetForTests();
    }

    // ===================== US-8.5: the carry-over =====================

    @Test
    void enchantmentNamesAreCaseAndWhitespaceInsensitive() {
        assertTrue(EnchantmentRegistry.isKnown("FLAMING"), "a player typing in caps is not an error");
        assertTrue(EnchantmentRegistry.isKnown("  frost  "));
        assertTrue(EnchantmentRegistry.isKnown("Vampiric"), "nor is ordinary capitalisation");
        Weapon w = EnchantmentRegistry.apply("  FLAMING ", new BasicWeapon("Iron Sword", 3.0, 40, 8));
        assertEquals("Flaming Iron Sword", w.getName());
        assertFalse(EnchantmentRegistry.isKnown("sparkly"), "unknown is still unknown");
    }

    @Test
    void theMonsterStateEventIsSpelledCorrectly() {
        assertNotNull(EventType.valueOf("MONSTER_STATE_CHANGED"));
        for (EventType t : EventType.values()) {
            assertFalse(t.name().contains("SATE"), "left-over typo: " + t.name());
        }
    }
// ===================== US-9.4: carried from Sprint 9 =====================

    @Test
    void theDamageWarningTracksThePlayersOwnMaximumNotTheStartingValue() {
        Player p = new Player("Tester");
        EventBus bus = new EventBus();
        List<String> heard = new ArrayList<>();
        bus.subscribe(e -> {if (e.getType() == EventType.MESSAGE) heard.add(String.valueOf(e.get("text")));});
        bus.subscribe(new DamageMeter(bus, p, 0.25));

        int originalMax = p.getMaxHp(); // 80 with the default config
        p.setMaxHp(originalMax * 2);    // 160

        int between = (int) Math.round(0.25 * originalMax) + 10;

        p.setHp((int) Math.round(0.25 * p.getMaxHp()) + 10);
        bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN, "amount", 1));
        assertTrue(heard.stream().noneMatch(s -> s.contains("DANGER")), "50 of 160 is not nearly dead");

        p.setHp(between);
        bus.publish(GameEvent.of(EventType.DAMAGE_TAKEN, "amount", 1));
        assertTrue(heard.stream().anyMatch(s -> s.contains("DANGER")),
                "30 of 160 is below a quarter, so the warning must fire");
    }


}

