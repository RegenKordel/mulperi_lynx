package com.mulperi.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Service;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	public String featuresToConfigurationRequest(List<String> features, ParsedModel model) throws Exception {
		model.populateFeatureParentRelations();
    	
    	ArrayList<Stack<Feature>> featureStacks = new ArrayList<>();
    	
    	for(String feature : features) {
    		featureStacks.add(model.findPath(feature));
    	}
    	
    	HashMap<Feature, Element> processed = new HashMap<>();
    	
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        Element xmlRoot = doc.createElement("xml");
        doc.appendChild(xmlRoot);
        
        Element modelElement = doc.createElement("model");
        Attr modelNameAttribute = doc.createAttribute("name");
        modelNameAttribute.setValue(model.getModelName());
        modelElement.setAttributeNode(modelNameAttribute);
        xmlRoot.appendChild(modelElement);
        
        Element confRoot = doc.createElement("configuration");
        xmlRoot.appendChild(confRoot);
        
        for(Stack<Feature> setOfFeatures : featureStacks) {
        	while(!setOfFeatures.isEmpty()) {
            	Feature feature = setOfFeatures.pop();
            	if(processed.containsKey(feature)) { //this element already exists
            		continue;
            	}
            	
            	Element featureElement = doc.createElement("feature");
            	processed.put(feature, featureElement);
            	
                Attr nameAttribute = doc.createAttribute("name");
                nameAttribute.setValue(feature.getRoleNameInModel());
                featureElement.setAttributeNode(nameAttribute);
                
                Attr typeAttribute = doc.createAttribute("type");
                typeAttribute.setValue(feature.getType());
                featureElement.setAttributeNode(typeAttribute);
            	
            	if(feature.getParent() == null) { //child of root
            		confRoot.appendChild(featureElement);
            	} else { //child of another element
            		Element parentElement = processed.get(feature.getParent());
            		parentElement.appendChild(featureElement);
            	}
        	}
        	
        }
        
//      Output to console for testing
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        DOMSource source = new DOMSource(doc);
//        StreamResult consoleResult = new StreamResult(System.out);
//        transformer.transform(source, consoleResult);
        
        return documentToString(doc);
	}
	
	public static String documentToString(Document doc) {
	    try {
	        StringWriter sw = new StringWriter();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer transformer = tf.newTransformer();
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	
	        transformer.transform(new DOMSource(doc), new StreamResult(sw));
	        return sw.toString();
	    } catch (Exception ex) {
	        throw new RuntimeException("Error converting to String", ex);
	    }
	}


}
