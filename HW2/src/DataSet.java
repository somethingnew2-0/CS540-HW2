import java.util.ArrayList;
import java.util.List;

/**
 * This class organizes the information of a data set into simple structures.
 *
 * Do not modify.
 * 
 */
public class DataSet {

	public List<Instance> instances = null; // ordered list of instances
	private final String DELIMITER = " ";  // Used to split input strings

	/**
	 * Add instance to collection.
	 */
	public void addInstance(String line) {
		if (instances == null) {
			instances = new ArrayList<Instance>();
		}
		Instance instance = new Instance();
		
		String[] splitline = line.split(DELIMITER);
		for(int i = 0; i < splitline.length - 1; i ++)
			instance.addAttribute(splitline[i]);
		instance.setLabel(splitline[splitline.length - 1]);
		
		instances.add(instance);
	}
}
