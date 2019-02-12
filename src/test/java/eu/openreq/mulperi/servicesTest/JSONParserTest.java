package eu.openreq.mulperi.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.openreq.mulperi.models.json.Release;
import eu.openreq.mulperi.services.JSONParser;

public class JSONParserTest {

	static String jsonString;
	
	@BeforeClass
	public static void setUp() throws IOException, JSONException{		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"release_parsing_data.json"))); 
	}
	
	@Test
	public void parseReleaseVersionsTest() throws IOException, JSONException {
		JSONParser.parseToOpenReqObjects(jsonString);
		
		for (Release rel : JSONParser.releases) {
			System.out.println(rel.getId());
			System.out.println(rel.getRequirements());
		}
		
		assertTrue(true);
	}
	
}
