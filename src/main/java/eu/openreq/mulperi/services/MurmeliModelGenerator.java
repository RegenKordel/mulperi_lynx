package eu.openreq.mulperi.services;

import eu.openreq.mulperi.models.json.*;
import fi.helsinki.ese.murmeli.*;
import fi.helsinki.ese.murmeli.RelationshipType.NameType;

public class MurmeliModelGenerator {

	public RelationshipType mapDependency(Dependency dep) {
		
		RelationshipType.NameType type = null;
		
		switch(dep.getDependency_type()) {
		case CONTRIBUTES:
			break;
		case DAMAGES:
			break;
		case DECOMPOSITION:
			break;
		case DUPLICATES:
			type = NameType.DUPLICATES;
			break;
		case INCOMPATIBLE:
			type = NameType.INCOMPATIBLE;
			break;
		case REFINES:
			type = NameType.REFINES;
			break;
		case REPLACES:
			type = NameType.REPLACES;
			break;
		case REQUIRES:
			type = NameType.REQUIRES;
			break;
		case SIMILAR:
			type = NameType.SIMILAR;
			break;
		default:
			break;
		}
		
		Element from = mapElement(dep.getFromId());
		Element to = mapElement(dep.getToId());
		
		RelationshipType relationship = new RelationshipType(type, from, to);
		
		return relationship;
	}
	
	private Element mapElement(Requirement req) {
        return null;
    }
}
