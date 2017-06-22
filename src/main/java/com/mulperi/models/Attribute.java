package com.mulperi.models;

import java.util.ArrayList;

public class Attribute {

	String name;
	String role;
	ArrayList<String> values;
	
	public Attribute() {
	}

	public Attribute(String name, String role, ArrayList<String> values) {
		this.name = name;
		this.role = role;
		this.values = values;
	}
	
	public Attribute(String name, ArrayList<String> values) {
		this(name, null, values);
	}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public ArrayList<String> getValues() {
		return values;
	}
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	
}
