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

	public AttributeValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(AttributeValue defaultValue) {
		this.defaultValue = defaultValue;
	}

	public AttributeValueType getValueType() {
		return valueType;
	}

	public void setValueType(AttributeValueType valueType) {
		this.valueType = valueType;
	}

	public Optionality getOptionality() {
		return optionality;
	}

	public void setOptionality(Optionality optionality) {
		this.optionality = optionality;
	}
	
	
}
