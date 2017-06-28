package com.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

public class Feature {

	String type;
	String name;
	List<SubFeature> subFeatures;
	List<Constraint> constraints;
	List<Attribute> attributes;
	
	
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

	public List<SubFeature> getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(List<SubFeature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<Attribute> attributes) {
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
