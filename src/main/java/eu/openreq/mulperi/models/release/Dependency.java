package eu.openreq.mulperi.models.release;

public class Dependency {
	String dependency_type;
	String fromId;
	String toId;
	
	public String getDependencyType() {
		return dependency_type;
	}
	
	public String getFrom() {
		return fromId;
	}
	
	public String getTo() {
		return toId;	
	}
	
	public void setDependencyType(String dt) {
		this.dependency_type = dt;
	}
	
	public void setFrom(String from) {
		this.fromId = from;
	}
	
	public void setTo(String to) {
		this.toId = to;
	}
}
