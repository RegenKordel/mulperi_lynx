package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.openreq.mulperi.models.json.*;
import fi.helsinki.ese.murmeli.*;
import fi.helsinki.ese.murmeli.AttributeValueType.BaseType;
import fi.helsinki.ese.murmeli.AttributeValueType.Bound;
import fi.helsinki.ese.murmeli.AttributeValueType.Cardinality;
import fi.helsinki.ese.murmeli.Relationship.NameType;
import fi.helsinki.ese.murmeli.AttributeValue.Source;

public class MurmeliModelGenerator {

	private HashMap<String, AttributeValueType> attributeValueTypes;
	private HashMap<String, ElementType> elementTypes;
	private HashMap<String, Element> elements;
	private Container rootContainer;
	private Set<Relationship> relations;
	private HashMap<Integer, Constraint> constraints;
	private HashMap<Integer, AttributeValue<?>> attributeValues;
	private List<Container> subContainers;
	private HashSet<String> requirementsInReleases;
	private HashMap<String, AttributeValue<?>> resolutions;
	private HashMap<String, AttributeValue<?>> platforms;
	private HashMap<String, AttributeValue<?>> versions;
	private HashMap<String, AttributeValue<?>> statuses;
	private HashMap<String, AttributeValue<?>> environments;
	private HashMap<String, AttributeValue<?>> labels;
	private HashMap<String, AttributeValue<?>> fixVersions;
	private HashMap<String, AttributeValue<?>> components;
	
	public MurmeliModelGenerator() {
		
		this.attributeValueTypes = new HashMap<>();
		this.elementTypes = new HashMap<>();
		this.elements = new HashMap<>();
		this.rootContainer = null;
		this.relations = new HashSet<>();
		this.constraints = new HashMap<>();
		this.attributeValues = new HashMap<>();
		this.subContainers = new ArrayList<>();
		this.resolutions = new HashMap<>();
		this.platforms = new HashMap<String, AttributeValue<?>>();
		this.versions = new HashMap<String, AttributeValue<?>>();
		this.statuses = new HashMap<String, AttributeValue<?>>();
		this.environments = new HashMap<String, AttributeValue<?>>();
		this.labels = new HashMap<String, AttributeValue<?>>();
		this.fixVersions = new HashMap<String, AttributeValue<?>>();
		this.components = new HashMap<String, AttributeValue<?>>();
		
		this.requirementsInReleases = new HashSet<>();
		
		initializeElementTypes();
	}
	
	private void initializeElementTypes() {
		
		AttributeDefinition priorityDefinition = initializePriorityType();
		
		AttributeDefinition statusDefinition = initializeStatusType();
		
		AttributeDefinition effortDefinition = initializeEffortType();
		
		ElementType bug = new ElementType("bug");
		bug.addAttributeDefinition(priorityDefinition);
		bug.addAttributeDefinition(statusDefinition);
		bug.addAttributeDefinition(effortDefinition);
		
		ElementType task = new ElementType("task");
		task.addAttributeDefinition(priorityDefinition);
		task.addAttributeDefinition(statusDefinition);
		task.addAttributeDefinition(effortDefinition);
		
		ElementType issue = new ElementType("issue");
		issue.addAttributeDefinition(priorityDefinition);
		issue.addAttributeDefinition(statusDefinition);
		issue.addAttributeDefinition(effortDefinition);
		
		ElementType userStory = new ElementType("user-story");
		userStory.addAttributeDefinition(priorityDefinition);
		userStory.addAttributeDefinition(statusDefinition);
		userStory.addAttributeDefinition(effortDefinition);
		
		ElementType epic = new ElementType("epic");
		epic.addAttributeDefinition(priorityDefinition);
		epic.addAttributeDefinition(statusDefinition);
		epic.addAttributeDefinition(effortDefinition);
		
		ElementType initiative = new ElementType("initiative");
		initiative.addAttributeDefinition(priorityDefinition);
		initiative.addAttributeDefinition(statusDefinition);
		initiative.addAttributeDefinition(effortDefinition);
		
		ElementType functional = new ElementType("functional");
		functional.addAttributeDefinition(priorityDefinition);
		functional.addAttributeDefinition(statusDefinition);
		functional.addAttributeDefinition(effortDefinition);
		
		ElementType nonFunctional = new ElementType("non-functional");
		nonFunctional.addAttributeDefinition(priorityDefinition);
		nonFunctional.addAttributeDefinition(statusDefinition);
		nonFunctional.addAttributeDefinition(effortDefinition);
		
		ElementType prose = new ElementType("prose");
		prose.addAttributeDefinition(priorityDefinition);
		prose.addAttributeDefinition(statusDefinition);
		prose.addAttributeDefinition(effortDefinition);
		
		ElementType requirement = new ElementType("requirement");
		requirement.addAttributeDefinition(priorityDefinition);
		requirement.addAttributeDefinition(statusDefinition);
		requirement.addAttributeDefinition(effortDefinition);
		
		ElementType mock = new ElementType("mock");
		mock.addAttributeDefinition(priorityDefinition);
		mock.addAttributeDefinition(statusDefinition);
		mock.addAttributeDefinition(effortDefinition);
		
		this.elementTypes.put("bug", bug);
		this.elementTypes.put("task", task);
		this.elementTypes.put("initiative", initiative);
		this.elementTypes.put("issue", issue);
		this.elementTypes.put("user-story", userStory);
		this.elementTypes.put("epic", epic);
		this.elementTypes.put("functional", functional);
		this.elementTypes.put("non-functional", nonFunctional);
		this.elementTypes.put("prose", prose);
		this.elementTypes.put("requirement", requirement);
		this.elementTypes.put("mock", mock);
		
		initializePotentialParts();
		
		initializeCapacity();
		
		initializeDependencyStatus();
	}

