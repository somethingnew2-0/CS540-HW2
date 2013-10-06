/**
 * This class provides a framework for accessing a decision tree.
 * Do not modify or place code here, instead create an
 * implementation in a file DecisionTreeImpl. 
 * 
 */
abstract class DecisionTree {
	/**
	 * Evaluates the learned decision tree on a test set
	 * @return the classification accuracy of the test set
	 */
	abstract public String[] classify(DataSet testSet);
	
	/**
	 * Prints the tree in specified format. It is recommended, but not
	 * necessary, that you use the print method of DecTreeNode.
	 * 
	 * Example:
	 * Root {Existing checking account?}
	 *   A11 (2)
	 *   A12 {Foreign worker?}
	 *     A71 {Credit Amount?}
	 *       A (1)
	 *       B (2)
	 *     A72 (1)
	 *   A13 (1)
	 *   A14 (1)
	 *         
	 */
	abstract public void print();
}
