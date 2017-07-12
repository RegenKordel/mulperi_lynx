package com.mulperi.services;

import java.util.List;

import com.mulperi.models.selections.FeatureSelection;

public class Utils {

	public void diffFeatures(FeatureSelection original, FeatureSelection modified, FeatureSelection diff) {
		for(FeatureSelection feat1 : modified.getFeatures()) {
			FeatureSelection feat2 = null;
			
			if(original != null) {
				feat2 = findFromThisLevel(original.getFeatures(), feat1);
				
				//if the feature exists in original and contains the same elements, then skip
				if(feat2 != null && feat1.getFullContentString().equals(feat2.getFullContentString())) {
					continue;
				}
			}
			
			FeatureSelection nodeOnly = new FeatureSelection();
			nodeOnly.setName(feat1.getName());
			nodeOnly.setType(feat1.getType());
			nodeOnly.setAttributes(feat1.getAttributes());
			diff.getFeatures().add(nodeOnly);
			diffFeatures(feat2, feat1, nodeOnly);
		}
	}
	
	private FeatureSelection findFromThisLevel(List<FeatureSelection> haystack, FeatureSelection needle) {
		if(haystack == null || needle == null) {
			return null;
		}
		
		for(FeatureSelection feat : haystack) {
			if(feat.getName().equals(needle.getName()) && feat.getType().equals(needle.getType())) {
				return feat;
			}
		}
		return null;
	}
}
