package eu.openreq.mulperi.services;

import java.io.StringWriter;
import java.util.zip.DataFormatException;

import javax.management.IntrospectionException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Client used to send models and configurations to CaaS
 * 
 * @author iivorait
 * @author tlaurinen
 */
public class CaasClient {
	
	/**
	 * Sends the Kumbang model to the specified CaaS address
	 * @param modelName
	 * @param modelContent
	 * @param caasAddress
	 * @return response from the CaaS server (success or failure?)
	 * @throws Exception
	 */
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
		} catch (HttpClientErrorException e) {
			modelErrorHandling(e);
		}

		String result = response.getBody();
		System.out.println(result);
		return result;

	}

	/**
	 * Sends a request with configuration parameters to CaaS address
	 * @param configurationRequest XML
	 * @param caasAddress
	 * @return a working configuration if possible
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
		} catch (HttpClientErrorException e) {
			throw new Exception(e.getResponseBodyAsString());
		}

		String result = response.getBody();
		System.out.println(result);

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

	private void modelErrorHandling(HttpClientErrorException e) throws Exception {
		if (e.getStatusCode() == HttpStatus.CONFLICT
				&& e.getResponseBodyAsString().contains("There are no configurations that satisfy the given model.")) {
			throw new IntrospectionException(e.getResponseBodyAsString());
		}
		if (e.getStatusCode() == HttpStatus.BAD_REQUEST
				&& e.getResponseBodyAsString().contains("Parsing of model failed.")) {
			throw new DataFormatException(e.getResponseBodyAsString());
		}
		if (e.getStatusCode() != HttpStatus.CREATED) {
			throw new Exception(e.getResponseBodyAsString());
		}
	}

}
