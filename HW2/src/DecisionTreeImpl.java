import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Fill in the implementation details of the class DecisionTree using this file.
 * Any methods or secondary classes that you want are fine but we will only
 * interact with those methods in the DecisionTree framework.
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
	 *            the training set
	 */
	DecisionTreeImpl(DataSet train) {
		if (train == null || train.instances == null
				|| train.instances.isEmpty()) {
			return;
		}
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Instance instance : train.instances) {
			for (int i = 0; i < instance.attributes.size(); i++) {
				if (i > attributes.size() - 1) {
					attributes.add(new Attribute(i));
				}
				attributes.get(i).addValue(instance.attributes.get(i));
			}
		}

		root = trainTree(train.instances, attributes, train.instances, "ROOT");
	}

	/**
	 * Build a decision tree given a training set then prune it using a tuning
	 * set.
	 * 
	 * @param train
	 *            the training set
	 * @param tune
	 *            the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {
		this(train);
		prune(tune);
	}

	private DecTreeNode trainTree(List<Instance> examples,
			List<Attribute> attributes, List<Instance> parentExamples,
			String parentAttributeValue) {
		if (examples.isEmpty() || attributes.isEmpty() || sameLabel(examples)) {
			return new LeafDecTreeNode(plurality(parentExamples), parentAttributeValue);
		} else {
			Attribute importantAttribute = importance(attributes, examples);
			//System.out.println("Winning Attribute: "+importantAttribute.category.getName());
			List<Attribute> childAttributes = new ArrayList<Attribute>(
					attributes);
			childAttributes.remove(importantAttribute);
			
			if (Attribute.Type.NUMERICAL.equals(importantAttribute.category.getType())) {
				double midpoint = midpoint(examples, importantAttribute.index);				
				
				List<Instance> positiveChildExamples = new ArrayList<Instance>(), negativeChildExamples = new ArrayList<Instance>();
				for (Instance example : examples) {
					if(Integer.parseInt(example.attributes.get(importantAttribute.index)) > midpoint) {
						positiveChildExamples.add(example);
					} else {
						negativeChildExamples.add(example);
					}					
				}
				
				List<DecTreeNode> children = new ArrayList<DecTreeNode>();
				children.add(trainTree(positiveChildExamples,
						childAttributes, examples, "B"));
				children.add(trainTree(positiveChildExamples,
						childAttributes, examples, "A"));
				
				return new NumericalInternalDecTreeNode(plurality(examples), importantAttribute, parentAttributeValue, children, midpoint);
			} else {
				Map<String, List<Instance>> childExamples = new LinkedHashMap<String, List<Instance>>();
				for (Instance example : examples) {
					String importantAttributeValue = example.attributes
							.get(importantAttribute.index);
					List<Instance> childExample = childExamples
							.get(importantAttributeValue);
					if (childExample == null) {
						childExample = new ArrayList<Instance>();
						childExamples.put(importantAttributeValue, childExample);
					}
					childExample.add(example);
				}
				
				List<DecTreeNode> children = new ArrayList<DecTreeNode>();
				for (String attribute : importantAttribute.values) {
					List<Instance> childExamplesForAttribute = childExamples.get(attribute);
					if(childExamplesForAttribute == null) {
						childExamplesForAttribute = new ArrayList<Instance>();
					}
					children.add(trainTree(childExamplesForAttribute,
							childAttributes, examples, attribute));
				}
				return new InternalDecTreeNode(plurality(examples), importantAttribute, parentAttributeValue, children);
			}
		}
	}

	private String plurality(List<Instance> examples) {
		Map<String, Integer> scores = new LinkedHashMap<String, Integer>();
		for (Instance instance : examples) {
			Integer score = scores.get(instance.label);
			if (score == null) {
				score = 0;
			}
			scores.put(instance.label, score + 1);
		}
		int winningScore = Integer.MIN_VALUE;
		String winner = null;
		for (String label : scores.keySet()) {
			if (!label.equals(winner)) {
				int score = scores.get(label);
				if (winningScore == score) {
					if (label.compareToIgnoreCase(winner) < 0) {
						winner = label;
					}
				} else if (winningScore < score) {
					winner = label;
					winningScore = score;
				}
			}
		}
		return winner;
	}

	private Attribute importance(List<Attribute> attributes,
			List<Instance> examples) {
		double winningEntropy = Double.NEGATIVE_INFINITY;
		Attribute winningAttribute = null;

		// Calculate H(Credit)
		double givenCredit = 0;
		for (Instance example : examples) {
			if ("1".equals(example.label)) {
				givenCredit++;
			}
		}
		double creditEntropy = booleanEntropy(givenCredit / examples.size());
//		System.out.println("H(Credit) = " + creditEntropy);

		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);

			// Use these LinkedHashMaps to add up probabilities for
			// attributes both given credit and without
			Map<String, Double> attributeScore = new LinkedHashMap<String, Double>(
					attribute.values.size());
			Map<String, Double> attributeScoreGivenCredit = new LinkedHashMap<String, Double>(
					attribute.values.size());
			int examplesWithCredit = 0;

			if (Attribute.Type.NUMERICAL.equals(attribute.category.getType())) {
				double midpoint = midpoint(examples, attribute.index);
				
				List<Instance> examplesGivenCredits = new ArrayList<Instance>();
				for (int j = 0; j < examples.size(); j++) {
					Instance example = examples.get(j);
					if ("1".equals(example.label)) {
						examplesGivenCredits.add(example);
					}
				}				
				double midpointGivenCredits = midpoint(examplesGivenCredits, attribute.index);
				
				for (int j = 0; j < examples.size(); j++) {
					Instance example = examples.get(j);
					int value = Integer.parseInt(example.attributes.get(i));
					String larger = String.valueOf(value > midpoint);
					Double score = attributeScore.get(larger);
					if (score == null) {
						score = 0.0;
					}
					attributeScore.put(larger, score + 1);

					if ("1".equals(example.label)) {
						String largerGivenCredit = String.valueOf(value > midpointGivenCredits);
						Double scoreGivenCredit = attributeScoreGivenCredit
								.get(largerGivenCredit);
						if (scoreGivenCredit == null) {
							scoreGivenCredit = 0.0;
						}
						attributeScoreGivenCredit.put(largerGivenCredit,
								scoreGivenCredit + 1);
						examplesWithCredit++;
					}
				}
			} else {
				for (int j = 0; j < examples.size(); j++) {
					Instance example = examples.get(j);
					String value = example.attributes.get(i);
					Double score = attributeScore.get(value);
					if (score == null) {
						score = 0.0;
					}
					attributeScore.put(value, score + 1);

					if ("1".equals(example.label)) {
						Double scoreGivenCredit = attributeScoreGivenCredit
								.get(value);
						if (scoreGivenCredit == null) {
							scoreGivenCredit = 0.0;
						}
						attributeScoreGivenCredit.put(value,
								scoreGivenCredit + 1);
						examplesWithCredit++;
					}
				}
			}

			// Calculate H(Credit|Attribute)
			double attributeEntropy = 0;
			for (String value : attributeScore.keySet()) {
				Double score = attributeScore.get(value);
				Double scoreGivenCredit = attributeScoreGivenCredit.get(value);
				if (score != null && scoreGivenCredit != null && score != 0
						&& scoreGivenCredit != 0) {
					attributeEntropy += (score / examples.size() * booleanEntropy(scoreGivenCredit
							/ examplesWithCredit));
				}
			}

			// Calculate I(Credit;Attribute) = H(Credit) - H(Credit|Attribute)
			double totalEntropy = creditEntropy - attributeEntropy;
//			System.out.println("I(Credit;" + attribute.category.getName()	+ ") = " + totalEntropy);
			if (totalEntropy > winningEntropy) {
				winningEntropy = totalEntropy;
				winningAttribute = attribute;
			}

		}
		return winningAttribute;
	}

	private static final double LOG_OF_2 = Math.log(2);

	private double booleanEntropy(double q) {
		if (q <= 0 || q >= 1) {
			return 0;
		}
		return -(((q * Math.log(q)) / LOG_OF_2) + (((1 - q) * Math.log(1 - q)) / LOG_OF_2));
	}

	private boolean sameLabel(List<Instance> examples) {
		if (examples == null || examples.isEmpty()) {
			return true;
		}
		boolean positive = "1".equals(examples.get(0).label);
		for (Instance instance : examples) {
			if (("2".equals(instance.label) && positive)
					|| ("1".equals(instance.label) && !positive)) {
				return false;
			}
		}
		return true;
	}

	private double midpoint(List<Instance> examples, int attributeIndex) {
		if (examples == null || examples.isEmpty()) {
			return 0.0;
		}
		double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
		for (Instance instance : examples) {
			int attribute = Integer.parseInt(instance.attributes
					.get(attributeIndex));
			if (attribute > max) {
				max = attribute;
			}
			if (attribute < min) {
				min = attribute;
			}
		}
		return 0.5 * (max + min);
	}

	@Override
	/**
	 * Evaluates the learned decision tree on a test set.
	 * @return the label predictions for each test instance 
	 * 	according to the order in data set list
	 */
	public String[] classify(DataSet test) {
		String[] classification = new String[test.instances.size()];
		for (int i = 0; i < test.instances.size(); i++) {
			Instance example = test.instances.get(i);
			if(root instanceof InternalDecTreeNode) {
				classification[i] = ((InternalDecTreeNode)root).classify(example);
			} else {
				classification[i] = root.label; 
			}
		}
		return classification;
	}
	
	private void prune(DataSet tune) {
		double originalAccuracy = calcTestAccuracy(tune, classify(tune));
		// Can't really go about pruning if the root isn't internal
		if(root instanceof InternalDecTreeNode) {			
			InternalDecTreeNode nodeToPrune = null, parentNodeToPrune = null;			
			double maxAccuracy = originalAccuracy;
			
			InternalDecTreeNode savedInternalNode = (InternalDecTreeNode) root;
			LeafDecTreeNode prunedLeafNode = new LeafDecTreeNode(savedInternalNode.label, savedInternalNode.parentAttributeValue);
			
			// Test pruning the root node
			root = prunedLeafNode;
			// Calculate the test accuracy with this node pruned
			double accuracy = calcTestAccuracy(tune, classify(tune));
			if(accuracy >= maxAccuracy) {
				maxAccuracy = accuracy;
				nodeToPrune = (InternalDecTreeNode)savedInternalNode;				
			}			
			
			// Return tree back to original state
			root = savedInternalNode;
			
			// Do a BFS search to determine which node to prune
			Queue<InternalDecTreeNode> queue = new LinkedList<InternalDecTreeNode>();
			queue.add((InternalDecTreeNode)root);
			while(!queue.isEmpty()) {
				InternalDecTreeNode internalNode = queue.remove();
				for (int i = 0; i < internalNode.children.size(); i++) {
					DecTreeNode child = internalNode.children.get(i);
					if(child instanceof InternalDecTreeNode) {
						// Calculate the test accuracy with this node pruned
						savedInternalNode = (InternalDecTreeNode) child;
						prunedLeafNode = new LeafDecTreeNode(savedInternalNode.label, savedInternalNode.parentAttributeValue);
						
						internalNode.removeChild(child);						
						internalNode.addChild(prunedLeafNode);
						
						accuracy = calcTestAccuracy(tune, classify(tune));
						if(accuracy >= maxAccuracy) {
							maxAccuracy = accuracy;
							nodeToPrune = savedInternalNode;
							parentNodeToPrune = internalNode;
						}
						
						internalNode.removeChild(prunedLeafNode);
						internalNode.returnChild(i, child);
						
						queue.add(savedInternalNode);
					}
				}
			}
			if(nodeToPrune != null && maxAccuracy > originalAccuracy) {
				if(parentNodeToPrune != null) {
					parentNodeToPrune.removeChild(nodeToPrune);
					parentNodeToPrune.addChild(new LeafDecTreeNode(nodeToPrune.label, nodeToPrune.parentAttributeValue));

					// Keep pruning until we can't get better accuracy than current
					prune(tune);
				} else {
					root = new LeafDecTreeNode(nodeToPrune.label, nodeToPrune.parentAttributeValue);
				}
			} 
		}
	}

	private double calcTestAccuracy(DataSet test, String[] results) {
		if(results == null) {
			 System.out.println("Error in calculating accuracy: " +
			 		"You must implement the classify method");
			 return 0.0;
		}
		
		List<Instance> testInsList = test.instances;
		if(testInsList.size() == 0) {
			System.out.println("Error: Size of test set is 0");
			return 0.0;
		}
		if(testInsList.size() > results.length) {
			System.out.println("Error: The number of predictions is inconsistant " +
					"with the number of instances in test set, please check it");
			return 0.0;
		}
		
		int correct = 0, total = testInsList.size();
		for(int i = 0; i < testInsList.size(); i ++)
			if(testInsList.get(i).label.equals(results[i]))
				correct ++;
		
		return correct * 1.0 / total;
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
		root.print(0);
	}

}
