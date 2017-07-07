package com.mulperi.models.database;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 * This class is for storing requirements into database for easy retrieve
 * 
 * @author iivorait
 *
 */
@Entity
public class StoredRequirement extends AbstractPersistable<Long> {
	
	private static final long serialVersionUID = 3872008742349976511L;
	
	private String modelName;
	private String parentId; //String for better compatibility
	private String requirementId;
	private String requirementName;
	
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}
	public String getRequirementName() {
		return requirementName;
	}
	public void setRequirementName(String requirementName) {
		this.requirementName = requirementName;
	}
	
	
}
