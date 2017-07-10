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

import java.util.ArrayList;
import java.util.List;

@RestController
public class ConfigurationController {

	private FormatTransformerService transform = new FormatTransformerService();
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	private CaasClient caasClient = new CaasClient();
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@RequestMapping(value = "/models/{model}/configurations", method = RequestMethod.POST)
    public ResponseEntity<?> requestConfiguration(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
		
		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
		
		if(model == null) {
			return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<String> features = new ArrayList<>(); //Make list of features from another type of list of features, TBD according to WP4 requirements
		for(FeatureSelection selection : selections) {
			features.add(selection.getType());
		}
		
		String configurationRequest;
    	try {
    		configurationRequest = transform.featuresToConfigurationRequest(features, model);
		} catch (Exception e) {
			return new ResponseEntity<>("Failed to create configurationRequest (feature typos?): " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
    	try {
			return new ResponseEntity<>(caasClient.getConfiguration(configurationRequest, caasAddress), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("Configuration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    	
    }
	
	@RequestMapping(value = "/models/{model}/configurations/isconsistent", method = RequestMethod.POST)
    public ResponseEntity<?> isConsistent(@RequestBody List<FeatureSelection> selections, @PathVariable("model") String modelName) {
		
		ParsedModel model = parsedModelRepository.findFirstByModelName(modelName);
		
		if(model == null) {
			return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<String> features = new ArrayList<>(); //Make list of features from another type of list of features, TBD according to WP4 requirements
		for(FeatureSelection selection : selections) {
			features.add(selection.getType());
		}
		
		String configurationRequest;
    	try {
    		configurationRequest = transform.featuresToConfigurationRequest(features, model);
		} catch (Exception e) {
			return new ResponseEntity<>("Failed to create configurationRequest (feature typos?): " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
    	//TDB: Notice copy-paste above
    	
    	//Todo: all invalid configurations could return HTTP 400 to tell server errors and invalid configurations apart
    	try {
    		String response = caasClient.getConfiguration(configurationRequest, caasAddress);
    		
    		for(String feature : features) { //TBD: do this nicer
    			if(!response.contains("\"" + feature + "\"")) {
    				return new ResponseEntity<>("no", HttpStatus.OK);
    			}
    		}
    		
			return new ResponseEntity<>("yes", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("no", HttpStatus.OK);
		}
    	
    }

	
	@RequestMapping(value = "/selections", method = RequestMethod.POST)
	public String postSelectionsForConfiguration(@RequestBody ArrayList<FeatureSelection> selections, //DEPRECATED
			@RequestParam("modelName") String modelName) { 

		CaasClient client = new CaasClient();

		String result = "";

		try {
			result = client.getConfiguration(modelName, selections, caasAddress);

		} catch (Exception e) {
			return "Configuration failed.\n\n" + e;
		}
		return "Configuration successful.\n\n - - - \n\n" + result;
	}

}

