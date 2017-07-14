package com.mulperi.models.selections;

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
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AttributeSelection [name=" + name + ", value=" + value + "]";
	}
	
	
}
