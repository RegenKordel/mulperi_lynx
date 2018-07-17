package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.openreq.mulperi.models.json.*;
import fi.helsinki.ese.murmeli.*;
import fi.helsinki.ese.murmeli.AttributeValueType.BaseType;
import fi.helsinki.ese.murmeli.AttributeValueType.Bound;
import fi.helsinki.ese.murmeli.AttributeValueType.Cardinality;
import fi.helsinki.ese.murmeli.RelationshipType.NameType;
import fi.helsinki.ese.murmeli.AttributeValue.Source;

public class MurmeliModelGenerator {

	
	private HashMap<String, AttributeValueType> attributeValueTypes;
	private HashMap<String, ElementType> elementTypes;
	private HashMap<String, Element> elements;
	private Container rootContainer;
	private List<RelationshipType> relations;
	private HashMap<Integer, Constraint> constraints;
	private HashMap<Integer, AttributeValue> attributeValues;
	
	public MurmeliModelGenerator() {
		
		this.attributeValueTypes = new HashMap();
		this.elementTypes = new HashMap();
		this.elements = new HashMap();
		this.rootContainer = null;
		this.relations = new ArrayList();
		this.constraints = new HashMap();
		this.attributeValues = new HashMap();
		
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
		this.elementTypes.put("functinal", functional);
		this.elementTypes.put("non-functional", nonFunctional);
		this.elementTypes.put("prose", prose);
		this.elementTypes.put("mock", mock);
		
		initializePotentialParts();
		
		initializeCapacity();
	}

	private void initializeCapacity() {
		
		AttributeValueType capacity = new AttributeValueType(BaseType.INT, Cardinality.SINGLE, "capacity");
		capacity.setBound(Bound.UNBOUND);
		
		this.attributeValueTypes.put("capacity", capacity);
	}

	private AttributeDefinition initializeEffortType() {
		
		AttributeValueType effortType = new AttributeValueType(BaseType.INT, Cardinality.SINGLE, "effort");
		effortType.setBound(Bound.UNBOUND);
		
		AttributeValue defaultEffort = new AttributeValue("effort", true, 0);
		defaultEffort.setType(effortType);
		defaultEffort.setSource(Source.DEFAULT);
		
		AttributeDefinition def = new AttributeDefinition(defaultEffort, effortType);
		
		this.attributeValues.put(defaultEffort.getID(), defaultEffort);
		this.attributeValueTypes.put("effort", effortType);
		
		return def;
	}

	private void initializePotentialParts() {
		
		List<ElementType> potentialParts = new ArrayList();
		
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
		
		AttributeValue priorityDefault = new AttributeValue("priority", true, 4);
		priorityDefault.setSource(Source.DEFAULT);
		priorityDefault.setType(priorityType);
		
		AttributeDefinition atr = new AttributeDefinition(priorityDefault, priorityType);
		
		this.attributeValueTypes.put("priority", priorityType);
		this.attributeValues.put(priorityDefault.getID(), priorityDefault);
		
		return atr;
	}

	private AttributeDefinition initializeStatusType() {
		
		AttributeValueType statusType = new AttributeValueType(BaseType.STRING, Cardinality.SINGLE, "status");
		statusType.setBound(Bound.ENUM);
		
		AttributeValue submitted = new AttributeValue("status", false, "submitted");
		AttributeValue deferred = new AttributeValue("status", false, "deferred");
		AttributeValue pending = new AttributeValue("status", false, "pending");
		AttributeValue inProgress = new AttributeValue("status", false, "inProgress");
		AttributeValue rejected = new AttributeValue("status", false, "rejected");
		AttributeValue draft = new AttributeValue("status", false, "draft");
		AttributeValue accepted = new AttributeValue("status", false, "accepted");
		AttributeValue completed = new AttributeValue("status", false, "completed");
		AttributeValue newReq = new AttributeValue("status", false, "new");
		AttributeValue planned = new AttributeValue("status", false, "planned");
		AttributeValue recommended = new AttributeValue("status", false, "recommended");
		
		List<AttributeValue> statuses = new ArrayList();
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
		
		for (AttributeValue status : statuses) {
			status.setType(statusType);
			this.attributeValues.put(status.getID(), status);
		}
		
		statusType.setValues(statuses);
		
		this.attributeValueTypes.put("statuses", statusType);
		
		AttributeDefinition def = new AttributeDefinition(submitted, statusType);
		submitted.setSource(Source.DEFAULT);
		
		return def;
	}
	
