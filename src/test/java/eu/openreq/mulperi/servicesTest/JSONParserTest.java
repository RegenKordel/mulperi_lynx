package eu.openreq.mulperi.servicesTest;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.openreq.mulperi.models.json.Dependency;
import eu.openreq.mulperi.models.json.Release;
import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.services.OpenReqJSONParser;

public class JSONParserTest {

	static String jsonString;
	
	@BeforeClass
	public static void setUp() throws IOException, JSONException{		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"release_parsing_data_nofixver.json"))); 
	}
	
	@Test
	public void parseReleaseVersionsTest() throws IOException, JSONException {
		OpenReqJSONParser parser = new OpenReqJSONParser(jsonString);
		
		for (Release rel : parser.getReleases()) {
			System.out.println(rel.getId());
			System.out.println(rel.getRequirements());
		}
		
		assertTrue(true);
	}
	
	@Test
	public void duplicateCombinationTest() throws IOException, JSONException {
		OpenReqJSONParser parser = new OpenReqJSONParser(jsonString);
		parser.combineDuplicates();
		
		for (Requirement req : parser.getFilteredRequirements()) {
			System.out.println(req.getId());
			System.out.println(req.getName());
		}
		for (Dependency dep : parser.getFilteredDependencies()) {
			System.out.println(dep.getDependency_score() + "_" + dep.getToid());
			System.out.println(dep.getDependency_type());
		}
		for (Release rel : parser.getReleases()) {
			System.out.println(rel.getId());
			System.out.println(rel.getRequirements());
		}
		
		assertTrue(true);
	}
	
}
