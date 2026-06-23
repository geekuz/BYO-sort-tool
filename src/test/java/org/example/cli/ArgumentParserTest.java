package org.example.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArgumentParserTest {

    private final ArgumentParser parser = new ArgumentParser();

    @Test
    @DisplayName("with no args, defaults to merge sort reading stdin")
    void defaultsToMergeAndStdin() {
        SortOptions options = parser.parse(new String[] {});

        assertThat(options.algorithm()).isEqualTo(Algorithm.MERGE);
        assertThat(options.unique()).isFalse();
        assertThat(options.randomSort()).isFalse();
        assertThat(options.files()).isEmpty();
        assertThat(options.readsStdin()).isTrue();
    }

    @Test
    @DisplayName("a bare argument is treated as a file")
    void parsesFileArgument() {
        SortOptions options = parser.parse(new String[] {"words.txt"});

        assertThat(options.files()).containsExactly("words.txt");
        assertThat(options.readsStdin()).isFalse();
    }

    @Test
    @DisplayName("-u and --unique both enable unique")
    void parsesUniqueShortAndLong() {
        assertThat(parser.parse(new String[] {"-u"}).unique()).isTrue();
        assertThat(parser.parse(new String[] {"--unique"}).unique()).isTrue();
    }

    @Test
    @DisplayName("-R and --random-sort both enable random sort")
    void parsesRandomSortShortAndLong() {
        assertThat(parser.parse(new String[] {"-R"}).randomSort()).isTrue();
        assertThat(parser.parse(new String[] {"--random-sort"}).randomSort()).isTrue();
    }

    @Test
    @DisplayName("--algorithm NAME (space form) selects the algorithm")
    void parsesAlgorithmSpaceSeparated() {
        assertThat(parser.parse(new String[] {"--algorithm", "quick"}).algorithm())
                .isEqualTo(Algorithm.QUICK);
    }

    @Test
    @DisplayName("--algorithm=NAME (equals form) selects the algorithm")
    void parsesAlgorithmEqualsForm() {
        assertThat(parser.parse(new String[] {"--algorithm=heap"}).algorithm())
                .isEqualTo(Algorithm.HEAP);
    }

    @Test
    @DisplayName("flags and a file can be combined in any order")
    void combinesFlagsAndFile() {
        SortOptions options = parser.parse(new String[] {"-u", "--algorithm", "radix", "words.txt"});

        assertThat(options.unique()).isTrue();
        assertThat(options.algorithm()).isEqualTo(Algorithm.RADIX);
        assertThat(options.files()).containsExactly("words.txt");
    }

    @Test
    @DisplayName("a lone '-' is kept as a (stdin) file, not rejected as an option")
    void treatsDashAsStdinFile() {
        assertThat(parser.parse(new String[] {"-"}).files()).containsExactly("-");
    }

    @Test
    @DisplayName("unknown option fails fast")
    void rejectsUnknownOption() {
        assertThatThrownBy(() -> parser.parse(new String[] {"--nope"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown option");
    }

    @Test
    @DisplayName("unknown algorithm name fails fast")
    void rejectsUnknownAlgorithm() {
        assertThatThrownBy(() -> parser.parse(new String[] {"--algorithm", "bogus"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown algorithm");
    }

    @Test
    @DisplayName("--algorithm with no value fails fast")
    void algorithmRequiresValue() {
        assertThatThrownBy(() -> parser.parse(new String[] {"--algorithm"}))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requires a value");
    }
}
