package eu.openreq.mulperi.servicesTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.openreq.mulperi.models.json.Requirement;
import eu.openreq.mulperi.services.InputChecker;
import eu.openreq.mulperi.services.JSONParser;

public class InputCheckerFailTest {

	static InputChecker checker;
	private static List<String> specReqIds;
	private static List<String> reqIds;
	
	@BeforeClass
	public static void setUp() throws IOException, JSONException{
		checker = new InputChecker();
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"failing_input_data.json"))); 
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
		assertFalse(checker.noNegativeEffortRequirements(JSONParser.requirements));
	}
	
	@Test
	public void noNegativeCapacityReleases() {
		assertFalse(checker.noNegativeCapacityReleases(JSONParser.releases));
	}
	
	@Test
	public void allSpecifiedRequirementsIncluded() {
		assertFalse(checker.allSpecifiedRequirementsIncluded(specReqIds, reqIds));
	}
	
	@Test
	public void onlySpecifiedRequirements() {
		assertFalse(checker.onlySpecifiedRequirements(reqIds, specReqIds));
	}
	
	@Test
	public void allSpecifiedRequirementsInReleases() {
		assertFalse(checker.allSpecifiedRequirementsInReleases(specReqIds, 
				JSONParser.releases));
	}

	@Test
	public void allReleaseRequirementsIncluded() {
		assertFalse(checker.allReleaseRequirementsIncluded(JSONParser.releases, specReqIds, reqIds));
	}
	
	@Test
	public void allDependencyRequirementsIncluded() {
		assertFalse(checker.allDependencyRequirementsIncluded(JSONParser.dependencies, specReqIds, reqIds));
	}
	
	@Test
	public void noDuplicateDependencies() {
		assertFalse(checker.noDuplicateDependencies(JSONParser.dependencies));
	}
	
//	@Test
//	public void releasesInOrder() {
//		assertFalse(checker.releasesInOrder(JSONParser.releases));
//	}
	
	@Test
	public void requirementNotInMultipleReleases() {
		assertFalse(checker.requirementNotInMultipleReleases(JSONParser.releases));
	}
	
	@Test
	public void checkInput() throws JSONException {
		assertFalse(checker.checkInput(JSONParser.project, JSONParser.requirements, 
				JSONParser.dependencies, JSONParser.releases).equals("OK"));
	}
		
}