	private void initializeDependencyStatus() {
		
		AttributeValueType dependencyStatus = new AttributeValueType(BaseType.STRING, Cardinality.SINGLE, "relationshipStatus");
		dependencyStatus.setBound(Bound.ENUM);
		
		AttributeValue<String> accepted = new AttributeValue<String>("relationshipStatus", true, "accepted");
		accepted.setType(dependencyStatus);
		accepted.setSource(Source.DEFAULT);
		
		AttributeValue<String> proposed = new AttributeValue<String>("relationshipStatus", true, "proposed");
		proposed.setType(dependencyStatus);
		proposed.setSource(Source.DEFAULT);
		
		AttributeValue<String> rejected = new AttributeValue<String>("relationshipStatus", true, "rejected");
		rejected.setType(dependencyStatus);
		rejected.setSource(Source.DEFAULT);
		
		dependencyStatus.addValue(accepted);
		dependencyStatus.addValue(proposed);
		dependencyStatus.addValue(rejected);
		
		this.attributeValues.put(rejected.getID(), rejected);
		this.attributeValues.put(proposed.getID(), proposed);
		this.attributeValues.put(accepted.getID(), accepted);

		this.attributeValueTypes.put("relationshipStatus", dependencyStatus);
	}

	private void initializeCapacity() {
		
		AttributeValueType capacity = new AttributeValueType(BaseType.INT, Cardinality.SINGLE, "capacity");
		capacity.setBound(Bound.UNBOUND);
		
		this.attributeValueTypes.put("capacity", capacity);
	}

	private AttributeDefinition initializeEffortType() {
		
		AttributeValueType effortType = new AttributeValueType(BaseType.INT, Cardinality.SINGLE, "effort");
		effortType.setBound(Bound.UNBOUND);
		
		AttributeValue<Integer> defaultEffort = new AttributeValue<Integer>("effort", true, (int) 0);
		defaultEffort.setType(effortType);
		defaultEffort.setSource(Source.DEFAULT);
		
		AttributeDefinition def = new AttributeDefinition(defaultEffort, effortType);
		
		this.attributeValues.put(defaultEffort.getID(), defaultEffort);
		this.attributeValueTypes.put("effort", effortType);
		
		return def;
	}

	private void initializePotentialParts() {
		
		List<ElementType> potentialParts = new ArrayList<>();
		
		for (ElementType type : this.elementTypes.values()) {
			
			potentialParts.add(type);
		}
		
		PartDefinition partDefinition = new PartDefinition(0, 200, "decomposition");
		partDefinition.setTypes(potentialParts);
		
		for (ElementType type : this.elementTypes.values()) {
			
			type.addPotentialPart(partDefinition);
		}
	}

