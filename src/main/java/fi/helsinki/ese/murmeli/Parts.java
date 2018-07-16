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
	
	@SerializedName("parts")
	@Expose
	private List<Integer> parts = new ArrayList<Integer>();
	
	private static int hid = 0;
	
	public Parts(int min, int max, String role, int id) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.id = id;
	}
	
	public Parts(int min, int max, String role) {
		this.cardinality_max = max;
		this.cardinality_min = min;
		this.role = role;
		this.id = hid;
		hid++;
	}
	
	public void addPart(Element part) {
		
		this.parts.add(part.getID());
	}
	
	public void addPartAsId(int id) {
		
		this.parts.add(id);
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
	
	public List<Integer> getType() {
		return parts;
	}
	
	public void setParts(List<Element> parts) {
		
		for (Element element : parts) {
			this.parts.add(element.getID());
		}
	}
	
	public void setPartsAsIds(List<Integer> parts) {
		this.parts = parts;
	}
}