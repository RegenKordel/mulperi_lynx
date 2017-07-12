package com.mulperi.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mulperi.models.kumbang.Attribute;
import com.mulperi.models.kumbang.Constraint;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.kumbang.SubFeature;
import com.mulperi.repositories.ParsedModelRepository;
import com.mulperi.services.CaasClient;
import com.mulperi.services.FormatTransformerService;
import com.mulperi.services.KumbangModelGenerator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@Autowired
	private FormatTransformerService transform;
	
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public String postDataForModel(@RequestBody ParsedModel model) {
    	
    	KumbangModelGenerator generator = new KumbangModelGenerator();
		
		String kumbangModel = generator.generateKumbangModelString(model);
		
		CaasClient client = new CaasClient();
		
		try {
			client.uploadConfigurationModel(model.getModelName(), kumbangModel, caasAddress);
			parsedModelRepository.save(model);
		}	catch(Exception e) {
            return "Couldn't upload the configuration model\n\n" + e; 
		} 
		return "Configuration model upload successful.\n\n - - - \n\n" + kumbangModel;
    }
    
    @RequestMapping(value = "/test2", method = RequestMethod.POST)
    public String testDatabaseFeatures(@RequestBody String xml) {
    	
    	transform.xmlToFeatures(xml);
    	return "OK";
    }
    
    @ResponseBody
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ParsedModel getExampleJSON() {
    	
    	ParsedModel model = new ParsedModel("Car", "A car, best used for driving");
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
		SubFeature geartype = new SubFeature("Auto", "geartype", "0-1");
		geartype.addType("Manual");
		model.getFeatures().get(3).addSubFeature(geartype);
		model.getFeatures().get(0).addConstraint(new Constraint("Motor","Gearbox"));
		model.getFeatures().get(0).addAttribute(new Attribute("TestAtt", "testatt", values));
		model.getFeatures().get(1).addAttribute(new Attribute("EngineType", "enginetype", engine));
		
		return model;
    }
    
}
