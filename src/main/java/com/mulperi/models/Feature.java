package com.mulperi.models;

import java.util.ArrayList;

public class Feature {

	String featureType;
	String featureRole;
	ArrayList<Feature> subFeatures;
	ArrayList<Constraint> constraints;

	public Feature(String type, String role) {
		featureType = type;
		featureRole = role;
		subFeatures = new ArrayList<Feature>();
		constraints = new ArrayList<Constraint>();
	}

	public Feature(String type) {
		featureType = type;
		featureRole = "";
		subFeatures = new ArrayList<Feature>();
		constraints = new ArrayList<Constraint>();
	}

	public void addSubFeature(Feature subfeature) {
		subFeatures.add(subfeature);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}

	public String getFeatureType() {
		return featureType;
	}

	public String getFeatureRole() {
		return featureRole;
	}

	public ArrayList<Feature> getSubFeatures() {
		return subFeatures;
	}

	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}

	@Override
	public String toString() {
		if (!featureRole.equals("")) {
			return featureType + " " + featureRole;
		}
		return featureType;
	}

}
