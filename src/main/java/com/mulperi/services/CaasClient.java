package com.mulperi.services;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CaasClient {

	public String uploadConfigurationModel(String name, String model, String kumbangAddress) {
		RestTemplate rt = new RestTemplate();
		
		String result = "nothing";
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		
		
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();

			Element rootElement = doc.createElement("model");
			doc.appendChild(rootElement);

			Attr attr = doc.createAttribute("name");
			attr.setValue(name);
			rootElement.setAttributeNode(attr);
			
			doc.createTextNode(model);
			
			HttpEntity<String> entity = new HttpEntity<String>(doc.toString(), headers);
			
			System.out.println(doc.toString());
			result = rt.postForObject(kumbangAddress, entity, String.class);
			System.out.println(result.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;

	}

	// def upload_configuration_model(self, model_name, model):
	// path = '/KumbangConfigurator'
	// url = self.base_url + path
	// headers = {'content-type': 'application/xml'}
	//
	// payload_list = []
	// payload_list.append('<xml>')
	// payload_list.append('<model name="')
	// payload_list.append(model_name)
	// payload_list.append('">')
	// payload_list.append(model)
	// payload_list.append('</model></xml>')
	// payload = ''.join(payload_list)
	//
	// # Send request
	// response = self._send_request('post', url, data=payload, headers=headers,
	// timeout=10)
	//
	// if response.status_code == 201:
	// event_handler.add_event('Model %s sent to Caas: %s' % (model_name,
	// payload))
	// else:
	// raise CaasInternalServerError(response.content)

}
