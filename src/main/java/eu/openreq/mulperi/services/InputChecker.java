package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.List;

import eu.openreq.mulperi.models.json.Dependency;
import eu.openreq.mulperi.models.json.Project;
import eu.openreq.mulperi.models.json.Release;
import eu.openreq.mulperi.models.json.Requirement;

public class InputChecker {
	
	public InputChecker() {
		
	}
	
	public String checkInput(Project project, List<Requirement> requirements, 
			List<Dependency> dependencies, List<Release> releases) {		
			
		List<String> reqIds = new ArrayList<String>();
		for (Requirement req : requirements) {
			reqIds.add(req.getId());
		}
	
		String errorMessage = "";
		
		if (requirements.size() == 0) {
			return "No requirements received!";
		}
		
		if (releases.size() == 0) {
			errorMessage += "No release versions included!\n";
		}	
		
		if (!noNegativeEffortRequirements(requirements)) {
			errorMessage += "Negative effort in some requirement(s)!\n";
		}
		
		if (!noNegativeCapacityReleases(releases)) {
			errorMessage += "Negative capacity in some release(s)!\n";
		}
		
//		if (!noDuplicateDependencies(dependencies)) {
//			errorMessage += "Some duplicate(s) in dependencies!\n";
//		}
		
//		if (!releasesInOrder(releases)) {
//			errorMessage += "Some versions missing between releases!\n";
//		}
		
		
		if (project!=null) {		
			
			List<String> specReqIds = new ArrayList<String>();
			
			for (String specReqId : project.getSpecifiedRequirements()) {
				specReqIds.add(specReqId);
			}
			
			if (specReqIds.size() == 0) {
				errorMessage += "No project requirements specified!\n";
			}
			
//			if (!allSpecifiedRequirementsIncluded(specReqIds, reqIds)) {
//				errorMessage += "Some specified requirement(s) not included in requirements!\n";
//			}
			
			if (!onlySpecifiedRequirements(reqIds, specReqIds)) {
				errorMessage += "Some requirement(s) not included in specified requirements!\n";
			}
			
//			if (!allSpecifiedRequirementsInReleases(specReqIds, releases)) {
//				errorMessage += "Some specified requirement(s) not included in releases!\n";
//			}
			
			if (!allReleaseRequirementsIncluded(releases, specReqIds, reqIds)) {
				errorMessage += "Unspecified requirement(s) in releases!\n";
			}
			
			if (!allDependencyRequirementsIncluded(dependencies, specReqIds, reqIds)) {
				errorMessage += "Unspecified requirement(s) in dependencies!\n";
			}
			
			if (!requirementNotInMultipleReleases(releases)) {
				errorMessage += "Some requirement(s) included in multiple releases!\n";
			}
		
		}
		
		if (errorMessage.isEmpty()) {
			return "OK";
		}
		
		return errorMessage;
	}

	/**
	 * Check that there are no negative efforts in requirements
	 * @param requirements
	 * @return
	 */
	public boolean noNegativeEffortRequirements(List<Requirement> requirements) {
		for (Requirement req : requirements) {
			if (req.getEffort()<0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Check that there are no negative capacities in releases
	 * @param releases
	 * @return
	 */
	public boolean noNegativeCapacityReleases(List<Release> releases) {
		for (Release release : releases) {
			if (release.getCapacity()<0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * All specified requirements must be included as actual requirements
	 * @param specReqIds
	 * @param reqIds
	 * @return
	 */
	public boolean allSpecifiedRequirementsIncluded(List<String> specReqIds, List<String> reqIds) {
		for (String specReqId : specReqIds) {
				if (!reqIds.contains(specReqId)) {
					return false;
				}
			}
		return true;
	}
	
	/**
	 * All requirements included must be only those specified
	 * @param reqIds
	 * @param specReqIds
	 * @return
	 */
	public boolean onlySpecifiedRequirements(List<String> reqIds, List<String> specReqIds) {
		for (String reqId : reqIds) {
				if (!specReqIds.contains(reqId)) {
					return false;
				}
			}
		return true;
	}
	
	/**
	 * All specified requirements must be included in some release
	 * @param specReqIds
	 * @param releases
	 * @return
	 */
	public boolean allSpecifiedRequirementsInReleases(List<String> specReqIds, List<Release> releases) {
		for (String specReqId : specReqIds) {
			if (!requirementIdInReleases(specReqId, releases)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * All requirements in releases must be specified in the project
	 * @param releases
	 * @param specReqIds
	 * @return
	 */
	public boolean allReleaseRequirementsIncluded(List<Release> releases, List<String> specReqIds, 
			List<String> reqIds) {
		for (Release release : releases) {
			for (String relId : release.getRequirements()){
				if (!specReqIds.contains(relId) || !reqIds.contains(relId)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Check if the requirement is in releases, zero ID release does not count
	 * @param reqId
	 * @param releases
	 * @return
	 */
	private boolean requirementIdInReleases(String reqId, List<Release> releases) {
		for (Release release : releases) {
			int releaseId = Integer.parseInt(release.getId());
			if (release.getRequirements().contains(reqId) && releaseId!=0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 	The source and destination requirements of dependencies must exist in the project
	 * @param dependencies
	 * @param specReqIds
	 * @return
	 */
	public boolean allDependencyRequirementsIncluded(List<Dependency> dependencies, List<String> specReqIds, 
			List<String> reqIds) {
		for (Dependency dep : dependencies) {
			if (!specReqIds.contains(dep.getFromid()) || !specReqIds.contains(dep.getToid()) || 
				!reqIds.contains(dep.getFromid()) || !reqIds.contains(dep.getToid())) {
				return false;
			}		
		}
	return true;
	}
	
	/**
	 * Check that there are no duplicates in dependencies
	 * @param dependencies
	 * @return
	 */
	public boolean noDuplicateDependencies(List<Dependency> dependencies) {
		List<String> depsChecked = new ArrayList<String>();
		for (Dependency dep : dependencies) {
			if (!depsChecked.contains(dep.getFromid() + " + " + dep.getToid())) {
				depsChecked.add(dep.getFromid() + " + " + dep.getToid());
				//Reverse relationship counts as a duplicate too
				depsChecked.add(dep.getToid() + " + " + dep.getFromid());
			} else {
				return false;
			}
		}
		return true;
	}

//	Probably unused
//	/**
//	 * Check that no versions are skipped between releases. (1, 2, 3, 4) and (1, 4, 3, 2) are valid but not (1, 3, 5, 2)
//	 * @param releases
//	 * @return
//	 */
//	public boolean releasesInOrder(List<Release> releases) {
//		int i = 1;
//		int expectedTotal = 0;
//		int total = 0;
//		
//		for (Release release : releases) {
//			int releaseId = Integer.parseInt(release.getId());
//			if (release.getCapacity()<0) {
//				return false;
//			}	
//			expectedTotal += i;
//			total += releaseId;
//			i++;	
//		}
//		
//		return total == expectedTotal;
//	}
	
	/**
	 * Check that a requirement is not included in multiple releases
	 * @param reqIds
	 * @param releases
	 * @return
	 */
	public boolean requirementNotInMultipleReleases(List<Release> releases) {
		List<String> uniqueReqs = new ArrayList<String>();	
		for (Release rel : releases) {;
			for (String req : rel.getRequirements()) {
				if (uniqueReqs.contains(req)) {
					return false;
				}
				uniqueReqs.add(req);
			}
		}
		return true;
	}
	
	
}



