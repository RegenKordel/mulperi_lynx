package eu.openreq.mulperi.models.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public enum Requirement_status {

//	SUBMITTED,
//	PENDING,
//	ACCEPTED,
//	DRAFT,
//	DEFERRED,
//	NEW,
//	PLANNED,
//	COMPLETED,
//	REJECTED,
//	RECOMMENDED,
//	IN_PROGRESS
	
	@SerializedName(value="submitted", alternate= {"SUBMITTED"})
	SUBMITTED,
	@SerializedName(value="pending", alternate= {"PENDING"})
	PENDING,
	@SerializedName(value="accepted", alternate= {"ACCEPTED"})
	ACCEPTED,
	@SerializedName(value="draft", alternate= {"DRAFT"})
	DRAFT,
	@SerializedName(value="deferred", alternate= {"DEFERRED"})
	DEFERRED,
	@SerializedName(value="new", alternate= {"NEW"})
	NEW,
	@SerializedName(value="planned", alternate= {"PLANNED"})
	PLANNED,
	@SerializedName(value="completed", alternate= {"COMPLETED"})
	COMPLETED,
	@SerializedName(value="rejected", alternate= {"REJECTED"})
	REJECTED,
	@SerializedName(value="recommended", alternate= {"RECOMMENDED"})
	RECOMMENDED,
	@SerializedName(value="in_progress", alternate= {"IN_PROGRESS"})
	IN_PROGRESS
	
	
//	@JsonProperty("submitted")
//	SUBMITTED,
//	@JsonProperty("pending")
//	PENDING,
//	@JsonProperty("accepted")
//	ACCEPTED,
//	@JsonProperty("draft")
//	DRAFT,
//	@JsonProperty("deferred")
//	DEFERRED,
//	@JsonProperty("new")
//	NEW,
//	@JsonProperty("planned")
//	PLANNED,
//	@JsonProperty("completed")
//	COMPLETED,
//	@JsonProperty("rejected")
//	REJECTED,
//	@JsonProperty("recommended")
//	RECOMMENDED,
//	@JsonProperty("in_progress")
//	IN_PROGRESS
}
