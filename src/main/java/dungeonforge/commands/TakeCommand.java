package dungeonforge.commands;

import dungeonforge.events.GameEvent;
import dungeonforge.items.Item;

import java.util.Iterator;

public class TakeCommand extends AbstractCommand {

    public TakeCommand(GameContext ctx, String itemName) { super(ctx, itemName); }

    @Override
    protected boolean doExecute() {
        Item found = null;
        for (Iterator<Item> it = room().contents(); it.hasNext(); ) {
            Item candidate = it.next();
            if (matches(candidate)) {
                it.remove();
                found = candidate;
                break;
            }
        }

        if (found == null) {
            bus().message("There is nothing like that here.");
            return false;
        }
        if (!player().canCarry(found)) {
            bus().message(String.format("The %s weighs %.1fkg and you are already carrying %.1f of %.1fkg.",
                    found.getName(), found.getWeight(),
                    player().carriedWeight(), player().carryCapacity()));
            return false;
        }

        player().addItem(found);
        bus().publish(GameEvent.message("You take " + found.getName() + "."));
        return true;
    }

    private boolean matches(Item i) { return args.isBlank() || i.getName().toLowerCase().contains(args.toLowerCase()); }

    @Override
    public String getDescription() { return ("take " + args).trim(); }

}
