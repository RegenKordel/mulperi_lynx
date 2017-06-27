package com.mulperi.models.reqif;

public class Attribute {
	
	private String id; //internal to .reqif
	private String type; //common name, for example "foreignId" or "isMandatory"
	private String value;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
