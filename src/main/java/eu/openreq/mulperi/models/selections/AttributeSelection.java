package eu.openreq.mulperi.models.selections;

public class AttributeSelection {

	private String name;
	private String value;
	
	public AttributeSelection() {
	}
	
	public AttributeSelection(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name!=null)
			name = name.replaceAll(" ", "_").replaceAll("-", "_");
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		if (value!=null)
			value = value.replaceAll(" ", "_").replaceAll("-", "_");
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttributeSelection [name=" + name + ", value=" + value + "]";
	}
	
	
}
