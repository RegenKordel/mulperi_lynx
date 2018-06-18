package eu.openreq.mulperi.services;

import java.util.List;
import org.json.JSONException;
import com.google.gson.Gson;
import eu.openreq.mulperi.models.release.Dependency;
import eu.openreq.mulperi.models.release.InputExtractor;
import eu.openreq.mulperi.models.release.Project;
import eu.openreq.mulperi.models.release.Release;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.release.Requirement;

public class ReleaseJSONParser {
	static Gson gson = new Gson();
	static Project project;
	static List<Requirement> requirements;
	static List<Release> releases;
	static List<Dependency> dependencies;
	static InputExtractor input;

	public static ReleasePlan parseProjectJSON(String jsonString) 
			throws ReleasePlanException, JSONException {
		
			input = gson.fromJson(jsonString, InputExtractor.class);
			project = input.getProject();
			requirements = input.getRequirements();
			releases = input.getReleases();
			dependencies = input.getDependencies();
			
			// Here we do some initializing and setting up.
			addAssignedReleasesToRequirements();
			if (dependencies != null) {
				addRequiredDependenciesForRequirements();
			}
			
			// Here we start creating the ReleasePlan
			ReleasePlan releasePlan = new ReleasePlan();
			releasePlan.setProject(project);
			for (Requirement requirement : requirements) {
				Requirement old = 
						releasePlan.addRequirement(requirement);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Requirement with ID: " + old.getId());
			}
			for (Release release: releases) {
				Release old =
						releasePlan.addRelease(release);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Release with ID: " + old.getId());
			}
			
			return releasePlan;
	}
	
	public static void addAssignedReleasesToRequirements() {
		for (Release rel: releases) {
			List<String> reqIds = rel.getRequirements();
			for (String reqId: reqIds) {
				input.findRequirementById(reqId).setAssignedRelease(rel.getId());
				input.findRequirementById(reqId).setRequiresDependencies();
			}
		}
	}

	public static void addRequiredDependenciesForRequirements() {
		for (Dependency dep: dependencies) {
			String from_id = dep.getFrom();
			String to_id = dep.getTo();
			Requirement r = input.findRequirementById(from_id);
			r.addRequiresDependency(to_id);
		}
	}
	
}
