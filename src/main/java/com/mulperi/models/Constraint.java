package com.mulperi.models;

public class Constraint {
	String ifPresent;
	String thenRequired;
	
	public Constraint(String name1, String name2) {
		ifPresent = name1;
		thenRequired = name2;
	}
	
	@Override
	public String toString() {
		return "present(" + ifPresent + ") => present(" + thenRequired + ")";
	}
}
