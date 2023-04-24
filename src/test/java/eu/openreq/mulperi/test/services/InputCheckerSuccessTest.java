package eu.openreq.mulperi.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.closedreq.bridge.models.json.Requirement;
import eu.openreq.mulperi.services.InputChecker;
import eu.openreq.mulperi.services.OpenReqJSONParser;

public class InputCheckerSuccessTest {

	static InputChecker checker;
	private static List<String> specReqIds;
	private static List<String> reqIds;
	static OpenReqJSONParser parser;

	@BeforeClass
	public static void setUp() throws IOException, JSONException{
		checker = new InputChecker();
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"correct_input_data.json"))); 
		parser = new OpenReqJSONParser(jsonString);
		
		
		List<String> specReqList = new ArrayList<String>();
		for (String specReqId : parser.getProject().getSpecifiedRequirements()) {
			specReqList.add(specReqId);
		}
		specReqIds = specReqList;
		
		
		List<String> reqIdList = new ArrayList<String>();
		for (Requirement req : parser.getRequirements()) {
			reqIdList.add(req.getId());
		}
		reqIds = reqIdList;
		
	}
	
	@Test
	public void noNegativeEffortRequirements() {
		assertTrue(checker.noNegativeEffortRequirements(parser.getRequirements()));
	}
	
	@Test
	public void noNegativeCapacityReleases() {
		assertTrue(checker.noNegativeCapacityReleases(parser.getReleases()));
	}
	
	@Test
	public void allSpecifiedRequirementsIncluded() {
		assertTrue(checker.allSpecifiedRequirementsIncluded(specReqIds, reqIds));
	}
	
	@Test
	public void onlySpecifiedRequirements() {
		assertTrue(checker.onlySpecifiedRequirements(reqIds, specReqIds));
	}
	
	@Test
	public void allSpecifiedRequirementsInReleases() {
		assertTrue(checker.allSpecifiedRequirementsInReleases(specReqIds, 
				parser.getReleases()));
	}

	@Test
	public void allReleaseRequirementsIncluded() {
		assertTrue(checker.allReleaseRequirementsIncluded(parser.getReleases(), specReqIds, reqIds));
	}
	
	@Test
	public void allDependencyRequirementsIncluded() {
		assertTrue(checker.allDependencyRequirementsIncluded(parser.getDependencies(), specReqIds, reqIds));
	}
	
	@Test
	public void noDuplicateDependencies() {
		assertTrue(checker.noDuplicateDependencies(parser.getDependencies()));
	}
	
//	@Test
//	public void releasesInOrder() {
//		assertTrue(checker.releasesInOrder(JSONParser.releases));
//	}
	
	@Test
	public void requirementNotInMultipleReleases() {
		assertTrue(checker.requirementNotInMultipleReleases(parser.getReleases()));
	}
	
	@Test
	public void checkInput() throws JSONException {
		assertEquals(checker.checkInput(parser.getProject(), parser.getRequirements(), 
				parser.getDependencies(), parser.getReleases()), "OK");
	}
		
}
