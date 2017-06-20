package com.mulperi.models;

public class Constraint {
	String ifPresent;
	String thenRequired;
	
	public Constraint() {
		
	}
	
	public Constraint(String name1, String name2) {
		ifPresent = name1;
		thenRequired = name2;
	}
	
	@Override
	public String toString() {
		return "present(" + ifPresent + ") => present(" + thenRequired + ")";
	}

	public String getIfPresent() {
		return ifPresent;
	}

	public void setIfPresent(String ifPresent) {
		this.ifPresent = ifPresent;
	}

	public String getThenRequired() {
		return thenRequired;
	}

	public void setThenRequired(String thenRequired) {
		this.thenRequired = thenRequired;
	}
	
	
}
