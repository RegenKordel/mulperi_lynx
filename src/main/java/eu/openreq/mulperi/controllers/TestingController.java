package eu.openreq.mulperi.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.chocosolver.solver.constraints.nary.nValue.amnv.differences.D;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import eu.openreq.mulperi.graveyard.ReleaseXMLParser;
import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.models.json.Requirement_type;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.reqif.SpecObject;
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.models.selections.Selections;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.CaasClient;
import eu.openreq.mulperi.services.FormatTransformerService;
import eu.openreq.mulperi.services.JSONParser;
import eu.openreq.mulperi.services.MurmeliModelGenerator;
import eu.openreq.mulperi.services.ReleaseCSPPlanner;
import eu.openreq.mulperi.services.ReleaseJSONParser;
import eu.openreq.mulperi.services.ReqifParser;
import fi.helsinki.ese.murmeli.ElementModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class TestingController { 

	private FormatTransformerService transform = new FormatTransformerService();

	@Value("${mulperi.caasAddress}")
	private String caasAddress;

	private CaasClient caasClient = new CaasClient();

	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@Autowired
	private MulperiController mulperiController;
	

	/**
	 * Get a configuration of a model
	 * @param selections optional selected features
	 * @param modelName ID of the model
	 * @return
	 */
	@ApiOperation(value = "Get configuration",
			notes = "Get a configuration of a model with optional selections",
			response = FeatureSelection.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"),
			@ApiResponse(code = 409, message = "Failure, configuration with selected features is impossible")}) 
	@CrossOrigin
	@RequestMapping(value = "/models/{model}/configurations", method = RequestMethod.POST)
	public ResponseEntity<?> requestConfiguration(@RequestBody Selections selections, @PathVariable("model") String modelName) {
		String configurationRequest;
		try {
			configurationRequest = mulperiController.makeConfigurationRequest(selections, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		try {
			String configurationXml = caasClient.getConfiguration(configurationRequest, caasAddress);
			FeatureSelection response = this.transform.xmlToFeatureSelection(configurationXml);
			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}


	/**
	 * Check whether a configuration is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return "yes" or "no"
	 */
	@ApiOperation(value = "Is configuration consistent",
			notes = "Check whether a configuration is consistent",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns \"yes\" or \"no\""),
			@ApiResponse(code = 400, message = "Failure, ex. model not found")}) 
	@RequestMapping(value = "/models/{model}/configurations/isconsistent", method = RequestMethod.POST)
	public ResponseEntity<?> isConsistent(@RequestBody Selections selections, @PathVariable("model") String modelName) {

		String configurationRequest;
		try {
			configurationRequest = mulperiController.makeConfigurationRequest(selections, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}

		//Todo: all invalid configurations could return HTTP 400 to tell server errors and invalid configurations apart
		try {
			String response = caasClient.getConfiguration(configurationRequest, caasAddress);

			List<FeatureSelection> features = selections.getFeatureSelections();

			for(FeatureSelection feat : features) { //TBD: do this nicer - check if even necessary with Choco (Smodels version might silently drop a feature)
				if(!response.contains("\"" + feat.getType() + "\"")) {
					return new ResponseEntity<>("no", HttpStatus.OK);
				}
			}

			return new ResponseEntity<>("yes", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("no", HttpStatus.OK);
		}

	}


	/**
	 * Find out the consequences of selecting some features, i.e. which additional features get selected
	 * @param selections The base features
	 * @param modelName
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Find consequences",
			notes = "Find out the consequences of selecting some features, i.e. which additional features get selected",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, TODO final return format"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"),
			@ApiResponse(code = 409, message = "Failure, configuration with selected features is impossible")}) 
	@RequestMapping(value = "/models/{model}/configurations/consequences", method = RequestMethod.POST)
	public ResponseEntity<?> findDirectConsequences(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) throws Exception {

		String configurationRequest;
		try {
			Selections selectionsObject = new Selections();
			selectionsObject.setFeatureSelections(selections);
			configurationRequest = mulperiController.makeConfigurationRequest(selectionsObject, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress + "/directConsequences"), HttpStatus.OK);

	}

	
//	/**
//	 * Import a model in checkForConsistency(Project) XML format for release planning
//	 * @param projectXML
//	 * @return
//	 * @throws JSONException 
//	 * @throws ParserConfigurationException 
//	 * @throws IOException 
//	 */
//	@ApiOperation(value = " Import a model in checkForConsistency(Project) XML format for release planning",
//			notes = " Import a model in checkForConsistency(Project) XML format for release planning",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
//			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
//			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
//	@RequestMapping(value = "/project", method = RequestMethod.POST)
//	public ResponseEntity<?> projectToKumbang(@RequestBody String projectXML) throws JSONException, IOException, ParserConfigurationException {
//		ReleasePlan releasePlan= null;
//		try {
//			releasePlan 
//			= ReleaseJSONParser.parseProjectJSON(projectXML);
//			List<String> problems = releasePlan.generateParsedModel(); 
//			if (!problems.isEmpty())
//				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
//			ParsedModel pm = releasePlan.getParsedModel();
//			System.out.println(pm);
//			return  mulperiController.sendModelToCaasAndSave(releasePlan.getParsedModel(), caasAddress);
//		}
//		catch (ReleasePlanException ex) {
//			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
//					(ex.getMessage() == null ? "":	ex.getMessage()) +
//					(ex.getCause() == null ? "" : ex.getCause().toString()),
//					HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	/**
//	 * Import a model in ReqIF format
//	 * @param reqifXML
//	 * @return
//	 */
//	@ApiOperation(value = "Import ReqIF model",
//			notes = "Import a model in ReqIF format",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
//			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
//			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
//	@RequestMapping(value = "/reqif", method = RequestMethod.POST, consumes="application/xml")
//	public ResponseEntity<?> reqif(@RequestBody String reqifXML) {
//		ReqifParser parser = new ReqifParser();
//		String name = mulperiController.generateName(reqifXML);
//
//		try {
//			Collection<SpecObject> specObjects = parser.parse(reqifXML).values();
//			ParsedModel pm = transform.parseReqif(name, specObjects);
//			return mulperiController.sendModelToCaasAndSave(pm, caasAddress);
//		} catch (Exception e) { //ReqifParser error
//			return new ResponseEntity<>("Syntax error in ReqIF\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
//		}
//	}
	
	
	@ApiOperation(value = "Method for testing JSON parsing",
			notes = "Method for testing JSON parsing",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Check of inconsistency returns JSON {\"response\": {\"consistent\": false}}")}) 
	@RequestMapping(value = "/projects/jsonToMurmeli", method = RequestMethod.POST)
	public void jsonToMurmeli(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		Gson gson = new Gson();
		ElementModel model = gson.fromJson(jsonString, ElementModel.class);
		
		String json = gson.toJson(model);
		
		System.out.println("\n" + json);
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
	@RequestMapping(value = "/testKelju", method = RequestMethod.POST)
	public ResponseEntity<?> testKelju(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
		
		System.out.println("Post To Caas");
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String actualPath = "/testKelju"; 

		String completeAddress = caasAddress + actualPath;
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		
		JSONParser.parseToOpenReqObjects(jsonString);
		
//		for (Requirement req : JSONParser.requirements) {
//			if (req.getRequirement_type() == null) {
//				req.setRequirement_type(Requirement_type.REQUIREMENT);
//			}
//		} 
		
		ElementModel model = generator.initializeElementModel(JSONParser.requirements, JSONParser.dependencies);
		
		Gson gson = new Gson();
		String murmeli = gson.toJson(model);
		
		System.out.println("Post To Caas");
		
		HttpEntity<String> entity = new HttpEntity<String>(murmeli, headers);
		ResponseEntity<?> response = null;
		try {
			response = rt.postForEntity(completeAddress, entity, String.class);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Kelju error:\n\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}

		return response;
	}
	
}
