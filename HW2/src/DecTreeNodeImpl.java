public class DecTreeNodeImpl extends DecTreeNode {
	Attribute attribute;
	double midpoint;
	
	DecTreeNodeImpl(String _label, Attribute _attribute,
			String _parentAttributeValue, boolean _terminal) {
		super(_label, _attribute.attribute.getName(), _parentAttributeValue, _terminal);
		this.attribute = _attribute;
	}
	
	public void setMidpoint(double _midpoint) {
		this.midpoint = _midpoint;
	}
	
	public String classify(Instance example) {
		String childExampleAttributeValue = example.attributes.get(attribute.index);
		if(Attribute.Type.NUMERICAL.equals(attribute.attribute.getType())) {
			for (DecTreeNode childNode : children) {
				if("A".equals(childNode.parentAttributeValue) == (Integer.parseInt(childExampleAttributeValue) < midpoint)) {
					if(childNode instanceof DecTreeNodeImpl) {
						return ((DecTreeNodeImpl)childNode).classify(example);
					} else {
						return childNode.label;
					}
				}
			}
		} else {
			for (DecTreeNode childNode : children) {
				if(childExampleAttributeValue.equals(childNode.parentAttributeValue)) {
					if(childNode instanceof DecTreeNodeImpl) {
						return ((DecTreeNodeImpl)childNode).classify(example);
					} else {
						return childNode.label;
					}					
				}
			}
		}
		return label;
	}

}
