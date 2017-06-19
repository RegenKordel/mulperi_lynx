package com.mulperi.services;

import com.mulperi.models.Component;
import com.mulperi.models.Constraint;
import com.mulperi.models.Feature;
import com.mulperi.models.ParsedModel;

public class KumbangModelGenerator {
	
	String kbm;
	ParsedModel model;
	

	public String generateKumbangModelString(ParsedModel data) {
		this.model = data;
		this.kbm = "Kumbang model " + model.getModelName() +"\n\t root component " + model.getModelName() +
		"\n\t root feature " + model.getModelName() + "\n";
		
		if (!data.getComponents().isEmpty()) {
			listComponents();
		}
		
		kbm += "\n";
		
		if (!data.getFeatures().isEmpty()) {
			listFeatures();
		}
		
		return kbm;
	}

	private void listComponents() {
		kbm += "\n//---components-----\n\n";
		
		for(Component com : model.getComponents()) {
			kbm += "component type " + com.getComponentType() + " {\n}\n";
		}
		
	}
	
	private void listFeatures() {
		kbm += "\n//---features-----\n\n";
		
		for(Feature feat : model.getFeatures()) {
			kbm += "feature type " + feat.getFeatureType() + " {\n";
			
			if (!feat.getSubFeatures().isEmpty()) {
				listSubfeatures(feat);
			}
			
			if (!feat.getConstraints().isEmpty()) {
				listConstraints(feat);
			}
			
			kbm += "}\n\n\n";
		}
	}
	
	private void listSubfeatures(Feature feat) {
		kbm += "\tsubfeatures\n";
		
		for(Feature sub : feat.getSubFeatures()) {
			kbm += "\t\t" + sub.toString() + ";\n";
		}
	}
	
	private void listConstraints(Feature feat)	{
		kbm += "\tconstraints\n";
		
		for(Constraint con : feat.getConstraints()) {
			kbm += "\t\t" + con.toString() + ";\n"; 
		}
	}
}
