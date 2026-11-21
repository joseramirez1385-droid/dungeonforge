package dungeonforge.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WEEK 4 -- a container of loot, placed by a TreasureRoomPopulator or BossRoomPopulator.
 *
 * Note it is NOT an Item. In Week 13 it becomes part of a COMPOSITE and that changes, but
 * building for that now would be guessing at a design you have not been taught yet.
 */
public class Chest implements Item{

    private static final double TARE_WEIGHT = 5.0;
    private final String name;
    private final List<Item> contents = new ArrayList<>();

    public Chest(String name) { this.name = name; }

    public String getName()        { return name; }

    @Override
    public double getWeight() {
        double sum = TARE_WEIGHT;
        for (Item i : contents) sum += i.getWeight();
        return sum;
    }

    @Override
    public int getValue() {
        int sum = 0;
        for (Item i : contents) sum += i.getValue();
        return sum;
    }

    @Override
    public Iterator<Item> children() {
        return contents.iterator();
    }

    @Override
    public boolean isContainer() {
        return Item.super.isContainer();
    }

    public List<Item> getContents() { return contents; }

    public void add(Item item)     {
        if (item == this) throw new IllegalArgumentException("a chest cannot contain itself.");
        contents.add(item);
    }

    public boolean remove(Item item) {
        return contents.remove(item);
    }

    public boolean isEmpty()       { return contents.isEmpty(); }

    @Deprecated
    public int totalValue() {
        return this.getValue();
    }

    @Override
    public String describe() {
        return name + " (" + String.format("%.1f", getWeight()) + "kg, " + getValue() + "g, " + contents.size() +
                " item" + (contents.size() == 1 ? "" : "s") + ")";
    }

}
