package com.mulperi.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Test;

import com.mulperi.models.selections.FeatureSelection;

public class UtilsTests {

	private FormatTransformerService transform = new FormatTransformerService();
	private Utils utils = new Utils();
	
	@Test
	public void test() throws FileNotFoundException {
		Scanner scanner = new Scanner( new File("src/test/resources/configuration1.xml") );
		String text = scanner.useDelimiter("\\A").next();
		
		FeatureSelection root1 = transform.xmlToFeatures(text);
		
		scanner = new Scanner( new File("src/test/resources/configuration2.xml") );
		text = scanner.useDelimiter("\\A").next();
		
		FeatureSelection root2 = transform.xmlToFeatures(text);
		
		FeatureSelection diff = new FeatureSelection();
		
		utils.diffFeatures(root1, root2, diff);
		
		assertEquals("(null,null)/(root,Car1234-testatt=second)/(motor,Motor-enginetype=Gasoline)/(gearbox,Gearbox)/(geartype,Manual)/", diff.getFullContentString());
	}

}
