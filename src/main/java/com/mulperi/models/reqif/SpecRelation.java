package com.mulperi.models.reqif;

public class SpecRelation {

	private String id; //internal to .reqif
	private String type; //common name, for example "requires"
	private SpecObject source;
	private SpecObject target;
	
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
	public SpecObject getSource() {
		return source;
	}
	public void setSource(SpecObject source) {
		this.source = source;
	}
	public SpecObject getTarget() {
		return target;
	}
	public void setTarget(SpecObject target) {
		this.target = target;
	}
	
}
