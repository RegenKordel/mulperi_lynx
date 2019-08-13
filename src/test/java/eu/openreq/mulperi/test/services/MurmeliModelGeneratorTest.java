package eu.openreq.mulperi.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import eu.openreq.mulperi.models.json.*;
import eu.openreq.mulperi.services.MurmeliModelGenerator;
import fi.helsinki.ese.murmeli.ElementModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MurmeliModelGeneratorTest {
	
	private MurmeliModelGenerator modelGenerator;
	private Requirement req1;
	private Requirement req2;
	private Requirement req3;
	private Requirement req4;
	private Requirement req5;
	private Dependency dep1;
	private Dependency dep2;
	private Dependency dep3;
	List<Requirement> requirements;
	List<Dependency> dependencies;
	
	private String projectName;
	private ElementModel model;
	
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    	modelGenerator = new MurmeliModelGenerator();
    	req1 = new Requirement();
    	req1.setId("001");
    	req1.setName("req1");
    	req1.setCreated_at(123);
    	req1.setRequirement_type(Requirement_type.ISSUE);
    	req1.setStatus(Requirement_status.ACCEPTED);
    	
    	req2 = new Requirement();
    	req2.setId("002");
    	req2.setName("req2");
    	req2.setCreated_at(1234);
    	req2.setRequirement_type(Requirement_type.BUG);
    	req2.setStatus(Requirement_status.ACCEPTED);
    	
    	req3 = new Requirement();
    	req3.setId("003");
    	req3.setName("req3");
    	req3.setCreated_at(12345);
    	req3.setRequirement_type(Requirement_type.EPIC);
    	req3.setStatus(Requirement_status.ACCEPTED);
    	
    	req4 = new Requirement();
    	req4.setId("004");
    	req4.setName("req4");
    	req4.setCreated_at(123456);
    	req4.setRequirement_type(Requirement_type.FUNCTIONAL);
    	req4.setStatus(Requirement_status.RECOMMENDED);
    	
    	req5 = new Requirement();
    	req5.setId("003");
    	req5.setName("req3");
    	req5.setCreated_at(12345);
    	req5.setRequirement_type(Requirement_type.NON_FUNCTIONAL);
    	req5.setStatus(Requirement_status.DEFERRED);
    	
    	
    	dep1 = new Dependency();
    	dep1.setCreated_at(12);
    	dep1.setDependency_type(Dependency_type.DECOMPOSITION);
    	dep1.setFromid(req3.getId());
    	dep1.setToid(req1.getId());
    	
    	dep2 = new Dependency();
    	dep2.setCreated_at(12);
    	dep2.setDependency_type(Dependency_type.REQUIRES);
    	dep2.setFromid(req2.getId());
    	dep2.setToid(req1.getId());
    	
    	dep3 = new Dependency();
    	dep3.setCreated_at(12);
    	dep3.setDependency_type(Dependency_type.DAMAGES);
    	dep3.setFromid(req4.getId());
    	dep3.setToid(req5.getId());
    	   
    	requirements = new ArrayList<Requirement>();
    	dependencies = new ArrayList<Dependency>();
    	
    	requirements.add(req1);
    	requirements.add(req2);
    	requirements.add(req3);
    	requirements.add(req4);
    	requirements.add(req5);
    	
    	dependencies.add(dep1);
    	dependencies.add(dep2);
    	dependencies.add(dep3);
    	
    	projectName = "project1";
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
    	assertEquals(model.getRelations().size(), 3); //Decompositions DO go to relations
    }
    
    @Test
    public void createdElementModelElementsSizeCorrect() {
    	assertEquals(model.getElements().size(), 4);
    }
    
    @Test
    public void createdElementModelConstraintsSizeCorrect() {
    	assertEquals(model.getConstraints().size(), 0);
    }

}
