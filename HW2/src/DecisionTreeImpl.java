import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.util.*;


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
	DecTreeNode learnedTree = null;
	DecTreeNode tunedTree = null;
	private Map<String, List<String> > attributeValues = null;
	private List<String> attributes = null;
	private List<String> labels = null;

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
	 * @param train the training set
	 */
	DecisionTreeImpl(DataSet train) {
		learnedTree = makeTree(train);
		// TODO: add code here
		//DO I want to create a new data set after a question is asked?
		//Seems complicated removing the information for the question though.

		//What I want to do now is make a function to see what label would win out 
		//at a node.
	}

	/**
	 * Build a decision tree given a training set then prune it
	 * using a tuning set.
	 * 
	 * @param train the training set
	 * @param tune the tuning set
	 */
	DecisionTreeImpl(DataSet train, DataSet tune) {
		if(train.instances.size() ==1){
			learnedTree = makeTree(train);
			tunedTree = deepCopy(learnedTree);		
		}else if(train.sameMetaValues(tune)){
			learnedTree = makeTree(train);
			tunedTree = pruneTree(learnedTree, tune);
		} else
			System.err.print("The Tuning set does not match the Training set!!!");

		// TODO: add code here

	}

	@Override
	public String classify(Instance instance) {
		String classification = "";

		if(tunedTree != null){
			classification = classify(tunedTree,instance);
		}else if(learnedTree != null){
			classification = classify(learnedTree,instance);
		}else
			System.err.println("There is not tree to classify on");

		return classification;
	}

	@Override
	public void print() {
		if(tunedTree != null)
			tunedTree.print(0);
		else if(learnedTree != null)
			learnedTree.print(0);

		// TODO: add code here

	}

	public void rootMutualInformation(DataSet train) {
		double num1 = labelEntropy(train);
		int size = train.attributes.size();
		double[] attributeEnt = new double[size];
		for(int i = 0; i < size ; i++){
			attributeEnt[i] = featureEntropy(train,i); 
		}

		System.out.println("Entropy for the label is " + setPrec(num1));
		for(int k = 0; k < size; k ++){
			System.out.print("Entropy for the label|" + train.attributes.get(k));
			System.out.println(" = " + setPrec(num1 - attributeEnt[k]));
		}
		// TODO: add code here

	}

	//------------------------------------------------------------------------------------
	public DecTreeNode pruneTree(DecTreeNode tree, DataSet pruneSet ){
		DecTreeNode tuning = deepCopy(tree);
		DecTreeNode navigator = tuning;
		int numInternalNodes = countInternalNodes(tree);
		Stack<DecTreeNode> visited = new Stack<DecTreeNode>();
		
		//Keeps track of the internal node that was already visited.
		List<DecTreeNode> usedNode = new ArrayList<DecTreeNode>();
		int[] errors  = new int[numInternalNodes];
		int[] treeSizes = new int[numInternalNodes];

		visited.add(navigator);
		while(!visited.isEmpty()){
			if(!navigator.terminal  && !usedNode.contains(navigator)){
				usedNode.add(navigator);
				for(int j = 0; j < navigator.children.size(); j++){
					visited.add(navigator.children.get(j));
				}
			}
			navigator = visited.pop();
		}

		//System.out.println(usedNode.toString());

		String classified = "";
		int classifiedInt = 0;
		int leastErrors = pruneSet.instances.size();
		int leastErrorsIndex = 0;

		for(int i = 0; i <= numInternalNodes ; i++){
			if(i != numInternalNodes){
				usedNode.get(i).terminal = true;
				if(i != 0){
					usedNode.get(i-1).terminal = false;
				}
				for(int j = 0; j < pruneSet.instances.size(); j++){
					classified = classify(tuning,pruneSet.instances.get(j));
					for(int k = 0; k < labels.size(); k ++){
						if(classified == labels.get(k)){
							classifiedInt = k;
							
							
						}
					}
				//	System.out.println("Classified Label: " +classifiedInt 
				//			+ "; True Label: "+pruneSet.instances.get(j).label);
					if(classifiedInt != pruneSet.instances.get(j).label){
						
						errors[i]++;
					}
				}
				//tuning.print(0);
				treeSizes[i] = countNodes(tuning);
				
			//	System.out.println("Errors: " +errors[i] + ", out of "
			//			+ pruneSet.instances.size() + " instances.");
			//	System.out.println();
			} else
				usedNode.get(i-1).terminal = false;
		}

		for(int i = 0; i < numInternalNodes; i++){
			
			if(errors[i] < leastErrors){
				leastErrors = errors[i];
				leastErrorsIndex = i;
			} else if(errors[i] == leastErrors){
				if(treeSizes[leastErrorsIndex] < treeSizes[i]){
					leastErrorsIndex = i;
				}
					
			}
		}
		
		usedNode.get(leastErrorsIndex).children.clear();
		usedNode.get(leastErrorsIndex).terminal = true;

		return tuning;
	}

	public int countNodes(DecTreeNode node){
		int count = 0;
		if(node.terminal)
			count = 1;
		else{
			count += 1;
			for(int i = 0; i < node.children.size(); i++){
				count += countNodes(node.children.get(i));
			}
		}
		return count;
	}
	public int countInternalNodes(DecTreeNode node){
		int count = 0;
		if(node.terminal)
			count = 0;

		else{
			count = 1;

			for(int i = 0; i < node.children.size(); i++){
				DecTreeNode tempNode = node.children.get(i);
				count +=  countInternalNodes(tempNode);
			}
		}
		return count;
	}

	public String classify(DecTreeNode tree, Instance currentInstance){
		String temp = "";
		String label = "";
		String question = "";
		int intAttrib = 0;
		int intAttribVal = 0;
		DecTreeNode curLoc  = tree;
		int numIterations = currentInstance.attributes.size();
		while (curLoc != null){
			if(!curLoc.terminal){
				question = curLoc.attribute;
				//Integer representation of the attribute
				for(int g = 0; g < attributes.size(); g ++){
					if(question == attributes.get(g)){
						intAttrib = g;
						break;
					}
				}
				numIterations = attributeValues.get(question).size();
				for(int i = 0; i < numIterations; i++){
					temp = curLoc.children.get(i).parentAttributeValue;


					//Integer representation of the attribute value
					for(int k = 0; k < numIterations;k++){
						if(temp == attributeValues.get(question).get(k)){
							intAttribVal = k;
							break;
						}
					}
					if(intAttribVal == currentInstance.attributes.get(intAttrib)){
						curLoc = curLoc.children.get(i);
						i = numIterations;
					}

				}

			} else {

				label = curLoc.label;
				curLoc = null;
			}
		}
		return label;
	}

	public DecTreeNode makeTree(DataSet set){
		attributeValues = set.attributeValues;
		attributes = set.attributes;
		labels = set.labels;

		String str = "";
		Vector<String> attribs = new Vector<String>();
		for(int i = 0; i < set.attributes.size();i++){
			str = set.attributes.get(i);
			attribs.add(str);
		}
		return makeTree(set,attribs,null,null);
	}

	public DecTreeNode makeTree(DataSet set, Vector<String> attributes, DataSet parentSet, String parentAttributeValue){
		DecTreeNode node = null;
		String lable = "";
		String attr = "";
		//printDataSet(set);
		int attributeNum = 0;
		int numDataItems = set.instances.size();
		if(numDataItems == 1&&parentSet == null){
			lable = dominantLabel(set);
			node = new DecTreeNode(	lable,"Aleaf","ROOT",true);	

		}else if(set.instances.isEmpty()){
			lable = dominantLabel(parentSet);

			node = new DecTreeNode(lable, "Aleaf",parentAttributeValue, true);

		} else if (isPure(set)){

			lable = dominantLabel(set);

			node = new DecTreeNode(lable, "Aleaf",parentAttributeValue,true);
		}else if (attributes.size() == 0){
			lable = dominantLabel(set);

			node = new DecTreeNode(lable, "Aleaf",parentAttributeValue, true);
		} else {
			String curAttributeValue = "";
			int numValues = 0;
			DataSet curSet = set;
			String parentAttribute = "";

			attr = bestQuestion(set,attributes);
			attributeNum = attrStringToInt(set,attr);
			attributes.removeElement(attr);
			numValues = set.attributeValues.get(attr).size();
			if(parentSet == null)
				parentAttribute = "ROOT";
			else
				parentAttribute = parentAttributeValue;

			node = new DecTreeNode(dominantLabel(curSet),attr,parentAttribute,false);
			for(int i = 0; i < numValues; i++){
				DataSet tempSet = deepCopy(curSet);

				curAttributeValue = attrValSelector(curSet,attributeNum,i);
				for(int j = numDataItems -1; j >= 0; j --){
					if(curSet.instances.get(j).attributes.get(attributeNum) != i){
						tempSet.instances.remove(j);
					}
				}
				node.addChild(makeTree(tempSet,attributes,curSet,curAttributeValue));

			}

		}

		return node;
	}

	//Returns the corresponding string attribute
	static String attrSelector(DataSet set, int k){

		return set.attributes.get(k);
	}
	static String attrValSelector(DataSet set, int k, int j){
		String str = set.attributes.get(k);
		return set.attributeValues.get(str).get(j);
	}



	private String dominantLabel(DataSet set){
		//String str = "";
		int items = set.instances.size();
		int max = 0;
		int cur = 0;
		int labelSize = set.labels.size();
		int labelVal = 0 ;
		//int attrs =   0; 
		int[] labelCount = new int[labelSize];


		for(int i = 0; i < items; i++){
			labelCount[set.instances.get(i).label]++;
		}
		for(int m = 0; m < labelSize ; m++){
			cur = labelCount[m];
			if(cur > max){
				max = cur;
				labelVal = m;
			}
		}



		return set.labels.get(labelVal);
	}


	public String bestQuestion(DataSet set, Vector<String> questsAvailable){
		String attribute = "";
		String str = "";

		int bestQuest = 0;
		double max = 0;
		double infoGain = 0;
		//if(questsAvailable == null){
		int numAttr = set.attributes.size();
		for(int i = 0; i < numAttr; i++){
			str = attrSelector(set,i);
			if(questsAvailable.contains(str)){
				infoGain= labelEntropy(set) - featureEntropy(set,i);
				if(infoGain >= max ){
					max = infoGain;
					bestQuest = i;
				}
			}

		}
		attribute = attrSelector(set,bestQuest);

		//System.out.println("InfoGain: "+max + " /w "+attribute);

		return attribute;
	}

	/*
	public DecTreeNode deepCopy(DecTreeNode copyFrom){
		DecTreeNode temp = null;
		DecTreeNode theCopy = null;
		DecTreeNode cur = copyFrom;
		Queue<DecTreeNode> theQueue = new LinkedList<DecTreeNode>();

		theQueue.add(cur);
		while(!theQueue.isEmpty()){
			cur = theQueue.poll();
			if(cur.parentAttributeValue == "ROOT"){
				temp = copyNodes(cur);
				theCopy = temp;

			}else if(attributeValues.get(temp.attribute).contains(cur.parentAttributeValue)){
				temp.addChild(copyNodes(cur));
			}

			for(int i = 0; i < cur.children.size(); i ++){
				theQueue.add(cur.children.get(i));
			}


		}


		return temp;
	}*/

	public DecTreeNode deepCopy(DecTreeNode copyFrom){
		DecTreeNode tree = null;
		if (copyFrom.terminal == true){
			tree = copyNodes(copyFrom);
		}else {
			tree = copyNodes(copyFrom);
			for(int i = 0; i < copyFrom.children.size(); i++){
				tree.addChild(copyFrom.children.get(i));
			}
		}


		return tree;

	}



	private DecTreeNode copyNodes(DecTreeNode copyFrom){
		DecTreeNode node = new DecTreeNode(null,null,null,false);
		node.label = copyFrom.label;
		node.attribute = copyFrom.attribute;
		node.parentAttributeValue = copyFrom.parentAttributeValue;
		node.terminal = copyFrom.terminal;

		return node;
	}
	private DataSet deepCopy(DataSet copyFrom){
		DataSet newSet = new DataSet();
		String tempAttribute = "";
		String tempValue = "";
		List<String> temp2 = null;
		newSet.labels = new ArrayList<String>();
		newSet.attributes = new ArrayList<String>();
		newSet.attributeValues =  new HashMap<String, List<String>>();
		newSet.instances =  new ArrayList<Instance>();

		for(int i = 0; i < copyFrom.labels.size(); i++){
			newSet.labels.add(copyFrom.labels.get(i));
		}
		for(int i= 0; i < copyFrom.attributes.size(); i ++){
			temp2 = new ArrayList<String>();
			tempAttribute =copyFrom.attributes.get(i);
			newSet.attributes.add(tempAttribute);
			for(int j = 0; j < copyFrom.attributeValues.get(tempAttribute).size();j++){
				tempValue = copyFrom.attributeValues.get(tempAttribute).get(j);
				temp2.add(tempValue);
			}
			newSet.attributeValues.put(tempAttribute,temp2);
			//temp2.clear();
		}
		int tempNum= 0;

		for(int i =0; i < copyFrom.instances.size(); i++){
			Instance newInst = new Instance();
			newInst.label = copyFrom.instances.get(i).label;
			newInst.attributes = new ArrayList<Integer>();
			for(int j = 0; j < copyFrom.instances.get(i).attributes.size();j++){
				tempNum = copyFrom.instances.get(i).attributes.get(j);
				newInst.attributes.add(tempNum);
			}

			newSet.instances.add(newInst);
		}



		return newSet;
	}



	private boolean isPure(DataSet set){
		boolean flag = true;
		boolean first = false;
		int curLabel = 0;
		int items = set.instances.size();
		for(int i = 0; i < items; i++){
			if(!first){
				curLabel = set.instances.get(i).label;
				first = true;
			} else{
				if(set.instances.get(i).label != curLabel)
					flag = false;
			}

		}
		return flag;
	}

	private int attrStringToInt(DataSet set,String str){
		int num = 0;
		for(int i = 0 ; i < set.attributes.size(); i++){
			if(set.attributes.get(i)==str){
				num = i;
				break;
			}
		}

		return num;
	}



	//Returns double value with precision of 3
	private double setPrec(double d){
		int num = 0;
		d *= 1000;
		num = (int)d;
		return ((double)num/1000);
	}

	//Finds the entropy of the labels in the given data set
	public double labelEntropy(DataSet set){
		double sum = 0;
		double curNum = 0;

		int items = set.instances.size();
		int[] iterations = new int[set.labels.size()];
		for(int i = 0; i < items; i++){
			iterations[set.instances.get(i).label]++;		
		}

		for(int i = 0; i < iterations.length; i++){
			curNum = (double)iterations[i]/items;
			if(curNum != 0)
				sum += -1*(curNum) * (Math.log(curNum)/Math.log(2));
		}

		return (sum);
	}

	public double featureEntropy(DataSet set, int curAttr){
		//Pre-condition: set must exist
		double sum = 0;
		double curNum = 0;
		double probNum = 0;
		if(curAttr > set.attributes.size())
			System.err.println("Trying to access list of attribute values that doesn't exist.");
		else{
			int items = set.instances.size();
			int num = set.attributeValues.get(set.attributes.get(curAttr)).size();
			int iterations = set.labels.size();
			int[] prob = new int[num];
			int[][] prob2 = new int[iterations][num];
			double[] sumSet = new double[num];

			for(int i = 0 ; i < items; i++){
				prob[set.instances.get(i).attributes.get(curAttr)]++;	
			}
			//Have now check for labels
			for(int i = 0; i < iterations; i++){
				for(int j = 0; j < items; j++){
					if(set.instances.get(j).label == i){
						prob2[i][set.instances.get(j).attributes.get(curAttr)]++;
					}
				}
			}
			//For every attribute value
			for(int g = 0; g < num; g++){
				for(int k = 0; k  < iterations; k++){
					if(prob[g] !=0 && prob2[k][g] != 0){
						if(prob[g] != 0)
						curNum  = (double)prob2[k][g]/prob[g];
						else
							curNum = 0;
						if(curNum != 0)
						sum += -1*(curNum) * (Math.log(curNum)/Math.log(2));
					}
				}
				probNum = (double)prob[g]/items;
				sumSet[g] = sum*probNum;
				sum = 0;
			}
			for (int i = 0; i < sumSet.length; i++){
				sum += sumSet[i];
			}

		}

		return (sum);
	}

	public void printDataSet(DataSet set){
		if(set != null){
			//System.out.println("Labels: " + set.labels.toString());

			System.out.println(//"Attributes: " + set.attributes.toString() +
					"\nAttribute Values: " + set.attributeValues.toString());
			System.out.println();
			printInstData(set);
		}
	}



	public void printInstData(DataSet theSet){
		List<Instance> aList = theSet.instances;
		List<String>[] attrs = new List[theSet.attributes.size()];
		int size1 = aList.size();
		int size2 = theSet.attributes.size();
		//int tempNum = 0;
		//String tempStr = "";
		for(int i = 0; i < theSet.attributes.size(); i++){
			attrs[i] = theSet.attributeValues.get(theSet.attributes.get(i));
		}

		for(int i = 0; i < size1; i++){
			System.out.println("Inst" + i + " Class: " + theSet.labels.get(aList.get(i).label));
			System.out.print("Inst" + i + " Attributes: ");

			for(int j = 0; j < size2; j++){
				//tempNum = aList.get(i).attributes.get(j);
				//tempStr = attrs[j].get(tempNum);
				System.out.print(attrs[j].get(aList.get(i).attributes.get(j)) + " ");

			}
			System.out.println();

		}
		System.out.println("\n");

	}


}

