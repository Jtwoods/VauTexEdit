package spellcheck.spell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Scanner;


/**
 * A spell checker maintains a dictionary of words that are read in from a text
 * file. It also tracks the frequency of each word as observed in some corpus of
 * literature, which is also read in from a text file. Given some string, the
 * spell checker can search through its dictionary for similar words and return
 * a list of suggested spelling corrections. The corrections are ranked in order
 * their relevance.
 * 
 * @author James Woods
 */
public class Checker {

	/**
	 * ONE is used to represent the number one.
	 */
	private static final int ONE = 1;

	/**
	 * When searching for suggestions, the spell checker will only consider
	 * words which are this far away from the given string in terms of
	 * Levenshtein distance.
	 */
	public static final int MAX_EDIT_DISTANCE = 2;

	/**
	 * index is used to track the size of the array of suggestions
	 */
	private int index;

	/** The root node of the prefix tree used to maintain the dictionary. */
	private Node root = new Node(this);

	/**
	 * Used to track how much work is done during various dictionary operations,
	 * where work is measured by the number of nodes visited. YOU MAY NEVER
	 * CHANGE THE VALUE OF THIS VARIABLE.
	 */
	int nodesVisited = 0;

	/**
	 * Creates a spell checker with no words in the dictionary.
	 */
	public Checker() {
		index = 0;
	}

	/**
	 * Creates a spell checker which recognizes all the words in the
	 * <code>dictionary</code> file as correctly spelled words and which counts
	 * the frequency of those words in the given <code>corpus</code> file.
	 * 
	 * Both files should be plain text with words separated by one or more
	 * whitespace characters.
	 * 
	 * @param dictionary
	 *            a file of words which are correctly spelled
	 * @param corpus
	 *            a file of common literature from which frequency counts will
	 *            be derived
	 * @throws FileNotFoundException
	 *             if either file is not found
	 */
	public Checker(File dictionary, File corpus) throws FileNotFoundException {

		index = 0;
		// Open a scanner for the dictionary.
		Scanner dictionScan = new Scanner(dictionary);
		// Scan each line of the dictionary.
		while (dictionScan.hasNext()) {
			// Open a scanner for the line.
			String lineToInsert = dictionScan.nextLine();
			Scanner lineScan = new Scanner(lineToInsert);
			// scan each word of the line.
			while (lineScan.hasNext()) {
				String wordToInsert = Utilities.sanitize(lineScan.next());
				// Insert each word into the prefix-tree structure.
				addWord(wordToInsert);

			}
			lineScan.close();
		}
		dictionScan.close();

		// Open a scanner for the corpus.
		Scanner corpusScan = new Scanner(corpus);
		// Scan each line of the corpus.
		while (corpusScan.hasNext()) {
			// Open a scanner for the line.
			String lineToInsert = corpusScan.nextLine();
			Scanner lineScan = new Scanner(lineToInsert);
			// scan each word of the line.
			while (lineScan.hasNext()) {
				String wordToIncrease = Utilities.sanitize(lineScan.next());
				// If the word is in the prefix-tree increment the count of the
				// word.
				try {
					increaseFrequency(wordToIncrease);
				} catch (IllegalArgumentException e) {

				}
			}
			lineScan.close();
		}
		corpusScan.close();
	}

	/**
	 * Adds a word to the dictionary of correctly spelled words. If the word is
	 * already in this dictionary, nothing changes (including the frequency
	 * count of that word). Newly added words have a frequency of 1.
	 * 
	 * @param word
	 *            the new word to be added
	 */
	public void addWord(String word) {
		Node temp = root;
		for (int i = 0; i < word.length(); i++) {
			temp = temp.addChild(word.charAt(i));
			if (i == (word.length() - 1) && temp.getFrequency() == 0)
				temp.incrementFrequency();
		}
	}

