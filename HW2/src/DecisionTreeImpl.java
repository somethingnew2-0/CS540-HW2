import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	}

	private DecTreeNode trainTree(List<Instance> examples,
			List<Attribute> attributes, List<Instance> parentExamples,
			String parentAttributeValue) {
		if (examples.isEmpty() || attributes.isEmpty() || sameLabel(examples)) {
			return plurality(parentExamples, parentAttributeValue);
		} else {
			Attribute importantAttribute = importance(attributes, examples);
			System.out.println("Winning Attribute: "+importantAttribute.attribute.getName());
			List<Attribute> childAttributes = new ArrayList<Attribute>(
					attributes);
			childAttributes.remove(importantAttribute);
			DecTreeNodeImpl node = new DecTreeNodeImpl("",
					importantAttribute,
					parentAttributeValue, false);
			Map<String, List<Instance>> childExamples = new LinkedHashMap<String, List<Instance>>();
			if (Attribute.Type.NUMERICAL.equals(importantAttribute.attribute.getType())) {
				double midpoint = midpoint(examples, importantAttribute.index);
				node.setMidpoint(midpoint);
				for (Instance example : examples) {
					String importantAttributeValue = (Integer.parseInt(example.attributes
							.get(importantAttribute.index)) < midpoint?"A":"B");
					List<Instance> childExample = childExamples
							.get(importantAttributeValue);
					if (childExample == null) {
						childExample = new ArrayList<Instance>();
						childExamples.put(importantAttributeValue, childExample);
					}
					childExample.add(example);
				}
			} else {
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
			}
			for (String attribute : childExamples.keySet()) {
				node.children.add(trainTree(childExamples.get(attribute),
						childAttributes, examples, attribute));
			}
			return node;
		}
	}

	private DecTreeNode plurality(List<Instance> examples,
			String parentAttributeValue) {
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
		return new DecTreeNode(winner, "", parentAttributeValue, true);
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
		System.out.println("H(Credit) = " + creditEntropy);

		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);

			// Use these LinkedHashMaps to add up probabilities for
			// attributes both given credit and without
			Map<String, Double> attributeScore = new LinkedHashMap<String, Double>(
					attribute.values.size());
			Map<String, Double> attributeScoreGivenCredit = new LinkedHashMap<String, Double>(
					attribute.values.size());
			int examplesWithCredit = 0;

			if (Attribute.Type.NUMERICAL.equals(attribute.attribute.getType())) {
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
			System.out.println("I(Credit;" + attribute.attribute.getName()
					+ ") = " + totalEntropy);
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
			classification[i] = ((DecTreeNodeImpl)root).classify(example);
		}
		return classification;
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
