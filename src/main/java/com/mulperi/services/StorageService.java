package com.mulperi.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mulperi.models.database.Hierarchy;
import com.mulperi.models.database.StoredRequirement;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.repositories.StoredRequirementRepository;

@Service
public class StorageService {

	@Autowired
	private StoredRequirementRepository storedRequirementRepository;
	
	public Collection<StoredRequirement> storeRequirementsToDatabase(String modelName, List<Requirement> requirements) {
		
		Collection<StoredRequirement> storedReqs = new ArrayList<StoredRequirement>();
		
		for(Hierarchy req : requirements) {
			StoredRequirement storedReq = new StoredRequirement();
			storedReq.setModelName(modelName);
			storedReq.setParentId(req.getParentId());
			storedReq.setRequirementId(req.getRequirementId());
			storedReq.setRequirementName(req.getRequirementName());
			storedReqs.add(storedReq);
		}
		
		this.storedRequirementRepository.save(storedReqs);
		
		return storedReqs;
	}
	
	public String findPath(StoredRequirement requirement) {
		String path = "";
		
		StoredRequirement currentReq = requirement;
		
		do {
			path = "." + currentReq.getRequirementId() + path;
			currentReq = storedRequirementRepository.findByModelNameAndRequirementId(currentReq.getModelName(), currentReq.getParentId());
		} while (currentReq != null);
		
		path = "root" + path;
		return path;
	}
}
