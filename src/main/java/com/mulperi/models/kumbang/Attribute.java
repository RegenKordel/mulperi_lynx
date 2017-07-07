package com.mulperi.models.kumbang;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Attribute  extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 8108228292152293692L;
	
	String name;
	String role;
	@ElementCollection
	List<String> values;
	
	public Attribute() {
	}

	public Attribute(String name, String role, List<String> values) {
		this.name = name;
		this.role = role;
		this.values = values;
	}
	
	public Attribute(String name, List<String> values) {
		this(name, null, values);
	}
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
	        return true;
	    if (obj == null)
	        return false;
	    if (!(obj instanceof Attribute))
	        return false;
	    Attribute other = (Attribute) obj;
	    if (name == null) {
	        if (other.name != null)
	            return false;
	    } else if (!name.equals(other.name))
	        return false;
	    return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
	}
	
}
