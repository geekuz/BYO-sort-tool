package org.example.cli;

import java.util.List;

/**
 * Immutable, parsed view of the command line.
 *
 * @param files     input files in the order given; empty means "read standard input". A file
 *                  named {@code "-"} also means standard input.
 * @param unique    whether the {@code -u} flag was supplied (drop duplicate lines)
 * @param algorithm which lexicographic sort to use when {@code randomSort} is false
 * @param randomSort whether {@code -R}/{@code --random-sort} was supplied (overrides algorithm)
 */
public record SortOptions(List<String> files, boolean unique, Algorithm algorithm, boolean randomSort) {

    public SortOptions {
        // Defensive copy so the options object cannot be mutated through the caller's list.
        files = List.copyOf(files);
    }

    /** True when no file arguments were given and input should come from standard input. */
    public boolean readsStdin() {
        return files.isEmpty();
    }
}
