package com.mulperi.models.selections;

import java.util.ArrayList;

public class FeatureSelection {

	private String name;
	private String type;
	private ArrayList<AttributeSelection> attributes;
	private ArrayList<FeatureSelection> features;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String value) {
		this.type = value;
	}

	public ArrayList<AttributeSelection> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<AttributeSelection> attributes) {
		this.attributes = attributes;
	}
	
	public ArrayList<FeatureSelection> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<FeatureSelection> features) {
		this.features = features;
	}
}
