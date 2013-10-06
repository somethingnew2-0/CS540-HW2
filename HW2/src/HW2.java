import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

/**
 * Do not modify.
 * 
 */
public class HW2 {
	
	/**
	 * Runs the tests for HW2.
	 */
	public static void main(String[] args) {
		 if (args.length != 4) {
			 System.out.println("usage: java HW2 <modeFlag> <trainFilename> " +
			 		"<tuneFilename> <testFilename>");
			 System.exit(-1);
		 }
		 
		 /*
		  * mode 1 : create a decision tree using the training set, then print 
		  *      	   the tree and the prediction accuracy on the test set
		  *      2 : create a decision tree using the training set, prune using 
		  *        	   the tuning set, then print the tree and prediction accuracy 
		  *        	   on the test set
		  */
		 int mode = Integer.parseInt(args[0]);
		 if (mode < 1 || mode > 2) {
			 System.out.println("Error: modeFlag must be an integer 1 or 2");
			 System.exit(-1);
		 }
		 
		 // Turn text into array
		 // Only create the sets that we intend to use
		 DataSet trainSet = null, tuneSet = null, testSet = null;

		 trainSet = createDataSet(args[1], mode);
		 testSet = createDataSet(args[3], mode);
		 if(mode > 1)
			 tuneSet = createDataSet(args[2], mode);
		 
		 // Create decision tree
		 DecisionTree tree = null;
		 if (mode == 1) {
			 tree = new DecisionTreeImpl(trainSet);
		 } else {
			 if(tuneSet == null) {
				 System.out.println("Empty tuning set");
				 System.exit(-1);
			 }
			 tree = new DecisionTreeImpl(trainSet, tuneSet);
		 }

		 // print the tree and calculate accuracy
		 tree.print();
		 calcTestAccuracy(testSet, tree.classify(testSet));
	}

	/**
	 * Converts from text file format to DataSet format.
	 * 
	 */
	private static DataSet createDataSet(String file, int modeFlag) {
		DataSet set = new DataSet();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			while (in.ready()) { 
				String line = in.readLine(); 
					set.addInstance(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		} 
		
		return set;
	}
	
	/**
	 * Calculate predication accuracy on the test set.
	 * Note that you should implement classify() method firstly.
	 * DO NOT MODIFY
	 */
	private static void calcTestAccuracy(DataSet test, String[] results) {
		
		if(results == null) {
			 System.out.println("Error in calculating accuracy: " +
			 		"You must implement the classify method");
			 System.exit(-1);
		}
		
		List<Instance> testInsList = test.instances;
		if(testInsList.size() == 0) {
			System.out.println("Error: Size of test set is 0");
			System.exit(-1);
		}
		if(testInsList.size() > results.length) {
			System.out.println("Error: The number of predictions is inconsistant " +
					"with the number of instances in test set, please check it");
			System.exit(-1);
		}
		
		int correct = 0, total = testInsList.size();
		for(int i = 0; i < testInsList.size(); i ++)
			if(testInsList.get(i).label.equals(results[i]))
				correct ++;
		
		System.out.println("Prediction accuracy on the test set is: " 
				+ String.format("%.5f", correct * 1.0 / total));
		return;
	}
}
