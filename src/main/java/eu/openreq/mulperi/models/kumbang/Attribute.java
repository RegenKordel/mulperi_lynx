package eu.openreq.mulperi.models.kumbang;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Attribute extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 8108228292152293692L;

	String type;
	String role;
	@ElementCollection
	List<String> values;
	String defaultValue;

	public Attribute() {
	}

	public Attribute(String type, String role, List<String> values, String defaultValue) {
		this.type = type;
		this.role = role;
		this.values = values;
		this.defaultValue = defaultValue;
	}

	public Attribute(String type, String role, List<String> values) {
		this(type, role, values, null);
	}
	
	public Attribute(String type, List<String> values, String defaultValue) {
		this(type, type, values, defaultValue);
	}

	public Attribute(String type, List<String> values) {
		this(type, type, values, null);
	}

	public Attribute(String type, String role) {
		this(type, role, null, null);
	}

	public String getType() {
		return type;
	}

	public void setType(String name) {
		if (this.type != null)
			name = name.replaceAll(" ", "_").replaceAll("-", "_");
		this.type = name;
		if (this.role == null) {
			this.role = name; //type is the default role type
		}
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		if (role != null) {
			role = role.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("-", "_");
			this.role = role;
		}
	}

	public List<String> getValues() {
		return values;
	}
	
	public List<String> getValuesDefaultFirst() {
		List<String> arrangedVals = new ArrayList<String>();

		if (this.defaultValue != null) {
			arrangedVals.add(this.defaultValue);
		} else {
			return values;
		}

		for (String value : this.values) {
			if (!value.equals(this.defaultValue)) {
				arrangedVals.add(value);
			}
		}
		return arrangedVals;
	}

	public void setValues(List<String> values) {
		List<String> newValues = new ArrayList<>();
		for (String val : values) {
			String newVal = val.replaceAll(" ", "_").replaceAll("-", "_");
			newValues.add(newVal);
		}
		this.values = newValues;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		if (defaultValue != null)
			defaultValue = defaultValue.replaceAll(" ", "_").replaceAll("-", "_");
		this.defaultValue = defaultValue;
	}

	/**
	 * Automatically generated with Eclipse
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		Attribute other = (Attribute) obj;
		if (defaultValue == null) {
			if (other.defaultValue != null)
				return false;
		} else if (!defaultValue.equals(other.defaultValue))
			return false;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Attribute [type=" + type + ", values=" + values + ", defaultValue=" + defaultValue + "]";
	}

}
