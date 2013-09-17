package spellcheck.spell;

/**
 * A group of useful utilities for creating a spell checker.
 * 
 * @author Stephen G. Ware
 */
public class Utilities {

	/** An array of all valid letters in order */
	public static final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	
	/** The integer value of the first letter */
	private static final int CHAR_INDEX_OFFSET = (int) LETTERS[0];
	
	/**
	 * Given one of the letters in {@link #LETTERS}, this function returns the
	 * index of that letter in the {@link #LETTERS} array.
	 * 
	 * @param c the letter
	 * @return the index of that letter in {@link #LETTERS}
	 */
	public static final int indexOf(char c){
		int index = ((int) Character.toUpperCase(c)) - CHAR_INDEX_OFFSET;
		if(index < 0 || index > (LETTERS.length - 1))
			throw new IllegalArgumentException("The character '" + c + "' is not a letter.");
		else
			return index;
	}
	
	/**
	 * Given some string, this functions converts it to upper case and removes
	 * all non-letter characters.
	 *  
	 * @param string the string to sanitize
	 * @return the sanitized string
	 */
	public static final String sanitize(String string){
		return string.toUpperCase().replaceAll("[^A-Z]+", "");
	}
	
	/**
	 * Given some string, this function first {@link #sanitize}s it and then
	 * converts it to an array of characters.
	 * 
	 * @param string the string to convert
	 * @return the sanitized string, as an array of characters
	 */
	public static final char[] getLetters(String string){
		return sanitize(string).toCharArray();
	}
}
