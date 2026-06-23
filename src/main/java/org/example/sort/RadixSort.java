package org.example.sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * MSD (most-significant-digit) radix sort for variable-length strings.
 *
 * <p>Unlike the comparison sorts, radix sort never compares two whole strings. Instead it
 * distributes strings into buckets by their character at position {@code d}, then recursively
 * sorts each bucket on position {@code d + 1}. Processing the most significant character first
 * means that once two strings differ at some position, their relative order is fixed.
 *
 * <p>Two details make the order match {@link String#compareTo}:
 * <ul>
 *   <li>A string that has <em>ended</em> at position {@code d} (i.e. {@code d >= length}) sorts
 *       before any string that still has a character there — that is why {@code "A" < "AB"}.</li>
 *   <li>Within a level we visit buckets in ascending {@code char} order (via {@link TreeMap}),
 *       which mirrors {@code compareTo}'s UTF-16 code-unit comparison.</li>
 * </ul>
 *
 * <p>We bucket only on characters that are actually present at each level (a sparse map rather
 * than a fixed 65,536-slot array), which keeps memory use proportional to the data.
 *
 * <p>Time: roughly O(n * k) where k is the average distinguishing prefix length. Space: O(n).
 */
public final class RadixSort implements Sorter {

    @Override
    public String[] sort(String[] input) {
        List<String> items = new ArrayList<>(input.length);
        for (String s : input) {
            items.add(s);
        }
        List<String> sorted = msd(items, 0);
        return sorted.toArray(new String[0]);
    }

    private static List<String> msd(List<String> items, int d) {
        if (items.size() <= 1) {
            return items;
        }

        List<String> finished = new ArrayList<>();          // strings with no char at position d
        TreeMap<Character, List<String>> buckets = new TreeMap<>();

        for (String s : items) {
            if (d >= s.length()) {
                finished.add(s);
            } else {
                buckets.computeIfAbsent(s.charAt(d), k -> new ArrayList<>()).add(s);
            }
        }

        List<String> result = new ArrayList<>(items.size());
        result.addAll(finished);                            // shorter (prefix) strings come first
        for (Map.Entry<Character, List<String>> entry : buckets.entrySet()) {
            result.addAll(msd(entry.getValue(), d + 1));    // recurse on the next character
        }
        return result;
    }
}