	private AttributeDefinition initializePriorityType() {
		
		AttributeValueType priorityType = new AttributeValueType(Cardinality.SINGLE, "priority", 0, 6);
		priorityType.setBound(Bound.RANGE);
		
		AttributeValue<Integer> priorityDefault = new AttributeValue<Integer>("priority", true, 4);
		priorityDefault.setSource(Source.DEFAULT);
		priorityDefault.setType(priorityType);
		
		AttributeDefinition atr = new AttributeDefinition(priorityDefault, priorityType);
		
		this.attributeValueTypes.put("priority", priorityType);
		this.attributeValues.put(priorityDefault.getID(), priorityDefault);
		
		return atr;
	}

	private AttributeDefinition initializeStatusType() {
		
		AttributeValueType statusType = new AttributeValueType(BaseType.STRING, Cardinality.SINGLE, "elementStatus");
		statusType.setBound(Bound.ENUM);
		
		AttributeValue<String> submitted = new AttributeValue<String>("elementStatus", false, "submitted");
		AttributeValue<String> deferred = new AttributeValue<String>("elementStatus", false, "deferred");
		AttributeValue<String> pending = new AttributeValue<String>("elementStatus", false, "pending");
		AttributeValue<String> inProgress = new AttributeValue<String>("elementStatus", false, "inProgress");
		AttributeValue<String> rejected = new AttributeValue<String>("elementStatus", false, "rejected");
		AttributeValue<String> draft = new AttributeValue<String>("elementStatus", false, "draft");
		AttributeValue<String> accepted = new AttributeValue<String>("elementStatus", false, "accepted");
		AttributeValue<String> completed = new AttributeValue<String>("elementStatus", false, "completed");
		AttributeValue<String> newReq = new AttributeValue<String>("elementStatus", false, "open");
		AttributeValue<String> planned = new AttributeValue<String>("elementStatus", false, "planned");
		AttributeValue<String> recommended = new AttributeValue<String>("elementStatus", false, "recommended");
		
		List<AttributeValue<?>> statuses = new ArrayList<>();
		statuses.add(submitted);
		statuses.add(deferred);
		statuses.add(pending);
		statuses.add(inProgress);
		statuses.add(rejected);
		statuses.add(draft);
		statuses.add(accepted);
		statuses.add(completed);
		statuses.add(newReq);
		statuses.add(recommended);
		statuses.add(planned);
		
		for (AttributeValue<?> status : statuses) {
			status.setType(statusType);
			this.attributeValues.put(status.getID(), status);
		}
		
		statusType.setValues(statuses);
		
		this.attributeValueTypes.put("elementStatus", statusType);
		
		AttributeDefinition def = new AttributeDefinition(submitted, statusType);
		submitted.setSource(Source.DEFAULT);
		
		return def;
	}
	
	/**
	 * Map OpenReq dependencies to Murmeli relationships
	 * @param dep
	 * @return
	 */

	public Relationship mapDependency(Dependency dep) {
		
		Relationship.NameType type = null;
		
		Element from = null;
		Element to = null;
		if(dep.getDependency_type() != null && dep != null)
		{
			switch (dep.getDependency_type())
			{
				case CONTRIBUTES:
					type = NameType.CONTRIBUTES;
					break;
				case DAMAGES:
					type = NameType.DAMAGES;
					break;
				case DECOMPOSITION:

					from = findRequirement(dep.getFromid());
					to = findRequirement(dep.getToid());
					//TODO CHECK if adding this creates side effects
					//it passes DECOMPOSITION also as dependency
					//works for ReleasePlanner, but maybe not for something else?
					type = NameType.DECOMPOSITION;

					break;
				/*
				 * NOTE! Passing DECOMPOSITION as parts has been disabled in OpenReq project.
				 * DECOMPOSITION is still passed on as relationships. What to do with further projects TBD.
				if (!from.getParts().isEmpty()) {
					from.getParts().get(0).addPart(to);
					break;
				}

				Parts part = new Parts("decomposition");
				part.addPart(to);
				from.addPart(part);

				break;
				*/
				case DUPLICATES:
					type = NameType.DUPLICATES;
					break;
				case INCOMPATIBLE:
					type = NameType.INCOMPATIBLE;
					break;
				case REFINES:
					type = NameType.REFINES;
					break;
				case REPLACES:
					type = NameType.REPLACES;
					break;
				case REQUIRES:
					type = NameType.REQUIRES;
					break;
				case SIMILAR:
					type = NameType.SIMILAR;
					break;
				case EXCLUDES:
					type = NameType.EXCLUDES;
					break;
				case IMPLIES:
					type = NameType.IMPLIES;
					break;
			}
		}
		
		if (type == null) {
			return null;
		}
		
		if (from == null) {
			from = findRequirement(dep.getFromid());
		}
		
		if (to == null) {
			to = findRequirement(dep.getToid());
		}
		
		Relationship relationship = new Relationship(type, from.getNameID(), to.getNameID());
		
		factorRelationshipStatus(relationship, dep);
		
		// TODO Check with Lalli 
		AttributeValue<List<String>> description = new AttributeValue<List<String>>("description", true, dep.getDescription());
		this.attributeValues.put(description.getID(), description);
		relationship.addAttribute(description);
		
		if (description.getValue()!=null && dep.getFromid().lastIndexOf('-')!=-1 && !dep.getFromid().substring(0, dep.getFromid()
				.lastIndexOf('-')).equals(dep.getToid().substring(0, dep.getToid().lastIndexOf('-')))) {
			description.getValue().add("crossProjectTrue");
		}
		
		AttributeValue<Double> dependency_score = new AttributeValue<Double>("dependency_score", true, dep.getDependency_score());
		this.attributeValues.put(dependency_score.getID(), dependency_score);
		relationship.addAttribute(dependency_score);

		this.relations.add(relationship);
		
		return relationship;
	}

