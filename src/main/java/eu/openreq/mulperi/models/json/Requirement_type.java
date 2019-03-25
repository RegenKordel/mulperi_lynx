package eu.openreq.mulperi.models.json;

import com.google.gson.annotations.SerializedName;

public enum Requirement_type {

//	PROSE,
//	REQUIREMENT,
//	FUNCTIONAL,
//	NON_FUNCTIONAL,
//	ISSUE,
//	USER_STORY,
//	EPIC,
//	INITIATIVE,
//	TASK,
//	BUG
	
	@SerializedName(value="prose", alternate= {"PROSE"})
	PROSE,
	@SerializedName(value="requirement", alternate= {"REQUIREMENT"})
	REQUIREMENT,
	@SerializedName(value="functional", alternate= {"FUNCTIONAL"})
	FUNCTIONAL,
	@SerializedName(value="non_functional", alternate= {"NON_FUNCTIONAL"})
	NON_FUNCTIONAL,
	@SerializedName(value="issue", alternate= {"ISSUE"})
	ISSUE,
	@SerializedName(value="user_story", alternate= {"USER_STORY"})
	USER_STORY,
	@SerializedName(value="epic", alternate= {"EPIC"})
	EPIC,
	@SerializedName(value="initiative", alternate= {"INITIATIVE"})
	INITIATIVE,
	@SerializedName(value="task", alternate= {"TASK"})
	TASK,
	@SerializedName(value="bug", alternate= {"BUG"})
	BUG
	
//	@JsonProperty("prose")
//	PROSE,
//	@JsonProperty("requirement")
//	REQUIREMENT,
//	@JsonProperty("functional")
//	FUNCTIONAL,
//	@JsonProperty("non_functional")
//	NON_FUNCTIONAL,
//	@JsonProperty("issue")
//	ISSUE,
//	@JsonProperty("user_story")
//	USER_STORY,
//	@JsonProperty("epic")
//	EPIC,
//	@JsonProperty("initiative")
//	INITIATIVE,
//	@JsonProperty("task")
//	TASK,
//	@JsonProperty("bug")
//	BUG
}
