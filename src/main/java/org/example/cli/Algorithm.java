package org.example.cli;

/**
 * The comparison/distribution sort algorithms the user can select with {@code --algorithm}.
 *
 * <p>{@code --random-sort} is handled separately because it is not a lexicographic ordering.
 */
public enum Algorithm {
    MERGE,
    QUICK,
    HEAP,
    RADIX;

    /**
     * Parses a flag value such as {@code "merge"} into an {@link Algorithm} (case-insensitive).
     *
     * @throws IllegalArgumentException if the name is null or not a known algorithm
     */
    public static Algorithm fromFlag(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Algorithm name is required");
        }
        return switch (value.toLowerCase()) {
            case "merge" -> MERGE;
            case "quick" -> QUICK;
            case "heap" -> HEAP;
            case "radix" -> RADIX;
            default -> throw new IllegalArgumentException(
                    "Unknown algorithm: " + value + " (expected one of merge, quick, heap, radix)");
        };
    }
}
