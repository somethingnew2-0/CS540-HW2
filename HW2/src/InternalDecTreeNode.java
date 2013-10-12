
public class InternalDecTreeNode extends DecTreeNode {
	Attribute attribute;
	
	InternalDecTreeNode(String _label, Attribute _attribute, String _parentAttributeValue) {
		super(_label, _attribute.category.getName(), _parentAttributeValue, false);
		this.attribute = _attribute;
	}
	
	public void returnChild(int index, DecTreeNode child) {
		children.add(index, child);
	}
	
	public void removeChild(DecTreeNode child) {
		children.remove(child);
	}

	public String classify(Instance example) {
		String childExampleAttributeValue = example.attributes.get(attribute.index);
		for (DecTreeNode childNode : children) {
			if(childExampleAttributeValue.equals(childNode.parentAttributeValue)) {
				if(childNode instanceof InternalDecTreeNode) {
					return ((InternalDecTreeNode)childNode).classify(example);
				} else {
					return childNode.label;
				}					
			}
		}
		return label;
	}
	
}
