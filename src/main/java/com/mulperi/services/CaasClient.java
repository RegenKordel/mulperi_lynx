package com.mulperi.services;

import org.springframework.web.client.RestTemplate;

public class CaasClient {

	public void uploadConfigurationModel(String name, String model) {
		RestTemplate rt = new RestTemplate();
		
	}
	
//	def upload_configuration_model(self, model_name, model):
//        path = '/KumbangConfigurator'
//        url = self.base_url + path
//        headers = {'content-type': 'application/xml'}
//
//        payload_list = []
//        payload_list.append('<xml>')
//        payload_list.append('<model name="')
//        payload_list.append(model_name)
//        payload_list.append('">')
//        payload_list.append(model)
//        payload_list.append('</model></xml>')
//        payload = ''.join(payload_list)
//
//        # Send request
//        response = self._send_request('post', url, data=payload, headers=headers, timeout=10)
//
//        if response.status_code == 201:
//            event_handler.add_event('Model %s sent to Caas: %s' % (model_name, payload))
//        else:
//            raise CaasInternalServerError(response.content)

}
