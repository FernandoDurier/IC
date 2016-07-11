package br.com.uniriotec.json;

/**
 * Abstraction of a process model Pool element.
 * Usually a Pool element in a process model is the top level element that can be either a macro Role, like 'Hotel', 'Restaurant' or sequences flows between lanes.
 */
public class JsonPool {
	
	private int id;
	private String label;
	
	public JsonPool(int id, String label) {
		this.id = id;
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
