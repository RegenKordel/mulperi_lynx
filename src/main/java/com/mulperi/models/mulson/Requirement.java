package com.mulperi.models.mulson;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.mulperi.models.database.Hierarchy;
import com.mulperi.models.kumbang.Attribute;

@Entity
public class Requirement extends AbstractPersistable<Long> implements Hierarchy {

	private static final long serialVersionUID = -8873722269641439557L;
	
	private String requirementId;
	private String name;
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Relationship> relationships;
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Attribute> attributes;
	
	public Requirement() {
		this.attributes = new ArrayList<>();
	}
	
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Relationship> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Relationship> relationships) {
		this.relationships = relationships;
	}		
	public List<Attribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	
	public boolean hasRelationshipType(String type) {
		if(relationships == null) {
			return false;
		}
		
		for(Relationship rel : relationships) {
			if(rel.getType().equals(type)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getParent() {
		if(relationships == null) {
			return null;
		}
		
		for(Relationship rel : relationships) {
			if(rel.getType().equals("isa")
					|| rel.getType().equals("isoptionalpartof")
					|| rel.getType().equals("ispartof")) {
				
				return rel.getTargetId();
			}
		}
		
		return null;
	}
	
	public List<String> getRequires() {
		List<String> requires = new ArrayList<>();
		if(relationships == null) {
			return requires;
		}
		for(Relationship rel : relationships) {
			if(rel.getType().equals("requires")) {
				requires.add(rel.getTargetId());
			}
		}
		return requires;
	}
	
	/**
	 * Cardinality in Kumbang form for Parsed Model
	 * @return String
	 */
	public String getCardinality() {
		if(hasRelationshipType("isoptionalpartof")) {
			return "0-1";
		}
		return "0-1"; //Defaults to optional 
	}

	@Override
	public String getParentId() {
		return this.getParent();
	}

	@Override
	public String getRequirementName() {
		return this.name;
	}
	
}
