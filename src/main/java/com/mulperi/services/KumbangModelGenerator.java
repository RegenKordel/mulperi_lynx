package com.mulperi.services;

import com.mulperi.models.Attribute;
import com.mulperi.models.Component;
import com.mulperi.models.Constraint;
import com.mulperi.models.Feature;
import com.mulperi.models.ParsedModel;

public class KumbangModelGenerator {

	String kbm;
	ParsedModel model;

	public String generateKumbangModelString(ParsedModel model) {
		this.model = model;
		this.kbm = "Kumbang model " + model.getModelName() + "\n\t root component " + model.getModelName()
				+ "\n\t root feature " + model.getModelName() + "\n\n";

		if (!model.getComponents().isEmpty()) {
			listComponents();
		}	

		if (!model.getFeatures().isEmpty()) {
			listFeatures();
		}	

		if (!model.getAttributes().isEmpty()) {
			listAttributes();
		}		

		return kbm;
	}

	private void listComponents() {
		kbm += "//---components-----\n\n";

		for (Component com : model.getComponents()) {
			kbm += "component type " + com.getComponentType() + " {\n}\n\n";
		}

	}

	private void listFeatures() {
		kbm += "//---features-----\n\n";

		for (Feature feat : model.getFeatures()) {
			kbm += "feature type " + feat.getType() + " {\n";

			if (!feat.getSubFeatures().isEmpty()) {
				listSubfeatures(feat);
			}

			if (!feat.getConstraints().isEmpty()) {
				listConstraints(feat);
			}
			
			if (!feat.getAttributes().isEmpty()) {
				listFeatureAttributes(feat);
			}

			kbm += "}\n\n";
		}
	}

	private void listAttributes() {
		kbm += "//---attributes-----\n\n";

		for (Attribute att : model.getAttributes()) {
			kbm += "attribute type " + att.getName() + " = {\n";
			for (String val : att.getValues()) {				
				kbm += "\t" + val;
				if (!val.equals(att.getValues().get(att.getValues().size()-1))) {
					kbm += ",";
				}
				kbm += "\n";
			}
			
			kbm += "}\n\n";
		}
	}

	private void listSubfeatures(Feature feat) {
		kbm += "\tsubfeatures\n";

		for (Feature sub : feat.getSubFeatures()) {
			kbm += "\t\t" + sub.toString() + ";\n";
		}
	}

	private void listConstraints(Feature feat) {
		kbm += "\tconstraints\n";

		for (Constraint con : feat.getConstraints()) {
			kbm += "\t\t" + con.toString() + ";\n";
		}
	}

	private void listFeatureAttributes(Feature feat) {
		kbm += "\tattributes\n";

		for (Attribute att : feat.getAttributes()) {
			kbm += "\t\t" + att.getName() + " " + att.getRole() + ";\n";
		}
	}
}
