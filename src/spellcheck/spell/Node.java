package spellcheck.spell;


/**
 * This class represents an individual node in the dictionary prefix tree.  The
 * path to a node from the root is a string of letters, so each node in the
 * tree represents a string (with the root node representing the empty string).
 * 
 * Each node keeps track of the following information:
 * <ul>
 *   <li>its parent node</li>
 *   <li>26 children nodes, one for each letter</li>
 *   <li>its depth in the tree (the root has depth 0)</li>
 *   <li>the letter for which this node is its parent's child</li>
 *   <li>whether or not the string represented by this node is a correctly
 *   spelled word in the dictionary</li>
 *   <li>the number of times this string has appeared in the corpus used to
 *   initialize the spell checker (nodes which do not represent correctly
 *   spelled words have a frequency of 0)</li>
 * </ul>
 * 
 * Initially, every child node is null.  As the prefix tree is expanded,
 * children nodes must be added via {@link Node#addChild(char)}.
 * 
 * This class keeps track of how much work is done by the spell checker, where
 * work is measured by the number of nodes visited.
 * 
 * YOU MAY NOT CHANGE THIS CLASS IN ANY WAY.
 * 
 * @author Stephen G. Ware
 */
final class Node {
	
	/** The spell checker to which this node belongs */
	private final Checker checker;
	
	/** The node's parent */
	public final Node parent;
	
	/** The depth at which this node appears in the tree */
	public final int depth;
	
	/** The letter to follow from the parent node to reach this node */
	public final char letter;
	
	/** The frequency of this string in the corpus used to initialize the spell checker */
	private int frequency = 0;
	
	/** The children nodes, one for each letter */
	private Node[] children = new Node[Utilities.LETTERS.length];
	
	/**
	 * Creates a child node from a parent node and a given letter.
	 * 
	 * @param checker the spell checker to which this node belongs
	 * @param parent the parent of this node
	 * @param letter the letter to follow from the parent node to reach this node
	 */
	private Node(Node parent, char letter){
		this.checker = parent.checker;
		this.parent = parent;
		this.depth = parent.depth + 1;
		this.letter = letter;
	}
	
	/**
	 * Creates a root node.
	 * 
	 * @param checker the spell checker to which this node belongs
	 */
	public Node(Checker checker){
		this.checker = checker;
		this.parent = null;
		this.depth = 0;
		this.letter = '\0';
	}
	
	/**
	 * Returns the frequency of the string represented by this node in the
	 * corpus used to initialize the spell checker.  Nodes with frequency 0 are
	 * not valid words.  Nodes with frequency of 1 or more are valid words.
	 * 
	 * @return the frequency of the string
	 */
	public int getFrequency(){
		return frequency;
	}
	
	/**
	 * Increments the frequency count of the string represented by this node by
	 * 1.
	 */
	public void incrementFrequency(){
		frequency++;
	}
	
	/**
	 * Gets the child node associated with a given letter.  The returned node
	 * represents this node's string with the given letter added to the end.
	 * 
	 * @param letter the letter to add to the string
	 * @return the node representing this string plus the letter, or null if no child node exists for that letter
	 */
	public Node getChild(char letter){
		checker.nodesVisited++;
		return children[Utilities.indexOf(letter)];
	}
	
	/**
	 * Adds a child node for a given letter and returns the new child node.  If
	 * a child node already exists for that letter, the existing child node is
	 * returned.
	 * 
	 * @param letter the letter for which to add a child node
	 * @return the newly created child node, or the child node that already existed
	 */
	public Node addChild(char letter){
		Node child = children[Utilities.indexOf(letter)];
		if(child == null){
			child = new Node(this, letter);
			children[Utilities.indexOf(letter)] = child;
		}
		checker.nodesVisited++;
		return child;
	}
	
	/**
	 * Returns the string represented by this node.
	 */
	public String toString(){
		char[] letters = new char[depth];
		Node current = this;
		for(int i = depth - 1; i >= 0; i--){
			letters[i] = current.letter;
			current = current.parent;
		}
		return new String(letters);
	}
}
