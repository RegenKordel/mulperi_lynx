package com.mulperi.services;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mulperi.models.reqif.Attribute;
import com.mulperi.models.reqif.SpecObject;
import com.mulperi.models.reqif.SpecRelation;

public class ReqifParser {

	public HashMap<String, SpecObject> parse(String xml) {

		try {
			Document doc = loadXMLFromString(xml);
			doc.getDocumentElement().normalize();

			XPath xPath =  XPathFactory.newInstance().newXPath();


			HashMap<String, String> specTypes = getSpecTypes(doc, xPath);
			HashMap<String, SpecObject> specObjects = getSpecObjects(doc, xPath, specTypes); 
			getSpecRelations(doc, xPath, specObjects); //The return value is discarded because SpecObjects' internal relation lists are populated
			populateSpecObjectParents(doc, xPath, specObjects);
			return specObjects;

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Document loadXMLFromString(String xml) throws Exception	{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(xml));
	    return builder.parse(is);
	}

	private HashMap<String, String> getSpecTypes(Document doc, XPath xPath) throws XPathExpressionException {
		HashMap<String, String> specTypes = new HashMap<String, String>();

		String expression = "//SPEC-ATTRIBUTES";	        
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node attributeList = nodeList.item(i);

			for(int j = 0; j < attributeList.getChildNodes().getLength(); j++) {
				Node attribute = attributeList.getChildNodes().item(j);

				if (attribute.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) attribute;
//					System.out.println("Identifier : " + eElement.getAttribute("IDENTIFIER") + ", Long name : " + eElement.getAttribute("LONG-NAME"));
					specTypes.put(eElement.getAttribute("IDENTIFIER"), eElement.getAttribute("LONG-NAME"));
				}

			}
		}
		return specTypes;
	}

	private HashMap<String, SpecObject> getSpecObjects(Document doc, XPath xPath, HashMap<String, String> specTypes) throws XPathExpressionException {
		HashMap<String, SpecObject> specObjects = new HashMap<String, SpecObject>();

		String expression = "//SPEC-OBJECT";	        
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node spec = nodeList.item(i);

			if (spec.getNodeType() == Node.ELEMENT_NODE) {
				Element specElement = (Element) spec;
				System.out.println("SpecObject found : " + specElement.getAttribute("IDENTIFIER"));

				SpecObject specObject = new SpecObject();
				specObject.setId(specElement.getAttribute("IDENTIFIER"));

				specObject.setAttributes(getAttributes(specElement, xPath, specTypes));
				specObjects.put(specObject.getId(), specObject);
			}

		}
		return specObjects;
	}

	private List<Attribute> getAttributes(Element specElement, XPath xPath, HashMap<String, String> specTypes) throws XPathExpressionException {
		ArrayList<Attribute> attributes = new ArrayList<>();

		//		NodeList attributeList = eElement.getElementsByTagName("VALUES").item(0).getChildNodes();
		NodeList attributeList = (NodeList) xPath.compile("./VALUES/*").evaluate(specElement, XPathConstants.NODESET);
		for (int j = 0; j < attributeList.getLength(); j++) {
			Node attribute = attributeList.item(j);
			//			System.out.println("\nAttribute type : " + attribute.getNodeName());

			if (attribute.getNodeType() == Node.ELEMENT_NODE) {
				Element attributeElement = (Element) attribute;

				Attribute attributeObject = new Attribute();
				attributeObject.setId(attributeElement.getElementsByTagName("DEFINITION").item(0).getTextContent().trim());

				attributeObject.setValue(attributeElement.getAttribute("THE-VALUE"));
				if(attributeObject.getValue().isEmpty()) {
					attributeObject.setValue(attributeElement.getElementsByTagName("THE-VALUE").item(0).getTextContent().trim());
				}

				if(specTypes.containsKey(attributeObject.getId())) { //should be true
					attributeObject.setType(specTypes.get(attributeObject.getId()));
				}

				attributes.add(attributeObject);
			}
		}
		return attributes;
	}

	private ArrayList<SpecRelation> getSpecRelations(Document doc, XPath xPath, HashMap<String, SpecObject> specObjects) throws XPathExpressionException {
		ArrayList<SpecRelation> specRelations = new ArrayList<>();

		String expression = "//SPEC-RELATION";	        
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node relation = nodeList.item(i);

			if (relation.getNodeType() == Node.ELEMENT_NODE) {
				Element relationElement = (Element) relation;
				
				SpecObject sourceObject = specObjects.get(relationElement.getElementsByTagName("SOURCE").item(0).getTextContent().trim());
				SpecObject targetObject = specObjects.get(relationElement.getElementsByTagName("TARGET").item(0).getTextContent().trim());
				
				SpecRelation relationObject = new SpecRelation();
				relationObject.setId(relationElement.getAttribute("IDENTIFIER"));
				relationObject.setSource(sourceObject);
				relationObject.setTarget(targetObject);
				
				//Find out relation type
				String relationId = relationElement.getElementsByTagName("TYPE").item(0).getTextContent().trim();
				expression = "//SPEC-RELATION-TYPE[@IDENTIFIER='" + relationId + "']";
				NodeList relationTypes = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
				Element relationTypeElement = (Element) relationTypes.item(0);
				relationObject.setType(relationTypeElement.getAttribute("LONG-NAME"));
				
				//Assign relations to specObjects
				sourceObject.addSourceOf(relationObject);
				targetObject.addTargetOf(relationObject);
				
				specRelations.add(relationObject);
			}

		}
		return specRelations;
	}
	
	private void populateSpecObjectParents(Document doc, XPath xPath, HashMap<String, SpecObject> specObjects) throws XPathExpressionException {
		
		for(SpecObject specObject : specObjects.values()) {
//			String expression = "//CHILDREN/SPEC-HIERARCHY/OBJECT/SPEC-OBJECT-REF[text()=\"" + specObject.getId() + "\"]";
			String expression = "//CHILDREN/SPEC-HIERARCHY/OBJECT/SPEC-OBJECT-REF[text()=\"" + specObject.getId() + "\"]/../../../../OBJECT/SPEC-OBJECT-REF"; 
			NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
			
			if(nodeList.getLength() == 0) {
				continue;
			}
			
			Node parent = nodeList.item(0);
			if (parent.getNodeType() == Node.ELEMENT_NODE) {
				Element parentIdElement = (Element) parent;
				specObject.setParent(specObjects.get(parentIdElement.getTextContent().trim()));
			}
			
		}
		
	}
}
