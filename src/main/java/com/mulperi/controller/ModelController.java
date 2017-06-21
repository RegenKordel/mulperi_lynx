package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.Configuration;
import com.mulperi.models.Feature;
import com.mulperi.models.Selection;
import com.mulperi.models.ParsedModel;
import com.mulperi.services.CaasClient;
import com.mulperi.services.KumbangModelGenerator;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class ModelController {
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String postDataForModel(@RequestBody ParsedModel model) {
    	
    	KumbangModelGenerator generator = new KumbangModelGenerator();
		
		String kumbangModel = generator.generateKumbangModelString(model);
		
		CaasClient client = new CaasClient();
		
		String address = "http://localhost:8080/kumbang.configurator.server/Kumb";
		
		try {
			client.uploadConfigurationModel(model.getModelName(), kumbangModel, address);
			
		}	catch(Exception e) {
            return "Couldn't upload the configuration model\n\n" + e; 
		} 
		return "Configuration model upload successful.\n\n - - - \n\n" + kumbangModel;
    }
    
    @RequestMapping(value = "/simple", method = RequestMethod.POST)
    public String postSelectionsForConfig(@RequestBody ArrayList<Selection> selections, @RequestParam 
    		String modelName) {
		
        CaasClient client = new CaasClient();
		
		String address = "http://localhost:8080/kumbang.configurator.server/Kumb";
		
		Configuration result = new Configuration("");
		
		try {
			result = client.getConfiguration(modelName, selections, address);
			
		}	catch(Exception e) {
            return "Couldn't receive any configurations\n\n" + e;            
		} 
        return "Configurations received.\n\n - - - \n\n" + result;
    }
    
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ParsedModel getExampleJSON() {
    	
    	ParsedModel model = new ParsedModel("Car");
		model.addFeature(new Feature("Motor"));
		model.addFeature(new Feature("Navigator"));
		model.addFeature(new Feature("Gearbox"));
		model.addFeature(new Feature("AutoOrManual"));
		model.getFeatures().get(0).addSubFeature(new Feature("Motor", "motor"));
		model.getFeatures().get(0).addSubFeature(new Feature("Navigator", "navigator", "0-1"));
		model.getFeatures().get(0).addSubFeature(new Feature("Gearbox", "gearbox"));
		model.getFeatures().get(3).addSubFeature(new Feature("AutoOrManual", "geartype", "0-1"));
		
		return model;
    }
    
}
