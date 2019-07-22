package wofter.calculator.internals.collections;

/**
 * Represents a list of values that can be pushed and popped.
 */
public interface SimpleList<T> {

    /**
     * Determines if this list is empty.
     */
    boolean isEmpty();

    /**
     * Peeks at the head of this list.
     */
    T peek();

    /**
     * Pops a value from this list.
     */
    T pop();

    /**
     * Pushes a value to this list.
     */
    void push(final T val);
}
