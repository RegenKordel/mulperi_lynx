package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.List;

import eu.openreq.mulperi.models.json.*;
import eu.openreq.mulperi.models.json.Release.Status;
import fi.helsinki.ese.murmeli.*;

public class OpenReqConverter {
	
	ElementModel model = new ElementModel();
	Project project = new Project();
	List<Requirement> requirements = new ArrayList<>();
	List<Dependency> dependencies = new ArrayList<>();
	List<Release> releases = new ArrayList<>();
	
	public OpenReqConverter(ElementModel model) {
		this.model = model;
		this.mapRootContainer();
		this.mapElements();
		this.mapSubContainers();
		this.mapRelationships();
	}
	
	public void mapRootContainer() {
		this.project.setId(model.getRootContainer().getNameID());
		this.project.setName(model.getRootContainer().getNameID()); // TODO: This is same as id, what to do? Root container has no name.
		if (model.getRootContainer().getAttributes().containsKey("created_at")) {
			int created_at = model.getRootContainer().getAttributes().get("created_at");
			this.project.setCreated_at((long) model.getAttributeValues().get(created_at).getValue());
		} else {
			this.project.setCreated_at((long) 0);
		}
		if (model.getRootContainer().getAttributes().containsKey("modified_at")) {
			int modified_at = model.getRootContainer().getAttributes().get("modified_at");
			this.project.setCreated_at((long) model.getAttributeValues().get(modified_at).getValue());
		}
		this.project.setSpecifiedRequirements(new ArrayList(model.getElements().keySet()));
	}
	
	public void mapElements() {
		for (Element element : model.getElements().values()) {
			Requirement req = new Requirement();
			req.setId(element.getNameID());
			switch (element.getType()) {
			case "bug":
				req.setRequirement_type(Requirement_type.BUG);
			case "epic":
				req.setRequirement_type(Requirement_type.EPIC);
			case "functional":
				req.setRequirement_type(Requirement_type.FUNCTIONAL);
			case "initiative":
				req.setRequirement_type(Requirement_type.INITIATIVE);
			case "issue":
				req.setRequirement_type(Requirement_type.ISSUE);
			case "non_functional":
				req.setRequirement_type(Requirement_type.NON_FUNCTIONAL);
			case "prose":
				req.setRequirement_type(Requirement_type.PROSE);
			case "requirement":
				req.setRequirement_type(Requirement_type.REQUIREMENT);
			case "task":
				req.setRequirement_type(Requirement_type.TASK);
			case "user_story":
				req.setRequirement_type(Requirement_type.USER_STORY);
			}
			mapElementAttributes(req, element);
			
			requirements.add(req);
		}
	}
	
	public void mapElementAttributes(Requirement req, Element element) {
		if (element.getAttributes().containsKey("status")) {
			switch((String) model.getAttributeValues().get(element.getAttributes().get("status")).getValue()) {
			case "pending":
				req.setStatus(Requirement_status.PENDING);
				break;
			case "completed":
				req.setStatus(Requirement_status.COMPLETED);
				break;
			case "accepted":
				req.setStatus(Requirement_status.ACCEPTED);
				break;
			case "deferred":
				req.setStatus(Requirement_status.DEFERRED);
				break;
			case "draft":
				req.setStatus(Requirement_status.DRAFT);
				break;
			case "in_progress":
				req.setStatus(Requirement_status.IN_PROGRESS);
				break;
			case "new":
				req.setStatus(Requirement_status.NEW);
				break;
			case "planned":
				req.setStatus(Requirement_status.PLANNED);
				break;
			case "recommended":
				req.setStatus(Requirement_status.RECOMMENDED);
				break;
			case "rejected":
				req.setStatus(Requirement_status.REJECTED);
				break;
			case "submitted":
				req.setStatus(Requirement_status.SUBMITTED);
				break;
			}
		}
		if (element.getAttributes().containsKey("modified_at")) {
			int modified_atID = element.getAttributes().get("modified_at");
			req.setCreated_at((long) model.getAttributeValues().get(modified_atID).getValue());
		}
		if (element.getAttributes().containsKey("created_at")) {
			int created_at = element.getAttributes().get("created_at");
			req.setCreated_at(created_at);
		} else {
			req.setCreated_at((long) 0);
		}
		if (element.getAttributes().containsKey("effort")) {
			req.setEffort((int) model.getAttributeValues().get(element.getAttributes().get("status")).getValue()); 
		}
		if (element.getAttributes().containsKey("priority")) {
			req.setEffort((int) model.getAttributeValues().get(element.getAttributes().get("priority")).getValue()); 
		}
	}
	
