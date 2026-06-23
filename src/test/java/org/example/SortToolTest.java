package org.example;

import org.example.cli.Algorithm;
import org.example.cli.SortOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortToolTest {

    @Test
    @DisplayName("sorts stdin lines lexicographically")
    void sortsStdinLexicographically() throws IOException {
        SortOptions options = new SortOptions(List.of(), false, Algorithm.MERGE, false);

        String result = run(options, "banana\napple\ncherry\n");

        assertThat(result).isEqualTo("apple\nbanana\ncherry\n");
    }

    @Test
    @DisplayName("-u removes duplicate lines")
    void uniqueRemovesDuplicates() throws IOException {
        SortOptions options = new SortOptions(List.of(), true, Algorithm.MERGE, false);

        String result = run(options, "b\na\nb\na\nc\n");

        assertThat(result).isEqualTo("a\nb\nc\n");
    }

    @Test
    @DisplayName("reads and sorts a file argument")
    void readsFromFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("input.txt");
        Files.writeString(file, "gamma\nalpha\ntango\n");
        SortOptions options = new SortOptions(List.of(file.toString()), false, Algorithm.QUICK, false);

        String result = run(options, "");

        assertThat(result).isEqualTo("alpha\ngamma\ntango\n");
    }

    @Test
    @DisplayName("a missing file produces a clear error")
    void missingFileThrows() {
        SortOptions options = new SortOptions(List.of("does-not-exist.txt"), false, Algorithm.MERGE, false);

        assertThatThrownBy(() -> run(options, ""))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("No such file");
    }

    @Test
    @DisplayName("the challenge's words sample sorts to A, ACTUAL, AGREE, AGREEMENT, AND with -u")
    void wordsSamplePrefixMatchesChallenge() throws IOException {
        Path resource = Path.of("src", "test", "resources", "words-sample.txt");
        SortOptions options = new SortOptions(List.of(resource.toString()), true, Algorithm.MERGE, false);

        String result = run(options, "");
        String[] firstFive = Arrays.copyOf(result.split("\n"), 5);

        assertThat(firstFive).containsExactly("A", "ACTUAL", "AGREE", "AGREEMENT", "AND");
    }

    /** Runs the tool with the given stdin content and returns everything written to stdout. */
    private static String run(SortOptions options, String stdin) throws IOException {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(captured, true, StandardCharsets.UTF_8);
        InputStream in = new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8));

        new SortTool(in).run(options, out);

        return captured.toString(StandardCharsets.UTF_8);
    }
}