	private void factorRelationshipStatus(Relationship relationship, Dependency dep) {
		
		List<Integer> values = this.attributeValueTypes.get("relationshipStatus").getValues();
		
		if (dep.getStatus() != null) {
			switch(dep.getStatus()) {
			case ACCEPTED:
				for (Integer id : values) {
					if (this.attributeValues.get(id).getValue().equals("accepted")) {
						relationship.addAttribute("relationshipStatus", id);
					}
				}
				break;
			case REJECTED:
				for (Integer id : values) {
					if (this.attributeValues.get(id).getValue().equals("rejected")) {
						relationship.addAttribute("relationshipStatus", id);
					}
				}
				break;
			case PROPOSED:
				for (Integer id : values) {
					if (this.attributeValues.get(id).getValue().equals("proposed")) {
						relationship.addAttribute("relationshipStatus", id);
					}
				}
				break;
			}
		}
	}

	private Element findRequirement(String id) {
		if (this.elements.containsKey(id)) {
			return this.elements.get(id);
		}
		
		Requirement req = new Requirement();
		req.setId(id);
		
		return this.mapRequirement(req);
	}

	/**
	 * Map OpenReq requirement to Murmeli Element
	 * @param req
	 * @return Element
	 */
	private Element mapRequirement(Requirement req) {
		
		
		if (this.elements.containsKey(req.getId())) {
			return this.elements.get(req.getId());
		}
		
		String name = req.getId();
		AttributeValue<Integer> priority = new AttributeValue<>("priority", true, (int) req.getPriority());
		priority.setSource(Source.FIXED);
		priority.setType(this.attributeValueTypes.get("priority"));
		
		this.attributeValues.put(priority.getID(), priority);
		
		AttributeValue<String> status;
		
		if (req.getStatus() != null) {
			status = factorStatus(req.getStatus());
		} else {
			status = factorStatus(Requirement_status.OPEN);
		}

		Element element = new Element(name);
		element.addAttribute(priority);
		element.addAttribute(status);
		
		resolutionToElement(req, element);
		
		titleToElement(req, element);
		
		if (req.getRequirement_type() == null) {
			ElementType mock = this.elementTypes.get("mock");
			element.setType(mock);
			
			for (AttributeDefinition def : mock.getAttributeDefinitions()) {
				element.addAttribute(this.attributeValues.get(def.getDefaultValue()));
			}
			
			element.setNameID(name + "-mock");
			
			if (this.elements.containsKey(element.getNameID())) {
				return this.elements.get(element.getNameID());
			}
		} else {
			switch (req.getRequirement_type()) {
			case BUG:
				element.setType(this.elementTypes.get("bug"));
				element.addAttribute(factorEffort(req, "bug"));
				break;
			case EPIC:
				element.setType(this.elementTypes.get("epic"));
				element.addAttribute(factorEffort(req, "epic"));
				break;
			case FUNCTIONAL:
				element.setType(this.elementTypes.get("functional"));
				element.addAttribute(factorEffort(req, "functional"));
				break;
			case INITIATIVE:
				element.setType(this.elementTypes.get("initiative"));
				element.addAttribute(factorEffort(req, "initiative"));
				break;
			case ISSUE:
				element.setType(this.elementTypes.get("issue"));
				element.addAttribute(factorEffort(req, "issue"));
				break;
			case NON_FUNCTIONAL:
				element.setType(this.elementTypes.get("non-functional"));
				element.addAttribute(factorEffort(req, "non-functional"));
				break;
			case PROSE:
				element.setType(this.elementTypes.get("prose"));
				element.addAttribute(factorEffort(req, "prose"));
				break;
			case REQUIREMENT:
				element.setType(this.elementTypes.get("requirement"));
				element.addAttribute(factorEffort(req, "requirement"));
				break;
			case TASK:
				element.setType(this.elementTypes.get("task"));
				element.addAttribute(factorEffort(req, "task"));
				break;
			case USER_STORY:
				element.setType(this.elementTypes.get("user-story"));
				element.addAttribute(factorEffort(req, "user-story"));
				break;
			}
		}
		// TODO: in case of mock, might cause problems when trying to find the 
		// element in the context of the project the mock element doesn't exist in
		this.elements.put(element.getNameID(), element);
		
		return element;
	}

