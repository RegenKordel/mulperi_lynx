package eu.openreq.mulperi.models.mulson;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Attribute extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 8108228292152293692L;

	String name;
	@ElementCollection
	List<String> values;
	String defaultValue;

	public Attribute() {
	}

	public Attribute(String name, List<String> values, String defaultValue) {
		this.name = name;
		this.values = values;
		this.defaultValue = defaultValue;
	}

	public Attribute(String name, List<String> values) {
		this(name, values, null);
	}

	public Attribute(String name) {
		this(name, null, null);
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		if (this.name != null)
			name = name.replaceAll(" ", "_").replaceAll("-", "_");
		this.name = name;
		if (this.name == null) {
			this.name = name; //type is the default role type
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
		return "Attribute [name=" + name + ", values=" + values + ", defaultValue=" + defaultValue + "]";
	}

}
