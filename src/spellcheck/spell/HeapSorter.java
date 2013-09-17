package spellcheck.spell;

import java.util.Comparator;

/**
 * Heap Sort is an in-place comparison sort which runs in Theta(n log_2(n)) in
 * all cases. Heap Sort has the same asymptotic run times as Merge Sort but only
 * requires a constant amount of additional space. Heap Sort is also not stable.
 * 
 * @author James Woods
 * @param <T>
 *            the type of element in the arrays that will be sorted
 */
public class HeapSorter<T> extends Sorter<T> {

	/**
	 * FIRST is the first position in the array
	 */
	private static final int FIRST = 0;
	

	/**
	 * OFFSET is the offset used when dealing with array items.
	 */
	private static final int OFFSET = 1;

	/**
	 * DOUBLE is used as a divisor and offset when working with power of two
	 * mapping used in PriorityHeap
	 */
	private static final int DOUBLE = 2;

	/**
	 * Creates a new sorter which uses Heap Sort to sort arrays.
	 * 
	 * @param comparator
	 *            the comparator which will specify the correct order of
	 *            elements
	 */
	public HeapSorter(Comparator<T> comparator) {
		super(comparator);
	}

	/**
	 * Creates a new sorter which uses Heap Sort to sort arrays.
	 * {@link DefaultComparator}.
	 */
	public HeapSorter() {
		super();
	}

	/**
	 * Rearranges an array so that the elements are in order from smallest to
	 * largest.
	 * 
	 * @param array
	 *            the array to be rearranged
	 */
	public void sort(T[] array) {
		buildMaxHeap(array);
		for (int h = array.length; h > 0; h--) {
			// swap index 0 and h - 1
			T temp = array[FIRST];
			array[FIRST] = array[h - OFFSET];
			array[h - OFFSET] = temp;
			maxHeapifyDown(array, h - OFFSET);
		}
	}

	/**
	 * Rearranges an array in any order into a max heap.
	 * 
	 * @param array
	 *            the array to be converted
	 */
	public void buildMaxHeap(T[] array) {
		int leaves = (array.length / DOUBLE) - OFFSET;
		for (int x = leaves; x >= 0; x--) {
			maxHeapifyDown(array, array.length, x);
		}
	}

	/**
	 * Given an array which is a max heap except for the root element, this
	 * method rearranges the array to make it fully a max heap (including the
	 * root). This method specifies a range which describes the length of the
	 * array that should be considered the heap. In other words, only the
	 * elements from index 0 (inclusive) to range (exclusive) will be
	 * rearranged. Elements outside the range will not be moved.
	 * 
	 * @param array
	 *            the array which is a max heap except for the root
	 * @param range
	 *            the length of the array to be considered the heap
	 */
	public void maxHeapifyDown(T[] array, int range) {
		maxHeapifyDown(array, range, FIRST);
	}

	/**
	 * maxHeapifyDown is an overloaded helper method for the maxHeapifyDown
	 * method that allows for a recursive call to perform the maxHeapifyDown
	 * process from a given start location.
	 * 
	 * @param array
	 *            the array which is a max heap except for the root
	 * @param range
	 *            the length of the array to be considered the heap
	 * @param start
	 *            the index of the starting node.
	 */
	private void maxHeapifyDown(T[] array, int range, int start) {

		// get children indexes
		int left = start * DOUBLE + OFFSET;
		int right = start * DOUBLE + DOUBLE;

		int comparisonLeft = 0;
		int comparisonRight = 0;

		T largest = array[start];
		int index = start;
		// make comparison between the current value and
		// its children.
		if (left < range) {
			comparisonLeft = comparator.compare(array[start], array[left]);
			if (comparisonLeft < 0) {
				index = left;
				largest = array[left];
			}

		}
		if (right < range) {
			comparisonRight = comparator.compare(largest, array[right]);
			if (comparisonRight < 0) {
				index = right;
			}

		}

		if (index != start) {
			// make the swap.
			T temp = array[index];
			array[index] = array[start];
			array[start] = temp;
			maxHeapifyDown(array, range, index);
		}

	}
}
