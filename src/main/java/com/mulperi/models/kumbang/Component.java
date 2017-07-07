package com.mulperi.models.kumbang;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Component extends AbstractPersistable<Long> {
	
	private static final long serialVersionUID = 2926936039323768837L;
	
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
