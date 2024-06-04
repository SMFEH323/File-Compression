/**
 * The partial coding trees that are stored in the priority queue while building
 * up the full coding tree from the character counts.
 * 
 * @author Sayf Elhawary
 */
public class WeightedCodingTree {

	private Trie<BitString,Character> codingTree_; // The coding tree

	private int treeWeight_; // The tree's weight

	/**
	 * Creates a WeightedCodingTree according to the desired weight and character
	 * 
	 * @param c
	 *          Uncompressed character.
	 * @param weight
	 *          The desired weight for the coding tree.
	 */
	public WeightedCodingTree ( int weight, char c ) {
		treeWeight_ = weight;
		codingTree_ = new Trie<BitString,Character>(c);
	}

	/**
	 * Creates a WeightedCodingTree with the desired first WeightedCodingTree as
	 * the 0 child of the root and the desired second WeightedCodingTree as the 1
	 * child of the root.
	 * 
	 * @param tree1
	 *          The first WeightedCodingTree.
	 * @param tree2
	 *          The second WeightedCodingTree.
	 */
	public WeightedCodingTree ( WeightedCodingTree tree1,
	                            WeightedCodingTree tree2 ) {
		treeWeight_ = tree1.getWeight() + tree2.getWeight();
		codingTree_ = new Trie<BitString,Character>();
		codingTree_.attach(codingTree_.getRoot(),tree1.codingTree_,BitString.ZERO);
		codingTree_.attach(codingTree_.getRoot(),tree2.codingTree_,BitString.ONE);
	}

	/**
	 * Gets a new CodingTree with the stored trie.
	 * 
	 * @return a new CodingTree with the stored trie.
	 */
	public CodingTree getCodingTree () {
		return new CodingTree(codingTree_);
	}

	/**
	 * Gets the weight.
	 * 
	 * @return the weight.
	 */
	public int getWeight () {
		return treeWeight_;
	}
}
