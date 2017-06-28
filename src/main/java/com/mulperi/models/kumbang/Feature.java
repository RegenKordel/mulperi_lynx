package com.mulperi.models.kumbang;

import java.util.ArrayList;

public class Feature {

	String type;
	String name;
	ArrayList<SubFeature> subFeatures;
	ArrayList<Constraint> constraints;
	ArrayList<Attribute> attributes;
	
	
	public Feature() {		
	}
	
	public Feature(String type, String comment) {
		this.type = type;
		this.name = comment;
		subFeatures = new ArrayList<SubFeature>();
		constraints = new ArrayList<Constraint>();
		attributes = new ArrayList<Attribute>();
	}

	public Feature(String type) {
		this(type, null);
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ArrayList<SubFeature> getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(ArrayList<SubFeature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String comment) {
		this.name = comment;
	}

	public void addSubFeature(SubFeature subfeature) {
		subFeatures.add(subfeature);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}
	
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
}
