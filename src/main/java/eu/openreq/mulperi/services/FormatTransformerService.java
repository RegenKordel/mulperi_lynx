package eu.openreq.mulperi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import eu.openreq.mulperi.models.MurmeliAndDuplicates;
import eu.openreq.mulperi.models.json.Dependency;
import eu.openreq.mulperi.models.json.Project;
import eu.openreq.mulperi.models.json.Release;
import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.models.json.Requirement_type;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.TransitiveClosure;

@Service
public class FormatTransformerService {
	
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public JsonObject murmeliClosureToOpenReqJson(List<String> responses) {
		Set<Requirement> requirements = new HashSet<Requirement>();
		Set<Dependency> dependencies = new HashSet<Dependency>();
		Map<Integer, List<String>> layers = new HashMap<Integer, List<String>>();
		
		for (String response : responses) {
			TransitiveClosure closure = gson.fromJson(response, TransitiveClosure.class);
			
			OpenReqConverter converter = new OpenReqConverter(closure.getModel());
			List<Requirement> convertedRequirements = converter.getRequirements();
			
			//Add Unknown requirement part if empty (hack?)
			convertedRequirements = converter.addUnknownIfEmpty(convertedRequirements);
			
			requirements.addAll(convertedRequirements);
			
			dependencies.addAll(converter.getDependencies());
			
			Map<Integer, List<String>> closureLayers = closure.getLayers();
			for (Integer i : closureLayers.keySet()) {
				if (layers.containsKey(i)) {
					List<String> combinedLayers = layers.get(i);
					combinedLayers.addAll(closureLayers.get(i));
					layers.put(i, combinedLayers);
				} else {
					layers.put(i, closureLayers.get(i));
				}
			}
		}
		
		JsonObject object = new JsonObject();

		object.add("requirements", gson.toJsonTree(requirements));
		object.add("dependencies", gson.toJsonTree(dependencies));
		object.add("layers", gson.toJsonTree(layers));
		
		return object;
	}
	
	/**
	 * Converts the given OpenReq JSON to Murmeli along with various checks, then sends it to Keljucaas
	 * 
	 * @param jsonString
	 * @param completeAddress
	 * @param duplicatesInResponse
	 * @param timeOut
	 * @return
	 * @throws JSONException
	 */
	public MurmeliAndDuplicates openReqJsonToMurmeli(String jsonString, 
			boolean duplicatesInResponse) throws JSONException {

		OpenReqJSONParser parser = new OpenReqJSONParser(jsonString);
		
		List<Requirement> requirements = parser.getRequirements();
		List<Dependency> dependencies = parser.getDependencies();
		
		for (Requirement req : requirements) {
			if (req.getRequirement_type() == null) {
				req.setRequirement_type(Requirement_type.REQUIREMENT);
			}
		} 
		
		Project project = null;
		String id = null;
		 
		if (requirements.size()>0) {
			id = requirements.get(0).getName();
		}
		if (parser.getProject() != null) {
			project = parser.getProject();
			id = project.getId();
		}
		
		List<Release> releases = new ArrayList<Release>();
		if (parser.getReleases() != null) {
			releases = parser.getReleases();
		}
		
		
		//Input checker
		//---------------------------------------------------------------
				
		InputChecker checker = new InputChecker();
		String result = checker.checkInput(project, requirements,  dependencies, releases);
		
		if (!result.equals("OK")) {
			return null;
		}
			
		//---------------------------------------------------------------
	
		//Combine requirements with dependency "duplicates"
		//---------------------------------------------------------------
		
		JsonArray changes = null;
		
		changes = parser.combineDuplicates();
		
		requirements = parser.getFilteredRequirements();
		dependencies = parser.getFilteredDependencies();
		releases = parser.getFilteredReleases();
		
		//---------------------------------------------------------------
		
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel murmeliModel = generator.initializeElementModel(requirements, new ArrayList<String>(), dependencies, releases, id);

		MurmeliAndDuplicates response = new MurmeliAndDuplicates(gson.toJson(murmeliModel), changes);
		
		return response;

	}
}