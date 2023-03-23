package eu.openreq.mulperi.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.openreq.mulperi.models.json.*;
import eu.openreq.mulperi.models.json.Release.Status;
import eu.openreq.mulperi.services.MurmeliModelGenerator;
import eu.openreq.mulperi.services.OpenReqConverter;
import fi.helsinki.ese.murmeli.ElementModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MurmeliModelGeneratorTest {
	
	private MurmeliModelGenerator modelGenerator;
	List<Requirement> requirements;
	List<Dependency> dependencies;
	List<String> reqIds;
	
	private String projectName;
	private ElementModel model;
	
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    public List<Requirement> generateReqList(int reqCount) {
    	List<Requirement> reqList = new ArrayList<>();
    	List<RequirementPart> parts = generateReqParts();
    	
    	for (int i = 0; i<reqCount; i++) {
	    	Requirement req  = new Requirement();
	    	req.setId("" + i);
	    	req.setName("req" + i);
	    	req.setCreated_at(123 + i);
	    	
	    	req.setRequirement_type("epic");

	    	req.setStatus("open");

			parts.get(5).setText("open");
	    	
	    	req.setRequirementParts(parts);
	    	
	    	reqList.add(req);
	    	
	    	reqIds.add("" + i);
    	}
    	
    	return reqList;
    }
    
    public List<RequirementPart> generateReqParts() {
    	List<RequirementPart> parts = new ArrayList<>();
    	
    	parts.add(generatePart("0", "Resolution", "testResolution"));
    	parts.add(generatePart("1", "Platforms", "testPlatforms"));
    	parts.add(generatePart("2", "Versions", "testVersions"));
    	parts.add(generatePart("3", "Labels", "testLabels"));
    	parts.add(generatePart("4", "Environment", "testEnvironment"));
    	parts.add(generatePart("5", "Status", "testStatus"));
    	parts.add(generatePart("6", "FixVersion", "testFixVersion"));
    	parts.add(generatePart("7", "Components", "testComponents"));
    	
    	return parts;
    	
    }
    
    public RequirementPart generatePart(String id, String name, String text) {
    	RequirementPart part = new RequirementPart();
    	part.setId(id);
    	part.setName(name);
    	part.setText(text);
    	return part;
    	
    }
    
    public List<Dependency> generateDepList(int depCount) {
    	List<Dependency> depList = new ArrayList<>();
    	
    	for (int i = 0; i<depCount; i++) {
	    	Dependency dep  = new Dependency();
	    	dep.setFromid("" + i);
	    	dep.setToid("" + i + 1);
	    	dep.setCreated_at(123 + i);

	    	dep.setDependency_type("duplicate");
	    	
	    	int depValue = Math.min(i, Dependency_status.values().length-1);
	    	
	    	dep.setStatus(Dependency_status.values()[depValue]);
	    	
	    	dep.setDescription(Arrays.asList("description" + i));
	    	
	    	depList.add(dep);
    	}
    	
    	return depList;
    }
    
    @Before
    public void setUp() {
    	modelGenerator = new MurmeliModelGenerator();
    	
    	reqIds = new ArrayList<>();
    	
    	projectName = "project1";
    	
    	requirements = generateReqList(15);
    	
    	dependencies = generateDepList(15);
    	
    	model = modelGenerator.initializeElementModel(requirements, dependencies, projectName);
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void createdElementModelIsNotNull() {
    	assertTrue(model!=null);
    }
    
    @Test
    public void createdElementModelRootContainerNameCorrect() {
    	assertEquals(model.getRootContainer().getNameID(), "project1");
    }
    
    @Test
    public void createdElementModelRelationsSizeCorrect() {
    	assertEquals(model.getRelations().size(), 15); //Decompositions DO go to relations
    }
    
    @Test
    public void createdElementModelElementsSizeCorrect() {
    	assertEquals(model.getElements().size(), 29);
    }
    
    @Test
    public void createdElementModelConstraintsSizeCorrect() {
    	assertEquals(model.getConstraints().size(), 0);
    }
    
    @Test
    public void createdElementModelWithReleasesAndConstraints() {
    	List<String> constraints = Arrays.asList("limits", "requires");
    	List<Release> releases = new ArrayList<Release>();
    	
    	Release release = new Release();
    	release.setId("release1");
    	release.setCreated_at(1);
    	release.setCapacity(50);
    	release.setStatus(Status.NEW);
    	release.setRequirements(reqIds);
    	releases.add(release);
    	
    	model = modelGenerator.initializeElementModel(requirements, constraints, dependencies, releases, projectName);
    	
    	assertEquals(model.getConstraints().size(), 2);
    	assertEquals(model.getSubContainers().size(), 2);
    }
    
    @Test
    public void modelCanBeConverted() throws JsonProcessingException {
    	List<String> constraints = Arrays.asList("limits", "requires");
    	List<Release> releases = new ArrayList<Release>();
    	
    	Release release = new Release();
    	release.setId("release1");
    	release.setCreated_at(1);
    	release.setCapacity(50);
    	release.setStatus(Status.NEW);
    	release.setRequirements(reqIds);
    	releases.add(release);
    	
    	model = modelGenerator.initializeElementModel(requirements, constraints, dependencies, releases, projectName);
    	
    	OpenReqConverter converter = new OpenReqConverter(model);
    	assertTrue(converter.getRequirements().size()>0);
    }
    

}
