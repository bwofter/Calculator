package wofter.calculator.internals.collections;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a simple queue implementation.
 */
final public class SimpleQueue<T> implements SimpleList<T> {

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
        return l.get(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public T pop() {
        return l.remove(0);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void push(T val) {
        l.add(val);
    }
}
