package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.ParsedModel;
import com.mulperi.services.CaasClient;
import com.mulperi.services.KumbangModelGenerator;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ModelController {
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String postDataForModel(@RequestParam ParsedModel data) {
    	KumbangModelGenerator generator = new KumbangModelGenerator();
		
		String kumbangModel = generator.generateKumbangModelString(data);
		
		CaasClient client = new CaasClient();
		
		String kumbangAddress = "localhost:8080/KumbangConfigurator";
		
		try {
			client.uploadConfigurationModel(data.getModelName(), kumbangModel, kumbangAddress);
			
		}	catch(Exception e) {
            return "Couldn't upload the configuration model";            
		} 
        return kumbangModel;
    }
    
}
