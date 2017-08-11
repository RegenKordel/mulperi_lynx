package com.mulperi.services;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mulperi.models.kumbang.Attribute;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.mulson.Relationship;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.services.FormatTransformerService;

public class FormatTransformerServiceTests {
	
	private FormatTransformerService transform = new FormatTransformerService();
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();
	
	@Test
	public void smallTestCase() {
		
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setRequirementId("T1");
		Relationship rel = new Relationship();
		rel.setTargetId("R2");
		rel.setType("requires");
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(rel);
		req.setRelationships(relationships);
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		
		
		
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
			+"		T1 T1[0-1];\n"
			+"	constraints\n"
			+"		present(T1) => present(R2);\n"
			+"}\n"
			+"\n"
			+"feature type T1 {\n"
			+"}\n"
			+"\n", kbString);
		
	}
	
	@Test
	public void sameNameAttributesRenamed() {
		ArrayList<Requirement> requirements = new ArrayList<>();
		
		Requirement req = new Requirement();
		req.setRequirementId("T1");	
		List<Attribute> attList = new ArrayList<>();
		Attribute att = new Attribute("Test", "test");
		List<String> attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("T2");
		attList = new ArrayList<>();
		att = new Attribute("Test", "test");
		attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("T3");
		attList = new ArrayList<>();
		att = new Attribute("Test", "test");
		attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		assertTrue(kbString.contains("feature type T1 {\n\tattributes\n\t\t"
				+ "Test test;"));
		assertTrue(kbString.contains("feature type T2 {\n\tattributes\n\t\t"
				+ "Test2 test;"));
		assertTrue(kbString.contains("feature type T3 {\n\tattributes\n\t\t"
				+ "Test3 test;"));
	}
	
	@Test
	public void attributesArrangedDefaultFirst() {
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setRequirementId("T1");
		List<Attribute> attList = new ArrayList<>();
		Attribute att = new Attribute("Test", "test");
		List<String> attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		assertFalse(kbString.contains("attribute type Test = {\n\t2,"));
		
		requirements = new ArrayList<>();	
		attList = new ArrayList<Attribute>();
		
		att.setDefaultValue("2");	
		
		attList.add(att);	
		req.setAttributes(attList);	
		requirements.add(req);		
		
		pm = transform.parseMulson("Test", requirements);
		
		kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		assertTrue(kbString.contains("attribute type Test = {\n\t2,"));
	}
	
	
}