	/**
	 * Checks whether or not a given word is part of this dictionary.
	 * 
	 * @param word
	 *            the word to check
	 * @return true if the word is in this dictionary, false otherwise
	 */
	public boolean isWord(String word) {
		Node temp = root;
		for (int i = 0; i < word.length(); i++) {
			if (temp != null) {
				temp = temp.getChild(word.charAt(i));
			} else if (temp == null)
				return false;
		}
		if (temp.getFrequency() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Returns the frequency of a given word as it was observed in the corpus
	 * used to initialize this spell checker.
	 * 
	 * @param word
	 *            the word for which the frequency will be checked
	 * @return the frequency of the word in the corpus
	 * @throws IllegalArgumentException
	 *             if the word is not in this dictionary
	 */
	public int getFrequency(String word) {
		Node temp = root;
		for (int i = 0; i < word.length(); i++) {
			temp = temp.getChild(word.charAt(i));
			if (temp == null)
				throw new IllegalArgumentException();
		}

		return temp.getFrequency();
	}

	/**
	 * Increments the frequency of the given word by 1.
	 * 
	 * @param word
	 *            the word whose frequency will be incremented
	 * @throws IllegalArgumentException
	 *             if the word is not in this dictionary
	 */
	public void increaseFrequency(String word) {
		Node temp = root;
		for (int i = 0; i < word.length(); i++) {
			temp = temp.getChild(word.charAt(i));
			if (temp == null)
				throw new IllegalArgumentException();
		}
		if (temp != root)
			temp.incrementFrequency();
	}

	/**
	 * Given some string, this method returns a list of {@link Suggestion}s for
	 * correctly spelled words that are similar to the string. The list of
	 * suggestions is sorted according to three criteria:
	 * <ul>
	 * <li>Suggestions with lower edit distance appear first.
	 * <li>
	 * <li>Between suggestions with the same edit distance, suggestions with
	 * higher frequency counts appear first.
	 * <li>
	 * <li>Between suggestions with the same frequency counts, suggestions first
	 * in alphabetical order appear first.</li>
	 * </ul>
	 * 
	 * The list of suggestions will include all words which are
	 * {@link #MAX_EDIT_DISTANCE} edit distance away from the given string.
	 * 
	 * If the given string is a correctly spelled word, the first suggestion
	 * will be the word itself.
	 * 
	 * @param string
	 *            the string for which suggestions will be returned
	 * @return a list of suggestions sorted by how likely they are to be the
	 *         intended correct spelling
	 */
	public Suggestion[] getSuggestions(String string) {
		int currentEditDistance = 0;
		int currentIndex = 0;

		/**
		 * SpellingComparator allows comparison of Suggestion objects for use in
		 * a sorting algorithm where editDistance is the first criterion,
		 * frequency the second, and alphabetical order the third.
		 * 
		 * @author James Woods
		 * 
		 */
		class SpellingComparator implements Comparator<Suggestion> {

			/**
			 * compare compares o1 to o2 and returns a positive value if o1 is
			 * greater, a negative if it is smaller and a zero if the values are
			 * the same.
			 */
			public int compare(Suggestion o1, Suggestion o2) {

				if (o1.editDistance != o2.editDistance)
					return o1.editDistance - o2.editDistance;
				else if (o1.frequency != o2.frequency)
					return o2.frequency - o1.frequency;
				else
					return o1.string.compareTo(o2.string);

			}

		}

		// Create a SpellingComparator to make comparisons between Suggestion
		// objects.
		SpellingComparator comparator = new SpellingComparator();

		Suggestion[] suggestions = new Suggestion[100];
		searchPrefixTree(string, currentEditDistance, currentIndex, root,
				suggestions);

		suggestions = trimArray(suggestions);

		// Create a HeapSort for the suggestions
		HeapSorter<Suggestion> sort = new HeapSorter<Suggestion>(comparator);
		// Sort
		sort.sort(suggestions);

		//reset the index.
		index = 0;
		// Return
		return suggestions;
	}

	/**
	 * searchPrefixTree is a private helper method that performs a search of the
	 * prefix-tree and adds found words within the edit distance to an array of
	 * words.
	 * 
	 * @param string
	 *            the string to find words for.
	 * @param currentEditDistance
	 *            the current edit distance.
	 * @param currentIndex
	 *            the index position in the string.
	 * @param current
	 *            the current node in the prefix tree.
	 * @param words
	 *            the array of words.
	 */
	private void searchPrefixTree(String string, int currentEditDistance,
			int currentIndex, Node current, Suggestion[] words) {

		if (current != null) {
			// If the current nodes string is less than x away.
			if (currentEditDistance < MAX_EDIT_DISTANCE) {

				// insert each letter.
				for (char toCheck : Utilities.LETTERS) {
					Node checkNode = current.getChild(toCheck);

					searchPrefixTree(string, currentEditDistance + ONE,
							currentIndex, checkNode, words);
					// substitute each letter.
					searchPrefixTree(string, currentEditDistance + ONE,
							currentIndex + ONE, checkNode, words);
				}
				// delete current letter.
				searchPrefixTree(string, currentEditDistance + ONE,
						currentIndex + ONE, current, words);

			}

			// If index < length of the string
			if (currentIndex < string.length()) {

				searchPrefixTree(string, currentEditDistance, currentIndex
						+ ONE, current.getChild(string.charAt(currentIndex)),
						words);
			}
			// If index == length and the string is a word.
			int freq = 0;
			if (currentIndex == string.length()
					&& (freq = current.getFrequency()) > 0) {
				// replace edit distance 2 words with frequency one words.
				if (currentEditDistance < 2
						&& arrayContains(words, new Suggestion(current
								.toString().toUpperCase(), 2, freq))) {
					// replace the edit distance two version.
					replaceInArray(words, new Suggestion(current.toString()
							.toUpperCase(), 2, freq), new Suggestion(current
							.toString().toUpperCase(), currentEditDistance,
							freq));
				}
				// if edit distance one words already exist don't add them
//				else if (currentEditDistance < 2
//						&& !arrayContains(words, new Suggestion(current
//								.toString().toUpperCase(), ONE, freq))) {
//					// insert the edit distance one word.
//					words = verifyCapacity(words);
//					words[index++] = new Suggestion(current.toString()
//							.toUpperCase(), currentEditDistance, freq);
//				}
				// if edit distance two words already exist don't add them
				// and if there is an edit distance one word don't add them
				else if (currentEditDistance == 2
						&& !arrayContains(words, new Suggestion(current
								.toString().toUpperCase(), 2, freq))
						&& !arrayContains(words, new Suggestion(current
								.toString().toUpperCase(), ONE, freq))) {
					// insert the edit distance two word.
					words[index++] = new Suggestion(current.toString()
							.toUpperCase(), currentEditDistance, freq);
				}

			}
		}

	}

	/**
	 * arrayContains is a helper method that returns a boolean representing
	 * whether a given item is in the given array.
	 * 
	 * @param array
	 *            the array to search.
	 * @param element
	 *            the element to find in the array.
	 * @return whether or not the item is in the array or not.
	 */
	private boolean arrayContains(Suggestion[] array, Suggestion element) {
		for (int i = 0; i < index; i++) {
			if (array[i].toString().equals(element.toString()))
				return true;
		}
		return false;
	}

	/**
	 * replaceInArray is a private helper method that finds an item in an array
	 * and replaces it with another item.
	 * 
	 * @param array
	 *            to perform replacement in.
	 * @param toReplace
	 *            the item to be replaced.
	 * @param replace
	 *            the item to place in the array.
	 */
	private void replaceInArray(Suggestion[] array, Suggestion toReplace,
			Suggestion replace) {
		for (int i = 0; i < index; i++) {
			if (array[i].toString().equals(toReplace.toString()))
				array[i] = replace;
		}
	}

	/**
	 * trimArray re-sizes an array so that it no longer contains any null
	 * values.
	 * 
	 * @param array
	 *            to be trimmed.
	 * @return the trimmed array.
	 */
	private Suggestion[] trimArray(Suggestion[] array) {

			Suggestion temp[] = new Suggestion[index];

			for (int i = 0; i < index; i++) {
				temp[i] = array[i];
			}
			return temp;

	}
}
