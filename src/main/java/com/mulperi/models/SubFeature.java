package com.mulperi.models;

public class SubFeature {

	private String type;
	private String role;
	private String cardinality;
	
	public SubFeature() {
	}
	
	public SubFeature(String type, String role, String cardinality) {
		this.type = type;
		this.role = role;
		this.cardinality = cardinality;
	}
	
	public SubFeature(String type, String role) {
		this(type, role, null);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	@Override
	public String toString() {
		String result = type;
		
		if (role != null && !role.equals("")) {
			result += " " + role;
			if (cardinality != null && !cardinality.equals("")) {
				result += "[" + cardinality + "]";
			}
		}
		
		return result;
	}

}
