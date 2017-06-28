package com.mulperi.models.kumbang;

public class Component {
	
	String componentType;
	
	public Component() {
		
	}
	
	public Component(String type) {
		componentType = type;
	}
	
	public String getComponentType() {
		return componentType;
	}

	public void setComponentType(String componentType) {
		this.componentType = componentType;
	}
	
}
