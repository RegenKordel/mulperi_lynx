package eu.openreq.mulperi.services;

import eu.openreq.mulperi.models.kumbang.Attribute;
import eu.openreq.mulperi.models.kumbang.Component;
import eu.openreq.mulperi.models.kumbang.Constraint;
import eu.openreq.mulperi.models.kumbang.Feature;
import eu.openreq.mulperi.models.kumbang.ParsedModel;
import eu.openreq.mulperi.models.kumbang.SubFeature;

/**
 * Turns a ParsedModel object into a Kumbang format string
 *
 * @author iivorait
 * @author tlaurinen
 */
public class KumbangModelGenerator {

	String kbm;
	ParsedModel model;

	public String generateKumbangModelString(ParsedModel model) {
		this.model = model;
		this.kbm = "Kumbang model " + model.getModelName() + "\n\t root component " + model.getModelName()
				+ "\n\t root feature " + model.getModelName() + "\n\n";

		if (model.getComment() != null) {
			kbm += "//" + model.getComment() + "\n\n";
		}

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

			if (feat.getComment() != null) {
				kbm += "//" + feat.getComment() + "\n";
			}

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

		for (Attribute att : model.getAttributesDefaultsFirst()) {
			kbm += "attribute type " + att.getType() + " = {\n";
			for (String val : att.getValues()) {
				kbm += "\t" + val;
				if (!val.equals(att.getValues().get(att.getValues().size() - 1))) {
					kbm += ",";
				}
				kbm += "\n";
			}

			kbm += "}\n\n";
		}
	}

	private void listSubfeatures(Feature feat) {
		kbm += "\tsubfeatures\n";

		for (SubFeature sub : feat.getSubFeatures()) {
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
				kbm += "\t\t" + att.getType() + " " + att.getRole() + ";"; 
				if (att.getDefaultValue()!=null) {
					kbm += " //default = " + att.getDefaultValue();
				}
				kbm += "\n";		
		}
	}
}
