package dungeonforge.events;

import dungeonforge.core.Player;

public class DangerMeter implements GameEventListener {

        private EventBus bus;
        private final Player player;
        private int damageThreshold;

        public DangerMeter(EventBus bus, Player player, int damageThreshold) {
            this.player = player;
            this.damageThreshold = damageThreshold;
            this.bus = bus;
        }

        @Override
        public void onEvent(GameEvent event) {
            if (event.getType().equals(EventType.DAMAGE_TAKEN)) {
                if (player.getHp() < damageThreshold) {
                    bus.message("~~~ DANGER " + player.getName() + " ROBINSON!!! ~~~\n" +
                            "    your HP is below the threshold -- " + damageThreshold);
                }
            }
        }
    }

