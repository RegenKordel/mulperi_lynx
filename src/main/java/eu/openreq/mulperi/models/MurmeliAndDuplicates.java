package eu.openreq.mulperi.models;

import com.google.gson.JsonArray;

public class MurmeliAndDuplicates {

	private String murmeliString;
	
	private JsonArray duplicatesString;
	
	public MurmeliAndDuplicates(String murmeliString, JsonArray duplicatesString) {
		this.murmeliString = murmeliString;
		this.duplicatesString = duplicatesString;
	}

	public String getMurmeliString() {
		return murmeliString;
	}

	public void setMurmeliString(String murmeliString) {
		this.murmeliString = murmeliString;
	}

	public JsonArray getDuplicatesString() {
		return duplicatesString;
	}

	public void setDuplicatesString(JsonArray duplicatesString) {
		this.duplicatesString = duplicatesString;
	}
	
	
}