	/**
	 * Map requirements title to a attribute and adds it to corresponding Element
	 * @param req
	 * @param element
	 */
	private void titleToElement(Requirement req, Element element) {
		
		if (req.getName() != null) {
			AttributeValue<String> atr = new AttributeValue<>("title", false, req.getName());
			this.attributeValues.put(atr.getID(), atr);
			element.addAttribute(atr);
		}
	}

	/**
	 * Map requirements resolution to a attribute and adds it to corresponding Element
	 * @param req
	 * @param element
	 */
	private void resolutionToElement(Requirement req, Element element) {
		if(req.getRequirementParts()!=null) {
			for (RequirementPart part : req.getRequirementParts()) {				
				if (part.getName().equals("Resolution")) {
					if (!this.resolutions.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("resolution", false, part.getText());
						this.resolutions.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
	
					element.addAttribute(this.resolutions.get(part.getText()));
				} else if (part.getName().equals("Platforms")) {
					if (!this.platforms.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("platforms", false, part.getText());
						this.platforms.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.platforms.get(part.getText()));
				} else if (part.getName().equals("Versions")) {
					if (!this.versions.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("versions", false, part.getText());
						this.versions.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.versions.get(part.getText()));
				} else if (part.getName().equals("Labels")) {
					if (!this.labels.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("labels", false, part.getText());
						this.labels.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.labels.get(part.getText()));
				} else if (part.getName().equals("Environment")) {
					if (!this.environments.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("environment", false, part.getText());
						this.environments.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.environments.get(part.getText()));
				} else if (part.getName().equals("Status")) {
					if (!this.statuses.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("status", false, part.getText());
						this.statuses.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.statuses.get(part.getText()));
				} else if (part.getName().equals("FixVersion")) {
					if (!this.fixVersions.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("fixVersion", false, part.getText());
						this.fixVersions.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.fixVersions.get(part.getText()));
				} else if (part.getName().equals("Components")) {
					if (!this.components.containsKey(part.getText())) {
						
						AttributeValue<String> atr = new AttributeValue<>("components", false, part.getText());
						this.components.put(part.getText(), atr);
						this.attributeValues.put(atr.getID(), atr);
					}
					
					element.addAttribute(this.components.get(part.getText()));
				}
				
			}
		}
		
	}

	private AttributeValue<Integer> factorEffort(Requirement req, String type) {
		
		for (AttributeDefinition def : this.elementTypes.get(type).getAttributeDefinitions()) {
			
			if (this.attributeValueTypes.get(def.getValueType()).getName().equals("effort")) {
				if (req.getEffort() == 0) {
					return (AttributeValue<Integer>)this.attributeValues.get(def.getDefaultValue());
				} else {
					AttributeValue<Integer> value = new AttributeValue<Integer>("effort", false, (int) req.getEffort());
					this.attributeValues.put(value.getID(), value);
					value.setType(this.attributeValueTypes.get("effort"));
					return value;
				}
			}
		}
		
		return null;
	}

	/**
	 * method to find the corresponding status AttributeValue for a status of an
	 * OpenReq requirement
	 * @param status
	 * @return
	 */
	private AttributeValue<String> factorStatus(Requirement_status status) {
		
		AttributeValueType statuses = this.attributeValueTypes.get("elementStatus");

		switch(status) {
		case ACCEPTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("accepted")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case COMPLETED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("completed")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case DEFERRED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("deferred")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			break;
		case DRAFT:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("draft")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case IN_PROGRESS:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("inProgress")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case PENDING:
			
			for (Integer value : statuses.getValues()) {
			
				if (this.attributeValues.get(value).getValue().equals("pending")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case REJECTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("rejected")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case SUBMITTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("submitted")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case OPEN:
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("open")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case PLANNED:

			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("planned")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		case RECOMMENDED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getValue().equals("recommended")) {
					return (AttributeValue<String>)this.attributeValues.get(value);
				}
			}
			
			break;
		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * Maps expression to Murmeli Constraints
	 * @param expression
	 * @param name
	 * @return
	 */
	public Constraint mapConstraint(String expression, String name) {
		
		Constraint cons = new Constraint(expression, name);
		this.constraints.put(cons.getID(), cons);
		
		return cons;
	}
	
	public Constraint mapConstraint(String expression) {
		return mapConstraint(expression, "");
	}
	
	public Container initializeRootContainer(String projectName) {
		
		if (this.rootContainer != null) {
			return this.rootContainer;
		}
		
		Container root = new Container(projectName);
		
		this.rootContainer = root;
		
		for (Element elmnt : this.elements.values()) {
			root.addElement(elmnt);
		}
		
		return root;
	}
	
	public void addElementsToRootContainer() {
		for (String element : this.elements.keySet()) {
			if (!this.requirementsInReleases.contains(element)) {
				this.rootContainer.addElement(element);
			}
		}
	}
	
	/**
	 * Method to create Murmeli model from OpenReq objects given as input 
	 * @param requirements
	 * @param constraints
	 * @param dependencies
	 * @param releases
	 * @return
	 */
	public ElementModel initializeElementModel(List<Requirement> requirements, List<String> constraints, List<Dependency> dependencies, List<Release> releases, String projectName) {
		
		ElementModel model = new ElementModel();
		
		for (Requirement req : requirements) {
			mapRequirement(req);
		}
		
		for (String constraint : constraints) {
			mapConstraint(constraint);
		}
		
		for (Dependency dep : dependencies) {
			if(dep != null)
			{
				mapDependency(dep);
			}
		}
		
		this.initializeRootContainer(projectName);
		
		int id = 1;
		
		for (Release release : releases) {
			mapRelease(release, id);
			id++;
		}
		
		this.addElementsToRootContainer();
		
		model.setConstraints(this.constraints);
		model.setElements(this.elements);
		model.setElementTypes(this.elementTypes);
		model.setRootContainer(this.rootContainer);
		model.setRelations(this.relations);
		model.setAttributeValueTypes(this.attributeValueTypes);
		model.setSubContainers(this.subContainers);
		model.setAttributeValues(this.attributeValues);
		
		this.rootContainer.setChildren(this.subContainers);
		
		return model;
	}
	
	private Container mapRelease(Release release, int id) {
		String releaseId = release.getId();
		
		Container rele = new Container(releaseId, id);
		
		AttributeValue<Integer> capacity = new AttributeValue<>("capacity", false, (int) release.getCapacity());
		
		capacity.setSource(Source.FIXED);
		capacity.setType(this.attributeValueTypes.get("capacity"));
		
		rele.addAttribute(capacity);
		this.attributeValues.put(capacity.getID(), capacity);
			
		for (String req : release.getRequirements()) {
			rele.addElement(findRequirement(req));
			this.requirementsInReleases.add(req);
		}
		this.subContainers.add(rele);
		return rele;
	}

	public ElementModel initializeElementModel(List<Requirement> requirements, List<String> constraints, List<Dependency> dependencies, String projectName) {
		
		//if there are no releases in input the method will create a dummy release, ID is set to 1 because of Choco
		Container dummy = new Container("dummy");
		dummy.setID(1);
		this.subContainers.add(dummy);
		
		AttributeValue<Integer> capacity = new AttributeValue<>("capacity", false, 10);
		capacity.setSource(Source.DEFAULT);
		capacity.setType(this.attributeValueTypes.get("capacity"));
		this.attributeValues.put(capacity.getID(), capacity);
		dummy.addAttribute(capacity);
		
		return this.initializeElementModel(requirements, constraints, dependencies, new ArrayList<Release>(), projectName);
	}
	
	public ElementModel initializeElementModel(List<Requirement> requirements, List<Dependency> dependencies, String projectName) {
		
		return this.initializeElementModel(requirements, new ArrayList<String>(), dependencies, projectName);
	}
}
