package eu.openreq.mulperi.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import eu.openreq.mulperi.models.json.Dependency;
import eu.openreq.mulperi.models.json.Project;
import eu.openreq.mulperi.models.json.Release;
import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.models.json.Requirement_type;
import eu.openreq.mulperi.services.InputChecker;
import eu.openreq.mulperi.services.JSONParser;
import eu.openreq.mulperi.services.MurmeliModelGenerator;
import eu.openreq.mulperi.services.OpenReqConverter;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.TransitiveClosure;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("models")
public class MulperiController {
	
	private Gson gson = new Gson();

	@Value("${mulperi.caasAddress}")
	private String caasAddress; 
	
	@Autowired
	RestTemplate rt;
	
	/**
	 * Import a model in JSON format
	 * @param requirements
	 * @return
	 * @throws JSONException 
	 * @throws ReleasePlanException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Import OpenReq JSON model to Caas",
			notes = "Import a model to Caas in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@PostMapping(value = "requirementsToChoco")
	public ResponseEntity<?> requirementsToChoco(@RequestBody String requirements) throws JSONException, IOException, ParserConfigurationException {
		
		//System.out.println("Received requirements from Milla " + requirements);
		JSONParser.parseToOpenReqObjects(requirements);
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model;
		if(JSONParser.projects!=null) {
			String projectId = JSONParser.projects.get(0).getId();
			model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, projectId);
		}
		else{
			model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, JSONParser.requirements.get(0).getId());
		}
		
		try {
			//return new ResponseEntity<>("Requirements received: " + requirements, HttpStatus.ACCEPTED);
			return this.sendModelToKeljuCaas(JSONParser.parseToJson(model));
		}
		catch (Exception e) {
			return new ResponseEntity<>("Cannot send the model to KeljuCaas", HttpStatus.EXPECTATION_FAILED); //change to something else?
		}
	}
	
	
	/**
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return JSON response
	 * 		{ 
	 * 			"response": {
	 * 				"consistent": false
	 * 			}
	 * 		}
	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent",
			notes = "Send model to Caas to check whether a release plan is consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Check of inconsistency returns JSON {\"response\": {\"consistent\": false}}")}) 
	@PostMapping(value = "/projects/uploadDataAndCheckForConsistency")
	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
		String completeAddress = caasAddress + "/uploadDataAndCheckForConsistency";	
		return convertToMurmeliAndPostToCaas(jsonString, completeAddress, false);		
	}
	
	

	/**
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return JSON response
	 * 		{ 
	 * 			"response": {
	 * 				"consistent": false, 
	 * 				"diagnosis": [
	 * 					[
	 * 						{
	 * 							"requirement": (requirementID)
	 * 						}
	 * 					]
	 * 				]
	 * 			}
	 * 		}

	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent and do diagnosis",
			notes = "Check whether a release plan is consistent. Provide diagnosis if it is not consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns JSON {\"response\": {\"consistent\": false, \"diagnosis\": [[{\"requirement\": (requirementID)}]]}}")}) 
	@PostMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis")
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
		String completeAddress = caasAddress + "/uploadDataCheckForConsistencyAndDoDiagnosis";	
		return convertToMurmeliAndPostToCaas(jsonString, completeAddress, false);
	}
	
	/**
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return JSON response
	 * 		{ 
	 * 			"response": {
	 * 				"consistent": false, 
	 * 				"diagnosis": [
	 * 					[
	 * 						{
	 * 							"requirement": (requirementID)
	 * 						}
	 * 					]
	 * 				]
	 * 			}
	 * 		}

	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent and do diagnosis v2",
			notes = "Check whether a release plan is consistent. Provide diagnosis if it is not consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns JSON {\"response\": {\"consistent\": false, \"diagnosis\": [[{\"requirement\": (requirementID)}]]}}")}) 
	@PostMapping(value = "/projects/consistencyCheckAndDiagnosis")
	public ResponseEntity<?> consistencyCheckAndDiagnosis(@RequestBody String jsonString,
			@RequestParam(required = false) Boolean analysisOnly) throws JSONException, IOException, ParserConfigurationException {
		if (analysisOnly == null) 
			analysisOnly = Boolean.FALSE;
		String completeAddress = caasAddress + "/consistencyCheckAndDiagnosis";
		if (analysisOnly== Boolean.FALSE) {
			completeAddress += "?analysisOnly=" + Boolean.FALSE;
		}
		return convertToMurmeliAndPostToCaas(jsonString, completeAddress, false);	
	}
	

		 //System.out.println("Requirements received from Mulperi");
		 //System.out.println(json);
	
	/**
	 * Converts the given OpenReq JSON to Murmeli along with various checks, then sends it to Keljucaas
	 * 
	 * @param jsonString
	 * @param completeAddress
	 * @param duplicatesInResponse
	 * @return
	 */
	public ResponseEntity<?> convertToMurmeliAndPostToCaas(String jsonString, String completeAddress, boolean duplicatesInResponse) throws JSONException {

		JSONParser.parseToOpenReqObjects(jsonString);
		
		List<Requirement> requirements = JSONParser.requirements;
		List<Dependency> dependencies = JSONParser.dependencies;
		
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
		if (JSONParser.project != null) {
			project = JSONParser.project;
			id = project.getId();
		}
		
		List<Release> releases = new ArrayList<Release>();
		if (JSONParser.releases != null) {
			releases = JSONParser.releases;
		}
		
		
		//Input checker
		//---------------------------------------------------------------
				
		InputChecker checker = new InputChecker();
		String result = checker.checkInput(project, requirements,  dependencies, releases);
		
		if (!result.equals("OK")) {
			return new ResponseEntity<>("Mulperi error: " + result, HttpStatus.BAD_REQUEST); 
		}
			
		//---------------------------------------------------------------
	
		//Combine requirements with dependency "duplicates"
		//---------------------------------------------------------------
		
		String changes = JSONParser.combineDuplicates();
		requirements = JSONParser.filteredRequirements;
		dependencies = JSONParser.filteredDependencies;
		releases = JSONParser.filteredReleases;
		
		//---------------------------------------------------------------
		
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model = generator.initializeElementModel(requirements, new ArrayList<String>(), dependencies, releases, id);
		
		Gson gson = new Gson();
		String murmeli = gson.toJson(model);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		if (duplicatesInResponse) {
			return new ResponseEntity<>(changes + "\nCaas response:\n\n" + response.getBody(), response.getStatusCode());
		}
		return new ResponseEntity<>(response.getBody(), response.getStatusCode());
	}
	
//	@ApiOperation(value = "Post ElementModel to KeljuCaaS",
//			notes = "The model is saved in KeljuCaaS",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 200, message = "Success, returns message model saved"),
//			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
//			@ApiResponse(code = 409, message = "Returns what?")}) 
//	@PostMapping(value = "/sendModelToKeljuCaas")
	public ResponseEntity<String> sendModelToKeljuCaas(String jsonString) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);

		HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);

		try {
			return rt.postForEntity(caasAddress + "/importModelAndUpdateGraph", entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
//	@ApiOperation(value = "Post ElementModel to KeljuCaaS",
//			notes = "The model is saved in KeljuCaaS",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 200, message = "Success, returns message model saved"),
//			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
//			@ApiResponse(code = 409, message = "Returns what?")}) 
//	@RequestMapping(value = "/postModelToCaas", method = RequestMethod.POST)
//	public ResponseEntity<?> postModelToCaas(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
//
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);
//
//		String actualPath = "/importModel"; 
//
//		String completeAddress = caasAddress + actualPath;
//		
//		MurmeliModelGenerator generator = new MurmeliModelGenerator();
//		
//		System.out.println(jsonString);
//		
//		JSONParser.parseToOpenReqObjects(jsonString);
//		
//		for (Requirement req : JSONParser.requirements) {
//			if (req.getRequirement_type() == null) {
//				req.setRequirement_type(Requirement_type.REQUIREMENT);
//			}
//		} 
//		
//		// TODO Should check if the element is in the model
//		ElementModel model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, JSONParser.project.getId());
//
//		String murmeli = gson.toJson(model);
//		
//		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
//		ResponseEntity<?> response = null;
//		try {
//			response = rt.postForEntity(completeAddress, entity, String.class);
//		} catch (HttpClientErrorException e) {
//			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
//		}
//
//		return response;
//	}
	
	
	@ApiOperation(value = "Get the transitive closure of a requirement",
			notes = "Returns the transitive closure of a given requirement up to the depth of 5",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON model"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Conflict")}) 
	@PostMapping(value = "/findTransitiveClosureOfRequirement")
	public ResponseEntity<?> findTransitiveClosureOfRequirement(@RequestBody List<String> requirementId, 
			@RequestParam(required = false) Integer layerCount) throws JSONException, IOException, ParserConfigurationException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = caasAddress + "/findTransitiveClosureOfElement";
		
		if (layerCount!=null) {
			completeAddress += "?layerCount=" + layerCount;
		}
		
		String response = null;
		try {
			List<Requirement> requirements = new ArrayList<Requirement>();
			List<Dependency> dependencies = new ArrayList<Dependency>();
			Map<Integer, List<String>> layers = new HashMap<Integer, List<String>>();
			for (String reqId : requirementId) {
				response = rt.postForObject(completeAddress, reqId, String.class);
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

			String requirementsAsString = gson.toJson(requirements);
			String dependenciesAsString = gson.toJson(dependencies);
			String layersAsString = gson.toJson(layers);
			
			String json = "{\"requirements\":" + requirementsAsString + ",\"dependencies\":" + dependenciesAsString + ",\"layers\":" + layersAsString + "}";
			return new ResponseEntity<>(json, HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}

	}

	@ApiOperation(value = "Get the transitive closure of a requirement, then check for consistency",
			notes = "Solves whether the transitive closure of the requirement is consistent",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON model"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Conflict")}) 
	@PostMapping(value = "/consistencyCheckForTransitiveClosure")
	public ResponseEntity<?> consistencyCheckForTransitiveClosure(@RequestBody List<String> requirementId, @RequestParam
			(required = false) Integer layerCount) throws JSONException, IOException, ParserConfigurationException {
		if (layerCount==null) {
			layerCount = 5;
		}
		ResponseEntity<?> transitiveClosure = findTransitiveClosureOfRequirement(requirementId, layerCount);
		String completeAddress = caasAddress + "/consistencyCheckAndDiagnosis";
		return convertToMurmeliAndPostToCaas(transitiveClosure.getBody().toString(), completeAddress, true);	
	}

}
