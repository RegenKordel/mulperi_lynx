package eu.openreq.mulperi.models.json;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Release {

	public enum Status {
		NEW,
        PLANNED,
        COMPLETED,
        REJECTED
	}
	
	/**
	 * (Required)
	 */
	@SerializedName("id")
	@Expose
	String id;
	
	@SerializedName("created_at")
	@Expose
	long created_at;
	
	@SerializedName("modified_at")
	@Expose
	long modified_at;
	
	@SerializedName("start_date")
	@Expose
	long start_date;
	
	@SerializedName("release_date")
	@Expose
	long release_date;
	
	/**
	 * (Required)
	 */
	@SerializedName("capacity")
	@Expose
	int capacity;
	
	@SerializedName("project")
	@Expose
	Project project;
	
	@SerializedName("requirements")
	@Expose
	List<String> requirements;
	
	/**
	 * (Required)
	 */
	@SerializedName("status")
	@Expose
	Status status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public long getStart_date() {
		return start_date;
	}

	public void setStart_date(long start_date) {
		this.start_date = start_date;
	}

	public long getRelease_date() {
		return release_date;
	}

	public void setRelease_date(long release_date) {
		this.release_date = release_date;
	}

	public long getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<String> requirements) {
		this.requirements = requirements;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	
}
