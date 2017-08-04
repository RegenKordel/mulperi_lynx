package com.mulperi.models.kumbang;

import java.util.List;
import java.util.Stack;

import java.util.ArrayList;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class ParsedModel extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -3020633975702914269L;

	String modelName;
	String comment;
	@OneToMany(cascade = { CascadeType.ALL })
	List<Component> components;
	@OneToMany(cascade = { CascadeType.ALL })
	List<Feature> features;
	@OneToMany(cascade = { CascadeType.ALL })
	List<Attribute> attributes;

	public ParsedModel() {
		components = new ArrayList<Component>();
		features = new ArrayList<Feature>();
		attributes = new ArrayList<Attribute>();
	}

	public ParsedModel(String name, String description) {
		this(); // initialize lists
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

	public List<Component> getComponents() {
		return components;
	}

	public List<Feature> getFeatures() {
		return features;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public void setAttributes(List<Attribute> attributes) {
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

	public void addAttributes(List<Attribute> newAttributes) {
		this.attributes.addAll(newAttributes);
	}
	
	public void addNewAttributes(List<Attribute> newAttributes) {
		// first remove duplicates
		this.attributes.removeAll(newAttributes);

		this.attributes.addAll(newAttributes);
	}

	/**
	 * Creates getParent-relations between model's features. Call after creating the model
	 */
	public void populateFeatureParentRelations() {
		for (Feature feature : this.features) {
			for (Feature parent : this.features) {
				for (SubFeature subfeat : parent.getSubFeatures()) {
					if (subfeat.getTypes().contains(feature.getType())) {
						feature.setParent(parent);
					}
				}
			}
		}
	}

	/**
	 * Find feature of given type from the model
	 * 
	 * @param type
	 * @return
	 */
	public Feature getFeature(String type) {
		for (Feature feature : this.features) {
			if (feature.getType().equals(type)) {
				return feature;
			}
		}
		return null;
	}

	public Stack<Feature> findPath(String type) {
		Stack<Feature> stack = new Stack<Feature>();
		Feature currentFeat = this.getFeature(type);

		do {
			stack.push(currentFeat);
			// path = ".(" + currentFeat.getType() + ", " + currentFeat.getRoleNameInModel() + ")" + path;
			currentFeat = currentFeat.getParent();
		} while (currentFeat != null);

		// path = "root" + path;
		return stack;
	}

	public List<Attribute> getAttributesDefaultsFirst() {
		List<Attribute> arrangedList = new ArrayList<>();
		for (Attribute att : this.attributes) {
			Attribute newAtt = att;

			List<String> arrangedVals = new ArrayList<String>();

			if (att.getDefaultValue() != null)
				arrangedVals.add(att.getDefaultValue());

			for (String value : att.getValues()) {
				if (!value.equals(att.getDefaultValue())) {
					arrangedVals.add(value);
				}
			}

			newAtt.setValues(arrangedVals);

			arrangedList.add(newAtt);
		}

		return arrangedList;
	}

	public void rolesForConstraints() {
		List<Feature> newFeatures = this.features;
		for (Feature feat : newFeatures) {
			for (Constraint cnst : feat.getConstraints()) {
				String presentFound = findRoleForType(cnst.getIfPresent());
				if (presentFound != null) {
					cnst.setIfPresent(presentFound);
				}
				String requiredFound = findRoleForType(cnst.getThenRequired());
				if (requiredFound != null) {
					cnst.setThenRequired(requiredFound);
				}
			}
		}
		this.features = newFeatures;
	}

	private String findRoleForType(String typeName) {
		for (Feature feat : this.features) {
			for (SubFeature sub : feat.getSubFeatures()) {
				for (String type : sub.getTypes()) {
					if (type.equals(typeName)) {
						return sub.getRole();
					}
				}
			}
		}

		return null;
	}
}
