package com.mulperi.services;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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
import com.mulperi.models.kumbang.Attribute;
import com.mulperi.models.kumbang.Constraint;
import com.mulperi.models.kumbang.Feature;
import com.mulperi.models.kumbang.ParsedModel;
import com.mulperi.models.kumbang.SubFeature;
import com.mulperi.models.mulson.Requirement;

/**
 * 
 * Methods used in parsing Mulson and Reqif to ParsedModel
 *
 */

@Service
public class FormatTransformerService {

	public ParsedModel parseMulson(String modelName, List<Requirement> requirements) {

		ParsedModel pm = new ParsedModel(modelName);

		requirements = attributeRenamingOperation(requirements);

		for (Requirement req : requirements) {
			Feature feat = new Feature(req.getRequirementId());
			pm.addFeature(feat);

			pm.addAttributes(req.getAttributes());

			feat.setSubFeatures(req.getSubfeatures());

			// if the requirement is not part of anything, then it's a subfeature of root
			if (findRequirementsParent(req.getRequirementId(), requirements) == null) {
				pm.getFeatures().get(0).addSubFeature(new SubFeature(req.getRequirementId(),
						req.getRequirementId(), req.getCardinality()));
			}

			// add requires-constraints
			for (String requiresId : req.getRelationshipsOfType("requires")) {
				Constraint constraint = new Constraint(req.getRequirementId(), requiresId);
				addConstraintToParsedModel(requirements, pm, req, feat, constraint);
			}
			// add incompatible-constraints
			for (String requiresId : req.getRelationshipsOfType("incompatible")) {
				Constraint constraint = new Constraint(req.getRequirementId(), requiresId, true);
				addConstraintToParsedModel(requirements, pm, req, feat, constraint);
			}

			// add attributes for feature
			feat.setAttributes(req.getAttributes());
		}

		return pm;
	}

	private void addConstraintToParsedModel(List<Requirement> requirements, ParsedModel pm, Requirement req,
			Feature feat, Constraint constraint) {
		if (findRequirementsParent(req.getRequirementId(), requirements) == null) {
			pm.getFeatures().get(0).addConstraint(constraint);
		} else {
			feat.addConstraint(constraint);
		}
	}

	/**
	 * Adds numbers to differentiate identical attribute names
	 * 
	 * @param requirements
	 * @return
	 */
	private List<Requirement> attributeRenamingOperation(List<Requirement> requirements) {
		HashMap<String, Integer> usedNames = new HashMap<String, Integer>();

		List<Requirement> newReqs = new ArrayList<Requirement>();
		for (Requirement req : requirements) {
			Requirement newReq = req;
			List<Attribute> newAttList = new ArrayList<Attribute>();
			for (Attribute att : req.getAttributes()) {
				String attName = att.getName();
				Attribute newAtt = att;
				if (usedNames.containsKey(attName)) {
					int amount = usedNames.get(attName) + 1;
					usedNames.put(attName, amount);
					attName += amount;
					newAtt.setName(attName);
				} else {
					usedNames.put(attName, 1);
				}
				newAttList.add(newAtt);
			}
			newReq.setAttributes(newAttList);
			newReqs.add(newReq);
		}

		return newReqs;
	}

	private Requirement findRequirementsParent(String needle, List<Requirement> haystack) {
		for (Requirement r : haystack) {
			for (SubFeature subfeature : r.getSubfeatures()) {
				if (subfeature.getTypes().contains(needle)) {
					return r;
				}
			}
		}
		return null;
	}

	/**
	 * TODO: Refactor with an interface?
	 * 
	 * @param modelName
	 * @param specObjects
	 * @return
	 */
	public ParsedModel parseReqif(String modelName, Collection<SpecObject> specObjects) {

		ParsedModel pm = new ParsedModel(modelName);

		for (SpecObject req : specObjects) {
			Feature feat = new Feature(req.getId());
			pm.addFeature(feat);

			// if the requirement is not part of anything, then it's a subfeature of root
			if (req.getParent() == null) {
				pm.getFeatures().get(0)
						.addSubFeature(new SubFeature(req.getId(), req.getId().toLowerCase(), req.getCardinality()));
			}

			// add the subfeatures of the requirement
			for (SpecObject subReq : specObjects) {
				SpecObject parent = subReq.getParent();
				if (parent != null && parent == req) {
					feat.addSubFeature(
							new SubFeature(subReq.getId(), subReq.getId().toLowerCase(), subReq.getCardinality()));
				}
			}

			// add constraints
			for (SpecObject requires : req.getRequires()) {
				Constraint constraint = new Constraint(req.getId().toLowerCase(), requires.getId().toLowerCase());

				// seek parent info if the other side of the constraint does not reside in this feature's subfeatures
				if (requires != null && requires.getParent() != null) {
					constraint = new Constraint(req.getId().toLowerCase(),
							requires.getParent().getId().toLowerCase() + "." + requires.getId().toLowerCase());
				}

				if (req.getParent() == null) {
					pm.getFeatures().get(0).addConstraint(constraint);
				} else {
					feat.addConstraint(constraint);
				}
			}

		}

		return pm;
	}

