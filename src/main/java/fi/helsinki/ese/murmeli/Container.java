package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Container
* <p>
* A container of elements
*
*/
public class Container {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("nameID")
	@Expose
	private String nameID;
	/**
	* Containers inside this container
	*
	*/
	@SerializedName("children")
	@Expose
	private List<Container> children = new ArrayList();
	/**
	* Containers directly after this one
	*
	*/
	@SerializedName("next")
	@Expose
	private List<Container> next = new ArrayList();
	/**
	* Elements in this container
	*
	*/
	@SerializedName("elements")
	@Expose
	private List<Element> elements = new ArrayList();
	/**
	* Attributes describing this container
	*
	*/
	@SerializedName("attributes")
	@Expose
	private List<AttributeValueType> attributes = new ArrayList();
	
	private static int hid = 0;
	
	public Container(String nameID, int id) {
		this.nameID = nameID;
		this.id = id;
	}
	
	public Container(String nameID) {
		this.nameID = nameID;
		this.id = hid;
		hid++;
	}
	
	public int getID() {
		return this.id;
	}
	
	public void addChild(Container child) {
		
		this.children.add(child);
	}
	
	public void addNext(Container next) {
		
		this.next.add(next);
	}
	
	public void addElement(Element el) {

		this.elements.add(el);
	}
	
	public void addAttribute(AttributeValueType value) {

		this.attributes.add(value);
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getNameID() {
		return nameID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setNameID(String nameID) {
		this.nameID = nameID;
	}
	
	/**
	* Containers inside this container
	*
	*/
	public List<Container> getChildren() {
		return children;
	}
	
	/**
	* Containers inside this container
	*
	*/
	public void setChildren(List<Container> children) {
		this.children = children;
	}
	
	/**
	* Containers directly after this one
	*
	*/
	public List<Container> getNext() {
		return next;
	}
	
	/**
	* Containers directly after this one
	*
	*/
	public void setNext(List<Container> next) {
		this.next = next;
	}
	
	/**
	* Elements in this container
	*
	*/
	public List<Element> getElements() {
		return elements;
	}
	
	/**
	* Elements in this container
	*
	*/
	public void setElements(List<Element> elements) {
		this.elements = elements;
	}
	
	/**
	* Attributes describing this container
	*
	*/
	public List<AttributeValueType> getAttributes() {
		return attributes;
	}
	
	/**
	* Attributes describing this container
	*
	*/
	public void setAttributes(List<AttributeValueType> attributes) {
		this.attributes = attributes;
	}
	
	public String toString() {
		StringBuilder lel = new StringBuilder();
		
		lel.append(this.nameID + ";");
		for(Container co : this.children) {
			lel.append(co.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (Container co : this.next) {
			lel.append(co.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (Element e : this.elements) {
			lel.append(e.getNameID() + ",");
		}
		
		lel.append(";");
		
		for (AttributeValueType a : this.attributes) {
			lel.append(a.getValues().toString() + ",");
		}
		
		return lel.toString();
	}
}
