package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.Feature;
import com.mulperi.models.ParsedModel;

import com.mulperi.services.CaasClient;
import com.mulperi.services.KumbangModelGenerator;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
            return "Couldn't upload the configuration model";            
		} 
        return kumbangModel;
    }
    
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ParsedModel getExampleJSON() {
    	
    	ParsedModel model = new ParsedModel("Car");
		model.addFeature(new Feature("Motor"));
		model.addFeature(new Feature("Navigator"));
		model.addFeature(new Feature("Gearbox"));
		model.addFeature(new Feature("AutoManual"));
		model.getFeatures().get(0).addSubFeature(new Feature("Motor", "motor"));
		model.getFeatures().get(0).addSubFeature(new Feature("Navigator", "navigator", "0-1"));
		model.getFeatures().get(0).addSubFeature(new Feature("Gearbox", "gearbox"));
		model.getFeatures().get(3).addSubFeature(new Feature("AutoManual", "geartype", "0-1"));
		
		return model;
    }
    
}
