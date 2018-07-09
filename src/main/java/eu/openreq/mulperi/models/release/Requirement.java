package eu.openreq.mulperi.models.release;

import java.util.LinkedList;
import java.util.List;


public class Requirement {
	
	int effort = 0;
	int assignedRelease = 0;
	LinkedList<String> requiresDependencies;
	LinkedList<String> excludesDependencies;
	final String id;
	
	public Requirement(String id) {
		super();
		this.id = id;
	}

	public int getEffort() {
		return effort;
	}

	public void setEffort(int effort) {
		this.effort = effort;
	}

	public int getAssignedRelease() {
		return assignedRelease;
	}

	public void setAssignedRelease(int assignedRelease) {
		this.assignedRelease = assignedRelease;
	}

	public String getId() {
		return id;
	}
	
	public boolean addRequiresDependency(String id) {
		return requiresDependencies.add(id);
	}
	
	public boolean addExcludesDependency(String id) {
		return excludesDependencies.add(id);
	}
	
	public final List<String> getRequiresDependencies() {
		return requiresDependencies;
	}
	
	public void setRequiresDependencies() {
		this.requiresDependencies = new LinkedList<>();
	}

	public final List<String> getExcludesDependencies() {
		return excludesDependencies;
	}

	public void setExcludesDependencies() {
		this.excludesDependencies = new LinkedList<>();
	}
	
}