	/**
	 * 
	 * @param features
	 *            Note: populate only the type and attributes of each FeatureSelection object
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public String featuresToConfigurationRequest(List<FeatureSelection> features, ParsedModel model) throws Exception {
		model.populateFeatureParentRelations();

		ArrayList<Stack<Feature>> featureStacks = new ArrayList<>();
		HashMap<String, Boolean> softMap = new HashMap<>();

		for (FeatureSelection feature : features) {
			featureStacks.add(model.findPath(feature.getType()));
			softMap.put(feature.getName(), feature.getIsSoft());
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

		for (Stack<Feature> setOfFeatures : featureStacks) {
			while (!setOfFeatures.isEmpty()) {
				Feature feature = setOfFeatures.pop();
				if (processed.containsKey(feature)) { // this element already exists
					continue;
				}

				Element featureElement = doc.createElement("feature");
				processed.put(feature, featureElement);

				Attr nameAttribute = doc.createAttribute("name");
				String featRoleName = feature.getRoleNameInModel();
				nameAttribute.setValue(featRoleName);
				featureElement.setAttributeNode(nameAttribute);

				Attr typeAttribute = doc.createAttribute("type");
				typeAttribute.setValue(feature.getType());
				featureElement.setAttributeNode(typeAttribute);
				
				if (softMap.get(featRoleName)!=null && softMap.get(featRoleName)==true) {
					Attr softAttribute = doc.createAttribute("soft");
					softAttribute.setValue("true");
					featureElement.setAttributeNode(softAttribute);
				}

				// Add this feature's attributes (all of them)
				this.addAttributes(doc, featureElement, findFeaturesAttributes(features, feature.getType()));

				if (feature.getParent() == null) { // child of root
					confRoot.appendChild(featureElement);
				} else { // child of another element
					Element parentElement = processed.get(feature.getParent());
					parentElement.appendChild(featureElement);
				}
			}

		}

		// Output to console for testing
		// System.out.println("KOE");
		// TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// Transformer transformer = transformerFactory.newTransformer();
		// DOMSource source = new DOMSource(doc);
		// StreamResult consoleResult = new StreamResult(System.out);
		// transformer.transform(source, consoleResult);

		return documentToString(doc);
	}

	/**
	 * Adds AttributeSelections as XML elements to a feature XML element
	 * 
	 * @param doc
	 * @param to
	 * @param attributes
	 */
	private void addAttributes(Document doc, Element to, List<AttributeSelection> attributes) {
		for (AttributeSelection attribute : attributes) {
			Element attributeElement = doc.createElement("attribute");

			Attr nameAttribute = doc.createAttribute("name");
			nameAttribute.setValue(attribute.getName());
			attributeElement.setAttributeNode(nameAttribute);

			attributeElement.appendChild(doc.createTextNode(attribute.getValue()));

			to.appendChild(attributeElement);
		}
	}

	private List<AttributeSelection> findFeaturesAttributes(List<FeatureSelection> haystack, String needle) {
		for (FeatureSelection feature : haystack) {
			if (feature.getType().equals(needle)) {
				return feature.getAttributes();
			}
		}
		return new ArrayList<AttributeSelection>();
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

	/**
	 * Transforms a configuration to a FeatureSelection with a tree structure
	 * 
	 * @param xml
	 * @return
	 */
	public FeatureSelection xmlToFeatureSelection(String xml) {

		FeatureSelection rootFeature = new FeatureSelection();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(xml)));
			doc.getDocumentElement().normalize();

