package org.example;

import org.example.cli.SortOptions;
import org.example.sort.HeapSort;
import org.example.sort.MergeSort;
import org.example.sort.QuickSort;
import org.example.sort.RadixSort;
import org.example.sort.RandomSort;
import org.example.sort.Sorter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the whole pipeline: read lines -> pick a {@link Sorter} -> sort -> (optionally)
 * drop duplicates -> write.
 *
 * <p>Standard input is injected via the constructor so the class is testable without touching
 * the real {@code System.in}.
 */
public final class SortTool {

    private static final String STDIN_MARKER = "-";

    private final InputStream stdin;

    public SortTool(InputStream stdin) {
        this.stdin = stdin;
    }

    public void run(SortOptions options, PrintStream out) throws IOException {
        String[] data = readAllLines(options).toArray(new String[0]);
        String[] sorted = sorterFor(options).sort(data);
        writeLines(sorted, options.unique(), out);
    }

    private List<String> readAllLines(SortOptions options) throws IOException {
        if (options.readsStdin()) {
            return readStdin();
        }
        List<String> lines = new ArrayList<>();
        for (String file : options.files()) {
            if (file.equals(STDIN_MARKER)) {
                lines.addAll(readStdin());
            } else {
                lines.addAll(readFile(file));
            }
        }
        return lines;
    }

    private static List<String> readFile(String file) throws IOException {
        try {
            return Files.readAllLines(Path.of(file), StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            // Surface a clear, sort-style message instead of a raw stack trace path.
            throw new IOException("No such file or directory: " + file);
        }
    }

    private List<String> readStdin() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stdin, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    /** {@code --random-sort} wins over {@code --algorithm}, mirroring GNU sort. */
    private static Sorter sorterFor(SortOptions options) {
        if (options.randomSort()) {
            return new RandomSort();
        }
        return switch (options.algorithm()) {
            case MERGE -> new MergeSort();
            case QUICK -> new QuickSort();
            case HEAP -> new HeapSort();
            case RADIX -> new RadixSort();
        };
    }

    /**
     * Writes each line, optionally collapsing runs of equal lines for {@code -u}. Because the
     * input is sorted (and the random sort groups equal keys), duplicates are always adjacent,
     * so comparing against the previous line is enough.
     */
    private static void writeLines(String[] sorted, boolean unique, PrintStream out) {
        String previous = null;
        for (String line : sorted) {
            if (unique && line.equals(previous)) {
                continue;
            }
            out.println(line);
            previous = line;
        }
    }
}
