package eu.openreq.mulperi.models.release;

import java.util.List;

public class Project {
	String id = null;
	String name = null;
	List<String> specificRequirements = null;
	
//	String version = "";
	
	
	public Project(String name) {
		super();
		this.name = name;
	}
	
	public Project() {
		super();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getSpecificRequirments() {
		return specificRequirements;
	}
	
	public void setSpecificRequirments(List<String> specificRequirements) {
		this.specificRequirements = specificRequirements;
	}
//	public String getVersion() {
//		return version;
//	}
//	public void setVersion(String version) {
//		this.version = version;
//	}

}
