package eu.openreq.mulperi.models.release;

//import eu.openreq.mulperi.models.json.*;
import eu.openreq.mulperi.models.release.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import eu.openreq.mulperi.models.kumbang.Attribute;
import eu.openreq.mulperi.models.kumbang.Component;
import eu.openreq.mulperi.models.kumbang.Constraint;
import eu.openreq.mulperi.models.kumbang.Feature;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.kumbang.SubFeature;
import eu.openreq.mulperi.models.selections.CalculationConstraint;

public class ReleasePlan {
	private Project project = null;
	private TreeMap<Integer, Release> releases = new TreeMap<>();
	private LinkedHashMap<String, Requirement> requirements = new LinkedHashMap<>();
	private ParsedModel parsedModel;
	private LinkedHashMap<String, String> modelNameToId= new LinkedHashMap<>();
	private LinkedHashMap<String, String> idToModelName= new LinkedHashMap<>();
	private ArrayList<Feature> releaseRootFeatures = new ArrayList<>();
	


	public List<String> getInconsistencies() {
		List<String> problems = new LinkedList<String>();
		problems.addAll(getIllegalDependencyReferences());
		problems.addAll(getIllegalReleaseAssignments());
		problems.addAll(getIllegalReleaseIds());
		return problems;	
	}

	public List<String> generateParsedModel() {

		List<String> problems = getInconsistencies();
		if (!problems.isEmpty())
		{
			problems.add(0, "Cannot process release plan. Errors in the request exist");
			return problems;
		}

		parsedModel = new ParsedModel();

		String modelName = makeModelName();
		if ((modelName == null) || modelName.isEmpty()) {
			problems.add("Project does not have identifying information. All Id, version, and name are empty");
			return problems;
		}
		parsedModel.setModelName(modelName);

		Feature rootFeature = new Feature(modelName);
		Component rootComponent = new Component(modelName);
		
		parsedModel.addFeature(rootFeature);
		parsedModel.addComponent(rootComponent);
		
		//generate mandatory subfeatures for each release
		for (Map.Entry<Integer, Release> releaseEntry : releases.entrySet()) {
			Integer releaseNum = releaseEntry.getKey();
			Release release = releaseEntry.getValue();
			String featureName = "Release_" + releaseNum.toString();
			String roleName = "release_" + releaseNum.toString();
			String cardinality = "1";
			Feature releaseRootFeature = new Feature(featureName);
			
			List<String> capacity = new LinkedList<>();
			capacity.add(Integer.toString(release.getMaxCapacity()));
			String attributeValueTypeName = "capacity_" + releaseNum.toString();
			Attribute releaseCapacity = new Attribute(attributeValueTypeName, "maxCapacity", capacity);
			releaseRootFeature.addAttribute(releaseCapacity);
			parsedModel.addFeature(releaseRootFeature);
			parsedModel.addAttribute(releaseCapacity);
			
			SubFeature releaseSubFeature = new SubFeature(featureName, roleName, cardinality);
			rootFeature.addSubFeature(releaseSubFeature);
			releaseRootFeatures.add(releaseRootFeature); //indices will be  releaseNum -1
		}
		
		//requirements, no dependencies yet
		for (Requirement requirement: requirements.values()) {
			
			
			String featureName = "Req_" + requirement.getId();
			Feature requirementFeature = new Feature(featureName, requirement.getId());
			featureName = requirementFeature.getType();
			modelNameToId.put(featureName, requirement.getId());
			idToModelName.put(requirement.getId(), featureName);
			
			List<String> effort = new LinkedList<>();
			effort.add(Integer.toString(requirement.getEffort()));
			String attrName = "effort_" + Integer.toString(requirement.getAssignedRelease());
			String attributeValueTypeName = "effort_" + featureName;

			Attribute reqEffort = new Attribute(attributeValueTypeName, attrName, effort);
			requirementFeature.addAttribute(reqEffort);
			
			parsedModel.addFeature(requirementFeature);
			parsedModel.addAttribute(reqEffort);

			String roleName = "req_" + requirement.getId();
			String cardinality = "0 - 1";
			SubFeature reqSubFeatureInRelease = new SubFeature(featureName, roleName, cardinality);
			Feature releaseFeature = releaseRootFeatures.get(requirement.getAssignedRelease() -1);
			releaseFeature.addSubFeature(reqSubFeatureInRelease);
		}
		
		//add dependencies in separate cycle, now feature names are known
		//need to establish compositional structure
		
		//TODO this seems to be run on runtime! So need to leave Type names as required. 
		//quite 'interesting' design.
		
		//parsedModel.populateFeatureParentRelations();

		//TODO present- stuff, i.e. these constraints does not work for multiple releases.
		//must take into account that required feature is in the same or earlier (which?) release
		//must check Kumbang.core if can express + extend constraint compilation

		for (Requirement requirement: requirements.values()) {
			if (requirement.getRequiresDependencies().isEmpty())
				continue;
			String requiringFeatureName = idToModelName.get(requirement.getId());
			//Feature requiringFeature = parsedModel.getFeature(requiringFeatureName);
			//String requiringRole = requiringFeature.getRoleNameInModel();
			Feature releaseFeature = releaseRootFeatures.get(requirement.getAssignedRelease() -1);
			for (String requiresDependency: requirement.getRequiresDependencies()) {
				String requiredFeatureName = idToModelName.get(requiresDependency);
				//Feature requiredFeature = parsedModel.getFeature(requiredFeatureName);
				//String requiredRole = requiredFeature.getRoleNameInModel();
				Constraint reqCstr = new Constraint(requiringFeatureName, requiredFeatureName, false);
				//Constraint reqCstr = new Constraint(requiringRole, requiredRole, false);
				releaseFeature.addConstraint(reqCstr);
			}
		}
		
		
		return problems;

	}

