package eu.openreq.mulperi.controllers;

import eu.openreq.mulperi.models.json.*;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.models.selections.Selections;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.FormatTransformerService;
import eu.openreq.mulperi.services.JSONParser;
import eu.openreq.mulperi.services.MurmeliModelGenerator;
import eu.openreq.mulperi.services.OpenReqConverter;
import fi.helsinki.ese.murmeli.AttributeDefinition;
import fi.helsinki.ese.murmeli.ElementModel;
import fi.helsinki.ese.murmeli.TransitiveClosure;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("models")
//@RequestMapping("uh/mulperi/models")
public class MulperiController {
	
	private FormatTransformerService transform = new FormatTransformerService();
	private Gson gson = new Gson();

	@Value("${mulperi.caasAddress}")
	private String caasAddress; 
	
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
			notes = "Import a model in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@RequestMapping(value = "requirementsToChoco", method = RequestMethod.POST)
	public ResponseEntity<?> requirementsToChoco(@RequestBody String requirements) throws ReleasePlanException, JSONException, IOException, ParserConfigurationException {
		
		//System.out.println("Received requirements from Milla " + requirements);
		JSONParser.parseToOpenReqObjects(requirements);
		
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model;
		if(JSONParser.projects!=null) {
			String projectId = JSONParser.projects.get(0).getId();
			model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, projectId);
		}
		else{
			model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, JSONParser.project.getId());
		}
		
		try {
			//return new ResponseEntity<>("Requirements received: " + requirements, HttpStatus.ACCEPTED);
			return this.sendModelToKeljuCaas(JSONParser.parseToJson(model));
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error", HttpStatus.EXPECTATION_FAILED); //change to something else?
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
			notes = "Check whether a release plan is consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Check of inconsistency returns JSON {\"response\": {\"consistent\": false}}")}) 
	@RequestMapping(value = "/projects/uploadDataAndCheckForConsistency", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = caasAddress + "/uploadDataAndCheckForConsistency";
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		
		JSONParser.parseToOpenReqObjects(jsonString);
		
		for (Requirement req : JSONParser.requirements) {
			if (req.getRequirement_type() == null) {
				req.setRequirement_type(Requirement_type.REQUIREMENT);
			}
		} 
		
		ElementModel model = generator.initializeElementModel(JSONParser.requirements, new ArrayList<String>(), JSONParser.dependencies, JSONParser.releases, JSONParser.project.getId());
		
		Gson gson = new Gson();
		String murmeli = gson.toJson(model);
		
		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		return response;
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
	@RequestMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
		Gson gson = new Gson();
		
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = caasAddress + "/uploadDataCheckForConsistencyAndDoDiagnosis";
		
		JSONParser.parseToOpenReqObjects(jsonString);
		
		for (Requirement req : JSONParser.requirements) {
			if (req.getRequirement_type() == null) {
				req.setRequirement_type(Requirement_type.REQUIREMENT);
			}
		}
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model = generator.initializeElementModel(JSONParser.requirements, new ArrayList<String>(), JSONParser.dependencies, JSONParser.releases, JSONParser.project.getId());

		String murmeli = gson.toJson(model);
		
		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}

		return response;
	}

	
	public String generateName(Object object) {
		int hashCode = object.hashCode();
		return "ID" + (hashCode > 0 ? hashCode : "_" + Math.abs(hashCode)); 
		//replace - with _, since Kumbang doesn't like hyphens
	}
	
	/*public String makeConfigurationRequest(Selections selections, String modelName) throws Exception {
		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);

		if(model == null) {
			throw new Exception("Model not found");
		}

		try {
			return transform.selectionsToConfigurationRequest(selections, model);
		} catch (Exception e) {
			throw new Exception("Failed to create configurationRequest (feature typos?): " + e.getMessage());
		}
	}
	*/
	
	@ApiOperation(value = "Post ElementModel to KeljuCaaS",
			notes = "The model is saved in KeljuCaaS",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns message model saved"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Returns what?")}) 
	@RequestMapping(value = "/sendModelToKeljuCaas", method = RequestMethod.POST)
	public ResponseEntity<String> sendModelToKeljuCaas(String jsonString) {
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);

		HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);

		ResponseEntity<String> response = null;

		try {
			response = rt.postForEntity(caasAddress + "importModelAndUpdateGraph", entity, String.class);
			return response;
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@ApiOperation(value = "Post ElementModel to KeljuCaaS",
			notes = "The model is saved in KeljuCaaS",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns message model saved"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Returns what?")}) 
	@RequestMapping(value = "/postModelToCaas", method = RequestMethod.POST)
	public ResponseEntity<?> postModelToCaas(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String actualPath = "/importModel"; 

		String completeAddress = caasAddress + actualPath;
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		
		JSONParser.parseToOpenReqObjects(jsonString);
		
		for (Requirement req : JSONParser.requirements) {
			if (req.getRequirement_type() == null) {
				req.setRequirement_type(Requirement_type.REQUIREMENT);
			}
		} 
		
		// TODO Should check if the element is in the model
		ElementModel model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies, JSONParser.project.getId());

		String murmeli = gson.toJson(model);
		
		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}

		return response;
	}
	
	
	@ApiOperation(value = "Get the transitive closure of a requirement",
			notes = "Returns the transitive closure of a given requirement to the specified depth",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON model"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Conflict")}) 
	@RequestMapping(value = "/findTransitiveClosureOfRequirement", method = RequestMethod.POST)
	public ResponseEntity<?> findTransitiveClosureOfRequirement(@RequestBody String requirementId) throws JSONException, IOException, ParserConfigurationException {
		RestTemplate rt = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = caasAddress + "/findTransitiveClosureOfElement";
		
		String response = null;
		try {
			response = rt.postForObject(completeAddress, requirementId, String.class);
			TransitiveClosure closure = gson.fromJson(response, TransitiveClosure.class);
			
			OpenReqConverter converter = new OpenReqConverter(closure.getModel());
			
			List<Requirement> requirements = converter.getRequirements();
			List<Dependency> dependencies = converter.getDependencies();
			Map<Integer, List<String>> layers = closure.getLayers();

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


}
