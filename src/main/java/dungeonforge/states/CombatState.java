package dungeonforge.states;

import dungeonforge.commands.GameContext;

import java.util.List;

public class CombatState implements GameState {

    private static final List<String> VERBS = List.of(
            "attack", "look", "use", "inventory", "status", "history", "undo", "help", "quit");

    @Override
    public void onEnter(GameContext ctx) {
        long count = ctx.getCurrentRoom().getMonsters().stream().filter(monster -> monster.isAlive()).count();
        ctx.getBus().message("Combat! " + count + " hostile(s). Try 'attack'.");
    }

    @Override
    public List<String> availableCommands() { return VERBS; }

    @Override
    public String name() { return "Combat"; }

}
