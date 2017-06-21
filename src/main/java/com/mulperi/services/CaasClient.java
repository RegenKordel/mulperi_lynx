package com.mulperi.services;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mulperi.models.Configuration;
import com.mulperi.models.Selection;

public class CaasClient {

	public String uploadConfigurationModel(String modelName, String model, String caasAddress)
			throws ParserConfigurationException, TransformerException {
		RestTemplate rt = new RestTemplate();

		String result = "nothing";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		//String hack = "<xml><model name=\""+ name + "\">" + model + "</model></xml>";
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder= dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
				
		Element rootElement = doc.createElement("xml");
		doc.appendChild(rootElement);
		Element modelElement = doc.createElement("model");
		rootElement.appendChild(modelElement);
		
		Attr attr = doc.createAttribute("name");
		attr.setValue(modelName);
		modelElement.setAttributeNode(attr);

		modelElement.appendChild(doc.createTextNode(model));
		
		HttpEntity<String> entity = new HttpEntity<String>(getStringFromDocument(doc), headers);

		System.out.println(entity.toString());
		ResponseEntity<String> response = rt.postForEntity(caasAddress, entity, String.class);
		result = response.toString();
		System.out.println(result.toString());

		return result;

	}
	
	public Configuration getConfiguration(String modelName, ArrayList<Selection> selections, String address) throws TransformerException, ParserConfigurationException {
		RestTemplate rt = new RestTemplate();

		String result = "nothing";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder= dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
				
		Element rootElement = doc.createElement("xml");
		doc.appendChild(rootElement);
		Element modelElement = doc.createElement("model");
		rootElement.appendChild(modelElement);
		
		Attr attr = doc.createAttribute("name");
		attr.setValue(modelName);
		modelElement.setAttributeNode(attr);

		Element configElement = doc.createElement("configuration");
		Element featureElement = doc.createElement("feature");
		
		Attr featName = doc.createAttribute("name");
		featName.setValue("root");
		featureElement.setAttributeNode(featName);
		
		Attr featType = doc.createAttribute("type");
		featType.setValue("Status");
		featureElement.setAttributeNode(featType);
		
		for(Selection sel : selections) {
			Element selectionElement = doc.createElement("attribute");
			
			Attr selName = doc.createAttribute("name");
			featName.setValue("root");
			selectionElement.setAttributeNode(featName);
			
			Attr selType = doc.createAttribute("type");
			featType.setValue(sel.getParam());
			selectionElement.setAttributeNode(featType);
		}
		
		modelElement.appendChild(configElement);
		
		HttpEntity<String> entity = new HttpEntity<String>(getStringFromDocument(doc), headers);

		System.out.println(entity.toString());
		ResponseEntity<String> response = rt.postForEntity(address, entity, String.class);
		result = response.toString();
		System.out.println(result.toString());

		return new Configuration("test");
	}
	
	public static String getStringFromDocument(Document doc) throws TransformerException {
		StringWriter writer = new StringWriter();
		DOMSource domSource = new DOMSource(doc);
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer();
	    transformer.transform(domSource, result);
	    return writer.toString();
	}

}
