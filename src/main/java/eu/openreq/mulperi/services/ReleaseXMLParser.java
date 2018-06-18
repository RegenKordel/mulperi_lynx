package eu.openreq.mulperi.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.openreq.mulperi.models.release.Project;
import eu.openreq.mulperi.models.release.Release;
import eu.openreq.mulperi.models.release.ReleasePlan;
import eu.openreq.mulperi.models.release.ReleasePlanException;
import eu.openreq.mulperi.models.release.Requirement;

/**
 * Parse Release XML to Mulperi's Release Java objects
 * 
 * @author jutiihon

 */
public class ReleaseXMLParser {

	
	public static ReleasePlan parseProjectXML(String xml) 
			throws ReleasePlanException {
		
		try {

			Element root = stringToXmlObject(xml);
	
			Project project = getProjectFromXml(root);
			List <Requirement> requirements = getRequirementsFromXml(root);
			List <Release> releases= getReleasesFromXml(root);
	
			ReleasePlan releasePlan = new ReleasePlan();
			
			releasePlan.setProject(project);
			for (Requirement requirement : requirements) {
				Requirement old = 
						releasePlan.addRequirement(requirement);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Requirement with ID: " + old.getId());
			}
	
			for (Release release: releases) {
				Release old =
						releasePlan.addRelease(release);
				if (old != null) 
					throw new 
						ReleasePlanException("Duplicate Release with ID: " + old.getId());
			}
			
			return releasePlan;
		}
		catch (XMLParseException| SAXException |IOException | ParserConfigurationException ex) {
			throw new ReleasePlanException (ex);
		}

	}

	
	//TODO generalize the methods below with Java type parameters
	// it would be better to have higher level tag <requirements>, it's children <requirement>
	// and method to read objects of a type from a node
	// Now lots of duplicate code.

	private static Project getProjectFromXml(Element root) throws XMLParseException {
		Project project= null;

		NodeList projectNode = root.getElementsByTagName("project");
		if (projectNode != null && projectNode.getLength() == 1) {

			project = new Project();

			Element projectElement = (Element) projectNode.item(0);
			String name = getUniqueChildValue(projectElement, "name");
			if (name != null)
				project.setName(name.trim());

			String id = getUniqueChildValue(projectElement, "id");
			if (id != null)
				project.setId(id.trim());

//			String version = getUniqueChildValue(projectElement, "version");
//			if (version != null)
//				project.setVersion(version.trim());

		}
		else if (projectNode.getLength() > 1) {
			throw new XMLParseException("Multiple projects within request not allowed.");
		}

		return project;
	}

	private static List<Requirement> getRequirementsFromXml(Element root)
		throws XMLParseException {
		LinkedList<Requirement> reqs = new LinkedList <>();
		NodeList requirementsNode = root.getElementsByTagName("requirements");
		if (requirementsNode != null && requirementsNode.getLength() == 1) {
			Element requirementsElement = (Element) requirementsNode.item(0);
			NodeList requirements = requirementsElement.getChildNodes();
			if (requirements != null) {
				for (int j = 0; j < requirements.getLength(); j++) {
					if (requirements.item(j).getNodeType() == Node.ELEMENT_NODE
							&& requirements.item(j).getNodeName().equals("requirement")) { // get only requirements elements
						Element requirementXmlElement = (Element) requirements.item(j);


						String id = getUniqueChildValue(requirementXmlElement, "id");
						Integer effort= getUniqueChildAsInteger(requirementXmlElement, "effort");
						Integer assignedRelease = getUniqueChildAsInteger(requirementXmlElement, "assignedRelease");

						if (
								(id != null) 
								//&& (effort != null) &&
								&& (assignedRelease != null)
								) {
							Requirement req = new Requirement(id.trim());
							if (effort != null)
								req.setEffort(effort.intValue());
							if (assignedRelease != null)
								req.setAssignedRelease(assignedRelease.intValue());
							reqs.add(req);
							List<String> requiresDependencies =
									getRequiresDependencies	(requirementXmlElement);
							for (String required: requiresDependencies)
								req.addRequiresDependency(required);
						}
						else {
							throw new XMLParseException("Invalid requirement from XML" + "id=" + "id" + ", effort=" + effort + ", assignedRelease=" + assignedRelease);
						}
					}
				}
			}
		}
		else if (requirementsNode.getLength() > 1) {
			throw new XMLParseException("Multiple 'requirements' nodes within request not allowed.");
		}
	return reqs;

	}

