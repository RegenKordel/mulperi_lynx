package com.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

public class ParsedModel {

	String modelName;
	String comment;
	ArrayList<Component> components;
	ArrayList<Feature> features;
	ArrayList<Attribute> attributes;
	
	public ParsedModel() {
		components = new ArrayList<Component>();
		features = new ArrayList<Feature>();
		attributes = new ArrayList<Attribute>();
	}
	
	public ParsedModel(String name, String description) {
		this(); //initialize lists
		modelName = name;
		comment = description;	
		components.add(new Component(name));
		features.add(new Feature(name));		
	}
	
	public ParsedModel(String name) {
		this(name, null);
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
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
	
	public void addNewAttributes(List<Attribute> newAttributes) {
		
		//first remove duplicates
		this.attributes.removeAll(newAttributes);
		
		this.attributes.addAll(newAttributes);
	}
}
