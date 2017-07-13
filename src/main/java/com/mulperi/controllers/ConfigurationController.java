package com.mulperi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.selections.FeatureSelection;
import com.mulperi.repositories.ParsedModelRepository;
import com.mulperi.services.CaasClient;
import com.mulperi.services.FormatTransformerService;
import com.mulperi.services.Utils;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ConfigurationController {

	private FormatTransformerService transform = new FormatTransformerService();
	private Utils utils = new Utils();
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	private CaasClient caasClient = new CaasClient();
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@RequestMapping(value = "/models/{model}/configurations", method = RequestMethod.POST, produces="application/xml")
    public ResponseEntity<?> requestConfiguration(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
		
		String configurationRequest;
		try {
			configurationRequest = makeConfigurationRequest(selections, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
    	try {
			return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    }
	
	@RequestMapping(value = "/models/{model}/configurations/isconsistent", method = RequestMethod.POST)
    public ResponseEntity<?> isConsistent(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
		
		String configurationRequest;
		try {
			configurationRequest = makeConfigurationRequest(selections, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    	//Todo: all invalid configurations could return HTTP 400 to tell server errors and invalid configurations apart
    	try {
    		String response = caasClient.getConfiguration(configurationRequest, caasAddress);
    		
    		for(FeatureSelection selection : selections) { //TBD: do this nicer
    			if(!response.contains("\"" + selection.getType() + "\"")) {
    				return new ResponseEntity<>("no", HttpStatus.OK);
    			}
    		}
    		
			return new ResponseEntity<>("yes", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("no", HttpStatus.OK);
		}
    	
    }
	
	@RequestMapping(value = "/models/{model}/configurations/consequences", method = RequestMethod.POST)
    public ResponseEntity<?> findDirectConsequences(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
		
		String emptyConfigurationRequest;
		try {
			emptyConfigurationRequest = makeConfigurationRequest(new ArrayList<FeatureSelection>(), modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		String configurationRequest;
		try {
			configurationRequest = makeConfigurationRequest(selections, modelName);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    	try {
    		String emptyResponse = caasClient.getConfiguration(emptyConfigurationRequest, caasAddress);
    		String response = caasClient.getConfiguration(configurationRequest, caasAddress);
    		
    		FeatureSelection original = this.transform.xmlToFeatures(emptyResponse);
    		FeatureSelection modified = this.transform.xmlToFeatures(response);
    		FeatureSelection diff = new FeatureSelection();
    		
    		this.utils.diffFeatures(original, modified, diff);
    		
			return new ResponseEntity<>(diff.getFeatures().get(0), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    }
	

	private String makeConfigurationRequest(List<FeatureSelection> selections, String modelName) throws Exception {
		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
		
		if(model == null) {
			throw new Exception("Model not found");
		}
		
		ArrayList<String> features = new ArrayList<>(); //Make list of features from another type of list of features, TBD according to WP4 requirements
		for(FeatureSelection selection : selections) {
			features.add(selection.getType());
		}
		
    	try {
    		return transform.featuresToConfigurationRequest(features, model);
		} catch (Exception e) {
			throw new Exception("Failed to create configurationRequest (feature typos?): " + e.getMessage());
		}
	}

}

