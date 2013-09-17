package spellcheck.spell;

/**
 * Represents a single suggested correction for some string.  A suggestion has
 * three parts:
 * <ul>
 *   <li>the string that is the suggested correction</li>
 *   <li>the minimum edit distance the suggested string is from the original string</li>
 *   <li>the frequency of the suggested string in the corpus used to initialize the spell checker</li>
 * </ul>
 * 
 * @author Stephen G. Ware
 */
public class Suggestion {

	/** The suggested spelling correction */
	public final String string;
	
	/** The minimum edit distance the suggested string is from the original string */
	public final int editDistance;
	
	/** The frequency of the string in the corpus used to initialize the spell checker */
	public final int frequency;
	
	/**
	 * Creates a new spelling suggestion.
	 * 
	 * @param string the suggested correction
	 * @param editDistance the minimum edit distance this string is from the original string being corrected
	 * @param frequency the frequency of this string in the corpus used to initialize the spell checker
	 */
	public Suggestion(String string, int editDistance, int frequency){
		this.string = string;
		this.editDistance = editDistance;
		this.frequency = frequency;
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Suggestion){
			Suggestion otherSuggestion = (Suggestion) other;
			return string.equals(otherSuggestion.string) && editDistance == otherSuggestion.editDistance && frequency == otherSuggestion.frequency;
		}
		else
			return false;
	}
	
	@Override
	public String toString(){
		return "[Suggestion: \"" + string + "\" (edit distance " + editDistance + ") (frequency " + frequency + ")]";
	}
}
