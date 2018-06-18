package eu.openreq.mulperi.models.release;

public class Dependency {
	String dependency_type;
	String from;
	String to;
	
	public String getDependencyType() {
		return dependency_type;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getTo() {
		return to;	
	}
	
	public void setDependencyType(String dt) {
		this.dependency_type = dt;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
}
