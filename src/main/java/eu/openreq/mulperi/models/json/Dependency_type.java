package eu.openreq.mulperi.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum Dependency_type {

//	CONTRIBUTES,
//	DAMAGES,
//	REFINES,
//	REQUIRES,
//	INCOMPATIBLE,
//	DECOMPOSITION,
//	SIMILAR,
//	DUPLICATES,
//	REPLACES
	
	@SerializedName(value="contributes", alternate= {"CONTRIBUTES"})
	CONTRIBUTES,
	@SerializedName(value="damages", alternate= {"DAMAGES"})
	DAMAGES,
	@SerializedName(value="refines", alternate= {"REFINES"})
	REFINES,
	@SerializedName(value="requires", alternate= {"REQUIRES"})
	REQUIRES,
	@SerializedName(value="incompatible", alternate= {"INCOMPATIBLE"})
	INCOMPATIBLE,
	@SerializedName(value="decomposition", alternate= {"DECOMPOSITION"})
	DECOMPOSITION,
	@SerializedName(value="similar", alternate= {"SIMILAR"})
	SIMILAR,
	@SerializedName(value="duplicates", alternate= {"DUPLICATES"})
	DUPLICATES,
	@SerializedName(value="replaces", alternate= {"REPLACES"})
	REPLACES,
	@SerializedName(value="excludes", alternate= {"EXCLUDES"})
	EXCLUDES,
	@SerializedName(value="implies", alternate= {"IMPLIES"})
	IMPLIES,
	
//	@JsonProperty("contributes")
//	CONTRIBUTES,
//	@JsonProperty("damages")
//	DAMAGES,
//	@JsonProperty("refines")
//	REFINES,
//	@JsonProperty("requires")
//	REQUIRES,
//	@JsonProperty("incompatible")
//	INCOMPATIBLE,
//	@JsonProperty("decomposition")
//	DECOMPOSITION,
//	@JsonProperty("similar")
//	SIMILAR,
//	@JsonProperty("duplicates")
//	DUPLICATES,
//	@JsonProperty("replaces")
//	REPLACES

}
