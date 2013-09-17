package spellcheck.spell;

import java.util.Comparator;

/**
 * A default comparator compares {java.lang.Comparable} objects according to
 * their {java.lang.Comparable#compare} method.  A default comparator can be
 * used for data structures that require a comparator when the user does not
 * provide a comparator.
 * 
 * @author Stephen G. Ware
 * @param <T> the type of object to be compared
 */
public class DefaultComparator<T extends Comparable<? super T>> implements Comparator<T> {

	public int compare(T left, T right){
		return left.compareTo(right);
	}
}
