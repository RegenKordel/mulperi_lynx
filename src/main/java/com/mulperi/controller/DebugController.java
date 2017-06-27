package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.Attribute;
import com.mulperi.models.Constraint;
import com.mulperi.models.Feature;

import com.mulperi.models.ParsedModel;
import com.mulperi.models.SubFeature;
import com.mulperi.services.CaasClient;
import com.mulperi.services.KumbangModelGenerator;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//Methods for testing & such

@RestController
public class DebugController {
	
	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String postDataForModel(@RequestBody ParsedModel model) {
    	
    	KumbangModelGenerator generator = new KumbangModelGenerator();
		
		String kumbangModel = generator.generateKumbangModelString(model);
		
		CaasClient client = new CaasClient();
		
		try {
			client.uploadConfigurationModel(model.getModelName(), kumbangModel, caasAddress);
			
		}	catch(Exception e) {
            return "Couldn't upload the configuration model\n\n" + e; 
		} 
		return "Configuration model upload successful.\n\n - - - \n\n" + kumbangModel;
    }
    
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ParsedModel getExampleJSON() {
    	
    	ParsedModel model = new ParsedModel("Car", "A car, mostly used for driving");
		model.addFeature(new Feature("Motor", "This better work"));
		model.addFeature(new Feature("Navigator"));
		model.addFeature(new Feature("Gearbox", "Auto or manual?"));
		model.addFeature(new Feature("Auto"));
		model.addFeature(new Feature("Manual"));
		ArrayList<String> values = new ArrayList<String>();
		values.add("first");
		values.add("second");
		model.addAttribute(new Attribute("TestAtt", values));
		ArrayList<String> engine = new ArrayList<String>();
		engine.add("Gasoline");
		engine.add("Diesel");
		model.addAttribute(new Attribute("EngineType", engine));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Motor", "motor"));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Navigator", "navigator", "0-1"));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Gearbox", "gearbox"));
		model.getFeatures().get(3).addSubFeature(new SubFeature("(Auto, Manual)", "geartype", "0-1"));
		model.getFeatures().get(0).addConstraint(new Constraint("Motor","Gearbox"));
		model.getFeatures().get(0).addAttribute(new Attribute("TestAtt", "testatt", values));
		model.getFeatures().get(1).addAttribute(new Attribute("EngineType", "enginetype", engine));
		
		return model;
    }
    
}
