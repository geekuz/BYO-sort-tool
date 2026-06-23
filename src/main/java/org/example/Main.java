package org.example;

import org.example.cli.ArgumentParser;
import org.example.cli.SortOptions;

import java.io.IOException;

/**
 * Command-line entry point for the "Build Your Own sort" tool.
 *
 * <p>Usage examples:
 * <pre>
 *   java -cp target/classes org.example.Main words.txt
 *   java -cp target/classes org.example.Main -u words.txt
 *   java -cp target/classes org.example.Main --algorithm radix words.txt
 *   java -cp target/classes org.example.Main --random-sort -u words.txt
 *   cat words.txt | java -cp target/classes org.example.Main
 * </pre>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        try {
            SortOptions options = new ArgumentParser().parse(args);
            new SortTool(System.in).run(options, System.out);
        } catch (IllegalArgumentException e) {
            // Bad usage (unknown flag / algorithm): exit code 2, like many CLI tools.
            System.err.println("ccsort: " + e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            // Missing file / read error.
            System.err.println("ccsort: " + e.getMessage());
            System.exit(1);
        }
    }
}