	public void mapRelationships() {
		for (Relationship rel : model.getRelations()) {
			Dependency dep = new Dependency();
			dep.setFromId(rel.getFromID());
			dep.setToId(rel.getToID());
			switch(rel.getNameType()) {
			case REFINES:
				dep.setDependency_type(Dependency_type.REFINES);
				break;
			case CONTRIBUTES:
				dep.setDependency_type(Dependency_type.CONTRIBUTES);
				break;
			case DAMAGES:
				dep.setDependency_type(Dependency_type.DAMAGES);
				break;
			case DUPLICATES:
				dep.setDependency_type(Dependency_type.DUPLICATES);
				break;
			case INCOMPATIBLE:
				dep.setDependency_type(Dependency_type.INCOMPATIBLE);
				break;
			case REPLACES:
				dep.setDependency_type(Dependency_type.REPLACES);
				break;
			case REQUIRES:
				dep.setDependency_type(Dependency_type.REQUIRES);
				break;
			case SIMILAR:
				dep.setDependency_type(Dependency_type.SIMILAR);
				break;
			}
			dependencies.add(dep);
		}
		mapElementParts();
	}
	
	public void mapElementParts() {
		for (Element element : model.getElements().values()) {
			if (element.getParts() != null || !element.getParts().isEmpty()) {
				for (Parts parts : element.getParts()) {
					if (parts.getRole() == "decomposition") {
						for (String toId : parts.getParts()) {
							Dependency dep = new Dependency();
							dep.setFromId(element.getNameID());
							dep.setToId(toId);
							dep.setDependency_type(Dependency_type.DECOMPOSITION);
							// What about the status? What to do? Mikko halp! Lalli useless.
							
							dependencies.add(dep);
						}
					}
				}
			}
		}
	}
	
	public void mapSubContainers() {
		for (Container container : model.getsubContainers()) {
			Release release = new Release();
			release.setId(container.getID());
			release.setRequirements(container.getElements());
			mapSubContainerAttributes(release, container);
			
			releases.add(release);
		}
	}
	
	public void mapSubContainerAttributes(Release release, Container container) {
		release.setCapacity((int) model.getAttributeValues().get(container.getAttributes().get("capacity")).getValue());
		
		switch((String) model.getAttributeValues().get(container.getAttributes().get("status")).getValue()) {
			case "new":
				release.setStatus(Status.NEW);
				break;
			case "completed":
				release.setStatus(Status.COMPLETED);
				break;
			case "planned":
				release.setStatus(Status.PLANNED);
				break;
			case "rejected":
				release.setStatus(Status.REJECTED);
				break;
		}
		
		if (container.getAttributes().containsKey("modified_at")) {
			int modified_atID = container.getAttributes().get("modified_at");
			release.setCreated_at((long) model.getAttributeValues().get(modified_atID).getValue());
		}
		
		if (container.getAttributes().containsKey("created_at")) {
			int created_at = container.getAttributes().get("created_at");
			release.setCreated_at(created_at);
		}
		
		if (container.getAttributes().containsKey("start_date")) {
			int start_date = container.getAttributes().get("start_date");
			release.setStart_date(start_date);
		}
		
		if (container.getAttributes().containsKey("release_date")) {
			int release_date = container.getAttributes().get("release_date");
			release.setRelease_date(release_date);
		}
	}
	
	public ElementModel getModel() {
		return model;
	}

	public void setModel(ElementModel model) {
		this.model = model;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(List<Release> releases) {
		this.releases = releases;
	}
	
}
