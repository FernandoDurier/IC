package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;

public class ElementLevel {
	
	String resourceId;
	ElementProperties properties;
	Stencil stencil;
	ArrayList<ElementLevel>childShapes;
	ArrayList<ElementLevel>outgoing;
	Bounds bounds;
	ArrayList<ElementLevel>dockers;
	
	public ElementLevel(){}
	
	public ElementLevel(ElementLevel source){
		this.resourceId = source.resourceId;
		if(source.properties != null)
			this.properties = new ElementProperties(source.properties);
		if(source.stencil != null)
			this.stencil = new Stencil(source.stencil);
		if(source.bounds != null)
			this.bounds = new Bounds(source.bounds);
		
		if(source.childShapes != null){
			this.childShapes = new ArrayList<ElementLevel>(source.childShapes.size());
			for(ElementLevel element : source.childShapes){
				ElementLevel newElement = new ElementLevel(element);
				this.childShapes.add(newElement);
			}
		}
		
		if(source.outgoing != null){
			this.outgoing = new ArrayList<ElementLevel>(source.outgoing.size());
			for(ElementLevel element : source.outgoing){
				ElementLevel newElement = new ElementLevel(element);
				this.outgoing.add(newElement);
			}
		}
		
		if(source.dockers != null){
			this.dockers = new ArrayList<ElementLevel>(source.dockers.size());
			for(ElementLevel element : source.dockers){
				ElementLevel newElement = new ElementLevel(element);
				this.dockers.add(newElement);
			}
		}
	}
	
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	public ElementProperties getProperties() {
		return properties;
	}
	public void setProperties(ElementProperties properties) {
		this.properties = properties;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public ElementProperties getProps() {
		return properties;
	}
	public void setProps(ElementProperties props) {
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
	public ArrayList<ElementLevel> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(ArrayList<ElementLevel> outgoing) {
		this.outgoing = outgoing;
	}
	public ArrayList<ElementLevel> getDockers() {
		return dockers;
	}
	public void setDockers(ArrayList<ElementLevel> dockers) {
		this.dockers = dockers;
	}
	
	
}
