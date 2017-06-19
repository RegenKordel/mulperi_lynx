package com.mulperi.models;

import java.util.ArrayList;

public class ParsedModel {

	String modelName;
	ArrayList<Component> components;
	ArrayList<Feature> features;
	
	public ParsedModel(String name) {
		modelName = name;
		components = new ArrayList<Component>();
		features = new ArrayList<Feature>();
		components.add(new Component(name));
		features.add(new Feature(name));
	}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	public void addFeature(Feature feature) {
		features.add(feature);
	}
	
	public String getModelName() {
		return modelName;
	}
	
	public ArrayList<Component> getComponents() {
		return components;
	}
	
	public ArrayList<Feature> getFeatures() {
		return features;
	}

}
