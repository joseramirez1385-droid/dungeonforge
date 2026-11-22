package dungeonforge.core;

import dungeonforge.items.Item;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CompositeIterator implements Iterator<Item> {
    private final Deque<Iterator<Item>> stack = new ArrayDeque<>();
    private Iterator<Item> lastUsed;
    private Iterator<Item> descendInto;

    public CompositeIterator(Iterator<Item> root) {
        stack.push(root);
    }

    @Override
    public boolean hasNext() {
        while (!stack.isEmpty() && !stack.peek().hasNext()) stack.pop();
        return !stack.isEmpty();
    }

    @Override
    public Item next() {
        if (!hasNext()) throw new NoSuchElementException();
        Iterator<Item> top = stack.peek();
        Item item = top.next();
        lastUsed = top;
        descendInto = null;
        if (item.isContainer()) {
            descendInto = item.children();
            stack.push(descendInto);
        }
        return item;
    }

    @Override
    public void remove() {
        if (lastUsed == null) throw new IllegalStateException("remove() before next()");
        if (descendInto != null && stack.peek() == descendInto) stack.pop();
        lastUsed.remove();
        lastUsed = null;
        descendInto = null;
    }

}
