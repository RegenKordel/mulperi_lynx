package com.mulperi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.models.reqif.SpecObject;
import com.mulperi.models.selections.FeatureSelection;
import com.mulperi.repositories.ParsedModelRepository;
import com.mulperi.services.CaasClient;
import com.mulperi.services.FormatTransformerService;
import com.mulperi.services.ReqifParser;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;

@RestController
@RequestMapping("configuration")
public class ConfigurationController {

	private FormatTransformerService transform = new FormatTransformerService();
	
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@RequestMapping(value = "/request", method = RequestMethod.POST)
    public ResponseEntity<?> request(@RequestBody List<FeatureSelection> selections, @RequestParam("model") String modelName) {
		
		ParsedModel model = parsedModelRepository.findByModelName(modelName);
		
		if(model == null) {
			return new ResponseEntity<>("Model not found", HttpStatus.BAD_REQUEST);
		}
		
		ArrayList<String> features = new ArrayList<>(); //Make list of features from another type of list of features, TBD according to WP4 requirements
		for(FeatureSelection selection : selections) {
			features.add(selection.getType());
		}
    	
		CaasClient caasClient = new CaasClient();
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
	

	@RequestMapping(value = "/selections", method = RequestMethod.POST)
	public String postSelectionsForConfiguration(@RequestBody ArrayList<FeatureSelection> selections,
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