package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;

public class LaneLevel {
	
	String resourceId;
	LaneProperties properties;
	Stencil stencil;
	ArrayList<ElementLevel> childShapes;
	ArrayList<ElementLevel> outgoing;
	Bounds bounds;
	ArrayList<ElementLevel> dockers;
	
	public ArrayList<ElementLevel> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(ArrayList<ElementLevel> outgoing) {
		this.outgoing = outgoing;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	public ArrayList<ElementLevel> getDockers() {
		return dockers;
	}
	public void setDockers(ArrayList<ElementLevel> dockers) {
		this.dockers = dockers;
	}
	public LaneProperties getProperties() {
		return properties;
	}
	public void setProperties(LaneProperties properties) {
		this.properties = properties;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public LaneProperties getProps() {
		return properties;
	}
	public void setProps(LaneProperties props) {
		this.properties = props;
	}
	public Stencil getStencil() {
		return stencil;
	}
	public void setStencil(Stencil stencil) {
		this.stencil = stencil;
	}
	public ArrayList<ElementLevel> getChildShapes() {
		return childShapes;
	}
	public void setChildShapes(ArrayList<ElementLevel> childShapes) {
		this.childShapes = childShapes;
	}
	
	

}
