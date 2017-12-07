package eu.openreq.mulperi.models.release;

public class Project {
	String name = null;
	String id = null;
	String version = "";
	
	
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
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}
