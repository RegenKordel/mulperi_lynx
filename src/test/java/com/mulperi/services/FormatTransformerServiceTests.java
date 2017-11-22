package com.mulperi.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.mulson.Attribute;
import com.mulperi.models.mulson.Relationship;
import com.mulperi.models.mulson.Requirement;
import com.mulperi.models.selections.CalculationConstraint;
import com.mulperi.models.selections.FeatureSelection;
import com.mulperi.models.selections.Selections;

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
	public void someIllegalSymbolsToUnderScores() {
		
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setRequirementId("T-1 special");
		Relationship rel = new Relationship();
		rel.setTargetId("R2 -extra-");
		rel.setType("owns");
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(rel);
		req.setRelationships(relationships);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("R2 -extra-");
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		
		assertTrue(kbString.contains("T_1_special"));
		assertTrue(kbString.contains("R2__extra_"));
	}
	
	@Test
	public void notPresentWorks() {
		
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setRequirementId("R1");
		Relationship rel = new Relationship();
		rel.setTargetId("R2");
		rel.setType("incompatible");
		List<Relationship> relationships = new ArrayList<Relationship>();
		relationships.add(rel);
		req.setRelationships(relationships);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("R2");
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		
		assertTrue(kbString.contains("present(R1) => not present(R2);"));
	}
	
	@Test
	public void sameNameAttributesRenamed() {
		ArrayList<Requirement> requirements = new ArrayList<>();
		
		Requirement req = new Requirement();
		req.setRequirementId("T1");	
		List<Attribute> attList = new ArrayList<>();
		Attribute att = new Attribute("Test");
		List<String> attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("T2");
		attList = new ArrayList<>();
		att = new Attribute("Test");
		attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		req = new Requirement();
		req.setRequirementId("T3");
		attList = new ArrayList<>();
		att = new Attribute("Test");
		attValues = new ArrayList<>();
		attValues.addAll(Arrays.asList("1", "2", "3"));
		att.setValues(attValues);
		attList.add(att);
		req.setAttributes(attList);
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		String kbString = kumbangModelGenerator.generateKumbangModelString(pm);
		assertTrue(kbString.contains("feature type T1 {\n\tattributes\n\t\t"
				+ "Test Test;"));
		assertTrue(kbString.contains("feature type T2 {\n\tattributes\n\t\t"
				+ "Test2 Test;"));
		assertTrue(kbString.contains("feature type T3 {\n\tattributes\n\t\t"
				+ "Test3 Test;"));
	}
	
	@Test
	public void attributesArrangedDefaultFirst() {
		ArrayList<Requirement> requirements = new ArrayList<>();
		Requirement req = new Requirement();
		req.setRequirementId("T1");
		List<Attribute> attList = new ArrayList<>();
		Attribute att = new Attribute("Test");
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
	
	@Test
	public void calculationSelectionInConfiguration() throws Exception {
		Selections selections = new Selections();
		
		List<CalculationConstraint> calcCstrs = new ArrayList<>();
		CalculationConstraint cstr = new CalculationConstraint();
		cstr.setAttName("testatt");
		cstr.setOperator("=");
		cstr.setValue("3");
		calcCstrs.add(cstr);
		selections.setCalculationConstraints(calcCstrs);
		
		String configString = transform.featuresToConfigurationRequest(selections, new ParsedModel());
		
		assertTrue(configString.contains("<calculation attribute=\"testatt\" operator=\"=\">3</calculation>"));
	}
	
	@Test
	public void softSelectionInConfiguration() throws Exception {
		List<Requirement> requirements = new ArrayList<Requirement>();
		Requirement req = new Requirement();
		req.setRequirementId("R1");
		requirements.add(req);
		
		ParsedModel pm = transform.parseMulson("Test", requirements);
		
		List<FeatureSelection> featSels = new ArrayList<FeatureSelection>();
		FeatureSelection selection = new FeatureSelection();
		selection.setType("R1");
		selection.setName("R1");
		selection.setIsSoft(true);
		featSels.add(selection);
		
		Selections selections = new Selections();
		selections.setFeatureSelections(featSels);
		
		String configString = transform.featuresToConfigurationRequest(selections, pm);
		
		assertTrue(configString.contains("<feature name=\"R1\" soft=\"true\" type=\"R1\"/>"));
	}
	
}
