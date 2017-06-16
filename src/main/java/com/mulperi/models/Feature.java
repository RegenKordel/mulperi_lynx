package com.mulperi.models;

import java.util.ArrayList;

public class Feature {

	//not sure if the subfeatures/constraints should just be strings and if
	//these models are necessary at all (just parse straight to kbm string instead?)
	
	String featureName;
	ArrayList<Subfeature> subFeatures;
	ArrayList<Constraint> constraints;
	
	public Feature(String name) {
		featureName = name;
		subFeatures = new ArrayList<Subfeature>();
		constraints = new ArrayList<Constraint>();
	}
	
	public void addSubFeature(Subfeature subfeature) {
		subFeatures.add(subfeature);
	}
	
	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}
	
	public String getFeatureName() {
		return featureName;
	}
	
	public ArrayList<Subfeature> getSubFeatures() {
		return subFeatures;
	}
	
	public ArrayList<Constraint> getConstraints() {
		return constraints;
	}
	
//	@Override
//	public String toString() {
//		String subf = new String();
//		for (String feat : subFeatures) {
//			subf = subf + " " + feat;
//		}
//		String con = new String();
//		for (Constraint cnstr : constraints) {
//			con = con + " " + cnstr.toString();
//		}
//		return featureName + " Features: " + subf + " Constraints: " + con;
//	}
	
	
}
