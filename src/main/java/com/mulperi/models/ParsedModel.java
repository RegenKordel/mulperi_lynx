package com.mulperi.models;

import java.util.ArrayList;

public class ParsedModel {

	String modelName;
	ArrayList<String> components;
	ArrayList<Feature> features;
	
	public ParsedModel(String name) {
		modelName = name;
		components = new ArrayList<String>();
		features = new ArrayList<Feature>();
	}
	
	public void addComponent(String component) {
		components.add(component);
	}
	
	public void addFeature(Feature feature) {
		features.add(feature);
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public ArrayList<String> getComponents() {
		return components;
	}
	
	public ArrayList<Feature> getFeatures() {
		return features;
	}

}
