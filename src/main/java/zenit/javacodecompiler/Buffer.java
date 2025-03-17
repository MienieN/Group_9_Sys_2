package main.java.zenit.javacodecompiler;

/**
 * A simple generic interface for a buffer that can store and retrieve elements.
 *
 * @param <C> The type of the elements in the buffer.
 */
public interface Buffer<C> {

    /**
     * Adds an element to the buffer.
     *
     * @param element The element to add to the buffer.
     */
    public void put(C element);

    /**
     * Retrieves an element from the buffer.
     *
     * @return The element retrieved from the buffer.
     */
    public C get();

    /**
     * Checks if the buffer is empty.
     *
     * @return true if the buffer is empty, false otherwise.
     */
    public boolean isEmpty();

}
