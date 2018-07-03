package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ElementModel {

	@SerializedName("valueTypes")
	@Expose
	private HashMap<String, AttributeValueType> valueTypes;

	@SerializedName("elementTypes")
	@Expose
	private HashMap<String, ElementType> elementTypes;
	
	@SerializedName("elements")
	@Expose
	private HashMap<String, Element> elements;
	
	@SerializedName("rootContainer")
	@Expose
	private Container rootContainer;
	
	@SerializedName("relations")
	@Expose
	private List<RelationshipType> relations;
	
	@SerializedName("constraints")
	@Expose
	private HashMap<Integer, Constraint> constraints;
	
	public ElementModel() {
		
		this.valueTypes = new HashMap();
		this.elementTypes = new HashMap();
		this.elements = new HashMap();
		this.rootContainer = null;
		this.relations = new ArrayList();
		this.constraints = new HashMap();
	}
	
	public HashMap<Integer, Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(HashMap<Integer, Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public void addConstraint(Constraint cons) {
		this.constraints.put(cons.getID(), cons);
	}

	public HashMap<String, AttributeValueType> getValueTypes() {
		return valueTypes;
	}

	public void setValueTypes(HashMap<String, AttributeValueType> valueTypes) {
		this.valueTypes = valueTypes;
	}

	public void addValueType(AttributeValueType type) {
		this.valueTypes.put(type.getName(), type);
	}
	
	public HashMap<String, ElementType> getElementTypes() {
		return elementTypes;
	}

	public void setElementTypes(HashMap<String, ElementType> elementTypes) {
		this.elementTypes = elementTypes;
	}
	
	public void addElementType(ElementType type) {
		this.elementTypes.put(type.getNameID(), type);
	}

	public HashMap<String, Element> getElements() {
		return elements;
	}

	public void setElements(HashMap<String, Element> elements) {
		this.elements = elements;
	}
	
	public void addElement(Element element) {
		this.elements.put(element.getNameID(), element);
	}

	public Container getRootContainer() {
		return rootContainer;
	}

	public void setRootContainer(Container rootContainer) {
		this.rootContainer = rootContainer;
	}

	public List<RelationshipType> getRelations() {
		return relations;
	}

	public void setRelations(List<RelationshipType> relations) {
		this.relations = relations;
	}
	
	public void addRelation(RelationshipType relation) {
		this.relations.add(relation);
	}
}