	/**
	 * Map OpenReq dependencies to Murmeli relationships
	 * @param dep
	 * @return
	 */

	public RelationshipType mapDependency(Dependency dep) {
		
		RelationshipType.NameType type = null;
		
		switch(dep.getDependency_type()) {
		case CONTRIBUTES:
			type = NameType.CONTRIBUTES;
			break;
		case DAMAGES:
			type = NameType.DAMAGES;
			break;
		case DECOMPOSITION:
			
			Element from = mapRequirement(dep.getFromId());
			Element to = mapRequirement(dep.getToId());
			
			if (!from.getParts().isEmpty()) {
				from.getParts().get(0).addPart(to);
				break;
			}
			
			Parts part = new Parts("decomposition");
			part.addPart(to);
			
			from.addPart(part);
			
			break;
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
		default:
			break;
		}
		
		if (type == null) {
			return null;
		}
		
		Element from = mapRequirement(dep.getFromId());
		Element to = mapRequirement(dep.getToId());
		
		RelationshipType relationship = new RelationshipType(type, from.getNameID(), to.getNameID());
		
		this.relations.add(relationship);
		
		return relationship;
	}

	private Element mapRequirement(Requirement req) {
		
		if (this.elements.containsKey(req.getName())) {
			return this.elements.get(req.getName());
		}
		
		String name = req.getId();
		AttributeValue<Integer> priority = new AttributeValue("priority", true, req.getPriority());
		priority.setType(this.attributeValueTypes.get("priority"));
		AttributeValue<String> status = factorStatus(req.getStatus());
		
		Element element = new Element(name);
		element.addAttribute(priority);
		element.addAttribute(status);
		
		switch (req.getRequirement_type()) {
		case BUG:
			element.setType(this.elementTypes.get("bug"));
			element.addAttribute(factorEffort("bug"));
			break;
		case EPIC:
			element.setType(this.elementTypes.get("epic"));
			element.addAttribute(factorEffort("epic"));
			break;
		case FUNCTIONAL:
			element.setType(this.elementTypes.get("functional"));
			element.addAttribute(factorEffort("functional"));
			break;
		case INITIATIVE:
			element.setType(this.elementTypes.get("initiative"));
			element.addAttribute(factorEffort("initiative"));
			break;
		case ISSUE:
			element.setType(this.elementTypes.get("issue"));
			element.addAttribute(factorEffort("issue"));
			break;
		case NON_FUNCTIONAL:
			element.setType(this.elementTypes.get("non-functional"));
			element.addAttribute(factorEffort("non-functional"));
			break;
		case PROSE:
			element.setType(this.elementTypes.get("prose"));
			element.addAttribute(factorEffort("prose"));
			break;
		case REQUIREMENT:
			element.setType(this.elementTypes.get("requirement"));
			element.addAttribute(factorEffort("requirement"));
			break;
		case TASK:
			element.setType(this.elementTypes.get("task"));
			element.addAttribute(factorEffort("task"));
			break;
		case USER_STORY:
			element.setType(this.elementTypes.get("user-story"));
			element.addAttribute(factorEffort("user-story"));
			break;
		default:
			//if requirement doesn't have a type it supposedly is not in the project
			//and comes from a dependency. hence we create a mock element.
			
			ElementType mock = this.elementTypes.get("mock");
			element.setType(mock);
			
			for (AttributeDefinition def : mock.getAttributeDefinitions()) {
				element.addAttribute(this.attributeValues.get(def.getDefaultValue()));
			}
			
			element.setNameID(name + "-mock");
			
			break;
		}
		
		this.elements.put(name, element);
		
		return element;
	}

