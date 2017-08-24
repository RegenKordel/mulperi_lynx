package com.mulperi.services;

import java.util.List;

import com.mulperi.models.kumbang.Attribute;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.selections.AttributeSelection;
import com.mulperi.models.selections.FeatureSelection;

/**
 * Various utilities
 *
 * @author iivorait
 * @author tlaurinen
 */
public class Utils {

//	/**
//	 * Transfers the features that exist in "modified" but not in "original" to "diff"
//	 * @param original
//	 * @param modified
//	 * @param diff
//	 */
//	public void diffFeatures(FeatureSelection original, FeatureSelection modified, FeatureSelection diff) {
//		for(FeatureSelection feat1 : modified.getFeatures()) {
//			FeatureSelection feat2 = null;
//			
//			if(original != null) {
//				feat2 = findFromThisLevel(original.getFeatures(), feat1);
//				
//				//if the feature exists in original and contains the same elements, then skip
//				if(feat2 != null && feat1.getFullContentString().equals(feat2.getFullContentString())) {
//					continue;
//				}
//			}
//			
//			FeatureSelection nodeOnly = new FeatureSelection();
//			nodeOnly.setName(feat1.getName());
//			nodeOnly.setType(feat1.getType());
//			nodeOnly.setAttributes(feat1.getAttributes()); //Note: makes a reference to array
//			diff.getFeatures().add(nodeOnly);
//			diffFeatures(feat2, feat1, nodeOnly);
//		}
//	}
	
	/**
	 * Finds a reference to a FeatureSelection that is equal to needle (same name and type)
	 * @param haystack
	 * @param needle
	 * @return
	 */
	private FeatureSelection findFromThisLevel(List<FeatureSelection> haystack, FeatureSelection needle) {
		return this.findFromThisLevel(haystack, needle, false);
	}
	
	/**
	 * 
	 * @param haystack
	 * @param needle
	 * @param typeOnly True if same type alone means equality
	 * @return
	 */
	private FeatureSelection findFromThisLevel(List<FeatureSelection> haystack, FeatureSelection needle, Boolean typeOnly) {
		if(haystack == null || needle == null) {
			return null;
		}
		
		for(FeatureSelection feat : haystack) {
			try {
				if(typeOnly && feat.getType().equals(needle.getType())) { //Compare using type only
					return feat;
				} else if(feat.getName().equals(needle.getName()) && feat.getType().equals(needle.getType())) {
					return feat;
				}
			} catch (Exception e) { //name or type strings might be null
			}
		}
		return null;
	}
	
	/**
	 * Sets default attribute values to response overriding any randomly set attributes
	 * @param response might contain random attribute values
	 * @param request attributes that were explicitly set
	 * @param defaults model that contains the default values
	 */
	public void setDefaults(FeatureSelection response, FeatureSelection request, ParsedModel defaults) {
		//go through the attributes of the current feature first
		//find default attribute values
		Feature defaultFeature = defaults.getFeature(response.getType());
		if(defaultFeature != null) {
			for(Attribute possibleAttribute : defaultFeature.getAttributes()) {
				//skip this attribute if default value is not set
				if(possibleAttribute.getDefaultValue() == null) {
					continue;
				}
				//find if this attribute is set in request - skip if is
				if(request.getAttribute(possibleAttribute.getType()) != null) {
					continue;
				}
				//set value to default in response
				AttributeSelection attribute = response.getAttribute(possibleAttribute.getType());
				if(attribute != null) {
					attribute.setValue(possibleAttribute.getDefaultValue());
					continue;
				}
				//attribute not found in response - add new
				attribute = new AttributeSelection(possibleAttribute.getType(), possibleAttribute.getDefaultValue());
				response.getAttributes().add(attribute);
			}
		}
		
		//process the children after the current element
		for(FeatureSelection responseSubfeature : response.getFeatures()) {
			FeatureSelection requestSubfeature = findFromThisLevel(request.getFeatures(), responseSubfeature, true);
			if(requestSubfeature == null) {
				requestSubfeature = new FeatureSelection(); //nothing set in request
			}
			setDefaults(responseSubfeature, requestSubfeature, defaults);
		}
	}
}
