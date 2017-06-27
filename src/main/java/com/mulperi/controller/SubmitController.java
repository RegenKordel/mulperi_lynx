package com.mulperi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.reqif.SpecObject;
import com.mulperi.models.selections.AttributeSelection;
import com.mulperi.models.submit.Requirement;
import com.mulperi.services.CaasClient;
import com.mulperi.services.FormatTransformerService;
import com.mulperi.services.ReqifParser;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;

@SpringBootApplication
@RestController
@RequestMapping("submit")
public class SubmitController {

	private FormatTransformerService transform = new FormatTransformerService();

	@Value("${mulperi.caasAddress}")

    private String caasAddress;
	
	@RequestMapping(value = "/simpleModel", method = RequestMethod.POST)
    public ResponseEntity<?> simpleIn(@RequestBody List<Requirement> requirements) {
		String name = generateName(requirements);
		
        String kumbangModel = transform.SimpleToKumbang(name, requirements);
        
        return sendToCaas(name, kumbangModel);
    }
	
	@RequestMapping(value = "/reqifModel", method = RequestMethod.POST, consumes="application/xml")
    public ResponseEntity<?> reqifIn(@RequestBody String reqifXML) {
		ReqifParser parser = new ReqifParser();
		String name = generateName(reqifXML);
		Collection<SpecObject> specObjects = parser.parse(reqifXML).values();
		
        String kumbangModel = transform.ReqifToKumbang(name, specObjects);
        
        return sendToCaas(name, kumbangModel);
    }

	private String generateName(Object object) {
		int hashCode = object.hashCode();
		return "ID" + (hashCode > 0 ? hashCode : "_" + Math.abs(hashCode)); 
		//replace - with _, since Kumbang doesn't like hyphens
	}
	
	private ResponseEntity<?> sendToCaas(String name, String kumbangModel) {
		CaasClient client = new CaasClient();
		
		try {
			client.uploadConfigurationModel(name, kumbangModel, caasAddress);
			
		} catch(IntrospectionException e) {
            return new ResponseEntity<>("Impossible model\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(DataFormatException e) {
            return new ResponseEntity<>("Syntax error\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
            return new ResponseEntity<>("Configuration model upload failed.\n\n" + e.toString(), HttpStatus.BAD_REQUEST);
		} 
		
		return new ResponseEntity<>("Configuration model upload successful.\n\n - - - \n\n" + kumbangModel, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/selections", method = RequestMethod.POST)
	public String postSelectionsForConfiguration(@RequestBody ArrayList<AttributeSelection> selections,
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