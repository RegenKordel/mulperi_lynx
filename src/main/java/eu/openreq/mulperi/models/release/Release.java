package eu.openreq.mulperi.models.release;

public class Release {
	final int id; 
	int maxCapacity;
	
	public Release(int id, int maxCapacity) {
		super();
		this.id = id;
		this.maxCapacity = maxCapacity;
	}

	public int getId() {
		return id;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

}
