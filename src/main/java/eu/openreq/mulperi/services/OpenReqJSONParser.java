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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.openreq.mulperi.models.json.*;
import fi.helsinki.ese.murmeli.ElementModel;

public class OpenReqJSONParser {
	static Gson gson = new Gson();
	private Project project;
	private Requirement requirement;
	private List<Project> projects;
	private List<Requirement> requirements;
	private List<Requirement> dependent_requirements;
	private List<Release> releases;
	private List<Dependency> dependencies;
	private List<Requirement> filteredRequirements;
	private List<Dependency> filteredDependencies;
	private List<Release> filteredReleases;
	
	public OpenReqJSONParser(String jsonString) 
			throws JSONException {
			InputExtractor input = gson.fromJson(jsonString, InputExtractor.class);
			this.project = input.getProject();
			this.projects = input.getProjects();
			this.requirement = input.getRequirement();
			this.requirements = input.getRequirements();
			this.dependencies = input.getDependencies();
			this.dependent_requirements = input.getDependentRequirements();
			if (input.getReleases()!=null) {
				this.releases = input.getReleases(); 
			} else {
				this.releases = parseReleaseVersionsFromReqParts();
			}
	}
	
	/**
	 * Parse and sort the release versions from requirement parts using ComparableVersion
	 * 
	 * @return
	 */
	public List<Release> parseReleaseVersionsFromReqParts() {
		HashMap<ComparableVersion, List<String>> releaseMap = new HashMap<ComparableVersion, List<String>>();
		List<String> noFixVerReqs = new ArrayList<String>();
		
		for (Requirement req : this.requirements) {
			if (req.getRequirementParts()==null) {
				continue;
			}
			for (RequirementPart reqPart : req.getRequirementParts()) {
				if (reqPart.getName().equals("FixVersion") && reqPart.getText()!=null) {			
					List<String> versions = parseVersions(reqPart.getText());
					for (String version : versions) {	
						if (!version.equals("")) {
							if (isFixVersion(version)) {
								ComparableVersion compVersion = new ComparableVersion(version);
								List<String> releaseReqs = new ArrayList<String>();
								if (releaseMap.containsKey(compVersion)) {
									releaseReqs = releaseMap.get(compVersion);
								} 
								releaseReqs.add(req.getId());
								releaseMap.put(compVersion, releaseReqs);
							} else {
								noFixVerReqs.add(req.getId());
							}
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
		
		if (!noFixVerReqs.isEmpty()) {
			Release rel = new Release();
			rel.setId("No FixVersion");
			rel.setCapacity(0);
			rel.setRequirements(noFixVerReqs);
			releases.add(rel);
		}
		return releases;
		
	}
	
	private static boolean isFixVersion(String version) {
		version = version.toLowerCase();	
		return !(version.equals("no fixversion") || 
				version.equals("some future version") || 
				version.equals("none"));
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
	 * @throws JSONException 
	 */
	public JsonArray combineDuplicates() throws JSONException {
		Stack<Dependency> dupsLeft = new Stack<Dependency>();
		List<Dependency> nonDups = new ArrayList<Dependency>();
		Map<String, Requirement> reqMap = new HashMap<String, Requirement>();
		
		List<Requirement> newRequirements = new ArrayList<Requirement>();
		List<Dependency> newDependencies = new ArrayList<Dependency>();
		List<Release> newReleases = new ArrayList<Release>();
		
		JsonArray changes = new JsonArray();
		
		for (Dependency dep : this.dependencies) {
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
				
				ComparableVersion fromVersion = new ComparableVersion(getFixVerFromReqParts(fromReq.getRequirementParts()));
				ComparableVersion toVersion = new ComparableVersion(getFixVerFromReqParts(toReq.getRequirementParts()));
				
				JsonObject newChange = new JsonObject();
				newChange.addProperty("from", fromReq.getId() + " " + fromVersion);
				newChange.addProperty("to", toReq.getId() + " " + toVersion);
				
				if (fromVersion.compareTo(toVersion)==0) {
					newChange.addProperty("relation", "equal"); 
					reqMap.put(fromReq.getId(), toReq);
					reqMap.put(toReq.getId(), toReq);	
				} else if (fromVersion.compareTo(toVersion)==1) {
					newChange.addProperty("relation", "higher");
					reqMap.put(fromReq.getId(), fromReq);
					reqMap.put(toReq.getId(), fromReq);	
				} else {
					newChange.addProperty("relation", "lower");
					reqMap.put(fromReq.getId(), toReq);
					reqMap.put(toReq.getId(), toReq);	
				}
				changes.add(newChange);
	
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
	
	private static String getFixVerFromReqParts(List<RequirementPart> requirementParts) {
		for (RequirementPart reqPart : requirementParts) {
			if (reqPart.getName().equals("FixVersion") && reqPart.getText()!=null) {
				String version = reqPart.getText();
				if (isFixVersion(version)) {
					return version;
				}
			}
		}
		return "No FixVersion";
	}

	public Project getProject() {
		return project;
	}

	public Requirement getRequirement() {
		return requirement;
	}

	public List<Project> getProjects() {
		return projects;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public List<Requirement> getDependent_requirements() {
		return dependent_requirements;
	}

	public List<Release> getReleases() {
		return releases;
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

	public List<Requirement> getFilteredRequirements() {
		return filteredRequirements;
	}

	public List<Dependency> getFilteredDependencies() {
		return filteredDependencies;
	}

	public List<Release> getFilteredReleases() {
		return filteredReleases;
	}
	
}
