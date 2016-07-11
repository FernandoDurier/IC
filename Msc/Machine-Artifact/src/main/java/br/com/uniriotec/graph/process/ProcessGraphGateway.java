package br.com.uniriotec.graph.process;


public class ProcessGraphGateway extends ProcessGraphNode {	
	public enum GatewayType {
		AND,
		XOR,
		OR,
		EVENT,
		UNDEFINED;
	}
	
	private GatewayType type;

	public ProcessGraphGateway(GatewayType type) {
		this.type = type;
	}
	
	public ProcessGraphGateway(GatewayType type, String name) {
		this.type = type;
		this.setName(name);
	}
	
	public GatewayType getGatewayType() {
		return type;
	}

	public void setGatewayType(GatewayType type) {
		this.type = type;
	}
	
	public boolean isEventBased() {
		return this.type == GatewayType.EVENT;
	}
	
	public boolean isXOR() {
		return this.type == GatewayType.XOR;
	}
	
	public boolean isAND() {
		return this.type == GatewayType.AND;
	}
	
	public boolean isOR() {
		return this.type == GatewayType.OR;
	}
}
