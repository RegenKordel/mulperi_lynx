package eu.openreq.mulperi.models.json;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import eu.openreq.mulperi.models.json.Dependency;

/**
* Dependency
* The dependency between requirements
* 
*/
public class Dependency {

	/**
	* The type of dependency between requirements
	* (Required)
	* 
	*/
	@SerializedName("dependency_type")
	@Expose
	private String dependency_type;
	/**
	* NLP engine's estimation of the reliability of a proposed dependency
	* 
	*/
	@SerializedName("dependency_score")
	@Expose
	private double dependency_score;
	/**
	* Status of dependency that has been detected by NLP and whether or not the dependency has been approved
	* 
	*/
	@SerializedName("status")
	@Expose
	private Dependency_status status;
	/**
	* The requirement having a dependency
	* (Required)
	* 
	*/
	@SerializedName("fromid")
	@Expose
	private String fromid;
	/**
	* The requirement dependent on another
	* (Required)
	* 
	*/
	@SerializedName("toid")
	@Expose
	private String toid;
	
	@SerializedName("description")
	@Expose
	private List<String> description;
	
	/**
	* Creation timestamp
	* (Required)
	* 
	*/
	@SerializedName("created_at")
	@Expose
	private long created_at;

	public String getDependency_type() {
		return dependency_type;
	}
	
	public void setDependency_type(String dependency_type) {
		this.dependency_type = dependency_type;
	}
	
	public double getDependency_score() {
		return dependency_score;
	}
	
	public void setDependency_score(double dependency_score) {
		this.dependency_score = dependency_score;
	}
	
	public Dependency_status getStatus() {
		return status;
	}
	
	public void setStatus(Dependency_status status) {
		this.status = status;
	}
	
	public String getFromid() {
		return fromid;
	}
	
	public void setFromid(String fromId) {
		this.fromid = fromId;
	}
	
	public String getToid() {
		return toid;
	}
	
	public void setToid(String toId) {
		this.toid = toId;
	}
	
	public List<String> getDescription() {
		return description;
	}

	public void setDescription(List<String> description) {
		this.description = description;
	}

	public long getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Dependency)) {
        	return false;
        }
        
		Dependency dep = (Dependency) obj;
		
		return (this.getFromid()==dep.getFromid() && this.getToid()==dep.getToid() 
				&& this.getDependency_type()==dep.getDependency_type());			
        
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.fromid, this.toid, this.dependency_type);
		
	}
	
}