	public List<String> getIllegalReleaseIds() {
		LinkedList<String> illegals = new LinkedList<>();
		Iterator<Integer> releaseIter = releases.keySet().iterator();
		for (int expected = 1; expected<= releases.size(); expected++ ) {
			if (!releaseIter.hasNext()) {
				illegals.add("Release numbering problem. Expected release = " + expected + ", no plan found.");
				break;
			}
			Integer releaseNum = releaseIter.next();
			if (releaseNum == null)
				illegals.add("Release numbering problem. Expected release = " + expected + ", no (null) release number!.");
			else {
				if (releaseNum.intValue() != expected)
					illegals.add("Release numbering problem. Expected release = " + expected + ", encountered release " + releaseNum + ". Release numbering must be continuous 1, 2, ...");
			}
		}
		return illegals;
	}

	public List<String> getIllegalDependencyReferences() {
		LinkedList<String> illegals = new LinkedList<>(); 
		for (Requirement requirement : requirements.values()) {
			List<String> requiresDependecies = requirement.getRequiresDependencies();
			for (String dep: requiresDependecies) {
				Requirement required = requirements.get(dep);
				if (required == null) {
					StringBuilder sb = new StringBuilder();
					sb.append("Requirement '" );
					sb.append(requirement.getId());
					sb.append("' has requiresDependency to '");
					sb.append(dep);
					sb.append("'. Cannot resolve");
					illegals.add(sb.toString());
				}
			}
		}
		return illegals;
	}

	public List<String> getIllegalReleaseAssignments() {
		LinkedList<String> illegals = new LinkedList<>(); 
		for (Requirement requirement : requirements.values()) {
			int assignedRelease = requirement.getAssignedRelease();
			Release release = releases.get(Integer.valueOf(assignedRelease));
			if (release == null) {
				StringBuilder sb = new StringBuilder();
				sb.append("Requirement '" );
				sb.append(requirement.getId());
				sb.append("' is assigned to release '");
				sb.append(assignedRelease);
				sb.append("'. Release does not exist.");
				illegals.add(sb.toString());
			}
		}
		return illegals;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Release getRelease(Integer release) {
		return releases.get(release);
	}

	public Collection<Release> getReleases() {
		return releases.values();
	}

	public Collection<Requirement> getRequirements() {
		return requirements.values();
	}

	public Release addRelease(Release release) {
		return releases.put(release.getId(), release);
	}

	public Requirement getRequirement(String requirement) {
		return requirements.get(requirement);
	}

	public Requirement addRequirement (Requirement requirement) {
		return requirements.put(requirement.getId(), requirement);
	}

	//TODO when stabilized so that ID will surely be included, remove Project.name from model name generation
	private String makeModelName() {
		StringBuilder sb = new StringBuilder();
		if (project.getId() != null) { 
			sb.append(project.getId());
			sb.append("_");
		}
//		OpenReqJSON contains no info about the version
//		if (project.getVersion() != null) { 
//			sb.append(project.getVersion());
//		}

		if (sb.length() == 0 )
			if (project.getName() != null) 
				sb.append(project.getName());
		return sb.toString();
	}

	public ParsedModel getParsedModel() {
		return parsedModel;
	}

	public List<CalculationConstraint> getEffortCalculationConstraints() {
		LinkedList<CalculationConstraint> efforCstrs = new LinkedList<>();
		for (Release rel : releases.values()) {
			CalculationConstraint releaseEfforCstr = new CalculationConstraint();
			releaseEfforCstr.setOperator("<=");
			releaseEfforCstr.setAttName("effort_" + rel.getId());
			releaseEfforCstr.setValue(Integer.toString(rel.getMaxCapacity()));
			efforCstrs.add(releaseEfforCstr);
		}
		return efforCstrs;
	}
	

}
