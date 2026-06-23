package org.example.sort;

/**
 * In-place quicksort, hardened for real-world data.
 *
 * <p>Idea: pick a pivot, partition the array so everything {@code <= pivot} is on the left and
 * everything {@code > pivot} is on the right, then recurse into each side. Two practical
 * tweaks keep it fast and safe on the dictionary-like input this tool sees:
 *
 * <ul>
 *   <li><b>Median-of-three pivot</b> — choosing the median of the first, middle and last
 *       elements avoids the {@code O(n^2)} blow-up that a fixed pivot hits on already-sorted
 *       or reverse-sorted input (exactly what a word list often is).</li>
 *   <li><b>Insertion-sort cutoff</b> — small subarrays are finished with insertion sort, which
 *       is faster than quicksort's overhead for tiny ranges.</li>
 *   <li><b>Tail-call elimination</b> — we recurse into the smaller partition and loop on the
 *       larger one, bounding stack depth to O(log n).</li>
 * </ul>
 *
 * <p>Time: O(n log n) average, O(n^2) worst case (rare with median-of-three). Space: O(log n)
 * stack. Not stable.
 */
public final class QuickSort implements Sorter {

    private static final int INSERTION_CUTOFF = 16;

    @Override
    public String[] sort(String[] input) {
        String[] a = input.clone();
        quicksort(a, 0, a.length - 1);
        return a;
    }

    private static void quicksort(String[] a, int lo, int hi) {
        while (lo < hi) {
            if (hi - lo + 1 <= INSERTION_CUTOFF) {
                insertionSort(a, lo, hi);
                return;
            }
            int p = partition(a, lo, hi);
            // Recurse into the smaller half, iterate on the larger -> O(log n) stack depth.
            if (p - lo < hi - p) {
                quicksort(a, lo, p - 1);
                lo = p + 1;
            } else {
                quicksort(a, p + 1, hi);
                hi = p - 1;
            }
        }
    }

    /** Lomuto partition using the median-of-three element (parked at {@code hi}) as the pivot. */
    private static int partition(String[] a, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        // Order lo, mid, hi so that a[lo] <= a[mid] <= a[hi].
        if (a[mid].compareTo(a[lo]) < 0) {
            swap(a, lo, mid);
        }
        if (a[hi].compareTo(a[lo]) < 0) {
            swap(a, lo, hi);
        }
        if (a[hi].compareTo(a[mid]) < 0) {
            swap(a, mid, hi);
        }
        // Median is now a[mid]; move it to hi to act as the pivot.
        swap(a, mid, hi);
        String pivot = a[hi];

        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j].compareTo(pivot) <= 0) {
                swap(a, ++i, j);
            }
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    private static void insertionSort(String[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            String key = a[i];
            int j = i - 1;
            while (j >= lo && a[j].compareTo(key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    private static void swap(String[] a, int i, int j) {
        String tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
