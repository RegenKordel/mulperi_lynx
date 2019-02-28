package eu.openreq.mulperi.servicesTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.services.InputChecker;
import eu.openreq.mulperi.services.JSONParser;

public class InputCheckerSuccessTest {

	static InputChecker checker;
	private static List<String> specReqIds;
	private static List<String> reqIds;
	
	@BeforeClass
	public static void setUp() throws IOException, JSONException{
		checker = new InputChecker();
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"correct_input_data.json"))); 
		JSONParser.parseToOpenReqObjects(jsonString);
		
		
		List<String> specReqList = new ArrayList<String>();
		for (String specReqId : JSONParser.project.getSpecifiedRequirements()) {
			specReqList.add(specReqId);
		}
		specReqIds = specReqList;
		
		
		List<String> reqIdList = new ArrayList<String>();
		for (Requirement req : JSONParser.requirements) {
			reqIdList.add(req.getId());
		}
		reqIds = reqIdList;
		
	}
	
	@Test
	public void noNegativeEffortRequirements() {
		assertTrue(checker.noNegativeEffortRequirements(JSONParser.requirements));
	}
	
	@Test
	public void noNegativeCapacityReleases() {
		assertTrue(checker.noNegativeCapacityReleases(JSONParser.releases));
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
				JSONParser.releases));
	}

	@Test
	public void allReleaseRequirementsIncluded() {
		assertTrue(checker.allReleaseRequirementsIncluded(JSONParser.releases, specReqIds, reqIds));
	}
	
	@Test
	public void allDependencyRequirementsIncluded() {
		assertTrue(checker.allDependencyRequirementsIncluded(JSONParser.dependencies, specReqIds, reqIds));
	}
	
	@Test
	public void noDuplicateDependencies() {
		assertTrue(checker.noDuplicateDependencies(JSONParser.dependencies));
	}
	
//	@Test
//	public void releasesInOrder() {
//		assertTrue(checker.releasesInOrder(JSONParser.releases));
//	}
	
	@Test
	public void requirementNotInMultipleReleases() {
		assertTrue(checker.requirementNotInMultipleReleases(JSONParser.releases));
	}
	
	@Test
	public void checkInput() throws JSONException {
		assertEquals(checker.checkInput(JSONParser.project, JSONParser.requirements, 
				JSONParser.dependencies, JSONParser.releases), "OK");
	}
		
}
