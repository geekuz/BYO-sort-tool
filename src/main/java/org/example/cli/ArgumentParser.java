package org.example.cli;

import java.util.ArrayList;
import java.util.List;

/**
 * Translates raw {@code String[] args} into a validated {@link SortOptions}.
 *
 * <p>Supported syntax:
 * <pre>
 *   ccsort [OPTIONS] [FILE...]
 *
 *   -u, --unique               drop duplicate lines
 *   -R, --random-sort          random (hash-based) permutation; equal keys stay together
 *       --algorithm NAME       merge | quick | heap | radix   (default: merge)
 *       --algorithm=NAME       same, equals form
 *   -                          read standard input (may be mixed with files)
 * </pre>
 *
 * <p>Unknown options and bad algorithm names fail fast with an {@link IllegalArgumentException},
 * validated here at the system boundary before any work begins.
 */
public final class ArgumentParser {

    /** Used when the user does not pass {@code --algorithm}. */
    public static final Algorithm DEFAULT_ALGORITHM = Algorithm.MERGE;

    private static final String ALGORITHM_EQUALS_PREFIX = "--algorithm=";

    public SortOptions parse(String[] args) {
        boolean unique = false;
        boolean randomSort = false;
        Algorithm algorithm = DEFAULT_ALGORITHM;
        List<String> files = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-u", "--unique" -> unique = true;
                case "-R", "--random-sort" -> randomSort = true;
                case "--algorithm" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("--algorithm requires a value");
                    }
                    algorithm = Algorithm.fromFlag(args[++i]);
                }
                default -> {
                    if (arg.startsWith(ALGORITHM_EQUALS_PREFIX)) {
                        algorithm = Algorithm.fromFlag(arg.substring(ALGORITHM_EQUALS_PREFIX.length()));
                    } else if (arg.length() > 1 && arg.startsWith("-")) {
                        // length > 1 so the lone "-" (stdin) is treated as a file, not an option.
                        throw new IllegalArgumentException("Unknown option: " + arg);
                    } else {
                        files.add(arg);
                    }
                }
            }
        }

        return new SortOptions(files, unique, algorithm, randomSort);
    }
}
