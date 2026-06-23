package org.example.sort;

/**
 * Top-down, <strong>stable</strong> merge sort.
 *
 * <p>Idea: recursively split the array in half until each piece has one element (trivially
 * sorted), then merge the sorted halves back together. Merging two sorted halves is linear,
 * and we do {@code log n} levels of merging, giving {@code O(n log n)} time in every case.
 *
 * <ul>
 *   <li>Time: O(n log n) best/avg/worst.</li>
 *   <li>Space: O(n) for the auxiliary buffer.</li>
 *   <li>Stable: equal elements keep their original relative order (we prefer the left half on
 *       ties). Stability is why this is the tool's default — it interacts cleanly with {@code -u}.</li>
 * </ul>
 */
public final class MergeSort implements Sorter {

    @Override
    public String[] sort(String[] input) {
        String[] a = input.clone();            // never mutate the caller's array
        if (a.length < 2) {
            return a;
        }
        String[] aux = new String[a.length];   // single reusable buffer
        sort(a, aux, 0, a.length - 1);
        return a;
    }

    private static void sort(String[] a, String[] aux, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(a, aux, lo, mid);
        sort(a, aux, mid + 1, hi);
        merge(a, aux, lo, mid, hi);
    }

    /** Merge the two sorted runs a[lo..mid] and a[mid+1..hi] back into a[lo..hi]. */
    private static void merge(String[] a, String[] aux, int lo, int mid, int hi) {
        System.arraycopy(a, lo, aux, lo, hi - lo + 1);
        int i = lo;        // pointer into left run
        int j = mid + 1;   // pointer into right run
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                a[k] = aux[j++];                       // left exhausted
            } else if (j > hi) {
                a[k] = aux[i++];                       // right exhausted
            } else if (aux[j].compareTo(aux[i]) < 0) {
                a[k] = aux[j++];                        // right is strictly smaller
            } else {
                a[k] = aux[i++];                        // tie -> take left (keeps it stable)
            }
        }
    }
}
