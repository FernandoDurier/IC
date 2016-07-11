package br.com.uniriotec.process.model;

/**
 * Abstraction of a process model Pool element.
 * Usually a Pool element in a process model is the top level element that can be either a macro Role, like 'Hotel', 'Restaurant' or sequences flows between lanes.
 */
public class Pool
{
	private int id;
	private String name;
	
	public Pool(int id, String name) {
		this.id  = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	

}
