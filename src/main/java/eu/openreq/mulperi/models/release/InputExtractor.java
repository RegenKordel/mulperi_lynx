package eu.openreq.mulperi.models.release;

import java.util.List;

// This class extracts models from the input Json

public class InputExtractor {
	Project project;
	List<Requirement> requirements;
	List<Release> releases;
	List<Dependency> dependencies;
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}
	
	public Project getProject() {
		return this.project;
	}
	
	public List<Requirement> getRequirements() {
		return this.requirements;
	}
	
	public List<Release> getReleases() {
		return this.releases;
	}
	
	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}
	
	public List<Dependency> getDependencies() {
		return dependencies;
	}
	
	public Requirement findRequirementById(String reqId) {
		for (Requirement r: requirements) {
			if (r.getId().equals(reqId)) {
				return r;
			}
		}
		return null;
	}
}


