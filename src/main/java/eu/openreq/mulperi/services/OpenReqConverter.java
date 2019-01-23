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
		if (model.getRootContainer() == null) {
			return;
		}
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
	
	private void mapElements() {
		for (Element element : model.getElements().values()) {
			Requirement req = new Requirement();
			
			req.setRequirementParts(new ArrayList());
			req.setId(element.getNameID());
			switch (element.getType()) {
			case "bug":
				req.setRequirement_type(Requirement_type.BUG);
				break;
			case "epic":
				req.setRequirement_type(Requirement_type.EPIC);
				break;
			case "functional":
				req.setRequirement_type(Requirement_type.FUNCTIONAL);
				break;
			case "initiative":
				req.setRequirement_type(Requirement_type.INITIATIVE);
				break;
			case "issue":
				req.setRequirement_type(Requirement_type.ISSUE);
				break;
			case "non_functional":
				req.setRequirement_type(Requirement_type.NON_FUNCTIONAL);
				break;
			case "prose":
				req.setRequirement_type(Requirement_type.PROSE);
				break;
			case "requirement":
				req.setRequirement_type(Requirement_type.REQUIREMENT);
				break;
			case "task":
				req.setRequirement_type(Requirement_type.TASK);
				break;
			case "user_story":
				req.setRequirement_type(Requirement_type.USER_STORY);
				break;
			}
			mapElementAttributes(req, element);
			
			requirements.add(req);
		}
	}
	
	private void mapElementAttributes(Requirement req, Element element) {
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
			case "open":
				req.setStatus(Requirement_status.OPEN);
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
			long created_at = element.getAttributes().get("created_at");
			req.setCreated_at(created_at);
		} else {
			req.setCreated_at((long) 0);
		}
		
		if (element.getAttributes().containsKey("effort")) {
			req.setEffort(((Double) model.getAttributeValues().get(element.getAttributes().get("effort")).getValue()).intValue()); 
		}
		
		if (element.getAttributes().containsKey("priority")) {
			req.setPriority(((Double) model.getAttributeValues().get(element.getAttributes().get("priority")).getValue()).intValue()); 
		}
		
		if (element.getAttributes().containsKey("resolution")) {
			
			RequirementPart resolution = new RequirementPart();
			resolution.setName("Resolution");
			resolution.setText(model.getAttributeValues().get(element.getAttributes().get("resolution")).getValue().toString());
			
			req.getRequirementParts().add(resolution);
		}
		
		if (element.getAttributes().containsKey("platforms")) {
			
			RequirementPart platforms = new RequirementPart();
			platforms.setName("Platforms");
			if (model.getAttributeValues().get(element.getAttributes().get("platforms")).getValue() != null) {
				platforms.setText(model.getAttributeValues().get(element.getAttributes().get("platforms")).getValue().toString());
			} else {
				platforms.setText(null);
			}
			
			req.getRequirementParts().add(platforms);
		}
		
		if (element.getAttributes().containsKey("versions")) {
			
			RequirementPart versions = new RequirementPart();
			versions.setName("Versions");
			
			if (model.getAttributeValues().get(element.getAttributes().get("versions")).getValue() != null) {
				versions.setText(model.getAttributeValues().get(element.getAttributes().get("versions")).getValue().toString());
			} else {
				versions.setText(null);
			}
			
			req.getRequirementParts().add(versions);
		}
		
		if (element.getAttributes().containsKey("labels")) {
			
			RequirementPart labels = new RequirementPart();
			labels.setName("Labels");
			
			if (model.getAttributeValues().get(element.getAttributes().get("labels")).getValue() != null) {
				labels.setText(model.getAttributeValues().get(element.getAttributes().get("labels")).getValue().toString());
			} else {
				labels.setText(null);
			}
			
			req.getRequirementParts().add(labels);
		}
		
		if (element.getAttributes().containsKey("environment")) {
			
			RequirementPart environment = new RequirementPart();
			environment.setName("Environment");
			
			if (model.getAttributeValues().get(element.getAttributes().get("environment")).getValue() != null) {
				environment.setText(model.getAttributeValues().get(element.getAttributes().get("environment")).getValue().toString());
			} else {
				environment.setText(null);
			}
			
			req.getRequirementParts().add(environment);
		}
		
		if (element.getAttributes().containsKey("status")) {
			
			RequirementPart status = new RequirementPart();
			status.setName("Status");
			
			if (model.getAttributeValues().get(element.getAttributes().get("status")).getValue() != null) {
				status.setText(model.getAttributeValues().get(element.getAttributes().get("status")).getValue().toString());
			} else {
				status.setText(null);
			}
			
			req.getRequirementParts().add(status);
		}
		
		if (element.getAttributes().containsKey("fixVersion")) {
			
			RequirementPart fixVersion = new RequirementPart();
			fixVersion.setName("FixVersion");
			
			if (model.getAttributeValues().get(element.getAttributes().get("fixVersion")).getValue() != null) {
				fixVersion.setText(model.getAttributeValues().get(element.getAttributes().get("fixVersion")).getValue().toString());
			} else {
				fixVersion.setText(null);
			}
			
			req.getRequirementParts().add(fixVersion);
		}
		
		if (element.getAttributes().containsKey("components")) {
			
			RequirementPart components = new RequirementPart();
			components.setName("Components");
			
			if (model.getAttributeValues().get(element.getAttributes().get("components")).getValue() != null) {
				components.setText(model.getAttributeValues().get(element.getAttributes().get("components")).getValue().toString());
			} else {
				components.setText(null);
			}
			
			req.getRequirementParts().add(components);
		}
		
		if (element.getAttributes().containsKey("title")) {
			
			req.setName(model.getAttributeValues().get(element.getAttributes().get("title")).getValue().toString());
		}
	}
	
	private void mapRelationships() {
		for (Relationship rel : model.getRelations()) {
			Dependency dep = new Dependency();
			dep.setFromid(rel.getFromID());
			dep.setToid(rel.getToID());
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
			mapRelationshipAttributes(dep, rel);
			
			dependencies.add(dep);
		}
		mapElementParts();
	}
	
	private void mapRelationshipAttributes(Dependency dep, Relationship relation) {
		
		if (relation.getAttributes().containsKey("relationshipStatus")) {
			switch((String) model.getAttributeValues().get(relation.getAttributes().get("relationshipStatus")).getValue()) {
			case "proposed":
				dep.setStatus(Dependency_status.PROPOSED);
				break;
			case "rejected":
				dep.setStatus(Dependency_status.REJECTED);
				break;
			case "accepted":
				dep.setStatus(Dependency_status.ACCEPTED);
				break;
			}
		}
		
		if (relation.getAttributes().containsKey("description")) {
			//TODO Check with Lalli
			List<String> description = (List<String>) model.getAttributeValues().get(relation.getAttributes().get("description")).getValue();
			dep.setDescription(description);
		}
		
		if (relation.getAttributes().containsKey("dependency_score")) {
			double dependency_score = (double) model.getAttributeValues().get(relation.getAttributes().get("dependency_score")).getValue();
			dep.setDependency_score(dependency_score);
		}
	}
	
	private void mapElementParts() {
		for (Element element : model.getElements().values()) {
			if (element.getParts() != null || !element.getParts().isEmpty()) {
				for (Parts parts : element.getParts()) {
					//if (parts.getRole() == "decomposition") {
					for (String toId : parts.getParts()) {
						Dependency dep = new Dependency();
						dep.setFromid(element.getNameID());
						dep.setToid(toId);
						dep.setDependency_type(Dependency_type.DECOMPOSITION);
						// TODO: What is the status was something else when transforming into Murmeli?
						dep.setStatus(Dependency_status.ACCEPTED);
						
						dependencies.add(dep);
					}
					//}
				}
			}
		}
	}
	
	private void mapSubContainers() {
		for (Container container : model.getsubContainers()) {
			Release release = new Release();
			release.setId(container.getID());
			release.setRequirements(container.getElements());
			mapSubContainerAttributes(release, container);
			
			releases.add(release);
		}
	}
	
	private void mapSubContainerAttributes(Release release, Container container) {
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
