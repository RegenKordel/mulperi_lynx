package com.mulperi.models.mulson;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

import com.mulperi.models.mulson.Attribute;
import com.mulperi.models.kumbang.SubFeature;

@Entity
public class Requirement extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -8873722269641439557L;
	
	private String requirementId;
	private String name;
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Relationship> relationships;
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Attribute> attributes;
	@OneToMany(cascade = {CascadeType.ALL})
	private List<SubFeature> subfeatures;
	
	public Requirement() {
		this.attributes = new ArrayList<>();
		this.subfeatures = new ArrayList<>();
		this.relationships = new ArrayList<>();
	}
	
	public String getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(String requirementId) {
		if (requirementId!=null)
			requirementId = requirementId.replaceAll(" ", "_").replaceAll("-", "_");
		this.requirementId = requirementId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (name!=null)
			name = name.replaceAll(" ", "_").replaceAll("-", "_");
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
	public List<SubFeature> getSubfeatures() {
		return subfeatures;
	}
	public void setSubfeatures(List<SubFeature> subfeatures) {
		this.subfeatures = subfeatures;
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
	
	public List<String> getRelationshipsOfType(String type) {
		List<String> requires = new ArrayList<>();
		if(relationships == null) {
			return requires;
		}
		for(Relationship rel : relationships) {
			if(rel.getType().equals(type)) {
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
	
}
