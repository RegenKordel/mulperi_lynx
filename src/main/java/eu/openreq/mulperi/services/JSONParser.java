package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.maven.artifact.versioning.ComparableVersion;
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
	public static List<Project> projects;
	public static List<Requirement> requirements;
	public static List<Requirement> dependent_requirements;
	public static List<Release> releases;
	public static List<Dependency> dependencies;
	public static InputExtractor input;
	
	public static void parseToOpenReqObjects(String jsonString) 
			throws JSONException {
		
			input = gson.fromJson(jsonString, InputExtractor.class);
			project = input.getProject();
			projects = input.getProjects();
			requirement = input.getRequirement();
			requirements = input.getRequirements();
			dependencies = input.getDependencies();
			dependent_requirements = input.getDependentRequirements();
			if (input.getReleases()!=null) {
				releases = input.getReleases(); 
			} else {
				releases = parseReleaseVersionsFromReqParts();
			}
	}
	
	public static List<Release> parseReleaseVersionsFromReqParts() {
		HashMap<ComparableVersion, List<String>> releaseMap = new HashMap<ComparableVersion, List<String>>();
		
		for (Requirement req : requirements) {
			if (req.getRequirementParts()==null) {
				continue;
			}
			for (RequirementPart reqPart : req.getRequirementParts()) {
				if (reqPart.getName().equals("Versions")) {
					
					List<String> versions = parseVersions(reqPart.getText());
					for (String version : versions) {	
						ComparableVersion compVersion = new ComparableVersion(version);
						if (releaseMap.containsKey(compVersion)) {
							releaseMap.get(compVersion).add(req.getId());
						} else {
							List<String> reqs = new ArrayList<String>();
							reqs.add(req.getId());
							releaseMap.put(compVersion, reqs);
						}
					}
					
				}
			}
		}
		
		List<ComparableVersion> keys = new ArrayList<ComparableVersion>(releaseMap.keySet());
		
		Collections.sort(keys);
		
		List<Release> releases = new ArrayList<Release>();
		
		//the first element in keys is an empty string (due to text splits)
		//using the empty string key returns an array of all requirements (not used)
		for (int i = 1; i < keys.size(); i++) {
			Release rel = new Release();
			rel.setId(i);
			rel.setCapacity(0);
			rel.setRequirements(releaseMap.get(keys.get(i)));
			releases.add(rel);
		}
		
		return releases;
		
	}
	
	private static List<String> parseVersions(String text) {
		String delims = "\\[\"|\"\\]|\", \"";
		
		String[] versions = text.split(delims);
		
		return Arrays.asList(versions);
	}

	public static String parseToJson(ElementModel model) {
		return gson.toJson(model);
	}
}
