package eu.openreq.mulperi.models.mulson;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Relationship extends AbstractPersistable<Long> {

	private static final long serialVersionUID = 8815373175639095921L;
	
	private String targetId;
	private String type;
	
	public String getTargetId() {
		return targetId;
	}
	public void setTargetId(String id) {
		if (id!=null)
			id = id.replaceAll(" ", "_").replaceAll("-", "_");
		this.targetId = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		if (type!=null)
			type = type.replaceAll(" ", "_").replaceAll("-", "_");
		this.type = type;
	}
	
	/**
	 * Automatically generated with Eclipse
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((targetId == null) ? 0 : targetId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Relationship other = (Relationship) obj;
		if (targetId == null) {
			if (other.targetId != null)
				return false;
		} else if (!targetId.equals(other.targetId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	
}
