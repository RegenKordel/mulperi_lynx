package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* RelationshipType
* <p>
* Relation between two elements
*
*/
public class RelationshipType {

	public enum NameType {
		
        REFINES,
        REQUIRES,
        INCOMPATIBLE,
        SIMILAR,
        DUPLICATES,
        REPLACES
	}
	
	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("nameType")
	@Expose
	private NameType nameType;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("from")
	@Expose
	private Element from;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("to")
	@Expose
	private Element to;
	
	private static int hid = 0;
	
	public RelationshipType(NameType nameType, Element from, Element to, int id) {
		this.nameType = nameType;
		this.from = from;
		this.to = to;
		this.id = id;
	}
	
	public RelationshipType(NameType nameType, Element from, Element to) {
		this.nameType = nameType;
		this.from = from;
		this.to = to;
		this.id = hid;
		hid++;
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public NameType getNameType() {
		return nameType;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setNameType(NameType nameType) {
		this.nameType = nameType;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public Element getFrom() {
		return from;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setFrom(Element from) {
		this.from = from;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public Element getTo() {
		return to;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setTo(Element to) {
		this.to = to;
	}
	
}