			rootFeature.setAttributes(processXmlAttributes(doc.getElementsByTagName("configuration").item(0)));
			rootFeature
					.setFeatures(processXmlFeatures(doc.getElementsByTagName("configuration").item(0).getChildNodes()));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootFeature;
	}

	private List<FeatureSelection> processXmlFeatures(NodeList xmlNodes) {
		ArrayList<FeatureSelection> children = new ArrayList<>();

		if (xmlNodes == null)
			return children;

		for (int i = 0; i < xmlNodes.getLength(); i++) {
			if (xmlNodes.item(i).getNodeType() == Node.ELEMENT_NODE
					&& xmlNodes.item(i).getNodeName().equals("feature")) { // get only feature elements
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

		if (xmlNodes == null)
			return attributes;

		for (int i = 0; i < xmlNodes.getLength(); i++) {
			if (xmlNodes.item(i).getNodeType() == Node.ELEMENT_NODE
					&& xmlNodes.item(i).getNodeName().equals("attribute")) { // get only attribute elements
				Element attributeXmlElement = (Element) xmlNodes.item(i);

				AttributeSelection attribute = new AttributeSelection();
				attribute.setName(attributeXmlElement.getAttribute("name"));
				attribute.setValue(attributeXmlElement.getAttribute("value"));
				attributes.add(attribute);
			}
		}
		return attributes;
	}

	/**
	 * Converts a list of individual FeatureSelection nodes into a FeatureSelection with a tree structure
	 * 
	 * @param selections
	 * @return
	 */
	public FeatureSelection listOfFeatureSelectionsToOne(List<FeatureSelection> selections, ParsedModel model) {

		FeatureSelection result = new FeatureSelection();

		model.populateFeatureParentRelations();

		ArrayList<Stack<Feature>> featureStacks = new ArrayList<>();

		for (FeatureSelection feature : selections) {
			featureStacks.add(model.findPath(feature.getType()));
		}

		HashMap<Feature, FeatureSelection> processed = new HashMap<>();

		for (Stack<Feature> setOfFeatures : featureStacks) {
			while (!setOfFeatures.isEmpty()) {
				Feature feature = setOfFeatures.pop();
				if (processed.containsKey(feature)) { // this element already exists
					continue;
				}

				FeatureSelection selection = new FeatureSelection();
				processed.put(feature, selection);

				selection.setName(feature.getName());
				selection.setType(feature.getType());

				// Add this selection's attributes
				for (FeatureSelection s : selections) {
					if (s.getType() == feature.getType()) {
						selection.setAttributes(s.getAttributes()); // note: makes a reference to array
					}
				}

				if (feature.getParent() == null) { // child of root
					result.getFeatures().add(selection);
				} else { // child of another element
					FeatureSelection parentElement = processed.get(feature.getParent());
					parentElement.getFeatures().add(selection);
				}
			}
		}

		return result;
	}

	/**
	 * Converts a FeatureSelection with a tree structure to individual FeatureSelections. 
	 * Only leaf nodes and features that have attributes.
	 * 
	 * @param selection
	 * @return
	 */
	public List<FeatureSelection> featureSelectionToList(FeatureSelection selection) {
		ArrayList<FeatureSelection> list = new ArrayList<>();

		this.addLeafsAndAttributeNodesToList(selection, list);

		return list;
	}

	private void addLeafsAndAttributeNodesToList(FeatureSelection selection, List<FeatureSelection> list) {
		// Add nodes with attributes and leafs
		if (!selection.getAttributes().isEmpty() || selection.getFeatures().isEmpty()) {
			list.add(selection);
		}

		if (!selection.getFeatures().isEmpty()) {
			for (FeatureSelection child : selection.getFeatures()) {
				addLeafsAndAttributeNodesToList(child, list);
			}
		}
	}
	
	public FeatureSelection parsedModelToFeatureSelection(ParsedModel model) {
		model.populateFeatureParentRelations();
		FeatureSelection result = new FeatureSelection();
		
		//feature 0 is the feature root
		featureToFeatureSelection(model, result, model.getFeatures().get(0));
		
		return result;
	}

	private void featureToFeatureSelection(ParsedModel model, FeatureSelection parent, Feature feature) {
		FeatureSelection newFeature = featureSelectionFromParsedFeature(parent, feature);
		
		for(SubFeature subfeat : feature.getSubFeatures()) {
			for(String type : subfeat.getTypes()) {
				Feature newSubfeature = model.getFeature(type);
				featureToFeatureSelection(model, newFeature, newSubfeature);
			}
		}
	}

	private FeatureSelection featureSelectionFromParsedFeature(FeatureSelection parent, Feature feature) {
		FeatureSelection blankFeat = new FeatureSelection();
		parent.getFeatures().add(blankFeat);
		blankFeat.setName(feature.getRoleNameInModel());
		blankFeat.setType(feature.getType());
		
		for(Attribute attribute : feature.getAttributes()) {
			for(String possibleValue : attribute.getValuesDefaultFirst()) {
				AttributeSelection blankAttr = new AttributeSelection();
				blankAttr.setName(attribute.getRole());
				blankAttr.setValue(possibleValue);
				blankFeat.getAttributes().add(blankAttr);
			}
		}
		
		return blankFeat;
	}
}
