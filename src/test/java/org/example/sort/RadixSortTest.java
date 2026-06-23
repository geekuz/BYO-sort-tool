package org.example.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Focused tests for the MSD radix sort's trickiest property: ordering strings that are prefixes
 * of one another, where "the shorter string sorts first" must match {@link String#compareTo}.
 */
class RadixSortTest {

    private final RadixSort sorter = new RadixSort();

    @Test
    @DisplayName("shared prefixes and varying lengths sort like compareTo")
    void sortsSharedPrefixesAndVariableLengths() {
        String[] input = {"AB", "A", "AA", "ABC", "", "B", "AB"};
        String[] expected = input.clone();
        Arrays.sort(expected);

        assertThat(sorter.sort(input)).containsExactly(expected);
    }

    @Test
    @DisplayName("a prefix sorts before the longer string that extends it")
    void prefixSortsBeforeExtension() {
        assertThat(sorter.sort(new String[] {"ABC", "AB"})).containsExactly("AB", "ABC");
    }

    @Test
    @DisplayName("mixed-case ordering follows char (code-unit) order, not dictionary order")
    void ordersByCharCode() {
        // Uppercase letters (65..90) sort before lowercase (97..122) in compareTo.
        String[] input = {"banana", "Banana", "apple", "Apple"};
        String[] expected = input.clone();
        Arrays.sort(expected);

        assertThat(sorter.sort(input)).containsExactly(expected);
    }
}
