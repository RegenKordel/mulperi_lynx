package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.selections.FeatureSelection;
import com.mulperi.services.CaasClient;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@RestController
@RequestMapping("configurations")
public class ConfigurationController {
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	
	@RequestMapping(value = "/submitSelections", method = RequestMethod.POST)
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
