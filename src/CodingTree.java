import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A coding tree.
 * 
 * @author Sayf Elhawary
 */
public class CodingTree {

	private Trie<BitString,Character> tree_; // The coding tree

	/**
	 * Creates a coding tree according to the desired trie.
	 * 
	 * @param tree
	 *          the desired trie for the coding tree.
	 */
	public CodingTree ( Trie<BitString,Character> tree ) {
		tree_ = tree;
	}

	/**
	 * Creates a coding tree by reading it from the desired BitInputStream.
	 * 
	 * @param in
	 *          input.
	 * @throws IOException
	 */
	public CodingTree ( BitInputStream in ) throws IOException {
		tree_ = helperCodingTree(in);
	}

	/**
	 * Creates a coding tree by reading it from the desired BitInputStream.
	 * 
	 * @param in
	 *          input.
	 * @return a Trie.
	 * @throws IOException
	 */
	private Trie<BitString,Character> helperCodingTree ( BitInputStream in )
	    throws IOException {
		Trie<BitString,Character> subtree = new Trie<BitString,Character>();
		BitString bitsRead = in.readBits(1);
		if ( bitsRead.equals(BitString.ONE) ) {
			BitString compressedChar = in.readBits(HuffConstants.BITS_PER_CHARACTER);
			subtree = new Trie<BitString,Character>(compressedChar.toCharacter());
			return subtree;
		} else {
			subtree.attach(subtree.getRoot(),helperCodingTree(in),BitString.ZERO);
			subtree.attach(subtree.getRoot(),helperCodingTree(in),BitString.ONE);
			return subtree;
		}

	}

	/**
	 * Writes the coding tree to the desired BitOutputstream.
	 * 
	 * @param out
	 *          output.
	 * @throws IOException
	 */
	public void write ( BitOutputStream out ) throws IOException {
		write(out,tree_.getRoot());
	}

	/**
	 * Writes the coding tree to the desired BitOutputstream.
	 * 
	 * @param out
	 *          output.
	 * @param current
	 *          The desired current node.
	 * @throws IOException
	 */
	private void write ( BitOutputStream out, Node<Character> current )
	    throws IOException {
		if ( tree_.isLeaf(current) ) {
			out.writeBits(BitString.ONE);
			out.writeBits(new BitString(current.getElement()));
		} else {
			out.writeBits(BitString.ZERO);
			write(out,tree_.getChild(current,BitString.ZERO));
			write(out,tree_.getChild(current,BitString.ONE));
		}
	}

	/**
	 * Reads the next compressed character from the input and gets the
	 * uncompressed version of that character as a Character.
	 * 
	 * @param in
	 *          input.
	 * @return The uncompressed version of the next compressed character as a
	 *         Character.
	 * @throws IOException
	 */
	public Character nextChar ( BitInputStream in ) throws IOException {
		Node<Character> current = tree_.getRoot();
		for ( ; !tree_.isLeaf(current) ; ) {
			BitString bitstr = in.readBits(1);
			current = tree_.getChild(current,bitstr);
		}
		return current.getElement();

	}

	/**
	 * Returns the encodings represented by this coding tree.
	 * 
	 * @return The encodings represented by this coding tree.
	 */
	public Map<Character,BitString> getEncodings () {
		Map<Character,BitString> encodings = new HashMap<Character,BitString>();
		getEncodings(tree_.getRoot(),new BitString(),encodings);
		return encodings;
	}

	/**
	 * Returns the encodings represented by this character node.
	 * 
	 * @param current
	 *          a character node.
	 * @param bitstr
	 *          a bitString
	 * @param encodings
	 *          encodings
	 * @return the encodings represented by this character node.
	 */
	private void getEncodings ( Node<Character> current, BitString bitstr,
	                            Map<Character,BitString> encodings ) {
		if ( tree_.isLeaf(current) ) {
			encodings.put(current.getElement(),bitstr);
		} else {
			getEncodings(tree_.getChild(current,BitString.ONE),
			             bitstr.concat(BitString.ONE),encodings);
			getEncodings(tree_.getChild(current,BitString.ZERO),
			             bitstr.concat(BitString.ZERO),encodings);
		}

	}
}
