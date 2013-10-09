import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private DecTreeNode root = null;
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
		if(train == null || train.instances == null || train.instances.isEmpty()) {
			return;
		}
		Set<String> attributes = new LinkedHashSet<String>();
		for (Instance instance : train.instances) {
			attributes.addAll(instance.attributes);
		}
		
		root = trainTree(train.instances, attributes, train.instances);
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
		this(train);
		// TODO: add code here
		
	}
	
	private DecTreeNode trainTree(List<Instance> examples, List<String> attributes, List<Instance> parentExamples) {
		if(parentExamples == null || parentExamples.isEmpty()) {
			return null;
		} else if(examples == null || examples.isEmpty()) {
			return plurality(parentExamples);
		} else if(attributes == null || attributes.isEmpty()) {
			return plurality(examples);
		} else {
			String attribute = importance(attributes, parentExamples);
			//DecTreeNode node = new DecTreeNode(_label, _attribute, "ROOT", false);
			for (Instance instance : parentExamples) {
				
			}
		}		
	}

	private DecTreeNode plurality(List<Instance> examples) {
		Map<String, Integer> scores = new LinkedHashMap<String, Integer>();
		for (Instance instance : examples) {
			Integer score = scores.get(instance.label);
			if(score == null) {
				score = 0;
			}
			scores.put(instance.label, score + 1);
		}
		int winningScore = Integer.MIN_VALUE;
		String winner = null;
		for (Map.Entry<String, Integer> entry : scores.entrySet()) {
			if(!entry.getKey().equals(winner)) {
				if(winningScore == entry.getValue()) {
					if (winner == null) {
						winner = entry.getKey();
					} else {
						if(entry.getKey().compareToIgnoreCase(winner) > 0) {
							winner = entry.getKey();
						}
					}
				} else if (winningScore < entry.getValue()) {
					winningScore = entry.getValue();
				}
			}
		}
		return new DecTreeNode(winner, winner, winner, true);
	}
	
	private String importance(List<String> attributes, List<Instance> examples) {
		
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

