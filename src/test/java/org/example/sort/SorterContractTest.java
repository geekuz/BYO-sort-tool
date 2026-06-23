package org.example.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Behavioural contract that every lexicographic {@link Sorter} must satisfy. Using
 * {@link Arrays#sort} as the trusted oracle, each algorithm is exercised over the same cases.
 */
class SorterContractTest {

    /** All comparison/distribution sorters under test (random sort has its own test). */
    static Stream<Sorter> sorters() {
        return Stream.of(new MergeSort(), new QuickSort(), new HeapSort(), new RadixSort());
    }

    @ParameterizedTest(name = "{0} sorts like Arrays.sort")
    @MethodSource("sorters")
    @DisplayName("matches the JDK reference sort on a large random dataset")
    void sortsLikeReferenceOnRandomData(Sorter sorter) {
        Random random = new Random(42);
        String[] data = randomStrings(random, 5000);
        String[] expected = data.clone();
        Arrays.sort(expected);

        assertThat(sorter.sort(data)).containsExactly(expected);
    }

    @ParameterizedTest(name = "{0} handles edge cases")
    @MethodSource("sorters")
    void handlesEdgeCases(Sorter sorter) {
        assertThat(sorter.sort(new String[] {})).isEmpty();
        assertThat(sorter.sort(new String[] {"x"})).containsExactly("x");
        assertThat(sorter.sort(new String[] {"b", "b", "b"})).containsExactly("b", "b", "b");
        assertThat(sorter.sort(new String[] {"c", "b", "a"})).containsExactly("a", "b", "c");
        assertThat(sorter.sort(new String[] {"a", "b", "c"})).containsExactly("a", "b", "c");
    }

    @ParameterizedTest(name = "{0} preserves duplicates (multiset)")
    @MethodSource("sorters")
    void preservesDuplicateCounts(Sorter sorter) {
        String[] data = {"pear", "apple", "pear", "apple", "apple", "fig"};
        String[] expected = data.clone();
        Arrays.sort(expected);

        assertThat(sorter.sort(data)).containsExactly(expected);
    }

    @ParameterizedTest(name = "{0} does not mutate its input")
    @MethodSource("sorters")
    void doesNotMutateInput(Sorter sorter) {
        String[] data = {"c", "a", "b"};
        sorter.sort(data);

        assertThat(data).containsExactly("c", "a", "b");
    }

    private static String[] randomStrings(Random random, int count) {
        String[] data = new String[count];
        for (int i = 0; i < count; i++) {
            data[i] = randomString(random);
        }
        return data;
    }

    private static String randomString(Random random) {
        int length = random.nextInt(8); // includes length 0 -> empty strings
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + random.nextInt(26)));
        }
        return sb.toString();
    }
}
