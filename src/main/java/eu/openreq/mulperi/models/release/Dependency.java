package eu.openreq.mulperi.models.release;

public class Dependency {
	String dependency_type;
	String fromId;
	String toId;
	
	public String getDependencyType() {
		return dependency_type;
	}
	
	public String getFromId() {
		return fromId;
	}
	
	public String getToId() {
		return toId;	
	}
	
	public void setDependencyType(String dt) {
		this.dependency_type = dt;
	}
	
	public void setFromId(String fromId) {
		this.fromId = fromId;
	}
	
	public void setToId(String toId) {
		this.toId = toId;
	}
}
