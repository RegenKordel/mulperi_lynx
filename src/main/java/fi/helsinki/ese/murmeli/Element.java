package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* ElementType
* <p>
* Defines an element
*
*/
public class Element {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	* The name of the elementType
	* (Required)
	*
	*/
	@SerializedName("nameID")
	@Expose
	private String nameID;
	
	/**
	* Attributes related to element
	*
	*/
	@SerializedName("attributes")
	@Expose
	private List<AttributeValue> attributes;
	
	@SerializedName("partDefinitions")
	@Expose
	private List<PartDefinition> partDefinitions;
	
	/**
	* Required interfaces
	*
	*/
	@SerializedName("requiredInterfaces")
	@Expose
	private List<Interface> requiredInterfaces;
	
	/**
	* Provided interfaces
	*
	*/
	@SerializedName("providedInterfaces")
	@Expose
	private List<Interface> providedInterfaces;
	
	/**
	* Required interfaces
	*
	*/
	@SerializedName("constraints")
	@Expose
	private List<Constraint> constraints;
	
	private static int hid = 0;
	
	public Element(String nameID, int id) {
		this.nameID = nameID;
		this.attributes = new ArrayList();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.partDefinitions = new ArrayList();
		this.constraints = new ArrayList();
		this.id = id;
	}
	
	public Element(String nameID) {
		this.nameID = nameID;
		this.attributes = new ArrayList();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.partDefinitions = new ArrayList();
		this.constraints = new ArrayList();
		this.id = hid;
		hid++;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addAttribute(AttributeValue attribute) {
		
		this.attributes.add(attribute);
	}
	
	public void addPart(PartDefinition part) {

		this.partDefinitions.add(part);
	}
	
	public void addRequiredInterface(Interface required) {

		this.requiredInterfaces.add(required);
	}
	
	public void addProvidedInterface(Interface provided) {
		
		this.providedInterfaces.add(provided);
	}
	
	/**
	* The name of the elementType
	* (Required)
	*
	*/
	public String getNameID() {
		return nameID;
	}
	
	/**
	* The name of the elementType
	* (Required)
	*
	*/
	public void setNameID(String nameID) {
		this.nameID = nameID;
	}
	
	/**
	* Attributes related to element
	*
	*/
	public List<AttributeValue> getAttributes() {
		return attributes;
	}
	
	/**
	* Attributes related to element
	*
	*/
	public void setAttributes(List<AttributeValue> attributes) {
		this.attributes = attributes;
	}
	
	public List<PartDefinition> getPartDefinitions() {
		return partDefinitions;
	}
	
	public void setPartDefinitions(List<PartDefinition> partDefinitions) {
		this.partDefinitions = partDefinitions;
	}
	
	/**
	* Required interfaces
	*
	*/
	public List<Interface> getRequiredInterfaces() {
		return requiredInterfaces;
	}
	
	/**
	* Required interfaces
	*
	*/
	public void setRequiredInterfaces(List<Interface> requiredInterfaces) {
		this.requiredInterfaces = requiredInterfaces;
	}
	
	/**
	* Provided interfaces
	*
	*/
	public List<Interface> getProvidedInterfaces() {
		return providedInterfaces;
	}
	
	/**
	* Provided interfaces
	*
	*/
	public void setProvidedInterfaces(List<Interface> providedInterfaces) {
		this.providedInterfaces = providedInterfaces;
	}
	
	public List<Constraint> getConstraints() {
		return this.constraints;
	}
	
	public void setConstraints(List<Constraint> cons) {
		this.constraints = cons;
	}
	
	public void addConstraint(Constraint constraint) {
		this.constraints.add(constraint);
	}
}
