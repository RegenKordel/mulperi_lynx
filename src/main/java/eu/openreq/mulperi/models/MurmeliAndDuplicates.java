package eu.openreq.mulperi.models;

public class MurmeliAndDuplicates {

	private String murmeliString;
	
	private String duplicatesString;
	
	public MurmeliAndDuplicates(String murmeliString, String duplicatesString) {
		super();
		this.murmeliString = murmeliString;
		this.duplicatesString = duplicatesString;
	}

	public String getMurmeliString() {
		return murmeliString;
	}

	public void setMurmeliString(String murmeliString) {
		this.murmeliString = murmeliString;
	}

	public String getDuplicatesString() {
		return duplicatesString;
	}

	public void setDuplicatesString(String duplicatesString) {
		this.duplicatesString = duplicatesString;
	}
	
	
}
