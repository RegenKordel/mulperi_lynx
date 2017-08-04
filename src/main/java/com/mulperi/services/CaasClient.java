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
import com.mulperi.models.selections.FeatureSelection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CaasClient {
	
	public String uploadConfigurationModel(String modelName, String modelContent, String caasAddress) throws Exception {

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

		String result = response.getBody();
		System.out.println(result);
		return result;

	}

	/**
	 * 
	 * @param configurationRequest XML
	 * @return
	 * @throws Exception
	 */
	public String getConfiguration(String configurationRequest, String caasAddress) throws Exception {

		RestTemplate rt = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);

		HttpEntity<String> entity = new HttpEntity<String>(configurationRequest, headers);

		ResponseEntity<String> response = null;

		System.out.println(entity);

		try {
			response = rt.postForEntity(caasAddress, entity, String.class);
		} catch (HttpServerErrorException e) {
			selectionErrorHandling(e);
		}

		String result = response.getBody();
		System.out.println(result);

		return result;
	}
	
	public String getConfiguration(String modelName, ArrayList<FeatureSelection> selections, String caasAddress) //DEPRECATED
			throws Exception {

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

	private String modelToXML(String modelName, String modelContent) throws XMLStreamException {
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

	private String selectionsToXML(String modelName, ArrayList<FeatureSelection> selections) throws XMLStreamException {
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

		xMLStreamWriter.writeStartDocument();
		xMLStreamWriter.writeStartElement("xml");
		xMLStreamWriter.writeStartElement("model");
		xMLStreamWriter.writeAttribute("name", modelName);
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeStartElement("configuration");
		for (FeatureSelection featSel : selections) {
			featureRecursion(featSel, xMLStreamWriter);
		}
		xMLStreamWriter.writeEndElement();
		xMLStreamWriter.writeEndDocument();

		xMLStreamWriter.flush();
		xMLStreamWriter.close();
		return stringWriter.getBuffer().toString();
	}

	private void featureRecursion(FeatureSelection featSel, XMLStreamWriter xMLStreamWriter) throws XMLStreamException {
		xMLStreamWriter.writeStartElement("feature");
		xMLStreamWriter.writeAttribute("name", featSel.getName());
		xMLStreamWriter.writeAttribute("type", featSel.getType());
		for (AttributeSelection attSel : featSel.getAttributes()) {
			xMLStreamWriter.writeStartElement("attribute");
			xMLStreamWriter.writeAttribute("name", attSel.getName());
			xMLStreamWriter.writeCharacters(attSel.getValue());
			xMLStreamWriter.writeEndElement();
		}
		for (FeatureSelection feats : featSel.getFeatures()) {
			featureRecursion(feats, xMLStreamWriter);
		}
		xMLStreamWriter.writeEndElement();
	}

	private void modelErrorHandling(HttpServerErrorException e) throws Exception {
		if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
				&& e.getResponseBodyAsString().contains("There are no configurations that satisfy the given model.")) {
			throw new IntrospectionException(e.getResponseBodyAsString());
		}
		if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR
				&& e.getResponseBodyAsString().contains("Parsing of model failed.")) {
			throw new DataFormatException(e.getResponseBodyAsString());
		}
		if (e.getStatusCode() != HttpStatus.CREATED) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}

	private void selectionErrorHandling(HttpServerErrorException e) throws Exception {
		if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}

}
