package com.mulperi.models.selections;

import java.util.ArrayList;
import java.util.List;

public class FeatureSelection {

	private String name;
	private String type;
	private List<AttributeSelection> attributes;
	private List<FeatureSelection> features;

	public FeatureSelection() {
		this.attributes = new ArrayList<>();
		this.features = new ArrayList<>();
	}
	
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

	public List<AttributeSelection> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeSelection> attributes) {
		this.attributes = attributes;
	}
	
	public List<FeatureSelection> getFeatures() {
		return features;
	}

	public void setFeatures(List<FeatureSelection> features) {
		this.features = features;
	}
	
	/**
	 * Forms a string of the feature's attributes and all subfeatures for nested comparison
	 * @return
	 */
	public String getFullContentString() {
		String content = "(" + name + "," + type;
		if(attributes != null) {
			for(AttributeSelection as : attributes) {
				content += "-" + as.getName() + "=" + as.getValue();
			}
		}
		content += ")/";
		if(features != null) {
			for(FeatureSelection fs : features) {
				content += fs.getFullContentString();
			}
		}
		return content;
	}
}
