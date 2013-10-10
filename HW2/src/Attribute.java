import java.util.LinkedHashSet;
import java.util.Set;


public class Attribute implements Cloneable {
	public boolean used = false;
	public Set<String> values = new LinkedHashSet<String>(); ;

	public void addValue(String value) {
		values.add(value);
	}
	
	public Attribute clone() {
		Attribute attribute = new Attribute();
		attribute.used = this.used;
		attribute.values = new LinkedHashSet<String>(this.values);
		return attribute;
	}
}
