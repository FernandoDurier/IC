package br.com.uniriotec.process.model;


/**
 * Generic representation of a process model element.
 * @author Edward
 */
public abstract class Element 
{
	/**
	 * The id (unique identification) of the element.
	 */
	private int id;
	/**
	 * The text contained by the element.
	 */
	private String label;
	/**
	 * ???
	 */
	private Lane lane;
	/**
	 * ???
	 */
	private Pool pool;

	public Element(int id, String label, Lane lane, Pool pool) 
	{
		this.id = id;
		this.label = label;
		this.lane = lane;
		this.pool = pool;
	}
	
	/**
	 * Gets the <code>Pool</code> associated with the element.
	 * @return The associated pool.
	 * @see Pool
	 */
	public Pool getPool() {
		return pool;
	}

	/**
	 * Gets the id.
	 * @return The element's id.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Gets the label of the element.
	 * @return The text contained by the element. 
	 */
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String newLabel){
		this.label = newLabel;
	}

	/**
	 * Gets the <code>Lane</code> associated with the element.
	 * @return The associated pool.
	 * @see Pool
	 */
	public Lane getLane() {
		return lane;
	}
}