	private AttributeValue factorEffort(String type) {
		
		for (AttributeDefinition def : this.elementTypes.get(type).getAttributeDefinitions()) {
			
			if (this.attributeValueTypes.get(def.getValueType()).getName().equals("effort")) {
				return this.attributeValues.get(def.getDefaultValue());
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
		
		AttributeValueType statuses = this.attributeValueTypes.get("statuses");
		
		switch(status) {
		case ACCEPTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("accepted")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case COMPLETED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("completed")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case DEFERRED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("deferred")) {
					return this.attributeValues.get(value);
				}
			}
			break;
		case DRAFT:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("draft")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case IN_PROGRESS:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("inProgress")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case PENDING:
			
			for (Integer value : statuses.getValues()) {
			
				if (this.attributeValues.get(value).getName().equals("pending")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case REJECTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("rejected")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case SUBMITTED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("submitted")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case NEW:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("new")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case PLANNED:

			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("planned")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		case RECOMMENDED:
			
			for (Integer value : statuses.getValues()) {
				
				if (this.attributeValues.get(value).getName().equals("recommended")) {
					return this.attributeValues.get(value);
				}
			}
			
			break;
		default:
			break;
		}
		
		return null;
	}
	
	public Constraint mapConstraint(String expression, String name) {
		
		Constraint cons = new Constraint(expression, name);
		this.constraints.put(cons.getID(), cons);
		
		return cons;
	}
	
	public Constraint mapConstraint(String expression) {
		return mapConstraint(expression, "");
	}
	
	public Container initializeRootContainer() {
		
		if (this.rootContainer != null) {
			return this.rootContainer;
		}
		
		Container root = new Container("root");
		
		for (Element elmnt : this.elements.values()) {
			root.addElement(elmnt);
		}
		
		return root;
	}
	
	/**
	 * Method to create Murmeli model from OpenReq objects given as input 
	 * @param requirements
	 * @param constraints
	 * @param dependencies
	 * @param releases
	 * @return
	 */
	public ElementModel initializeElementModel(List<Requirement> requirements, List<String> constraints, List<Dependency> dependencies, List<Release> releases) {
		
		ElementModel model = new ElementModel();
		
		for (Requirement req : requirements) {
			mapRequirement(req);
		}
		
		for (String constraint : constraints) {
			mapConstraint(constraint);
		}
		
		for (Dependency dep : dependencies) {
			mapDependency(dep);
		}
		
		this.initializeRootContainer();
		
		for (Release release : releases) {
			this.rootContainer.addChild(mapRelease(release));
		}
		
		model.setConstraints(this.constraints);
		model.setElements(this.elements);
		model.setElementTypes(this.elementTypes);
		model.setRootContainer(this.rootContainer);
		model.setRelations(this.relations);
		model.setAttributeValueTypes(this.attributeValueTypes);
		
		return model;
	}
	
	private Container mapRelease(Release release) {
		
		Container rele = new Container(release.getId());
		
		AttributeValue capacity = new AttributeValue("capacity", false, release.getCapacity());
		capacity.setSource(Source.FIXED);
		capacity.setType(this.attributeValueTypes.get("capacity"));
		
		rele.addAttribute(capacity);
		
		for (Requirement req : release.getRequirements()) {
			rele.addElement(mapRequirement(req));
		}
		
		return rele;
	}

	public ElementModel initializeElementModel(List<Requirement> requirements, List<String> constraints, List<Dependency> dependencies) {
		
		//if there are no releases in input the method will create a dummy release
		initializeRootContainer();
		
		Container dummy = new Container("dummy");
		this.rootContainer.addChild(dummy);
		
		return this.initializeElementModel(requirements, constraints, dependencies, new ArrayList<Release>());
	}
	
	public ElementModel initializeElementModel(List<Requirement> requirements, List<Dependency> dependencies) {
		
		return this.initializeElementModel(requirements, new ArrayList<String>(), dependencies);
	}
}
