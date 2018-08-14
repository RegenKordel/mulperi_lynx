//package eu.openreq.mulperi.graveyard;
//
//import java.io.IOException;
//import java.util.Collection;
//import java.util.List;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.chocosolver.solver.constraints.nary.nValue.amnv.differences.D;
//import org.json.JSONException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.google.gson.Gson;
//
//import eu.openreq.mulperi.models.kumbang.ParsedModel;
//import eu.openreq.mulperi.models.release.ReleasePlan;
//import eu.openreq.mulperi.models.release.ReleasePlanException;
//import eu.openreq.mulperi.models.reqif.SpecObject;
//import eu.openreq.mulperi.models.selections.FeatureSelection;
//import eu.openreq.mulperi.models.selections.Selections;
//import eu.openreq.mulperi.repositories.ParsedModelRepository;
//import eu.openreq.mulperi.services.CaasClient;
//import eu.openreq.mulperi.services.FormatTransformerService;
//import eu.openreq.mulperi.services.ReleaseCSPPlanner;
//import eu.openreq.mulperi.services.ReleaseJSONParser;
//import eu.openreq.mulperi.services.ReqifParser;
//import fi.helsinki.ese.murmeli.ElementModel;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//
//@RestController
//public class ArchivedTestingController { 
//
//
//
//// Old methods from testing controller stored here for later use ore something.
//	
//
//	/**
//	 * Get a configuration of a model
//	 * @param selections optional selected features
//	 * @param modelName ID of the model
//	 * @return
//	 */
//
//	// The old way of handling defaults with Smodels - make a second round with default attributes set in Mulperi
//	//	@RequestMapping(value = "/models/{model}/configurations/defaults", method = RequestMethod.POST, produces="application/xml")
//	//    public ResponseEntity<?> requestConfigurationWithDefaults(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
//	//		
//	//		String configurationRequest;
//	//		try {
//	//			configurationRequest = makeConfigurationRequest(selections, modelName);
//	//		} catch (Exception e) {
//	//			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//	//		}
//	//		
//	//    	try {
//	//    		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
//	//    		String responseWithoutDefaults = caasClient.getConfiguration(configurationRequest, caasAddress);
//	//    		FeatureSelection response = this.transform.xmlToFeatureSelection(responseWithoutDefaults);
//	//    		FeatureSelection request = this.transform.listOfFeatureSelectionsToOne(selections, model);
//	//    		
//	//    		this.utils.setDefaults(response, request, model);
//	//    		
//	//    		configurationRequest = makeConfigurationRequest(this.transform.featureSelectionToList(response), modelName);
//	//			return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress), HttpStatus.OK);
//	//
//	//		} catch (Exception e) {
//	//			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
//	//		}
//	//    	
//	//    }
//
//
//
//	/**
//	 * Check whether a project is consistent
//	 * @param selections checked selections
//	 * @param modelName
//	 * @return "yes" or "no"
//	 */
//	
//	/*
//	@ApiOperation(value = "Is release plan consistent",
//			notes = "Check whether a release plan is consistent",
//			response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 200, message = "Success, returns \"yes\" or \"no\""),
//			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
//			@ApiResponse(code = 409, message = "Diagnosis of inconsistency")}) 
//	@RequestMapping(value = "/projects/checkForConsistency", method = RequestMethod.POST)
//	public ResponseEntity<?> checkForConsistency(@RequestBody String projectXML) {
//
//		ReleasePlan releasePlan = null;
//		try {
//			releasePlan
//			= ReleaseXMLParser.parseProjectXML(projectXML);
//			List<String> problems = releasePlan.generateParsedModel(); 
//			if (!problems.isEmpty())
//				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
//		} 
//		catch (ReleasePlanException ex) {
//			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
//					(ex.getMessage() == null ? "":	ex.getMessage()) +
//					(ex.getCause() == null ? "" : ex.getCause().toString()),
//					HttpStatus.BAD_REQUEST);
//		}
//
//		ParsedModel model = null;
//		try {
//
//			ResponseEntity<?> resp = modelsService.projectToKumbang(projectXML);
//			switch (resp.getStatusCode()) {
//			case CREATED: //201
//				String modelName =(String) (resp.getBody());
//				System.out.println("modelName=" + modelName);
//				model = this.parsedModelRepository.findFirstByModelName(modelName);
//				if(model == null) {
//					return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
//				}
//
//				break;
//			default:
//				return resp;
//			}
//		}
//		catch (Exception e) {
//			return new ResponseEntity<>("no", HttpStatus.BAD_REQUEST);
//		}
//
//		FeatureSelection featSel = null;
//		ResponseEntity<?> modelsRresponse = modelsService.getModel(model.getModelName());
//		switch (modelsRresponse.getStatusCode()) {
//		case OK: //200
//			featSel=(FeatureSelection) (modelsRresponse.getBody());
//			System.out.println("featSel=" + featSel);
//			if(featSel == null) {
//				return new ResponseEntity<>("Selections not found", HttpStatus.BAD_REQUEST);
//			}
//
//			break;
//		default:
//			return modelsRresponse;
//		}
//		Selections selections = new Selections();
//		FormatTransformerService transform = new FormatTransformerService();
//		selections.setFeatureSelections(transform.featureSelectionToList(featSel));
//		selections.setCalculationConstraints(
//				releasePlan.getEffortCalculationConstraints());
//
//		ResponseEntity<?> isConsistentRresponse =
//				isConsistent(selections, model.getModelName());
//		switch (isConsistentRresponse.getStatusCode()) {
//		case OK: //200
//			String replyStatus =(String) (isConsistentRresponse.getBody());
//			if ("yes".equals(replyStatus.toLowerCase())) {
//				return new ResponseEntity<>(
//						transform.generateProjectXMLResponse(true, "Consistent"),
//						HttpStatus.OK);
//
//			}
//			else
//				if ("no".equals(replyStatus.toLowerCase())) {
//					return new ResponseEntity<>(
//							transform.generateProjectXMLResponse(false, "Diagnosis TBD"),
//							HttpStatus.CONFLICT);
//
//				}
//			return new ResponseEntity<>("Consistency detection failure. Internal error?, expected yes/no from caas", HttpStatus.BAD_REQUEST);
//
//		default:
//			return modelsRresponse;
//		}
//	}
//
//*/
////	/**
////	 * Check whether a project is consistent
////	 * @param selections checked selections
////	 * @param modelName
////	 * @return XML response
////	 * 		<response>
////	 *		<consistent>true / false</consistent>
////	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
////	 *		</response>
////	 * @throws JSONException 
////	 * @throws ParserConfigurationException 
////	 * @throws IOException 
////	 */
////	@ApiOperation(value = "Is release plan consistent",
////			notes = "OLD: Single release: Check whether a release plan is consistent. Provide diagnosis if not",
////			response = String.class)
////	@ApiResponses(value = { 
////			@ApiResponse(code = 200, message = "Success, returns XML <response><consistent>true</consistent><explanation>Consistent</explanation></response>"),
////			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
////			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns XML <response><consistent>false</consistent><explanation>Plain text Diagnosis</explanation></response>")}) 
////	@RequestMapping(value = "/projects/checkForConsistencySingleReleaseCaas", method = RequestMethod.POST)
////	public ResponseEntity<?> checkForConsistencySingleReleaseCaas(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
////
////		ReleasePlan releasePlan = null;
////		try {
////			releasePlan
////			= ReleaseJSONParser.parseProjectJSON(jsonString);
////			List<String> problems = releasePlan.generateParsedModel(); 
////			if (!problems.isEmpty())
////				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
////		} 
////		catch (ReleasePlanException ex) {
////			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
////					(ex.getMessage() == null ? "":	ex.getMessage()) +
////					(ex.getCause() == null ? "" : ex.getCause().toString()),
////					HttpStatus.BAD_REQUEST);
////		}
////
////		ParsedModel model = null;
////		try {
////
////			ResponseEntity<?> resp = projectToKumbang(jsonString);
////			switch (resp.getStatusCode()) {
////			case CREATED: //201
////				String modelName =(String) (resp.getBody());
////				System.out.println("modelName=" + modelName);
////				model = this.parsedModelRepository.findFirstByModelName(modelName);
////				if(model == null) {
////					return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
////				}
////
////				break;
////			default:
////				return resp;
////			}
////		}
////		catch (Exception e) {
////			return new ResponseEntity<>("no", HttpStatus.BAD_REQUEST);
////		}
////
////		FeatureSelection featSel = null;
////		ResponseEntity<?> modelsRresponse = mulperiController.getModel(model.getModelName());
////		switch (modelsRresponse.getStatusCode()) {
////		case OK: //200
////			featSel=(FeatureSelection) (modelsRresponse.getBody());
////			System.out.println("featSel=" + featSel);
////			if(featSel == null) {
////				return new ResponseEntity<>("Selections not found", HttpStatus.BAD_REQUEST);
////			}
////
////			break;
////		default:
////			return modelsRresponse;
////		}
////		Selections selections = new Selections();
////		FormatTransformerService transform = new FormatTransformerService();
////		selections.setFeatureSelections(transform.featureSelectionToList(featSel));
////		selections.setCalculationConstraints(
////				releasePlan.getEffortCalculationConstraints());
////
////		try {
////			String configurationRequest = mulperiController.makeConfigurationRequest(selections, model.getModelName());
////			ResponseEntity<?> checkAndDiagnoseResponse =
////					new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress + "/checkAndDiagnose"), HttpStatus.OK);
////			switch (checkAndDiagnoseResponse.getStatusCode()) {
////			case OK: //200
////				String replyStatus =(String) (checkAndDiagnoseResponse.getBody());
////				if ("ok".equals(replyStatus.toLowerCase())) {
////					return new ResponseEntity<>(
////							transform.generateProjectXMLResponse(true, "Consistent"),
////							HttpStatus.OK);
////
////				}
////				else
////					return new ResponseEntity<>(
////							transform.generateProjectXMLResponse(false, replyStatus),
////							HttpStatus.CONFLICT);
////			default:
////				return modelsRresponse;
////			}
////		}
////		catch (Exception ex) {
////			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
////		}
////	}
////	
//	
//	
////	/**
////	 * Check whether a project is consistent
////	 * @param selections checked selections
////	 * @param modelName
////	 * @return XML response
////	 * 		<response>
////	 *		<consistent>true / false</consistent>
////	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
////	 *		</response>
////	 * @throws JSONException 
////	 * @throws ParserConfigurationException 
////	 * @throws IOException 
////	 */
////	@ApiOperation(value = "Is release plan consistent",
////			notes = "Check whether a release plan is consistent.",
////			response = String.class)
////	@ApiResponses(value = { 
////			@ApiResponse(code = 200, message = "Success, returns JSON { \"response\": {\"consistent\": true}}"),
////			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
////			@ApiResponse(code = 409, message = "Check of inconsistency returns JSON { \"response\": {\"consistent\": false}}")}) 
////	@RequestMapping(value = "/projects/uploadDataAndCheckForConsistency", method = RequestMethod.POST)
////	public ResponseEntity<?> uploadDataAndCheckForConsistency(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
////
////		ReleasePlan releasePlan = null;
////		try {
////			releasePlan
////			= ReleaseJSONParser.parseProjectJSON(jsonString);
////			//Note! GenerateParsedModel uses old Kumbang objects, it is left here for demo purposes.
////			//Should be updated at some point.
////			List<String> problems = releasePlan.generateParsedModel(); 
////			if (!problems.isEmpty()) {
////				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
////			}
////		} 
////		catch (ReleasePlanException ex) {
////			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
////					(ex.getMessage() == null ? "":	ex.getMessage()) +
////					(ex.getCause() == null ? "" : ex.getCause().toString()),
////					HttpStatus.BAD_REQUEST);
////		}
////		
////		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(releasePlan);
////		rcspGen.generateCSP();
////		
////		boolean isConsistent = rcspGen.isReleasePlanConsistent();
////		if (isConsistent) {
////			return new ResponseEntity<>(
////				transform.generateProjectJsonResponse(true, "Consistent", false),
////				HttpStatus.OK);
////		}
////		
////		return new ResponseEntity<>(
////				transform.generateProjectJsonResponse(false, "Not consistent", false),
////				HttpStatus.CONFLICT);
////	}
////	
////
////
////	/**
////	 * Check whether a project is consistent
////	 * @param selections checked selections
////	 * @param modelName
////	 * @return XML response
////	 * 		<response>
////	 *		<consistent>true / false</consistent>
////	 *		<explanation>Consistent / Diagnosis: conflicts</explanation>
////	 *		</response>
////	 * @throws JSONException 
////	 * @throws ParserConfigurationException 
////	 * @throws IOException 
////	 */
////	@ApiOperation(value = "Is release plan consistent",
////			notes = "Check whether a release plan is consistent. Provide diagnosis if it is not consistent.",
////			response = String.class)
////	@ApiResponses(value = { 
////			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
////			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
////			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns JSON { \"response\": {\"consistent\": false, \"diagnosis\": [[{ \"requirement\": (requirementID)}]]}}")}) 
////	@RequestMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis", method = RequestMethod.POST)
////	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
////
////		ReleasePlan releasePlan = null;
////		try {
////			releasePlan
////			= ReleaseJSONParser.parseProjectJSON(jsonString);
////			//Note! GenerateParsedModel uses old Kumbang objects, it is left here for demo purposes.
////			//Should be updated at some point. 
////			List<String> problems = releasePlan.generateParsedModel(); 
////			if (!problems.isEmpty()) {
////				return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" + problems.toString(), HttpStatus.BAD_REQUEST);
////			}
////		} 
////		catch (ReleasePlanException ex) {
////			return new ResponseEntity<>("Erroneus releasePlan. Errors: \n\n" +
////					(ex.getMessage() == null ? "":	ex.getMessage()) +
////					(ex.getCause() == null ? "" : ex.getCause().toString()),
////					HttpStatus.BAD_REQUEST);
////		}
////		
////		ReleaseCSPPlanner rcspGen = new ReleaseCSPPlanner(releasePlan);
////		rcspGen.generateCSP();
////		
////		boolean isConsistent = rcspGen.isReleasePlanConsistent();
////		if (isConsistent) {
////			return new ResponseEntity<>(
////				transform.generateProjectJsonResponse(true, "Consistent", true),
////				HttpStatus.OK);
////		}
////		
////		String diagnosis = rcspGen.getDiagnosis();
////		
////		return new ResponseEntity<>(
////				transform.generateProjectJsonResponse(false, diagnosis, true),
////				HttpStatus.CONFLICT);
////	}
//
//
//
////	/**
////	 * Find out the consequences of selecting some features, i.e. which additional features get selected
////	 * @param selections The base features
////	 * @param modelName
////	 * @return
////	 * @throws Exception
////	 */
////	@ApiOperation(value = "Find consequences",
////			notes = "Find out the consequences of selecting some features, i.e. which additional features get selected",
////			response = String.class)
////	@ApiResponses(value = { 
////			@ApiResponse(code = 200, message = "Success, TODO final return format"),
////			@ApiResponse(code = 400, message = "Failure, ex. model not found"),
////			@ApiResponse(code = 409, message = "Failure, configuration with selected features is impossible")}) 
////	@RequestMapping(value = "/models/{model}/configurations/consequences", method = RequestMethod.POST)
////	public ResponseEntity<?> findDirectConsequences(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) throws Exception {
////
////		String configurationRequest;
////		try {
////			Selections selectionsObject = new Selections();
////			selectionsObject.setFeatureSelections(selections);
////			configurationRequest = mulperiController.makeConfigurationRequest(selectionsObject, modelName);
////		} catch (Exception e) {
////			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
////		}
////		return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress + "/directConsequences"), HttpStatus.OK);
//
//		//				String emptyConfigurationRequest;
//		//				try {
//		//					emptyConfigurationRequest = makeConfigurationRequest(new ArrayList<FeatureSelection>(), modelName);
//		//				} catch (Exception e) {
//		//					return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//		//				}		
//		//				
//		//    	try {
//		//    		
//		//    		String emptyResponse = caasClient.getConfiguration(emptyConfigurationRequest, caasAddress);
//		//    		String response = caasClient.getConfiguration(configurationRequest, caasAddress);
//		//    		
//		//    		FeatureSelection original = this.transform.xmlToFeatureSelection(emptyResponse);
//		//    		FeatureSelection modified = this.transform.xmlToFeatureSelection(response);
//		//    		FeatureSelection diff = new FeatureSelection();
//		//    		
//		//    		this.utils.diffFeatures(original, modified, diff);
//		//    		
//		//			return new ResponseEntity<>(diff.getFeatures().get(0), HttpStatus.OK);
//		//    		
//		//
//		//		} catch (Exception e) {
//		//			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
//		//		}
//
//	}
//	