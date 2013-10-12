import java.util.LinkedHashSet;
import java.util.Set;


public class Attribute {	
	public enum Type {
		QUALITATIVE,
		NUMERICAL;
	}
	
	public enum Category {
		CHECKING("Checking Account", Type.QUALITATIVE),
		HISTORY("Credit History", Type.QUALITATIVE),
		PURPOSE("Purpose", Type.QUALITATIVE),
		SAVINGS("Savings Account/Bonds", Type.QUALITATIVE),
		DURATION("Duration in Month", Type.NUMERICAL),
		AMOUNT("Credit Amount", Type.NUMERICAL),
		FOREIGN("Foreign Worker", Type.QUALITATIVE);
		
		private String name;
		private Type type;
		private Category(String name, Type type) {
			this.name = name;
			this.type = type;
		}
		public String toString() {
			return name;
		}
		public String getName() {
			return name;
		}
		public Type getType() {
			return type;
		}
		public static Attribute.Category getAttribute(int index) {
			return values()[index];
		}
	}
	
	public int index;
	public Set<String> values = new LinkedHashSet<String>();
	public Category category;
	
	public Attribute(int index) {
		this.index = index;
		this.category = Category.getAttribute(index);
	}

	public void addValue(String value) {
		values.add(value);
	}
	
	
}
