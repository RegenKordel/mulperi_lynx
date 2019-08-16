package eu.openreq.mulperi.controllers;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import eu.openreq.mulperi.services.KeljuService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@SpringBootApplication
@RestController
@RequestMapping("models")
public class MulperiController {

	@Autowired
	RestTemplate rt;
	
	@Autowired
	KeljuService keljuService;
	
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
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies "
					+ "in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@PostMapping(value = "murmeliModelToKeljuCaaS")
	public ResponseEntity<String> murmeliModelToKeljuCaaS(@RequestBody String requirements) throws JSONException, 
				IOException, ParserConfigurationException {
		return keljuService.murmeliModelToKeljuCaaS(requirements);
	}
	
	/**
	 * Update a model in JSON format
	 * @param requirements
	 * @return
	 * @throws JSONException 
	 * @throws ReleasePlanException 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 */
	@ApiOperation(value = "Update OpenReq JSON model in Caas",
			notes = "Import the updated requirements to Caas as a project in JSON format",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 201, message = "Success, returns received requirements and dependencies in OpenReq JSON format"),
			@ApiResponse(code = 400, message = "Failure, ex. malformed input"),
			@ApiResponse(code = 409, message = "Failure")}) 
	@PostMapping(value = "updateMurmeliModelInKeljuCaas")
	public ResponseEntity<?> updateMurmeliModelInKeljuCaas(@RequestBody String requirements) throws JSONException, IOException, ParserConfigurationException {
		return keljuService.updateMurmeliModelInKeljuCaas(requirements);
	}
	
	/**
	 * Checks whether a release plan is consistent and provides diagnosis if not
	 * 
	 * @param jsonString
	 * @param analysisOnly
	 * @param timeOut
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
	 * @throws IOException
	 * @throws ParserConfigurationException
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
			@RequestParam(required = false) boolean analysisOnly,
			@RequestParam(required = false, defaultValue = "0") int timeOut) 
					throws JSONException, IOException, ParserConfigurationException {
		return keljuService.consistencyCheckAndDiagnosis(jsonString, analysisOnly, timeOut);
	}	
	
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
		return keljuService.findTransitiveClosureOfRequirement(requirementId, layerCount);

	}

	@ApiOperation(value = "Get the transitive closure of a requirement, then check for consistency",
			notes = "Solves whether the transitive closure of the requirement is consistent",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON model"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Conflict")}) 
	@PostMapping(value = "/consistencyCheckForTransitiveClosure")
	public ResponseEntity<?> consistencyCheckForTransitiveClosure(@RequestBody List<String> requirementId, 
			@RequestParam(required = false) Integer layerCount, 
			@RequestParam(required = false) boolean analysisOnly,
			@RequestParam(required = false, defaultValue = "0") int timeOut) 
					throws JSONException, IOException, ParserConfigurationException {
		return keljuService.consistencyCheckForTransitiveClosure(requirementId, layerCount, analysisOnly, timeOut);
	}

	/**
	 * Check whether a release plan is consistent
	 * 
	 * @param jsonString
	 * @return JSON response
	 * 		{ 
	 * 			"response": {
	 * 				"consistent": false
	 * 			}
	 * 		}
	 * @throws JSONException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	
	// HIDDEN FROM SWAGGER!
	// @ApiOperation(value = "Is release plan consistent",
	//	notes = "Send model to Caas to check whether a release plan is consistent.",
	//	response = String.class)
//	@ApiResponses(value = { 
//			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
//			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
//			@ApiResponse(code = 409, message = "Check of inconsistency returns JSON {\"response\": {\"consistent\": false}}")}) 
//	@PostMapping(value = "/projects/uploadDataAndCheckForConsistency")
//	public ResponseEntity<String> uploadDataAndCheckForConsistency(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
//		String completeAddress = caasAddress + "/uploadDataAndCheckForConsistency";	
//		return convertToMurmeliAndPostToCaas(jsonString, completeAddress, false, 30000);		
//	}
	
	/**
	 * Checks whether a release plan is consistent and provides diagnosis if not
	 * 
	 * @param jsonString
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
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	/*HIDDEN FROM SWAGGER. PERHAPS OBSOLETE.
	 * 
	 * @ApiOperation(value = "Is release plan consistent and do diagnosis",
			notes = "Check whether a release plan is consistent. Provide diagnosis if it is not consistent.",
			response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 200, message = "Success, returns JSON {\"response\": {\"consistent\": true}}"),
			@ApiResponse(code = 400, message = "Failure, ex. model not found"), 
			@ApiResponse(code = 409, message = "Diagnosis of inconsistency returns JSON {\"response\": {\"consistent\": false, \"diagnosis\": [[{\"requirement\": (requirementID)}]]}}")}) 
	@PostMapping(value = "/projects/uploadDataCheckForConsistencyAndDoDiagnosis")*/
//	public ResponseEntity<?> uploadDataCheckForConsistencyAndDoDiagnosis(@RequestBody String jsonString) throws JSONException, IOException, ParserConfigurationException {
//		String completeAddress = caasAddress + "/uploadDataCheckForConsistencyAndDoDiagnosis";	
//		return convertToMurmeliAndPostToCaas(jsonString, completeAddress, false, 30000);
//	}
}
