package dungeonforge.core;

import dungeonforge.items.Item;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class RoomContentsIterator implements Iterator<Item> {
    private final List<Item> floor;
    private final List<Item> chest;
    private List<Item> current;
    private int index = -1;
    private boolean canRemove;

    public RoomContentsIterator(List<Item> floor, List<Item> chest) {
        this.floor = floor;
        this.chest = chest;
        this.current = null;
    }

    @Override
    public boolean hasNext() {
        if (current == null) return !floor.isEmpty() || (chest != null && !chest.isEmpty());
        if (index + 1 < current.size()) return true;
        return current == floor && chest != null && !chest.isEmpty();
    }

    @Override
    public Item next() {
        if (!hasNext()) throw new NoSuchElementException();
        if (current == null) {
            current = floor.isEmpty() ? chest : floor;
            index = -1;
        }
        if (index + 1 >= current.size()) {
            current = chest;
            index = -1;
        }
        return current.get(++index);
    }

    @Override
    public void remove() {
        if (current == null || index < 0) {
            throw new IllegalStateException("remove() requires a new call to next()");
        }
        current.remove(index--);
    }

}
