package org.example.sort;

/**
 * A sorting strategy for arrays of lines.
 *
 * <p>Every implementation must be a <em>pure</em> function of its input: it returns a new,
 * sorted array and never mutates the array it was given. This keeps callers safe from hidden
 * side effects and lets us swap algorithms freely (Strategy pattern).
 */
public interface Sorter {

    /**
     * Returns a new array containing the elements of {@code input} in sorted order.
     *
     * @param input the lines to sort; never mutated
     * @return a new sorted array
     */
    String[] sort(String[] input);
}
