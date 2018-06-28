package eu.openreq.mulperi.controllers;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.reqif.SpecObject;
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.models.selections.Selections;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.CaasClient;
import eu.openreq.mulperi.services.FormatTransformerService;
import eu.openreq.mulperi.services.ReleaseCSPPlanner;
import eu.openreq.mulperi.services.ReleaseJSONParser;
import eu.openreq.mulperi.services.ReleaseXMLParser;
import eu.openreq.mulperi.services.ReqifParser;
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

	// The old way of handling defaults with Smodels - make a second round with default attributes set in Mulperi
	//	@RequestMapping(value = "/models/{model}/configurations/defaults", method = RequestMethod.POST, produces="application/xml")
	//    public ResponseEntity<?> requestConfigurationWithDefaults(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
	//		
	//		String configurationRequest;
	//		try {
	//			configurationRequest = makeConfigurationRequest(selections, modelName);
	//		} catch (Exception e) {
	//			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	//		}
	//		
	//    	try {
	//    		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
	//    		String responseWithoutDefaults = caasClient.getConfiguration(configurationRequest, caasAddress);
	//    		FeatureSelection response = this.transform.xmlToFeatureSelection(responseWithoutDefaults);
	//    		FeatureSelection request = this.transform.listOfFeatureSelectionsToOne(selections, model);
	//    		
	//    		this.utils.setDefaults(response, request, model);
	//    		
	//    		configurationRequest = makeConfigurationRequest(this.transform.featureSelectionToList(response), modelName);
	//			return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress), HttpStatus.OK);
	//
	//		} catch (Exception e) {
	//			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	//		}
	//    	
	//    }

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
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return "yes" or "no"
	 */
	
	/*
	@ApiOperation(value = "Is release plan consistent",
			notes = "Check whether a release plan is consistent",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns \"yes\" or \"no\""),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency")}) 
	@RequestMapping(value = "/projects/checkForConsistency", method = RequestMethod.POST)
	public ResponseEntity<?> checkForConsistency(@RequestBody String projectXML) {

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseXMLParser.parseProjectXML(projectXML);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
		} 
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}

		ParsedModel model = null;
		try {

			ResponseEntity<?> resp = modelsService.projectToKumbang(projectXML);
			switch (resp.getStatusCode()) {
			case CREATED: //201
				String modelName =(String) (resp.getBody());
				System.out.println("modelName=" + modelName);
				model = this.parsedModelRepository.findFirstByModelName(modelName);
				if(model == null) {
					return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
				}

				break;
			default:
				return resp;
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>("no", HttpStatus.BAD_REQUEST);
		}

		FeatureSelection featSel = null;
		ResponseEntity<?> modelsRresponse = modelsService.getModel(model.getModelName());
		switch (modelsRresponse.getStatusCode()) {
		case OK: //200
			featSel=(FeatureSelection) (modelsRresponse.getBody());
			System.out.println("featSel=" + featSel);
			if(featSel == null) {
				return new ResponseEntity<>("Selections not found", HttpStatus.BAD_REQUEST);
			}

			break;
		default:
			return modelsRresponse;
		}
		Selections selections = new Selections();
		FormatTransformerService transform = new FormatTransformerService();
		selections.setFeatureSelections(transform.featureSelectionToList(featSel));
		selections.setCalculationConstraints(
				releasePlan.getEffortCalculationConstraints());

		ResponseEntity<?> isConsistentRresponse =
				isConsistent(selections, model.getModelName());
		switch (isConsistentRresponse.getStatusCode()) {
		case OK: //200
			String replyStatus =(String) (isConsistentRresponse.getBody());
			if ("yes".equals(replyStatus.toLowerCase())) {
				return new ResponseEntity<>(
						transform.generateProjectXMLResponse(true, "Consistent"),
						HttpStatus.OK);

			}
			else
				if ("no".equals(replyStatus.toLowerCase())) {
					return new ResponseEntity<>(
							transform.generateProjectXMLResponse(false, "Diagnosis TBD"),
							HttpStatus.CONFLICT);

				}
			return new ResponseEntity<>("Consistency detection failure. Internal error?, expected yes/no from caas", HttpStatus.BAD_REQUEST);

		default:
			return modelsRresponse;
		}
	}

*/
	/**
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return XML response
	 * 		<response>
	 *		<consistent>true / false</consistent>
	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
	 *		</response>
	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent",
			notes = "OLD: Single release: Check whether a release plan is consistent. Provide diagnosis if not",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns XML <response><consistent>true</consistent><explanation>Consistent</explanation></response>"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns XML <response><consistent>false</consistent><explanation>Plain text Diagnosis</explanation></response>")}) 
	@RequestMapping(value = "/projects/checkForConsistencySingleReleaseCaas", method = RequestMethod.POST)
	public ResponseEntity<?> checkForConsistencySingleReleaseCaas(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseJSONParser.parseProjectJSON(jsonString);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
		} 
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}

		ParsedModel model = null;
		try {

			ResponseEntity<?> resp = projectToKumbang(jsonString);
			switch (resp.getStatusCode()) {
			case CREATED: //201
				String modelName =(String) (resp.getBody());
				System.out.println("modelName=" + modelName);
				model = this.parsedModelRepository.findFirstByModelName(modelName);
				if(model == null) {
					return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
				}

				break;
			default:
				return resp;
			}
		}
		catch (Exception e) {
			return new ResponseEntity<>("no", HttpStatus.BAD_REQUEST);
		}

		FeatureSelection featSel = null;
		ResponseEntity<?> modelsRresponse = mulperiController.getModel(model.getModelName());
		switch (modelsRresponse.getStatusCode()) {
		case OK: //200
			featSel=(FeatureSelection) (modelsRresponse.getBody());
			System.out.println("featSel=" + featSel);
			if(featSel == null) {
				return new ResponseEntity<>("Selections not found", HttpStatus.BAD_REQUEST);
			}

			break;
		default:
			return modelsRresponse;
		}
		Selections selections = new Selections();
		FormatTransformerService transform = new FormatTransformerService();
		selections.setFeatureSelections(transform.featureSelectionToList(featSel));
		selections.setCalculationConstraints(
				releasePlan.getEffortCalculationConstraints());

		try {
			String configurationRequest = mulperiController.makeConfigurationRequest(selections, model.getModelName());
			ResponseEntity<?> checkAndDiagnoseResponse =
					new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress + "/checkAndDiagnose"), HttpStatus.OK);
			switch (checkAndDiagnoseResponse.getStatusCode()) {
			case OK: //200
				String replyStatus =(String) (checkAndDiagnoseResponse.getBody());
				if ("ok".equals(replyStatus.toLowerCase())) {
					return new ResponseEntity<>(
							transform.generateProjectXMLResponse(true, "Consistent"),
							HttpStatus.OK);

				}
				else
					return new ResponseEntity<>(
							transform.generateProjectXMLResponse(false, replyStatus),
							HttpStatus.CONFLICT);
			default:
				return modelsRresponse;
			}
		}
		catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	
	/**
	 * Check whether a project is consistent
	 * @param selections checked selections
	 * @param modelName
	 * @return XML response
	 * 		<response>
	 *		<consistent>true / false</consistent>
	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
	 *		</response>
	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent",
			notes = "Check whether a release plan is consistent. Provide diagnosis if not",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns XML <response><consistent>true</consistent><explanation>Consistent</explanation></response>"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns XML <response><consistent>false</consistent><explanation>Plain text Diagnosis</explanation></response>")}) 
	@RequestMapping(value = "/projects/uploadDataAndCheckForConsistency", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseJSONParser.parseProjectJSON(jsonString);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
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
	 * @return XML response
	 * 		<response>
	 *		<consistent>true / false</consistent>
	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
	 *		</response>
	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Is release plan consistent",
			notes = "Check whether a release plan is consistent. Provide diagnosis if not",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns XML <response><consistent>true</consistent><explanation>Consistent</explanation></response>"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns XML <response><consistent>false</consistent><explanation>Plain text Diagnosis</explanation></response>")}) 
	@RequestMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {

		ReleasePlan releasePlan = null;
		try {
			releasePlan
			= ReleaseJSONParser.parseProjectJSON(jsonString);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
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

		//				String emptyConfigurationRequest;
		//				try {
		//					emptyConfigurationRequest = makeConfigurationRequest(new ArrayList<FeatureSelection>(), modelName);
		//				} catch (Exception e) {
		//					return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		//				}		
		//				
		//    	try {
		//    		
		//    		String emptyResponse = caasClient.getConfiguration(emptyConfigurationRequest, caasAddress);
		//    		String response = caasClient.getConfiguration(configurationRequest, caasAddress);
		//    		
		//    		FeatureSelection original = this.transform.xmlToFeatureSelection(emptyResponse);
		//    		FeatureSelection modified = this.transform.xmlToFeatureSelection(response);
		//    		FeatureSelection diff = new FeatureSelection();
		//    		
		//    		this.utils.diffFeatures(original, modified, diff);
		//    		
		//			return new ResponseEntity<>(diff.getFeatures().get(0), HttpStatus.OK);
		//    		
		//
		//		} catch (Exception e) {
		//			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		//		}

	}
	
	/**
	 * Import a model in checkForConsistency(Project) XML format for release planning
	 * @param projectXML
	 * @return
	 * @throws JSONException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = " Import a model in checkForConsistency(Project) XML format for release planning",
			notes = " Import a model in checkForConsistency(Project) XML format for release planning",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "/project", method = RequestMethod.POST)
	public ResponseEntity<?> projectToKumbang(@RequestBody String projectXML) throws JSONException, IOException, ParserConfigurationException {
		ReleasePlan releasePlan= null;
		try {
			releasePlan 
			= ReleaseJSONParser.parseProjectJSON(projectXML);
			List<String> problems = releasePlan.generateParsedModel(); 
			if (!problems.isEmpty())
				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
			ParsedModel pm = releasePlan.getParsedModel();
			System.out.println(pm);
			return  mulperiController.sendModelToCaasAndSave(releasePlan.getParsedModel(), caasAddress);
		}
		catch (ReleasePlanException ex) {
			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
					(ex.getMessage() == null ? "":	ex.getMessage()) +
					(ex.getCause() == null ? "" : ex.getCause().toString()),
					HttpStatus.BAD_REQUEST);
		}
	}
	
	/**
	 * Import a model in ReqIF format
	 * @param reqifXML
	 * @return
	 */
	@ApiOperation(value = "Import ReqIF model",
			notes = "Import a model in ReqIF format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns the ID of the generated model"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure, imported model is impossible")}) 
	@RequestMapping(value = "/reqif", method = RequestMethod.POST, consumes="application/xml")
	public ResponseEntity<?> reqif(@RequestBody String reqifXML) {
		ReqifParser parser = new ReqifParser();
		String name = mulperiController.generateName(reqifXML);

		try {
			Collection<SpecObject> specObjects = parser.parse(reqifXML).values();
			ParsedModel pm = transform.parseReqif(name, specObjects);
			return mulperiController.sendModelToCaasAndSave(pm, caasAddress);
		} catch (Exception e) { //ReqifParser error
			return new ResponseEntity<>("Syntax error in ReqIF\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
