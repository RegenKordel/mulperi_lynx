package fi.helsinki.ese.murmeli;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttributeDefinition {
	
	enum Optionality {
		FIXED,
		REQUIRED,
		OPTIONAL
	}
	
	@SerializedName("defaultValue")
	@Expose
	private AttributeValue defaultValue;
	
	@SerializedName("valueType")
	@Expose
	private AttributeValueType valueType;
	
	@SerializedName("optionality")
	@Expose
	private Optionality optionality = Optionality.REQUIRED;
	
	public AttributeDefinition(AttributeValue defaultValue, AttributeValueType type, Optionality opt) {
		
		this.defaultValue = defaultValue;
		this.valueType = type;
		this.optionality = opt;
	}
	
	public AttributeDefinition(AttributeValue defaultValue, AttributeValueType type) {
		
		this(defaultValue, type, Optionality.REQUIRED);
	}
	
	public AttributeDefinition(AttributeValueType type) {
		
		this(null, type);
	}
}
