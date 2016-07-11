package br.com.uniriotec.json.strucuture;

public class Stencil {

	String id;
	
	public Stencil(){}
	
	public Stencil(Stencil source){
		this.id = source.id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public String toString() {
		return id;
	}
}
