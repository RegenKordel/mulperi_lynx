package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import eu.closedreq.bridge.models.json.*;
import eu.closedreq.bridge.models.json.Release;
import eu.closedreq.bridge.models.json.Release.Status;
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
			this.project.setCreated_at((Long) model.getAttributeValues().get(created_at).getValue());
		} else {
			this.project.setCreated_at(0L);
		}
		if (model.getRootContainer().getAttributes().containsKey("modified_at")) {
			int modified_at = model.getRootContainer().getAttributes().get("modified_at");
			this.project.setCreated_at((Long) model.getAttributeValues().get(modified_at).getValue());
		}
		this.project.setSpecifiedRequirements(model.getElements().keySet());
	}

	private void mapElements() {
		for (Element element : model.getElements().values()) {
			Requirement req = new Requirement();

			req.setRequirementParts(new HashSet<RequirementPart>());
			req.setId(element.getNameID());
			req.setRequirement_type(element.getType());

			mapElementAttributes(req, element);

			requirements.add(req);
		}
	}

	private void mapElementAttributes(Requirement req, Element element) {
		if (element.getAttributes().containsKey("status")) {
			String status = model.getAttributeValues().get(element.getAttributes().get("elementStatus")).getValue().toString();
			req.setStatus(status);
		}

		if (element.getAttributes().containsKey("modified_at")) {
			int modified_atID = element.getAttributes().get("modified_at");
			req.setCreated_at((Long) model.getAttributeValues().get(modified_atID).getValue());
		}

		if (element.getAttributes().containsKey("created_at")) {
			long created_at = element.getAttributes().get("created_at");
			req.setCreated_at(created_at);
		} else {
			req.setCreated_at(0L);
		}

		if (element.getAttributes().containsKey("effort")) {
			String effort = model.getAttributeValues().get(element.getAttributes().get("effort")).getValue().toString();
			req.setEffort((int) Double.parseDouble(effort));
		}

		if (element.getAttributes().containsKey("priority")) {
			String priority = model.getAttributeValues().get(element.getAttributes().get("priority")).getValue().toString();
			req.setPriority((int) Double.parseDouble(priority));
		}

		if (element.getAttributes().containsKey("resolution")) {

			RequirementPart resolution = new RequirementPart();
			resolution.setName("Resolution");
			resolution.setText(model.getAttributeValues().get(element.getAttributes().get("resolution")).getValue().toString());

			resolution.setId(element.getNameID() + "_RESOLUTION");

			req.addRequirementPart(resolution);
		}

		if (element.getAttributes().containsKey("platforms")) {

			RequirementPart platforms = new RequirementPart();
			platforms.setName("Platforms");
			if (model.getAttributeValues().get(element.getAttributes().get("platforms")).getValue() != null) {
				platforms.setText(model.getAttributeValues().get(element.getAttributes().get("platforms")).getValue().toString());
			} else {
				platforms.setText(null);
			}

			platforms.setId(element.getNameID() + "_PLATFORMS");

			req.addRequirementPart(platforms);
		}

		if (element.getAttributes().containsKey("versions")) {

			RequirementPart versions = new RequirementPart();
			versions.setName("Versions");

			if (model.getAttributeValues().get(element.getAttributes().get("versions")).getValue() != null) {
				versions.setText(model.getAttributeValues().get(element.getAttributes().get("versions")).getValue().toString());
			} else {
				versions.setText(null);
			}

			versions.setId(element.getNameID() + "_VERSIONS");

			req.addRequirementPart(versions);
		}

		if (element.getAttributes().containsKey("labels")) {

			RequirementPart labels = new RequirementPart();
			labels.setName("Labels");

			if (model.getAttributeValues().get(element.getAttributes().get("labels")).getValue() != null) {
				labels.setText(model.getAttributeValues().get(element.getAttributes().get("labels")).getValue().toString());
			} else {
				labels.setText(null);
			}

			labels.setId(element.getNameID() + "_LABELS");

			req.addRequirementPart(labels);
		}

		if (element.getAttributes().containsKey("environment")) {

			RequirementPart environment = new RequirementPart();
			environment.setName("Environment");

			if (model.getAttributeValues().get(element.getAttributes().get("environment")).getValue() != null) {
				environment.setText(model.getAttributeValues().get(element.getAttributes().get("environment")).getValue().toString());
			} else {
				environment.setText(null);
			}

			environment.setId(element.getNameID() + "_ENVIRONMENT");

			req.addRequirementPart(environment);
		}

		if (element.getAttributes().containsKey("status")) {

			RequirementPart status = new RequirementPart();
			status.setName("Status");

			if (model.getAttributeValues().get(element.getAttributes().get("status")).getValue() != null) {
				status.setText(model.getAttributeValues().get(element.getAttributes().get("status")).getValue().toString());
			} else {
				status.setText(null);
			}

			status.setId(element.getNameID() + "_STATUS");

			req.addRequirementPart(status);
		}

		if (element.getAttributes().containsKey("fixVersion")) {

			RequirementPart fixVersion = new RequirementPart();
			fixVersion.setName("FixVersion");

			if (model.getAttributeValues().get(element.getAttributes().get("fixVersion")).getValue() != null) {
				fixVersion.setText(model.getAttributeValues().get(element.getAttributes().get("fixVersion")).getValue().toString());
			} else {
				fixVersion.setText(null);
			}

			fixVersion.setId(element.getNameID() + "_FIXVERSION");

			req.addRequirementPart(fixVersion);
		}

		if (element.getAttributes().containsKey("components")) {

			RequirementPart components = new RequirementPart();
			components.setName("Components");

			if (model.getAttributeValues().get(element.getAttributes().get("components")).getValue() != null) {
				components.setText(model.getAttributeValues().get(element.getAttributes().get("components")).getValue().toString());
			} else {
				components.setText(null);
			}

			components.setId(element.getNameID() + "_COMPONENTS");

			req.addRequirementPart(components);
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
			dep.setDependency_type(rel.getNameType());
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
			@SuppressWarnings("unchecked")
			List<String> description = (List<String>) model.getAttributeValues().get(relation.getAttributes().get("description")).getValue();
			dep.setDescription(description);
		}

		if (relation.getAttributes().containsKey("dependency_score")) {
			Double dependency_score = (Double) model.getAttributeValues().get(relation.getAttributes().get("dependency_score")).getValue();
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
						dep.setDependency_type("blocks");
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
		for (Container container : model.getSubContainers()) {
			Release release = new Release();
			release.setId(container.getID()+"");
			release.setRequirements(container.getElements());
			mapSubContainerAttributes(release, container);

			releases.add(release);
		}
	}

	private void mapSubContainerAttributes(Release release, Container container) {
		release.setCapacity((Integer) model.getAttributeValues().get(container.getAttributes().get("capacity")).getValue());

		AttributeValue<?> status =  model.getAttributeValues().get(container.getAttributes().get("status"));
		if (status != null) {
			switch(status.getValue().toString().toLowerCase()) {
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
		}

		if (container.getAttributes().containsKey("modified_at")) {
			int modified_atID = container.getAttributes().get("modified_at");
			release.setCreated_at((Long) model.getAttributeValues().get(modified_atID).getValue());
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

	public List<Requirement> addUnknownIfEmpty(List<Requirement> requirements) {
		List<Requirement> newList = new ArrayList<Requirement>();
		for (Requirement req : requirements) {
			if (req.getRequirementParts().isEmpty()) {
				List<RequirementPart> rparts = new ArrayList<RequirementPart>();
				RequirementPart rpart = new RequirementPart();
				rpart.setName("Unknown");
				rpart.setText("Unknown");
				rpart.setCreated_at(0);
				rparts.add(rpart);
				req.setRequirementParts(new HashSet<>(rparts));
			}
			newList.add(req);
		}
		return newList;
	}

}
