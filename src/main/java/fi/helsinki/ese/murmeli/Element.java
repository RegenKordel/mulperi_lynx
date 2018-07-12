package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashMap;
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
	* The name of the element
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
	private HashMap<String, AttributeValue> attributes;
	
	@SerializedName("partDefinitions")
	@Expose
	private List<Parts> parts;
	
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
	
	@SerializedName("type")
	@Expose
	private ElementType type;
	
	private static int hid = 0;
	
	public Element(String nameID, int id) {
		this.nameID = nameID;
		this.attributes = new HashMap();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.parts = new ArrayList();
		this.id = id;
	}
	
	public Element(String nameID) {
		this.nameID = nameID;
		this.attributes = new HashMap();
		this.requiredInterfaces = new ArrayList();
		this.providedInterfaces = new ArrayList();
		this.parts = new ArrayList();
		this.id = hid;
		hid++;
	}
	
	public ElementType getType() {
		return type;
	}

	public void setType(ElementType type) {
		this.type = type;
	}

	public int getID() {
		return this.id;
	}
	
	public void addAttribute(AttributeValue attribute) {
		
		this.attributes.put(attribute.getNameID(), attribute);
	}
	
	public void addPart(Parts part) {

		this.parts.add(part);
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
	public HashMap<String, AttributeValue> getAttributes() {
		return attributes;
	}
	
	/**
	* Attributes related to element
	*
	*/
	public void setAttributes(HashMap<String, AttributeValue> attributes) {
		this.attributes = attributes;
	}
	
	public void setAttributes(List<AttributeValue> attributes) {
		
		for (AttributeValue atr : attributes) {
			this.attributes.put(atr.getNameID(), atr);
		}
	}
	
	public List<Parts> getParts() {
		return parts;
	}
	
	public void setPartDefinitions(List<Parts> parts) {
		this.parts = parts;
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
}
