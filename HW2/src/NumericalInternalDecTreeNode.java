import java.util.List;


public class NumericalInternalDecTreeNode extends InternalDecTreeNode {
	
	double midpoint;

	NumericalInternalDecTreeNode(String _label, Attribute _attribute,
			String _parentAttributeValue, List<DecTreeNode> _children, double _midpoint) {
		super(_label, _attribute, _parentAttributeValue, _children);
		this.midpoint = _midpoint;
	}
	
	
	public String classify(Instance example) {
		String childExampleAttributeValue = example.attributes.get(attribute.index);
		if(Attribute.Type.NUMERICAL.equals(attribute.category.getType())) {
			for (DecTreeNode childNode : children) {
				if("A".equals(childNode.parentAttributeValue) == (Integer.parseInt(childExampleAttributeValue) < midpoint)) {
					if(childNode instanceof InternalDecTreeNode) {
						return ((InternalDecTreeNode)childNode).classify(example);
					} else {
						return childNode.label;
					}
				}
			}
		} 
		return label;
	}
}
