package com.mulperi.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.mulperi.models.selections.AttributeSelection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CaasClient {

	public String uploadConfigurationModel(String modelName, String modelContent, String caasAddress) 
			throws Exception {
		
		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		String xmlString = modelToXML(modelName, modelContent);
		
		HttpEntity<String> entity = new HttpEntity<String>(xmlString, headers);
		
		ResponseEntity<String> response = null;
		
		System.out.println(entity);
		
		try {
			response = rt.postForEntity(caasAddress, entity, String.class);
		} catch (HttpServerErrorException e) {			
			modelErrorHandling(e);
		}
		
		String result = response.toString();
		System.out.println(result);
		return result;

	}
	
	public String getConfiguration(String modelName, ArrayList<AttributeSelection> selections, String caasAddress) throws Exception {
		
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		String xmlString = selectionsToXML(modelName, selections);
		
		HttpEntity<String> entity = new HttpEntity<String>(xmlString, headers);
		
		ResponseEntity<String> response = null;
		
		System.out.println(entity);
		
		try {
			response = rt.postForEntity(caasAddress, entity, String.class);
		} catch (HttpServerErrorException e) {			
			selectionErrorHandling(e);
		}
		
		String result = response.toString();
		System.out.println(result.toString());

		return result;
	}

	public String modelToXML(String modelName, String modelContent) throws XMLStreamException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();	
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("xml");
		xMLStreamWriter.writeStartElement("model");			
		xMLStreamWriter.writeAttribute("name", modelName);
		xMLStreamWriter.writeCharacters(modelContent);
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndDocument();
		
		xMLStreamWriter.flush();
		xMLStreamWriter.close();
		return stringWriter.getBuffer().toString();
	}
	
	public String selectionsToXML(String modelName, ArrayList<AttributeSelection> selections) throws XMLStreamException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();	
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("xml");
		xMLStreamWriter.writeStartElement("model");			
		xMLStreamWriter.writeAttribute("name", modelName);
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeStartElement("configuration");
		xMLStreamWriter.writeStartElement("feature");
		xMLStreamWriter.writeAttribute("name", "root");
		xMLStreamWriter.writeAttribute("type", "Car");
		for(AttributeSelection sel : selections) {
			xMLStreamWriter.writeStartElement("attribute");
			xMLStreamWriter.writeAttribute("name", sel.getName());;
			xMLStreamWriter.writeCharacters(sel.getValue());
			xMLStreamWriter.writeEndElement();
		}		
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndDocument();
		
		xMLStreamWriter.flush();
		xMLStreamWriter.close();
		return stringWriter.getBuffer().toString();
	}
	
	public void modelErrorHandling(HttpServerErrorException e) throws Exception {
		if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR 
				&& e.getResponseBodyAsString().contains("There are no configurations that satisfy the given model.")) {
			throw new IntrospectionException(e.getResponseBodyAsString());
		}
		if(e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR 
				&& e.getResponseBodyAsString().contains("Parsing of model failed.")) {
			throw new DataFormatException(e.getResponseBodyAsString());
		}
		if(e.getStatusCode() != HttpStatus.CREATED) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}
	
	private void selectionErrorHandling(HttpServerErrorException e) throws Exception {
		
	}

}
