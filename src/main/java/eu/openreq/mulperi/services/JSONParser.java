package eu.openreq.mulperi.services;

import java.util.List;

import org.json.JSONException;

import com.google.gson.Gson;

import eu.openreq.mulperi.models.json.*;
import fi.helsinki.ese.murmeli.ElementModel;

//import eu.openreq.mulperi.models.release.Dependency;
//import eu.openreq.mulperi.models.release.ReleaseInputExtractor;
//import eu.openreq.mulperi.models.release.Project;
//import eu.openreq.mulperi.models.release.Release;
//import eu.openreq.mulperi.models.release.ReleasePlan;
//import eu.openreq.mulperi.models.release.ReleasePlanException;
//import eu.openreq.mulperi.models.release.Requirement;

public class JSONParser {
	static Gson gson = new Gson();
	public static Project project;
	public static Requirement requirement;
	public static List<Requirement> requirements;
	public static List<Requirement> dependent_requirements;
	public static List<Release> releases;
	public static List<Dependency> dependencies;
	public static InputExtractor input;
	
	public static void parseToOpenReqObjects(String jsonString) 
			throws JSONException {
		
			input = gson.fromJson(jsonString, InputExtractor.class);
			
			project = input.getProject();
			requirement = input.getRequirement();
			requirements = input.getRequirements();
			releases = input.getReleases();
			dependencies = input.getDependencies();
			dependent_requirements = input.getDependentRequirements();
	}
	
	public static String parseToJson(ElementModel model) {
		return gson.toJson(model);
	}
}
