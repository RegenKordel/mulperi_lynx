package com.mulperi.models.mulson;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Relationship extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 8815373175639095921L;
	
	private String targetId;
	private String type;
	
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String id) {
		this.targetId = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
