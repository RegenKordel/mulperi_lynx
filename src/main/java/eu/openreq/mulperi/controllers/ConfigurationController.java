package eu.openreq.mulperi.controllers;

import java.util.List;

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
import eu.openreq.mulperi.models.selections.FeatureSelection;
import eu.openreq.mulperi.models.selections.Selections;
import eu.openreq.mulperi.repositories.ParsedModelRepository;
import eu.openreq.mulperi.services.CaasClient;
import eu.openreq.mulperi.services.FormatTransformerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.List;

@RestController
public class ConfigurationController {

	private FormatTransformerService transform = new FormatTransformerService();
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	
	private CaasClient caasClient = new CaasClient();
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
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
			configurationRequest = makeConfigurationRequest(selections, modelName);
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
			configurationRequest = makeConfigurationRequest(selections, modelName);
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
			configurationRequest = makeConfigurationRequest(selectionsObject, modelName);
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
	

	private String makeConfigurationRequest(Selections selections, String modelName) throws Exception {
		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
		
		if(model == null) {
			throw new Exception("Model not found");
		}
		
    	try {
    		return transform.slectionsToConfigurationRequest(selections, model);
		} catch (Exception e) {
			throw new Exception("Failed to create configurationRequest (feature typos?): " + e.getMessage());
		}
	}

}

