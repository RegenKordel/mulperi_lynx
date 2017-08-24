package eu.openreq.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class SubFeature extends AbstractPersistable<Long> {

	private static final long serialVersionUID = -187885907598416501L;

	@ElementCollection
	List<String> types;
	private String role;
	private String cardinality;

	public SubFeature() {
		this.types = new ArrayList<String>();
	}

	public SubFeature(String type, String role, String cardinality) {
		this();
		this.types.add(type);
		this.role = role;
		this.cardinality = cardinality;
	}

	public SubFeature(String type, String role) {
		this(type, role, null);
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		List<String> newTypes = new ArrayList<>(); 
		for (String type : types) {
			String newType = type.replace(" ", "_").replaceAll("-", "_");
			newTypes.add(newType);
		}
		this.types = newTypes;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		if (role != null) {
			role = role.replaceAll(" ", "_");
			this.role = role;
		}
	}

	public String getCardinality() {
		return cardinality;
	}

	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}

	public void addType(String type) {
		if (type!= null) {
			type = type.replaceAll(" ", "_").replaceAll("-", "_");
		}
		this.types.add(type);
	}

	/**
	 * 
	 * @return type in string form, for example "Manual" or "(Manual, Automatic)" 
	 */
	public String getTypeString() {
		if (this.types.size() == 1) {
			return this.types.get(0);
		} else if (types.size() >= 2) {
			return "(" + String.join(", ", this.types) + ")";
		}
		return "";
	}

	/**
	 * @return string with types, role and cardinality
	 */
	@Override
	public String toString() {
		String result = getTypeString();

		if (role != null && !role.equals("")) {
			result += " " + role;
			if (cardinality != null && !cardinality.equals("")) {
				result += "[" + cardinality + "]";
			}
			if (this.types.size() >= 2) {
				result += " {different}";
			}
		}

		return result;
	}

	/**
	 * Automatically generated with Eclipse
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((types == null) ? 0 : types.hashCode());
		return result;
	}

	/**
	 * Automatically generated with Eclipse
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SubFeature other = (SubFeature) obj;
		if (cardinality == null) {
			if (other.cardinality != null)
				return false;
		} else if (!cardinality.equals(other.cardinality))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}

	
}
