/**
 * Fill in the implementation details of the class DecisionTree
 * using this file. Any methods or secondary classes
 * that you want are fine but we will only interact
 * with those methods in the DecisionTree framework.
 * 
 * You must add code for the 4 methods specified below.
 * 
 * See DecisionTree for a description of default methods.
 */
public class DecisionTreeImpl extends DecisionTree {
	/**
	 * Answers static questions about decision trees.
	 */
	DecisionTreeImpl() {
		// no code necessary
		// this is void purposefully
	}
	
	/**
	 * Build a decision tree given only a training set.
	 * 
	 * @param train 
	 * 			the training set
	 */
	DecisionTreeImpl(DataSet train) {
		
		// TODO: add code here
		
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning set.
	 * 
	 * @param train 
	 * 			the training set
	 * @param tune 
	 * 			the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {

		// TODO: add code here
		
	}

	@Override
  /**
   * Evaluates the learned decision tree on a test set.
   * @return the label predictions for each test instance 
   * 	according to the order in data set list
   */
	public String[] classify(DataSet test) {
		
		// TODO: add code here
		
		return null;
	}

	@Override
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
	public void print() {
		
		// TODO: add code here
		
	}
	
}

