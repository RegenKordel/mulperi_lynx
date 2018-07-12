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
        REPLACES,
        DAMAGES,
        CONTRIBUTES
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
	@SerializedName("fromID")
	@Expose
	private Element fromID;
	
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("toID")
	@Expose
	private Element toID;
	
	@SerializedName("power")
	@Expose
	private int power = 0;
	
	private static int hid = 0;
	
	public RelationshipType(NameType nameType, Element from, Element to, int id) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
		this.id = id;
	}
	
	public RelationshipType(NameType nameType, Element from, Element to) {
		this.nameType = nameType;
		this.fromID = from;
		this.toID = to;
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
		return fromID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setFrom(Element from) {
		this.fromID = from;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public Element getTo() {
		return toID;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setTo(Element to) {
		this.toID = to;
	}
	
}
