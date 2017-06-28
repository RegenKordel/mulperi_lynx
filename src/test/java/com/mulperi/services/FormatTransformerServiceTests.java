package com.mulperi.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mulperi.models.mulson.Relationship;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.services.FormatTransformerService;

public class FormatTransformerServiceTests {
	
	private FormatTransformerService transform = new FormatTransformerService();
	
	@Test
	public void smallTestCase() {
		
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setId("T1");
		Relationship rel = new Relationship();
		rel.setTargetId("R2");
		rel.setType("requires");
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(rel);
		req.setRelationships(relationships);
		requirements.add(req);
		
		assertEquals("Kumbang model Test\n"
			+"	 root component Test\n"
			+"	 root feature Test\n"
			+"\n"
			+"//---components-----\n"
			+"\n"
			+"component type Test {\n"
			+"}\n"
			+"\n"
			+"//---features-----\n"
			+"\n"
			+"feature type Test {\n"
			+"	subfeatures\n"
			+"		T1 t1[0-1];\n"
			+"	constraints\n"
			+"		present(t1) => present(r2);\n"
			+"}\n"
			+"\n"
			+"feature type T1 {\n"
			+"}\n"
			+"\n", transform.simpleToKumbang("Test", requirements));
		
	}
	
	
}
