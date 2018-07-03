package eu.openreq.mulperi.services;

import java.util.List;

import org.json.JSONException;

import com.google.gson.Gson;

//import eu.openreq.mulperi.models.json.*;

import eu.openreq.mulperi.models.release.Dependency;
import eu.openreq.mulperi.models.release.InputExtractor;
import eu.openreq.mulperi.models.release.Project;
import eu.openreq.mulperi.models.release.Release;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.release.Requirement;

public class JSONParser {
	static Gson gson = new Gson();
	static Project project;
	static Requirement requirement;
	static List<Requirement> requirements;
	static List<Requirement> dependent_requirements;
	static List<Release> releases;
	static List<Dependency> dependencies;
	public static InputExtractor input;
	
	// TODO ATM this parses as release objects rather than OpenReq
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
}
