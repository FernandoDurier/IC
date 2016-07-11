package br.com.uniriotec.json;

public class JsonLane {
	private int id;
	private String label;
	private int poolId;
	
	public JsonLane(int id, String label, int poolId) {
		super();
		this.id = id;
		this.label = label;
		this.poolId = poolId;
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

	public int getPoolId() {
		return poolId;
	}

	public void setPoolId(int poolId) {
		this.poolId = poolId;
	}
}
