package eu.openreq.mulperi.test.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.openreq.mulperi.MulperiApplication;
import eu.openreq.mulperi.controllers.MulperiController;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=MulperiApplication.class)
@SpringBootTest
public class MulperiControllerTest {
	
	@Value("${mulperi.caasAddress}")
	private String caasAddress; 
	
	@Autowired
	MulperiController controller;
	
	@Autowired
	private RestTemplate rt;

	private MockMvc mockMvc;
	private MockRestServiceServer mockServer;
	
	private String jsonString;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Before
	public void setup() throws Exception {	
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		mockServer = MockRestServiceServer.createServer(rt);
		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"correct_input_data.json"))); 


	}

	@Test
	public void murmeliModelToKeljuCaasTest() throws Exception {
		mockServer.expect(requestTo(caasAddress + "/importModelAndUpdateGraph"))
				.andRespond(withSuccess("Nice", MediaType.TEXT_PLAIN));
		mockMvc.perform(post("/models/murmeliModelToKeljuCaas")
				.content(jsonString)
				.contentType(MediaType.APPLICATION_JSON))		
				.andExpect(status().isOk());
		mockServer.verify();
	}
	
	@Test
	public void updateMurmeliModelInKeljuCaasTest() throws Exception {
		mockServer.expect(requestTo(caasAddress + "/updateModel"))
				.andRespond(withSuccess("Nice", MediaType.TEXT_PLAIN));
		mockMvc.perform(post("/models/updateMurmeliModelInKeljuCaas")
				.content(jsonString)
				.contentType(MediaType.APPLICATION_JSON))		
				.andExpect(status().isOk());
		mockServer.verify();
	}

	@Test
	public void consistencyCheckAndDiagnosisTest() throws Exception {
		mockServer.expect(requestTo(caasAddress + "/consistencyCheckAndDiagnosis?analysisOnly=false&timeOut=0"))
				.andRespond(withSuccess("Nice", MediaType.TEXT_PLAIN));
		mockMvc.perform(post("/models/projects/consistencyCheckAndDiagnosis")
				.content(jsonString)
				.contentType(MediaType.APPLICATION_JSON))		
				.andExpect(status().isOk());
		mockServer.verify();
	}
	
//	@Test
//	public void consistencyCheckAndDiagnosisTimeoutTest() throws Exception {
//		mockServer.expect(requestTo(caasAddress + "/consistencyCheckAndDiagnosis?analysisOnly=false"))
//				.andRespond(withSuccess("Nice", MediaType.TEXT_PLAIN));
//		mockMvc.perform(post("/models/projects/consistencyCheckAndDiagnosis?timeOut=100")
//				.content(jsonString)
//				.contentType(MediaType.APPLICATION_JSON))		
//				.andExpect(status().isOk());
//		mockServer.verify();
//	}
	
	@Test
	public void transitiveClosureOfRequirementTest() throws Exception {
		List<String> id = Arrays.asList("req1");
		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String closureFromCaas = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"test_transitive_closure.json")));
		
		mockServer.expect(requestTo(caasAddress + "/findTransitiveClosureOfElement"))
				.andRespond(withSuccess(closureFromCaas, MediaType.TEXT_PLAIN));
		mockMvc.perform(post("/models/findTransitiveClosureOfRequirement")
				.content(mapper.writeValueAsString(id))
				.contentType(MediaType.APPLICATION_JSON))		
				.andExpect(status().isOk());
		mockServer.verify();
	}

	@Test
	public void consistencyCheckForTransitiveClosureTest() throws Exception {
		List<String> id = Arrays.asList("req1");
		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String closureFromCaas = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"test_transitive_closure.json"))); 
		String closureFromMallikas = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"closure_as_openreq.json"))); 
		
		mockServer.expect(requestTo(caasAddress + "/findTransitiveClosureOfElement"))
				.andRespond(withSuccess(closureFromCaas, MediaType.APPLICATION_JSON));
		
		mockServer.expect(requestTo(caasAddress + "/consistencyCheckAndDiagnosis?analysisOnly=false&timeOut=0"
				+ "&omitCrossProject=false"))
				.andRespond(withSuccess(closureFromMallikas, MediaType.TEXT_PLAIN));
		
		mockMvc.perform(post("/models/consistencyCheckForTransitiveClosure")
				.content(mapper.writeValueAsString(id))
				.contentType(MediaType.APPLICATION_JSON))		
				.andExpect(status().isOk());
		mockServer.verify();
	}
}
