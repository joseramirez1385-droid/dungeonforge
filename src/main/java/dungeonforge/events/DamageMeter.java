package dungeonforge.events;


import dungeonforge.core.Player;

public class DamageMeter implements GameEventListener {
    private final EventBus bus;
    private final Player player;
    private final double fraction;
    private boolean haveWarned;

    public DamageMeter(EventBus bus, Player player, double threshold) {
        this.bus = bus;
        this.player = player;
        this.fraction = threshold;
        this.haveWarned = false;
    }

    @Override
    public void onEvent(GameEvent event) {
        if (haveWarned) return;
        if (event.getType().equals(EventType.DAMAGE_TAKEN)) {
            int warnAt = (int) Math.round(fraction * player.getMaxHp());
            if (player.getHp() <= warnAt) {
                bus.message(String.format(
                        "~~~ DANGER %s ROBINSON: You're dangerously close to death!!!\n      HP: %d, WARN: %d ~~~",
                        player.getName(), player.getHp(), warnAt));
                haveWarned = true;
            }
        }
    }
}
