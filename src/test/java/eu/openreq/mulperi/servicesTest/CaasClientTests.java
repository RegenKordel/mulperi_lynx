package eu.openreq.mulperi.servicesTest;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.openreq.mulperi.services.CaasClient;

public class CaasClientTests {

	
	@Test
	public void ClientFailsWithWrongAddress() {
		CaasClient client = new CaasClient();
		try {
			client.uploadConfigurationModel("test", "asd", "http://localhost:8080/");
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
		fail();
	}
	
	

}
