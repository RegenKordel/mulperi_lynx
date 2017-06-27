package com.mulperi.services;

import static org.junit.Assert.*;

import org.junit.Test;

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
