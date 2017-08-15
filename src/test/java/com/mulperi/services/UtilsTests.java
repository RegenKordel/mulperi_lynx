package com.mulperi.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.junit.Test;

import com.mulperi.models.kumbang.Attribute;
import com.mulperi.models.kumbang.Constraint;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.kumbang.SubFeature;
import com.mulperi.models.selections.FeatureSelection;

public class UtilsTests {

	private FormatTransformerService transform = new FormatTransformerService();
	private Utils utils = new Utils();
	
//	@Test
//	public void testDiffFeatures() throws FileNotFoundException {
//		Scanner scanner = new Scanner( new File("src/test/resources/configuration1.xml") );
//		String text = scanner.useDelimiter("\\A").next();
//		
//		FeatureSelection root1 = transform.xmlToFeatureSelection(text);
//		
//		scanner = new Scanner( new File("src/test/resources/configuration2.xml") );
//		text = scanner.useDelimiter("\\A").next();
//		
//		FeatureSelection root2 = transform.xmlToFeatureSelection(text);
//		
//		FeatureSelection diff = new FeatureSelection();
//		
//		utils.diffFeatures(root1, root2, diff);
//		
//		assertEquals("(null,null)/(root,Car1234-testatt=second)/(motor,Motor-enginetype=Gasoline)/(gearbox,Gearbox)/(geartype,Manual)/", diff.getFullContentString());
//	}

	@Test
	public void testSetDefaults() throws FileNotFoundException {
		ParsedModel model = new ParsedModel("Car", "A car, best used for driving");
		model.addFeature(new Feature("Motor", "This better work"));
		model.addFeature(new Feature("Navigator"));
		model.addFeature(new Feature("Gearbox", "Auto or manual?"));
		model.addFeature(new Feature("Auto"));
		model.addFeature(new Feature("Manual"));
		ArrayList<String> values = new ArrayList<String>();
		values.add("first");
		values.add("second");
		model.addAttribute(new Attribute("TestAtt", values));
		ArrayList<String> engine = new ArrayList<String>();
		engine.add("Gasoline");
		engine.add("Diesel");
		model.addAttribute(new Attribute("EngineType", engine));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Motor", "motor"));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Navigator", "navigator", "0-1"));
		model.getFeatures().get(0).addSubFeature(new SubFeature("Gearbox", "gearbox"));
		SubFeature geartype = new SubFeature("Auto", "geartype", "0-1");
		geartype.addType("Manual");
		model.getFeatures().get(3).addSubFeature(geartype);
		model.getFeatures().get(0).addConstraint(new Constraint("Motor","Gearbox"));
		model.getFeatures().get(0).addAttribute(new Attribute("TestAtt", values));

		Attribute defaultAttributeTest = new Attribute("EngineType", engine);
		defaultAttributeTest.setDefaultValue("Diesel");
		model.getFeatures().get(1).addAttribute(defaultAttributeTest);
		
		ArrayList<FeatureSelection> selections = new ArrayList<>(); 
		FeatureSelection selection1 = new FeatureSelection();
		selection1.setType("Manual");
		selections.add(selection1);
		FeatureSelection request = this.transform.listOfFeatureSelectionsToOne(selections, model);
		
		Scanner scanner = new Scanner( new File("src/test/resources/configuration3.xml") );
		String text = scanner.useDelimiter("\\A").next();
		FeatureSelection response = transform.xmlToFeatureSelection(text); //this is a response from CaaS
		
		assertEquals("(null,null)/(root,Car1234-testatt=second)/(motor,Motor-enginetype=Gasoline)/(gearbox,Gearbox)/(geartype,Manual)/", response.getFullContentString());
		
		this.utils.setDefaults(response, request, model);
		
		assertEquals("(null,null)/(root,Car1234-testatt=second)/(motor,Motor-enginetype=Diesel)/(gearbox,Gearbox)/(geartype,Manual)/", response.getFullContentString());
	}
}