	private static List<String> getRequiresDependencies(Element root)
			throws XMLParseException {
			LinkedList<String> deps = new LinkedList <>(); 
			NodeList dependenciesNode = root.getElementsByTagName("dependencies");
			if (dependenciesNode != null && dependenciesNode.getLength() == 1) {
				Element depElement = (Element) dependenciesNode.item(0);
				NodeList reqDeps = depElement.getChildNodes();
				if (reqDeps != null) {
					for (int j = 0; j < reqDeps.getLength(); j++) {
						if (reqDeps.item(j).getNodeType() == Node.ELEMENT_NODE
								&& reqDeps.item(j).getNodeName().equals("requiresDependency")) { // get only requiresDependency elements
							Element requiremetnXmlElement = (Element) reqDeps.item(j);
							String id = requiremetnXmlElement.getFirstChild().getNodeValue();
							if (id != null) {
								deps.add(id.trim());
							}

						}
					}
				}
			}
			else if (dependenciesNode.getLength() > 1) {
				throw new XMLParseException("Multiple 'dependencies' nodes within request not allowed.");
			}
			return deps;

		}
	
	private static List<Release> getReleasesFromXml(Element root) 
			throws XMLParseException {
		LinkedList<Release> releases = new LinkedList <>();
		NodeList releasesNode = root.getElementsByTagName("releases");
		if (releasesNode != null && releasesNode.getLength() == 1) {
			Element releaseElement = (Element) releasesNode.item(0);
			NodeList releaseNodes = releaseElement.getChildNodes();
			if (releaseNodes != null) {
				for (int j = 0; j < releaseNodes.getLength(); j++) {
					if (releaseNodes.item(j).getNodeType() == Node.ELEMENT_NODE
							&& releaseNodes.item(j).getNodeName().equals("release")) { // get only requirements elements
						Element requirementXmlElement = (Element) releaseNodes.item(j);

						Integer id = getUniqueChildAsInteger(requirementXmlElement, "id");
						Integer maxCapacity= getUniqueChildAsInteger(requirementXmlElement, "maxCapacity");

						if (
								(id != null) 
								&& (maxCapacity != null)
								) {
							Release release = new Release(id.intValue(), maxCapacity.intValue());
							releases.add(release);
						}
						else {
							throw new XMLParseException("Invalid release from XML" + "id=" + "id" + ", maxCapacity=" + maxCapacity);
						}
					}
				}
			}
		}
		else if (releasesNode.getLength() > 1) {
			throw new XMLParseException("Multiple 'releases' nodes within request not allowed.");
		}
		
		return releases;
	}


	
	private static String getUniqueChildValue (Element element, String tagName)
			throws XMLParseException {
		//NodeList searchNodes = element.getElementsByTagName(tagName);

		String returnValue= null;
		boolean found = false;
		NodeList searchNodes = element.getChildNodes();


		for (int j = 0; j < searchNodes.getLength(); j++) {
			if (searchNodes.item(j).getNodeType() == Node.ELEMENT_NODE
					&& searchNodes.item(j).getNodeName().equals(tagName)) { // get only attribute elements
				Element foundXmlElement = (Element) searchNodes.item(j);
				if (found)
					throw new 
					XMLParseException(tagName + ": Multiple occurrences not allowed.");
				found = true;
				returnValue = foundXmlElement.getFirstChild().getNodeValue();
			}
		}

		return returnValue;
	}	

	private static Integer getUniqueChildAsInteger (Element element, String tagName)
			throws XMLParseException {
		String uniqueStringValue = getUniqueChildValue (element, tagName);
		if (uniqueStringValue == null)
			return null;

		try {
			Integer versionInteger = Integer.parseInt(uniqueStringValue);
			return versionInteger;
		} catch (NumberFormatException ex) {
			throw new XMLParseException ("Not integer in XML element " + tagName + " = " + uniqueStringValue);
		}

	}

	public static Element stringToXmlObject(String xmlString) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//factory.setValidating(false); //does not help to 'isStandalone = "yes"' causing problems 
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		Document dataDom = docBuilder.parse(new InputSource(new StringReader(xmlString)));
		Element root = dataDom.getDocumentElement();
		return root;
	}

}

