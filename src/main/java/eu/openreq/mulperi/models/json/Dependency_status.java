package eu.openreq.mulperi.models.json;

import com.google.gson.annotations.SerializedName;

public enum Dependency_status {

//	PROPOSED,
//	ACCEPTED,
//	REJECTED
//	
	@SerializedName(value="proposed", alternate= {"PROPOSED"})
	PROPOSED,
	@SerializedName(value="accepted", alternate= {"ACCEPTED"})
	ACCEPTED,
	@SerializedName(value="rejected", alternate= {"REJECTED"})
	REJECTED
	
//	@JsonProperty("proposed")
//	PROPOSED,
//	@JsonProperty("accepted")
//	ACCEPTED,
//	@JsonProperty("rejected")
//	REJECTED
	
}
