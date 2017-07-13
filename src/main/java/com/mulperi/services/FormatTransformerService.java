package com.mulperi.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import java.io.File;
import java.io.StringReader;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import com.mulperi.models.selections.AttributeSelection;
import com.mulperi.models.selections.FeatureSelection;
import com.mulperi.models.kumbang.Constraint;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.kumbang.SubFeature;
import com.mulperi.models.mulson.Requirement;

@Service
public class FormatTransformerService {
	
	public ParsedModel parseMulson(String modelName, List<Requirement> requirements) {
		
		ParsedModel pm = new ParsedModel(modelName);
		
		for(Requirement req : requirements) {
			Feature feat = new Feature(req.getRequirementId());
			pm.addFeature(feat);
			
			//add attributes that are not yet in parsed model
			pm.addNewAttributes(req.getAttributes());
			
			feat.setSubFeatures(req.getSubfeatures());
			
			//if the requirement is not part of anything, then it's a subfeature of root
			if(findRequirementsParent(req.getRequirementId(), requirements) == null) { 
				pm.getFeatures().get(0).addSubFeature(new SubFeature(req.getRequirementId(), req.getRequirementId().toLowerCase(), req.getCardinality()));
			}
			
			//add constraints
			for(String requiresId : req.getRequires()) {
				Constraint constraint = new Constraint(req.getRequirementId().toLowerCase(), requiresId.toLowerCase());
				
				//seek parent info if the other side of the constraint does not reside in this feature's subfeatures
				Requirement requires = findRequirementFromList(requiresId, requirements);
				if(requires != null && findRequirementsParent(requires.getRequirementId(), requirements) != null) {
					constraint = new Constraint(req.getRequirementId().toLowerCase(), 
							findRequirementsParent(requires.getRequirementId(), requirements).getRequirementId().toLowerCase() + "." + requiresId.toLowerCase());
				}
				
				if(findRequirementsParent(req.getRequirementId(), requirements) == null) { 
					pm.getFeatures().get(0).addConstraint(constraint);
				} else {
					feat.addConstraint(constraint);
				}
			}
			
			//add attributes for feature
			feat.setAttributes(req.getAttributes());
			
		}
		
		return pm;
	}

	private Requirement findRequirementFromList(String needle, List<Requirement> haystack) {
		for(Requirement r : haystack) {
			if(r.getRequirementId().equals(needle)) {
				return r;
			}
		}
		return null;
	}
	
	private Requirement findRequirementsParent(String needle, List<Requirement> haystack) {
		for(Requirement r : haystack) {
			for(SubFeature subfeature : r.getSubfeatures()) {
				if(subfeature.getTypes().contains(needle)) {
					return r;
				}
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
	public ParsedModel parseReqif(String modelName, Collection<SpecObject> specObjects) {
		
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
		
		return pm;
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

	public FeatureSelection xmlToFeatures(String xml) {

		FeatureSelection rootFeature = new FeatureSelection();
		
		try {	
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
			doc.getDocumentElement().normalize();

			rootFeature.setAttributes(processXmlAttributes(doc.getElementsByTagName("configuration").item(0)));
			rootFeature.setFeatures(processXmlFeatures(doc.getElementsByTagName("configuration").item(0).getChildNodes()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootFeature;
	}

	private List<FeatureSelection> processXmlFeatures(NodeList xmlNodes) {
		ArrayList<FeatureSelection> children = new ArrayList<>();
		
		if (xmlNodes == null) return children;

		for (int i = 0; i < xmlNodes.getLength(); i++) {
			if (xmlNodes.item(i).getNodeType() == Node.ELEMENT_NODE
					&& xmlNodes.item(i).getNodeName().equals("feature")) { //get only feature elements
				Element featureXmlElement = (Element) xmlNodes.item(i);

				FeatureSelection feature = new FeatureSelection();
				feature.setName(featureXmlElement.getAttribute("name"));
				feature.setType(featureXmlElement.getAttribute("type"));
				feature.setAttributes(processXmlAttributes(featureXmlElement));
				feature.setFeatures(processXmlFeatures(featureXmlElement.getChildNodes()));
				children.add(feature);
			}
		}
		return children;
	}
	
	private List<AttributeSelection> processXmlAttributes(Node element) {
		NodeList xmlNodes = element.getChildNodes();
		
		ArrayList<AttributeSelection> attributes = new ArrayList<>();
		
		if (xmlNodes == null) return attributes;

		for (int i = 0; i < xmlNodes.getLength(); i++) {
			if (xmlNodes.item(i).getNodeType() == Node.ELEMENT_NODE
					&& xmlNodes.item(i).getNodeName().equals("attribute")) { //get only attribute elements
				Element attributeXmlElement = (Element) xmlNodes.item(i);

				AttributeSelection attribute = new AttributeSelection();
				attribute.setName(attributeXmlElement.getAttribute("name"));
				attribute.setValue(attributeXmlElement.getAttribute("value"));
				attributes.add(attribute);
			}
		}
		return attributes;
	}
}
