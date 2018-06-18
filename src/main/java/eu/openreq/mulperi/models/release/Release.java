package eu.openreq.mulperi.models.release;

import java.util.List;

public class Release {
	final int id; 
	int capacity;
	List<String> requirements;
	
	public Release(int id, int capacity) {
		super();
		this.id = id;
		this.capacity = capacity;
	}

	public int getId() {
		return id;
	}

	public int getMaxCapacity() {
		return capacity;
	}

	public void setMaxCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public void setRequirements(List<String> req) {
		this.requirements = req;
	}
	
	public List<String> getRequirements() {
		return requirements;
	}
}
