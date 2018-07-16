package eu.openreq.mulperi.controllers;

//import eu.openreq.mulperi.models.json.*;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.openreq.mulperi.models.kumbang.Feature;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.mulson.Requirement;
import eu.openreq.mulperi.models.release.*;
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.models.selections.Selections;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.CaasClient;
import eu.openreq.mulperi.services.FormatTransformerService;
import eu.openreq.mulperi.services.JSONParser;
import eu.openreq.mulperi.services.KumbangModelGenerator;
import eu.openreq.mulperi.services.ReleaseCSPPlanner;
import eu.openreq.mulperi.services.ReleaseJSONParser;
import eu.openreq.mulperi.services.ReleaseXMLParser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("models")
//@RequestMapping("uh/mulperi/models")
public class MulperiController {
	
	private FormatTransformerService transform = new FormatTransformerService();
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();

	@Value("${mulperi.caasAddress}")
	private String caasAddress;

	@Autowired
	private ParsedModelRepository parsedModelRepository;

	/**
	 * Get all saved models
	 * @return
	 */
	@ApiOperation(value = "Get saved models",
			notes = "Get all saved models",
			response = ParsedModel.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success")}) 
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<ParsedModel> modelList() {
		return parsedModelRepository.findAll();
	}
	
	/**
	 * Get single model as FeatureSelection for selecting features
	 * @param modelName
	 * @return
	 */
	@ApiOperation(value = "Get the structure of a model",
			notes = "Get single model as FeatureSelection for selecting features",
			response = FeatureSelection.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found")}) 
	@CrossOrigin
	@RequestMapping(value = "{model}", method = RequestMethod.GET)
	public ResponseEntity<?> getModel(@PathVariable("model") String modelName) {

		ParsedModel model = this.parsedModelRepository.findFirstByModelName(modelName);

		if(model == null) {
			return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(transform.parsedModelToFeatureSelection(model), HttpStatus.OK);
	}
	
	/**
	 * Import a model in MulSON format
	 * @param requirements
	 * @return
	 * @throws JSONException 
	 * @throws ReleasePlanException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Import MulSON model",
			notes = "Import a model in MulSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "mulson", method = RequestMethod.POST)
	public ResponseEntity<?> chocoMulson(@RequestBody String requirements) throws ReleasePlanException, JSONException, IOException, ParserConfigurationException {
		
		//String name = generateName(requirements);
		System.out.println("Requirements received from Milla");
		
		//ParsedModel pm = transform.parseMulson(name, requirements);
		ParsedModel pm = new ParsedModel();

		
	//	return sendModelToCaasAndSave(pm, caasAddress);
		try {
			return new ResponseEntity<>("Requirements received: " + requirements, HttpStatus.ACCEPTED);
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

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseJSONParser.parseProjectJSON(jsonString);
			//Note! GenerateParsedModel uses old Kumbang objects, it is left here for demo purposes.
			//Should be updated at some point.
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty()) {
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
			}
		} 
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}
		
		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(releasePlan);
		rcspGen.generateCSP();
		
		boolean isConsistent = rcspGen.isReleasePlanConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(
				transform.generateProjectJsonResponse(true, "Consistent", false),
				HttpStatus.OK);
		}
		
		return new ResponseEntity<>(
				transform.generateProjectJsonResponse(false, "Not consistent", false),
				HttpStatus.CONFLICT);
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
	@ApiOperation(value = "Is release plan consistent",
			notes = "Check whether a release plan is consistent. Provide diagnosis if it is not consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns JSON {\"response\": {\"consistent\": false, \"diagnosis\": [[{\"requirement\": (requirementID)}]]}}")}) 
	@RequestMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseJSONParser.parseProjectJSON(jsonString);
			//Note! GenerateParsedModel uses old Kumbang objects, it is left here for demo purposes.
			//Should be updated at some point. 
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty()) {
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
			}
		} 
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}
		
		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(releasePlan);
		rcspGen.generateCSP();
		
		boolean isConsistent = rcspGen.isReleasePlanConsistent();
		if (isConsistent) {
			return new ResponseEntity<>(
				transform.generateProjectJsonResponse(true, "Consistent", true),
				HttpStatus.OK);
		}
		
		String diagnosis = rcspGen.getDiagnosis();
		
		return new ResponseEntity<>(
				transform.generateProjectJsonResponse(false, diagnosis, true),
				HttpStatus.CONFLICT);
	}

	
	public String generateName(Object object) {
		int hashCode = object.hashCode();
		return "ID" + (hashCode > 0 ? hashCode : "_" + Math.abs(hashCode)); 
		//replace - with _, since Kumbang doesn't like hyphens
	}
	
	public String makeConfigurationRequest(Selections selections, String modelName) throws Exception {
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
	
	public ResponseEntity<String> sendModelToCaasAndSave(ParsedModel pm, String caasAddress) {

		pm.rolesForConstraints();

		String kumbangModel = kumbangModelGenerator.generateKumbangModelString(pm);
		CaasClient client = new CaasClient();

		String result = new String();

		try {
			result = client.uploadConfigurationModel(pm.getModelName(), kumbangModel, caasAddress+"/generateModel");
		} catch(IntrospectionException e) {
			return new ResponseEntity<>("Impossible model\n\n" + e.getMessage(), HttpStatus.CONFLICT);
		} catch(DataFormatException e) {
			return new ResponseEntity<>("Syntax error\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
			return new ResponseEntity<>("Configuration model upload failed.\n\n" + e.toString(), HttpStatus.BAD_REQUEST);
		} 

		//Save model to database if send successful
		for (Feature f: pm.getFeatures()) {
			System.out.println(f.getType()+ "," +  f.getParent());
		}
		parsedModelRepository.save(pm);

		//return new ResponseEntity<>("Configuration model upload successful.\n\n - - - \n\n" + result, HttpStatus.CREATED);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}


}
