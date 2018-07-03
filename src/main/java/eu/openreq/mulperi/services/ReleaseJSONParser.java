package eu.openreq.mulperi.services;

import eu.openreq.mulperi.models.release.*;
//import eu.openreq.mulperi.models.json.*;

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

	public static ReleasePlan parseProjectJSON(String jsonString) 
			throws ReleasePlanException, JSONException {
		
			JSONParser.parseToOpenReqObjects(jsonString);
//			input = gson.fromJson(jsonString, InputExtractor.class);
//			project = input.getProject();
//			requirements = input.getRequirements();
//			releases = input.getReleases();
//			dependencies = input.getDependencies();
			
			// Here we do some initializing and setting up.
			addAssignedReleasesToRequirements();
			if (JSONParser.dependencies != null) {
				addRequiredDependenciesForRequirements();
			}
			
			// Here we start creating the ReleasePlan
			ReleasePlan releasePlan = new ReleasePlan();
			releasePlan.setProject(JSONParser.project);
			for (Requirement requirement : JSONParser.requirements) {
				Requirement old = 
						releasePlan.addRequirement(requirement);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Requirement with ID: " + old.getId());
			}
			for (Release release: JSONParser.releases) {
				Release old =
						releasePlan.addRelease(release);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Release with ID: " + old.getId());
			}
			
			return releasePlan;
	}
	
	public static void addAssignedReleasesToRequirements() {
		for (Release rel: JSONParser.releases) {
			List<String> reqIds = rel.getRequirements();
			for (String reqId: reqIds) {
				JSONParser.input.findRequirementById(reqId).setAssignedRelease(rel.getId());
				JSONParser.input.findRequirementById(reqId).setRequiresDependencies();
			}
		}
	}

	public static void addRequiredDependenciesForRequirements() {
		for (Dependency dep: JSONParser.dependencies) {
			String from_id = dep.getFrom();
			String to_id = dep.getTo();
			Requirement r = JSONParser.input.findRequirementById(from_id);
			r.addRequiresDependency(to_id);
		}
	}
	
}
