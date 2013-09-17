package spellcheck.spell;

import java.util.Comparator;

/**
 * A sorter is an object which rearranges an array of objects such that the
 * objects are in order from smallest to largest.  A sorter is created with a
 * {@link java.util.Comparator} that specifies what order the elements should
 * be in after the sort is finished.
 * 
 * @author Stephen G. Ware
 * @param <T> the type of element in the arrays that will be sorted
 */
public abstract class Sorter<T> {

	/** An object which, for a given pair of elements, specifies what order those elements should be in */
	protected final Comparator<T> comparator;
	
	/**
	 * Creates a sorter using a given {@link java.util.Comparator}.
	 * 
	 * @param comparator the comparator which will specify the correct order of elements
	 */
	public Sorter(Comparator<T> comparator){
		this.comparator = comparator;
	}
	
	/**
	 * Creates a sorter using a {@link DefaultComparator}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Sorter(){
		this((Comparator<T>) new DefaultComparator());
	}
	
	/**
	 * Rearranges an array so that the elements are in order from smallest to
	 * largest.
	 * 
	 * @param array the array to be rearranged
	 */
	public abstract void sort(T[] array);
	
	/**
	 * Returns a string representation of an array for debugging purposes.
	 * 
	 * @param array the array which will be represented as a string
	 * @return a string representation of the array
	 */
	public static final String toString(Object[] array){
		String str = "[";
		boolean first = true;
		for(int i=0; i<array.length; i++){
			if(first)
				first = false;
			else
				str += ", ";
			str += array[i];
		}
		return str + "]";
	}
}
