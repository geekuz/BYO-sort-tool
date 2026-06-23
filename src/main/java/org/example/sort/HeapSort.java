package org.example.sort;

/**
 * In-place heap sort using an implicit binary max-heap.
 *
 * <p>Idea: treat the array as a complete binary tree where node {@code i} has children at
 * {@code 2i+1} and {@code 2i+2}. First "heapify" the array into a max-heap (largest element at
 * the root). Then repeatedly swap the root (the current maximum) to the end, shrink the heap by
 * one, and sift the new root down to restore the heap property. Each removal places one element
 * in its final sorted position.
 *
 * <ul>
 *   <li>Time: O(n log n) in every case.</li>
 *   <li>Space: O(1) — sorts in place with no auxiliary array.</li>
 *   <li>Not stable.</li>
 * </ul>
 */
public final class HeapSort implements Sorter {

    @Override
    public String[] sort(String[] input) {
        String[] a = input.clone();
        int n = a.length;

        // Build a max-heap bottom-up: start at the last internal node and sift each down.
        for (int i = n / 2 - 1; i >= 0; i--) {
            siftDown(a, i, n);
        }

        // Repeatedly move the max (root) to the end of the unsorted region.
        for (int end = n - 1; end > 0; end--) {
            swap(a, 0, end);
            siftDown(a, 0, end);
        }
        return a;
    }

    /** Push the element at {@code i} down until the max-heap property holds within a[0..size). */
    private static void siftDown(String[] a, int i, int size) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int largest = i;

            if (left < size && a[left].compareTo(a[largest]) > 0) {
                largest = left;
            }
            if (right < size && a[right].compareTo(a[largest]) > 0) {
                largest = right;
            }
            if (largest == i) {
                return;
            }
            swap(a, i, largest);
            i = largest;
        }
    }

    private static void swap(String[] a, int i, int j) {
        String tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
