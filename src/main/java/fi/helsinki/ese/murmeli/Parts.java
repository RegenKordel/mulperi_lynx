package fi.helsinki.ese.murmeli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* PartDefinition
* <p>
* Possible children types of the element
*
*/
public class Parts {

	@SerializedName("id")
	@Expose
	final private int id;

	/**
	*
	* (Required)
	*
	*/
	@SerializedName("cardinality_min")
	@Expose
	private int cardinality_min;
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("cardinality_max")
	@Expose
	private int cardinality_max;
	/**
	*
	* (Required)
	*
	*/
	@SerializedName("role")
	@Expose
	private String role;
	
	@SerializedName("type")
	@Expose
	private List<Element> parts = null;
	
	private static int hid = 0;
	
	public Parts(int min, int max, String role, List<Element> parts, int id) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.parts = parts;
		this.id = id;
	}
	
	public Parts(int min, int max, String role, List<Element> parts) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.parts = parts;
		this.id = hid;
		hid++;
	}
	
	public Parts(int min, int max, String role) {
		this(min, max, role, new ArrayList<Element>());
	}
	
	public void addType(Element part) {
		this.parts.add(part);
	}
	
	public int getID() {
		return this.id;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public int getCardinality_min() {
		return cardinality_min;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setCardinality_min(int cardinality_min) {
		this.cardinality_min = cardinality_min;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public int getCardinality_max() {
		return cardinality_max;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setCardinality_max(int cardinality_max) {
		this.cardinality_max = cardinality_max;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public String getRole() {
		return role;
	}
	
	/**
	*
	* (Required)
	*
	*/
	public void setRole(String role) {
		this.role = role;
	}
	
	public List<Element> getType() {
		return parts;
	}
	
	public void setType(List<Element> parts) {
		this.parts = parts;
	}
	
}
