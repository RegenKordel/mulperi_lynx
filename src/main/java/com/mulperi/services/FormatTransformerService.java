package com.mulperi.services;

import java.util.List;

import com.mulperi.models.Constraint;
import com.mulperi.models.Feature;
import com.mulperi.models.ParsedModel;
import com.mulperi.models.submit.Requirement;

public class FormatTransformerService {
	
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();
	
	public String SimpleToKumbang(String modelName, List<Requirement> requirements) {
		
		ParsedModel pm = new ParsedModel(modelName);
		
		for(Requirement req : requirements) {
			Feature feat = new Feature(req.getId(), req.getId().toLowerCase(), req.getCardinality());
			pm.addFeature(feat);
			
			//if the requirement is not part of anything, then it's a subfeature of root
			if(req.getParent() == null) { 
				pm.getFeatures().get(0).addSubFeature(feat);
			}
			
			//add the subfeatures of the requirement
			for(Requirement subReq : requirements) {
				String parent = subReq.getParent();
				if(parent != null && parent.equals(req.getId())) {
					feat.addSubFeature(new Feature(subReq.getId(), subReq.getId().toLowerCase(), subReq.getCardinality()));
				}
			}
			
			//add constraints
			for(String requiresId : req.getRequires()) {
				Constraint constraint = new Constraint(req.getId().toLowerCase(), requiresId.toLowerCase());
				if(req.getParent() == null) { 
					pm.getFeatures().get(0).addConstraint(constraint);
				} else {
					feat.addConstraint(constraint);
				}
			}
			
		}
		
		return kumbangModelGenerator.generateKumbangModelString(pm);
	}
}
