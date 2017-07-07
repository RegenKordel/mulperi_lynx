package com.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class SubFeature extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -187885907598416501L;
	
	@ElementCollection
	List<String> types;
	private String role;
	private String cardinality;
	
	public SubFeature() {
		this.types = new ArrayList<String>();
	}
	
	public SubFeature(String type, String role, String cardinality) {
		this();
		this.types.add(type);
		this.role = role;
		this.cardinality = cardinality;
	}
	
	public SubFeature(String type, String role) {
		this(type, role, null);
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	
	public void addType(String type) {
		this.types.add(type);
	}
	
	/**
	 * 
	 * @return type in string form, for example "Manual" or "(Manual, Automatic)" 
	 */
	public String getTypeString() {
		if(this.types.size() == 1) {
			return this.types.get(0);
		} else if(types.size() >= 2) {
			return "(" + String.join(", ", this.types) + ")";
		}
		return "";
	}
	
	@Override
	public String toString() {
		String result = getTypeString();
		
		if (role != null && !role.equals("")) {
			result += " " + role;
			if (cardinality != null && !cardinality.equals("")) {
				result += "[" + cardinality + "]";
			}
		}
		
		return result;
	}

}
