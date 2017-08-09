package com.mulperi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.models.reqif.SpecObject;
import com.mulperi.repositories.ParsedModelRepository;
import com.mulperi.services.CaasClient;
import com.mulperi.services.FormatTransformerService;
import com.mulperi.services.KumbangModelGenerator;
import com.mulperi.services.ReqifParser;

import java.util.Collection;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;

@SpringBootApplication
@RestController
@RequestMapping("models")
public class ModelController {

	private FormatTransformerService transform = new FormatTransformerService();
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();

	@Value("${mulperi.caasAddress}")
    private String caasAddress;
	
	@Autowired
	private ParsedModelRepository parsedModelRepository;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
    public List<ParsedModel> modelList() {
        return parsedModelRepository.findAll();
    }
	@RequestMapping(value = "/mulson", method = RequestMethod.POST)
    public ResponseEntity<?> chocoMulson(@RequestBody List<Requirement> requirements) {
		
		String name = generateName(requirements);
		
        ParsedModel pm = transform.parseMulson(name, requirements);
		
		return sendModelToCaasAndSave(pm, caasAddress);
    }
	
	@RequestMapping(value = "/reqif", method = RequestMethod.POST, consumes="application/xml")
    public ResponseEntity<?> reqif(@RequestBody String reqifXML) {
		ReqifParser parser = new ReqifParser();
		String name = generateName(reqifXML);
		
		try {
			Collection<SpecObject> specObjects = parser.parse(reqifXML).values();
			ParsedModel pm = transform.parseReqif(name, specObjects);
	        return sendModelToCaasAndSave(pm, caasAddress);
		} catch (Exception e) { //ReqifParser error
			return new ResponseEntity<>("Syntax error in ReqIF\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
    }

	private String generateName(Object object) {
		int hashCode = object.hashCode();
		return "ID" + (hashCode > 0 ? hashCode : "_" + Math.abs(hashCode)); 
		//replace - with _, since Kumbang doesn't like hyphens
	}
	
	private ResponseEntity<?> sendModelToCaasAndSave(ParsedModel pm, String caasAddress) {
		
		pm.rolesForConstraints();
		
		String kumbangModel = kumbangModelGenerator.generateKumbangModelString(pm);
		CaasClient client = new CaasClient();
		
		String result = new String();
		
		try {
			result = client.uploadConfigurationModel(pm.getModelName(), kumbangModel, caasAddress);
		} catch(IntrospectionException e) {
            return new ResponseEntity<>("Impossible model\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(DataFormatException e) {
            return new ResponseEntity<>("Syntax error\n\n" + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch(Exception e) {
            return new ResponseEntity<>("Configuration model upload failed.\n\n" + e.toString(), HttpStatus.BAD_REQUEST);
		} 
		
		//Save model to database if send successful
		parsedModelRepository.save(pm);
		
		return new ResponseEntity<>("Configuration model upload successful.\n\n - - - \n\n" + result, HttpStatus.CREATED);
	}

}