package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
	public static List<Requirement> filteredRequirements;
	public static List<Dependency> filteredDependencies;
	public static List<Release> filteredReleases;
	
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
	
	/**
	 * Parse and sort the release versions from requirement parts using ComparableVersion
	 * 
	 * @return
	 */
	public static List<Release> parseReleaseVersionsFromReqParts() {
		HashMap<ComparableVersion, List<String>> releaseMap = new HashMap<ComparableVersion, List<String>>();
		
		for (Requirement req : requirements) {
			if (req.getRequirementParts()==null) {
				continue;
			}
			for (RequirementPart reqPart : req.getRequirementParts()) {
				if (reqPart.getName().equals("FixVersion") && reqPart.getText()!=null) {			
					List<String> versions = parseVersions(reqPart.getText());;
					for (String version : versions) {	
						if (!version.equals("")) {
							ComparableVersion compVersion = null;
							if (version.equals("No Fixversion") || version.equals("No FixVersion") || 
									version.equals("some future version") || version.equals("none")) {
								compVersion = new ComparableVersion(Integer.MAX_VALUE + ".FINAL");
							} else {
								compVersion = new ComparableVersion(version);
							}
							List<String> reqs = new ArrayList<String>();
							if (releaseMap.containsKey(compVersion)) {
								reqs = releaseMap.get(compVersion);
							} 
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

		for (int i = 0; i < keys.size(); i++) {
			Release rel = new Release();
			rel.setId(keys.get(i).toString());
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
	
	/**
	 * Combine duplicate requirements before sending them to Caas
	 * @return
	 */
	public static String combineDuplicates() {
		Stack<Dependency> dupsLeft = new Stack<Dependency>();
		List<Dependency> nonDups = new ArrayList<Dependency>();
		Map<String, Requirement> reqMap = new HashMap<String, Requirement>();
		
		List<Requirement> newRequirements = new ArrayList<Requirement>();
		List<Dependency> newDependencies = new ArrayList<Dependency>();
		List<Release> newReleases = new ArrayList<Release>();
		
		String changes = "";
		
		for (Dependency dep : dependencies) {
			if (dep.getDependency_type()==Dependency_type.DUPLICATES) {
				dupsLeft.add(dep);			
			} else {
				nonDups.add(dep);
			}
		}	
		
		if (!dupsLeft.isEmpty()) {
			
			for (Requirement req : requirements) {
				reqMap.put(req.getId(), req);
			}
			
			while (!dupsLeft.isEmpty()) {
				Dependency dup = dupsLeft.pop();		
				
				Requirement fromReq = reqMap.get(dup.getFromid());
				Requirement toReq = reqMap.get(dup.getToid());
				
				ComparableVersion fromVersion = new ComparableVersion("No FixVersion");
				ComparableVersion toVersion = new ComparableVersion("No FixVersion");
				
				for (RequirementPart reqPart : fromReq.getRequirementParts()) {
					if (reqPart.getName().equals("FixVersion") && reqPart.getText()!=null) {
						String version = reqPart.getText();
						if (version.equals("some future version") || version.equals("none") || version.equals("No Fixversion")) {
							version = "No FixVersion";
						}
						fromVersion = new ComparableVersion(version);
					}
				}
				
				for (RequirementPart reqPart : toReq.getRequirementParts()) {
					if (reqPart.getName().equals("FixVersion") && reqPart.getText()!=null) {
						String version = reqPart.getText();
						if (version.equals("some future version") || version.equals("none") || version.equals("No Fixversion")) {
							version = "No FixVersion";
						}
						toVersion = new ComparableVersion(version);
					}
				}
				
				if (fromVersion.compareTo(toVersion)==0) {
					changes += toReq.getId() + " duplicates " + fromReq.getId() + "\n"; 
					reqMap.put(fromReq.getId(), toReq);
					reqMap.put(toReq.getId(), toReq);	
				} else if (fromVersion.compareTo(toVersion)==1) {
					changes += fromReq.getId() + " " + fromVersion + " duplicates yet has higher version than " 
							+ toReq.getId() + " " + toVersion + "\n";
					reqMap.put(fromReq.getId(), fromReq);
					reqMap.put(toReq.getId(), fromReq);	
				} else {
					changes += toReq.getId() + " " + toVersion + " duplicates yet has higher version than " 
							+ fromReq.getId() + " " + fromVersion + "\n";
					reqMap.put(fromReq.getId(), toReq);
					reqMap.put(toReq.getId(), toReq);	
				}
	
			}
			
			for (String key : reqMap.keySet()) {
				Requirement req = reqMap.get(key);
				if (!newRequirements.contains(req)) {
					newRequirements.add(req);
				}		
			}
			
			for (Dependency dep : nonDups) {
				Requirement fromReq = reqMap.get(dep.getFromid());
				if (fromReq!=null) {
					dep.setFromid(fromReq.getId());
				}
				Requirement toReq = reqMap.get(dep.getToid());
				if (toReq!=null) {
					dep.setToid(toReq.getId());
				}	
				if (!newDependencies.contains(dep)) {
					newDependencies.add(dep);
				}
			}
			
			List<String> usedIds = new ArrayList<String>();
			for (Release rel : releases) {
				List<String> newIds = new ArrayList<String>();
				for (String reqId : rel.getRequirements()) {
					String newId = reqMap.get(reqId).getId();
					if (!usedIds.contains(newId)) {
						newIds.add(newId);
						usedIds.add(newId);
					}				
				}
				if (!newIds.isEmpty()) {
					rel.setRequirements(newIds);
					newReleases.add(rel);
				}
			}
			filteredRequirements = newRequirements;
			filteredDependencies = newDependencies;
			filteredReleases = newReleases;	
		} else {
			filteredRequirements = requirements;
			filteredDependencies = dependencies;
			filteredReleases = releases;
		}

		return changes;
	}
}
