package eu.openreq.mulperi.models.reqif;

import java.util.ArrayList;
import java.util.List;

public class SpecObject {

	private String id; //internal to .reqif
	private List<Attribute> attributes;
	private List<SpecRelation> sourcesOf;
	private List<SpecRelation> targetsOf;
	private SpecObject parent;
	
	public SpecObject() {
		sourcesOf = new ArrayList<>();
		targetsOf = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public List<Attribute> getAttributes() {
		return attributes;
	}


	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}


	public List<SpecRelation> getSourcesOf() {
		return sourcesOf;
	}


	public void setSourcesOf(List<SpecRelation> sourcesOf) {
		this.sourcesOf = sourcesOf;
	}


	public List<SpecRelation> getTargetsOf() {
		return targetsOf;
	}


	public void setTargetsOf(List<SpecRelation> targetsOf) {
		this.targetsOf = targetsOf;
	}


	public SpecObject getParent() {
		return parent;
	}


	public void setParent(SpecObject parent) {
		this.parent = parent;
	}


	public void addSourceOf(SpecRelation relation) {
		sourcesOf.add(relation);
	}
	
	public void addTargetOf(SpecRelation relation) {
		targetsOf.add(relation);
	}
	
	public String getForeignId() {
		return findAttributeValue("ReqIF.ForeignID");
	}
	
	public Boolean isMandatory() {
		String value = findAttributeValue("isMandatory");
		if(value == null || value.equals("false")) {
			return false;
		}
		if(value.equals("true")) {
			return true;
		}
		return false;
	}
	
	private String findAttributeValue(String type) {
		if(attributes == null || attributes.isEmpty()) {
			return null;
		}
		for(Attribute attribute : attributes) {
			if(attribute.getType().equals(type)) {
				return attribute.getValue();
			}
		}
		return null;
	}
	
	public List<SpecObject> getRequires() {
		ArrayList<SpecObject> requires = new ArrayList<>();
		if(sourcesOf == null) {
			return requires;
		}
		for(SpecRelation rel : sourcesOf) {
			if(rel.getType().equals("Requires")) {
				requires.add(rel.getTarget());
			}
		}
		return requires;
	}
	
	/**
	 * Cardinality in Kumbang form for Parsed Model
	 * @return String
	 */
	public String getCardinality() {
		if(isMandatory()) {
			return "1";
		}
		return "0-1"; //Defaults to optional 
	}
}
