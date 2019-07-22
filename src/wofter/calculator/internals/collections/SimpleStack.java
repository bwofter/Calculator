package wofter.calculator.internals.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple stack implementation.
 */
final public class SimpleStack<T> implements SimpleList<T> {

    /**
     * The internal list of this stack.
     */
    final private List<T> l = new ArrayList<>();

    /**
     * @inheritDoc
     */
    @Override
    public boolean isEmpty() {
        return l.isEmpty();
    }

    /**
     * @inheritDoc
     */
    @Override
    public T peek() {
        return l.get(l.size() - 1);
    }

    /**
     * @inheritDoc
     */
    @Override
    public T pop() {
        return l.remove(l.size() - 1);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void push(T val) {
        l.add(val);
    }
}
