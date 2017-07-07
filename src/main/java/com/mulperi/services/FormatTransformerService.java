package com.mulperi.services;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mulperi.models.reqif.SpecObject;
import com.mulperi.models.kumbang.Constraint;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.kumbang.SubFeature;
import com.mulperi.models.mulson.Requirement;

@Service
public class FormatTransformerService {
	
	private KumbangModelGenerator kumbangModelGenerator = new KumbangModelGenerator();
	
	public String mulsonToKumbang(String modelName, List<Requirement> requirements) {
		
		ParsedModel pm = new ParsedModel(modelName);
		
		for(Requirement req : requirements) {
			Feature feat = new Feature(req.getRequirementId());
			pm.addFeature(feat);
			
			//add attributes not yet in parsedmodel
			pm.addNewAttributes(req.getAttributes());
			
			//if the requirement is not part of anything, then it's a subfeature of root
			if(req.getParent() == null) { 
				pm.getFeatures().get(0).addSubFeature(new SubFeature(req.getRequirementId(), req.getRequirementId().toLowerCase(), req.getCardinality()));
			}
			
			//add the subfeatures of the requirement
			for(Requirement subReq : requirements) {
				String parent = subReq.getParent();
				if(parent != null && parent.equals(req.getRequirementId())) {
					feat.addSubFeature(new SubFeature(subReq.getRequirementId(), subReq.getRequirementId().toLowerCase(), subReq.getCardinality()));
				}
			}		
			
			//add constraints
			for(String requiresId : req.getRequires()) {
				Constraint constraint = new Constraint(req.getRequirementId().toLowerCase(), requiresId.toLowerCase());
				
				//seek parent info if the other side of the constraint does not reside in this feature's subfeatures
				Requirement requires = findRequirementFromList(requiresId, requirements);
				if(requires != null && requires.getParent() != null) {
					constraint = new Constraint(req.getRequirementId().toLowerCase(), requires.getParent().toLowerCase() + "." + requiresId.toLowerCase());
				}
				
				if(req.getParent() == null) { 
					pm.getFeatures().get(0).addConstraint(constraint);
				} else {
					feat.addConstraint(constraint);
				}
			}
			
			//add attributes for feature
			feat.setAttributes(req.getAttributes());
			
		}
		
		return kumbangModelGenerator.generateKumbangModelString(pm);
	}

	private Requirement findRequirementFromList(String needle, List<Requirement> haystack) {
		for(Requirement r : haystack) {
			if(r.getRequirementId().equals(needle)) {
				return r;
			}
		}
		return null;
	}
	
	/**
	 * TODO: Refactor with an interface?
	 * @param modelName
	 * @param specObjects
	 * @return
	 */
	public String reqifToKumbang(String modelName, Collection<SpecObject> specObjects) {
		
		ParsedModel pm = new ParsedModel(modelName);
		
		for(SpecObject req : specObjects) {
			Feature feat = new Feature(req.getId());
			pm.addFeature(feat);
			
			//if the requirement is not part of anything, then it's a subfeature of root
			if(req.getParent() == null) { 
				pm.getFeatures().get(0).addSubFeature(new SubFeature(req.getId(), req.getId().toLowerCase(), req.getCardinality()));
			}
			
			//add the subfeatures of the requirement
			for(SpecObject subReq : specObjects) {
				SpecObject parent = subReq.getParent();
				if(parent != null && parent == req) {
					feat.addSubFeature(new SubFeature(subReq.getId(), subReq.getId().toLowerCase(), subReq.getCardinality()));
				}
			}
			
			//add constraints
			for(SpecObject requires : req.getRequires()) {
				Constraint constraint = new Constraint(req.getId().toLowerCase(), requires.getId().toLowerCase());
				
				//seek parent info if the other side of the constraint does not reside in this feature's subfeatures
				if(requires != null && requires.getParent() != null) {
					constraint = new Constraint(req.getId().toLowerCase(), requires.getParent().getId().toLowerCase() + "." + requires.getId().toLowerCase());
				}
				
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
