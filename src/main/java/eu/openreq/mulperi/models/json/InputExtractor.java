package eu.openreq.mulperi.models.json;

import java.util.List;

//This class extracts models from the input Json

public class InputExtractor {
	Project project;
	Requirement requirement;
	List<Requirement> requirements;
	List<Requirement> dependent_requirements;
	List<Release> releases;
	List<Dependency> dependencies;
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	public void setRequirement(Requirement requirement) {
		this.requirement = requirement;
	}
	
	public void setDependentRequirements(List<Requirement> dependent_requirements) {
		this.dependent_requirements = dependent_requirements;
	}
	
	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}
	
	public Project getProject() {
		return project;
	}
	
	public Requirement getRequirement() {
		return requirement;
	}
	
	public List<Requirement> getRequirements() {
		return requirements;
	}
	
	public List<Requirement> getDependentRequirements() {
		return dependent_requirements;
	}
	
	public List<Release> getReleases() {
		return releases;
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
