package com.mulperi.models;

import java.util.ArrayList;

public class Feature {

	String type;
	String role;
	String cardinality;
	ArrayList<Feature> subFeatures;
	ArrayList<Constraint> constraints;

	public Feature() {
		
	}
	
	public Feature(String type, String role, String cardinality) {
		this.type = type;
		this.role = role;
		this.cardinality = cardinality;
		subFeatures = new ArrayList<Feature>();
		constraints = new ArrayList<Constraint>();
	}
	
	public Feature(String type, String role) {
		this(type, role, null);

	}
	
	public Feature(String type) {
		this(type, null, null);
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

	public ArrayList<Feature> getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(ArrayList<Feature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(ArrayList<Constraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	public String toString() {
		String result = type;
		
		if (role != null && !role.equals("")) {
			result += " " + role;
			if (cardinality != null && !cardinality.equals("")) {
				result += "[" + cardinality + "]";
			}
		}
		
		return result;
	}

	public void addSubFeature(Feature subfeature) {
		subFeatures.add(subfeature);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}
	
}
