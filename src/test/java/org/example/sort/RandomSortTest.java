package org.example.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RandomSortTest {

    @Test
    @DisplayName("output is a permutation of the input (same multiset)")
    void isPermutationOfInput() {
        String[] input = {"a", "b", "c", "a", "z", "b"};

        String[] output = new RandomSort(123L).sort(input);

        assertThat(output).containsExactlyInAnyOrder(input);
    }

    @Test
    @DisplayName("equal keys are grouped together (never split across the output)")
    void equalKeysAreAdjacent() {
        String[] input = {"a", "b", "a", "c", "b", "a", "c"};

        String[] output = new RandomSort(7L).sort(input);

        // Walk the output; a key may only start one contiguous run.
        Set<String> alreadySeen = new HashSet<>();
        String current = null;
        for (String value : output) {
            if (!value.equals(current)) {
                assertThat(alreadySeen)
                        .as("key '%s' appeared in two separate groups", value)
                        .doesNotContain(value);
                alreadySeen.add(value);
                current = value;
            }
        }
    }

    @Test
    @DisplayName("a fixed seed yields a reproducible ordering")
    void deterministicForFixedSeed() {
        String[] input = {"a", "b", "c", "d", "e", "f"};

        String[] first = new RandomSort(99L).sort(input);
        String[] second = new RandomSort(99L).sort(input);

        assertThat(first).containsExactly(second);
    }

    @Test
    @DisplayName("different seeds generally produce different orderings")
    void differentSeedsDiffer() {
        String[] input = {"a", "b", "c", "d", "e", "f", "g", "h"};

        String[] first = new RandomSort(1L).sort(input);
        String[] second = new RandomSort(2L).sort(input);

        // Not a hard guarantee for any single pair, but astronomically unlikely to match here.
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("does not mutate its input")
    void doesNotMutateInput() {
        String[] input = {"c", "a", "b"};

        new RandomSort(5L).sort(input);

        assertThat(input).containsExactly("c", "a", "b");
    }
}
