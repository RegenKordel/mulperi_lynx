package eu.openreq.mulperi.services;

import eu.openreq.mulperi.models.release.*;
//import eu.openreq.mulperi.models.json.*;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import com.google.gson.Gson;

public class ReleaseJSONParser {
	static Gson gson = new Gson();
	static Project project;
	static List<Requirement> requirements;
	static List<Release> releases;
	static List<Dependency> dependencies;
	static InputExtractor input;
	//static List<String> allReqIds;

	public static ReleasePlan parseProjectJSON(String jsonString) throws ReleasePlanException, JSONException {

		JSONParser.parseToOpenReqObjects(jsonString);
		// input = gson.fromJson(jsonString, InputExtractor.class);
		// project = input.getProject();
		// requirements = input.getRequirements();
		// releases = input.getReleases();
		// dependencies = input.getDependencies();

		// Here we tell requirements their assigned releases and create dependency
		// lists.
		addAssignedReleasesToRequirements();
		if (JSONParser.dependencies != null) {
			addDependenciesForRequirements();
		}

		// Here we start creating the ReleasePlan
		ReleasePlan releasePlan = new ReleasePlan();
		releasePlan.setProject(JSONParser.project);
		for (Requirement requirement : JSONParser.requirements) {
			Requirement old = releasePlan.addRequirement(requirement);
			if (old != null) {
				throw new ReleasePlanException("Duplicate Requirement with ID: " + old.getId());
			}
		}
		for (Release release : JSONParser.releases) {
			Release old = releasePlan.addRelease(release);
			if (old != null) {
				throw new ReleasePlanException("Duplicate Release with ID: " + old.getId());
			}
		}
		
		//this part is for preventing nullPointerExceptions in case a requirement in the project has not been assigned to any release,
		//in which case it's dependency lists would be null
		for (Requirement requirement : releasePlan.getRequirements()) {
			if(requirement.getExcludesDependencies()==null || requirement.getRequiresDependencies()==null) {
				requirement.setExcludesDependencies();
				requirement.setRequiresDependencies();
			}
		}
		return releasePlan;
	}

	public static void addAssignedReleasesToRequirements() {
	//	allReqIds = new ArrayList<>();
		for (Release release : JSONParser.releases) {
			List<String> reqIds = release.getRequirements();
			for (String reqId : reqIds) {
				JSONParser.input.findRequirementById(reqId).setAssignedRelease(release.getId());
				JSONParser.input.findRequirementById(reqId).setRequiresDependencies();
				JSONParser.input.findRequirementById(reqId).setExcludesDependencies();
			}
		//	allReqIds.addAll(reqIds);
		}
	}

	public static void addDependenciesForRequirements() {
		for (Dependency dependency : JSONParser.dependencies) {
			String from_id = dependency.getFromId();
			String to_id = dependency.getToId();
			Requirement req = JSONParser.input.findRequirementById(from_id);

			switch (dependency.getDependencyType()) {
			case "requires":
				req.addRequiresDependency(to_id);
				break;
			case "excludes":
				req.addExcludesDependency(to_id);
				break;
			}
		}
	}

}
