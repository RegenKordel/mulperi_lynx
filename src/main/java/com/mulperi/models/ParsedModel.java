package com.mulperi.models;

import java.util.ArrayList;

public class ParsedModel {

	String modelName;
	ArrayList<Component> components;
	ArrayList<Feature> features;
	ArrayList<Attribute> attributes;
	
	public ParsedModel() {
		
	}
	
	public ParsedModel(String name) {
		modelName = name;
		components = new ArrayList<Component>();
		features = new ArrayList<Feature>();
		attributes = new ArrayList<Attribute>();
		components.add(new Component(name));
		features.add(new Feature(name));
		
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
	
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public void setComponents(ArrayList<Component> components) {
		this.components = components;
	}

	public void setFeatures(ArrayList<Feature> features) {
		this.features = features;
	}
	
	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	public void addFeature(Feature feature) {
		features.add(feature);
	}
	
	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}
}
