package com.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Feature extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -5144125345854141995L;

	private String type;
	private String name;
	@OneToMany(cascade = { CascadeType.ALL })
	private List<SubFeature> subFeatures;
	@OneToMany(cascade = { CascadeType.ALL })
	private List<Constraint> constraints;
	@OneToMany(cascade = { CascadeType.ALL })
	private List<Attribute> attributes;
	private Feature parent;

	public Feature() {
		subFeatures = new ArrayList<SubFeature>();
		constraints = new ArrayList<Constraint>();
		attributes = new ArrayList<Attribute>();
	}

	public Feature(String type, String comment) {
		this();
		this.type = type;
		this.name = comment;
	}

	public Feature(String type) {
		this(type, null);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (type != null)
			type = type.replaceAll(" ", "_").replaceAll("-", "_");
		this.type = type;
	}

	public List<SubFeature> getSubFeatures() {
		return subFeatures;
	}

	public void setSubFeatures(List<SubFeature> subFeatures) {
		this.subFeatures = subFeatures;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public String getName() {
		return name;
	}

	public void setName(String comment) {
		this.name = comment;
	}

	public Feature getParent() {
		return parent;
	}

	public void setParent(Feature parent) {
		this.parent = parent;
	}

	public void addSubFeature(SubFeature subfeature) {
		subFeatures.add(subfeature);
	}

	public void addConstraint(Constraint constraint) {
		constraints.add(constraint);
	}

	public void addAttribute(Attribute attribute) {
		attributes.add(attribute);
	}

	/**
	 * Populate parent relations of model first 
	 * @return name of the role the feature participates in
	 */
	public String getRoleNameInModel() {
		if (this.parent == null) {
			return "root";
		}

		if (this.parent.getSubFeatures().isEmpty()) {
			return "error";
		}

		for (SubFeature subfeature : this.parent.getSubFeatures()) {
			if (subfeature.getTypes().contains(this.type)) {
				return subfeature.getRole();
			}
		}

		return "";
	}

	@Override
	public String toString() {
		return "Feature [type=" + type + ", name=" + name + "]";
	}

}
