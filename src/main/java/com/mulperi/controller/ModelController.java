package com.mulperi.controller;

import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.ParsedModel;
import com.mulperi.services.CaasClient;
import com.mulperi.services.KumbangModelGenerator;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class ModelController {
    
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String postDataForModel(@ModelAttribute("SpringWeb")ParsedModel data) {
    	KumbangModelGenerator generator = new KumbangModelGenerator();
		
		String kumbangModel = generator.generateKumbangModelString(data);
		
		CaasClient client = new CaasClient();
		
		try {
			client.uploadConfigurationModel(data.getModelName(), kumbangModel);
			
		}	catch(Exception e) {
            return "oops";            
		} 
        return kumbangModel;
    }
    
}
