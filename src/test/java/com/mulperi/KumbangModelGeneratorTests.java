package com.mulperi;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.mulperi.models.Feature;
import com.mulperi.models.ParsedModel;
import com.mulperi.services.KumbangModelGenerator;

public class KumbangModelGeneratorTests {
	
	ParsedModel model;
	KumbangModelGenerator kmg;
	String result;

	@Before
	public void initialize() {
		this.model = new ParsedModel("Car");
		this.model.addFeature(new Feature("Motor"));
		this.model.addFeature(new Feature("Navigator"));
		this.model.addFeature(new Feature("Gearbox"));
		this.model.addFeature(new Feature("Auto Manual"));
		this.model.getFeatures().get(0).addSubFeature(new Feature("Motor", "motor"));
		this.model.getFeatures().get(0).addSubFeature(new Feature("Navigator", "navigator [0-1]"));
		this.model.getFeatures().get(0).addSubFeature(new Feature("Gearbox", "gearbox"));
		this.model.getFeatures().get(3).addSubFeature(new Feature("Auto Manual", "geartype [0-1]"));
		this.kmg = new KumbangModelGenerator();
		this.result = kmg.generateKumbangModelString(model);
	}
	
	@Test
	public void printResultStringAndIsNotEmpty() {
		System.out.println(result);
		assertTrue(!this.result.isEmpty());
	}
	
	@Test
	public void simpleFeatureTest() {	
		assertTrue(result.contains("feature type Motor"));
	}
	
	@Test
	public void simpleSubfeatureTest() {		
		assertTrue(result.contains("Navigator navigator [0-1]"));
	}
	
	@Test
	public void wholeFeatureShowsProper() {	
		
		assertTrue(result.contains("feature type Car {\n\tsubfeatures\n\t\t"
				+ "Motor motor;\n\t\tNavigator navigator [0-1];\n\t\t"
				+ "Gearbox gearbox;\n}"));
	}
	
}
