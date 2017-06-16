package com.mulperi.models;

public class Subfeature {
	
	String type;
	String name;
	
	public Subfeature(String type, String name) {
		this.type = type;
		this.name = name;
	
	}
	
	@Override
	public String toString() {
		return type + " " + name;
	}
}
