package eu.openreq.mulperi.models.json;

import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
* Requirement
* A requirement within the OpenReq framework
* 
*/
public class Requirement {

	/**
	* The unique identifier for a requirement
	* (Required)
	* 
	*/
	@SerializedName("id")
	@Expose
	private String id;
	/**
	* The name of the requirement
	* 
	*/
	@SerializedName("name")
	@Expose
	private String name;
	/**
	* The textual description of the requirement
	* 
	*/
	@SerializedName("text")
	@Expose
	private String text;
	/**
	* The comments to the requirement
	* 
	*/
	@SerializedName("comments")
	@Expose
	private List<Comment> comments = null;
	/**
	* Creation timestamp
	* (Required)
	* 
	*/
	@SerializedName("created_at")
	@Expose
	private long created_at;
	/**
	* Last modification time
	* 
	*/
	@SerializedName("modified_at")
	@Expose
	private long modified_at;
	/**
	* The calculated priority of a requirement
	* 
	*/
	@SerializedName("priority")
	@Expose
	private int priority;

	/**
	* The type of a requirement
	* 
	*/
	@SerializedName("requirement_type")
	@Expose
	private String requirement_type;
	/**
	* The current status of a requirement
	* (Required)
	* 
	*/
	@SerializedName("status")
	@Expose
	private String status;
	/**
	* The requirements belonging to this requirement
	* 
	*/
	@SerializedName("children")
	@Expose
	private List<String> children = null;
	
	@SerializedName("effort")
	@Expose
	private int effort;
	
	/**
	* RequirementParts of a requirement
	* 
	*/
	@SerializedName("requirementParts")
	@Expose
	private List<RequirementPart> requirementParts = null;
	
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public long getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
	
	public long getModified_at() {
		return modified_at;
	}
	
	public void setModified_at(long modified_at) {
		this.modified_at = modified_at;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public int getEffort() {
		return effort;
	}

	public void setEffort(int effort) {
		this.effort = effort;
	}
	
	public String getRequirement_type() {
		return requirement_type;
	}
	
	public void setRequirement_type(String requirement_type) {
		this.requirement_type = requirement_type;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<String> getChildren() {
		return children;
	}
	
	public void setChildren(List<String> children) {
		this.children = children;
	}


	public List<RequirementPart> getRequirementParts() {
		return requirementParts;
	}

	public void setRequirementParts(List<RequirementPart> requirementParts) {
		this.requirementParts = requirementParts;
	}
	
	@Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Requirement)) {
        	return false;
        }
        Requirement req = (Requirement) obj;
        return this.id.equals((req.getId()));
    }

    @Override
    public int hashCode() {
    	return Objects.hash(this.id, this.name);
    } 
}