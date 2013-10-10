import java.util.LinkedHashSet;
import java.util.Set;


public class Attribute {
	public boolean used = false;
	public Set<String> values = new LinkedHashSet<String>(); ;

	public void addValue(String value) {
		values.add(value);
	}
}
