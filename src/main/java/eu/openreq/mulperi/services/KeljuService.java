package eu.openreq.mulperi.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import eu.openreq.mulperi.models.MurmeliAndDuplicates;
import fi.helsinki.ese.murmeli.ElementModel;

@Service
public class KeljuService {

	@Value("${mulperi.caasAddress}")
	private String caasAddress; 
	
	@Autowired
	RestTemplate rt; 
	
	@Autowired
	FormatTransformerService formatService;
	
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public ResponseEntity<String> murmeliModelToKeljuCaaS(String requirements) throws JSONException, 
			IOException, ParserConfigurationException {
		OpenReqJSONParser parser = new OpenReqJSONParser(requirements);
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model;
		String projectId = null;
		if(parser.getProjects()!=null) {
			projectId = parser.getProjects().get(0).getId();
			model = generator.initializeElementModel(parser.getRequirements(), parser.getDependencies(), projectId);
		}
		else	{
			model = generator.initializeElementModel(parser.getRequirements(), parser.getDependencies(), 
					parser.getRequirements().get(0).getId());
		}
		try {
			Date date = new Date();
			System.out.println("Sending " + projectId + " to KeljuCaas at " + date.toString());
			
			return this.postMurmeliToCaas(OpenReqJSONParser.parseToJson(model), caasAddress + "/importModelAndUpdateGraph");
		}
		catch (Exception e) {
			return new ResponseEntity<String>("Cannot send the model to KeljuCaas", HttpStatus.EXPECTATION_FAILED); //change to something else?
		}
	}
	
	public ResponseEntity<String> findTransitiveClosureOfRequirement(List<String> requirementId, 
			Integer layerCount) throws JSONException, IOException, ParserConfigurationException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String completeAddress = caasAddress + "/findTransitiveClosureOfElement";
		
		if (layerCount!=null) {
			completeAddress += "?layerCount=" + layerCount;
		}
		
		List<String> responses = new ArrayList<String>();
		
		try {
			for (String reqId : requirementId) {
				String response = rt.postForObject(completeAddress, reqId, String.class);
				responses.add(response);
			}
			JsonObject object = formatService.murmeliClosureToOpenReqJson(responses);
			return new ResponseEntity<>(object.toString(), HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
		catch (Exception e) {
			return new ResponseEntity<>("Error:\n " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	public ResponseEntity<String> consistencyCheckAndDiagnosis(String jsonString,
			boolean analysisOnly, int timeOut, boolean omitCrossProject, boolean omitReqRelDiag) 
					throws JSONException, IOException, ParserConfigurationException {
		
		String completeAddress = caasAddress + "/consistencyCheckAndDiagnosis?analysisOnly=" + analysisOnly 
				+ "&timeOut=" + timeOut  +"&omitCrossProject="+omitCrossProject
				+ "&omitReqRelDiag=" + omitReqRelDiag;
		MurmeliAndDuplicates murmeliModel = formatService.openReqJsonToMurmeli(jsonString);
		
		return postMurmeliToCaas(murmeliModel.getMurmeliString(), completeAddress);

	}
	
	public ResponseEntity<String> consistencyCheckForTransitiveClosure(List<String> requirementId, 
			 Integer layerCount, boolean analysisOnly, int timeOut, boolean omitCrossProject, boolean omitReqRelDiag) 
					throws JSONException, IOException, ParserConfigurationException {
		ResponseEntity<String> transitiveClosure = findTransitiveClosureOfRequirement(requirementId, layerCount);
		MurmeliAndDuplicates murmeliModel = formatService.openReqJsonToMurmeli(transitiveClosure.getBody().toString());

		ResponseEntity<String> response = postMurmeliToCaas(murmeliModel.getMurmeliString(), caasAddress + 
				"/consistencyCheckAndDiagnosis?analysisOnly=" + analysisOnly + "&timeOut=" + timeOut 
				+ "&omitCrossProject=" + omitCrossProject 
				+ "&omitReqRelDiag=" + omitReqRelDiag);
		JsonObject responseObject = gson.fromJson(response.getBody().toString(), JsonObject.class);
		responseObject.add("duplicates", murmeliModel.getDuplicatesString());;
		
		return new ResponseEntity<String>(gson.toJson(responseObject), response.getStatusCode());
		
	}
	
	
	public ResponseEntity<String> postMurmeliToCaas(String model, String completeAddress) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(model, headers);
		try {
			return rt.postForEntity(completeAddress, entity, String.class);
		} catch (ResourceAccessException e) {
			return new ResponseEntity<>("Request timed out", HttpStatus.REQUEST_TIMEOUT);
		} catch (HttpClientErrorException e) {
			return new ResponseEntity<>("Error:\n" + e.getResponseBodyAsString(), e.getStatusCode());
		}
	}

	
	public ResponseEntity<String> updateMurmeliModelInKeljuCaas(String requirements) throws JSONException, IOException, 
			ParserConfigurationException {
		
		OpenReqJSONParser parser = new OpenReqJSONParser(requirements);
		
		MurmeliModelGenerator generator = new MurmeliModelGenerator();
		ElementModel model;
		String projectId = null;
		if (parser.getProjects() != null) {
			projectId = parser.getProjects().get(0).getId();
			model = generator.initializeElementModel(parser.getRequirements(), parser.getDependencies(), projectId);
		} else {
			model = generator.initializeElementModel(parser.getRequirements(), parser.getDependencies(), 
					parser.getRequirements().get(0).getId());
		}
		
		try {
			Date date = new Date();
			System.out.println("Updating " + projectId + " in KeljuCaas at " + date.toString());
			
			return this.postMurmeliToCaas(OpenReqJSONParser.parseToJson(model), caasAddress + "/updateModel");
		} catch (Exception e) {
			return new ResponseEntity<>("Cannot send the model to KeljuCaas", HttpStatus.EXPECTATION_FAILED); //change to something else?
		}
	}
	
	
	
}
