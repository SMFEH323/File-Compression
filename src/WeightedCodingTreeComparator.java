import java.util.Comparator;

/**
 * Orders the WeightedCodingTrees within the priority queue.
 * 
 * @author Sayf Elhawary
 */
public class WeightedCodingTreeComparator
    implements Comparator<WeightedCodingTree> {

	/**
	 * Compares the desired WeightedCodingTrees and sorts them into the priority
	 * queue.
	 * 
	 * @param tree1
	 *          The first WeightedCodingTree.
	 * @param tree2
	 *          The second WeightedCodingTree.
	 * @return -1 if tree1 should come before tree2 in the priority queue, 1 if
	 *         tree1 should come after tree2 in the priority queue, and 0
	 *         otherwise.
	 */
	public int compare ( WeightedCodingTree tree1, WeightedCodingTree tree2 ) {
		if ( tree1.getWeight() > tree2.getWeight() ) {
			return 1;
		} else if ( tree1.getWeight() < tree2.getWeight() ) {
			return -1;
		}

		return 0;
	}

}
