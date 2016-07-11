package br.com.uniriotec.process.model;

/**
 * Abstraction of a process model Lane element.
 * Usually a Lane element in a process model is element that represent the roles that execute the activities, like 'Customer', 'Manager'.
 */
public class Lane {
	
	private int id;
	private String name;
	private Pool pool;
	
	public Lane(int id, String name, Pool pool) {
		this.id = id;
		this.name = name;
		this.pool = pool;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Pool getPool() {
		return pool;
	}
	
	

}
