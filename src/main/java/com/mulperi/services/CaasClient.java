package com.mulperi.services;

import java.io.StringWriter;

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

public class CaasClient {
	
	
	public String uploadConfigurationModel(String name, String model, String caasAddress)
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
		attr.setValue(name);
		modelElement.setAttributeNode(attr);

		modelElement.appendChild(doc.createTextNode(model));
		
		HttpEntity<String> entity = new HttpEntity<String>(getStringFromDocument(doc), headers);

		System.out.println(entity.toString());
		ResponseEntity<String> response = rt.postForEntity(caasAddress, entity, String.class);
		result = response.toString();
		System.out.println(result.toString());

		return result;

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
