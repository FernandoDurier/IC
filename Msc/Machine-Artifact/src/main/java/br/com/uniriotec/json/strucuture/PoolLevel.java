package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;

/***
 * This class store PoolLevel elements read from the Json File.
 * PoolLevel elements can be any of the following types: <br>
 * <ul>
 * 	<li>BPMN Pool</li>
 *  <li>BPMN Sequence Flow (Arcs)</li>
 * </ul>
 */
public class PoolLevel 
{
	String resourceId;
	PoolProperties properties;
	Stencil stencil;
	ArrayList<LaneLevel>childShapes;
	Target target;
	ArrayList<Outgoing> outgoing;
	Bounds bounds;
	ArrayList<Point>dockers;
	
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	public ArrayList<Point> getDockers() {
		return dockers;
	}
	public void setDockers(ArrayList<Point> dockers) {
		this.dockers = dockers;
	}
	public PoolProperties getProperties() {
		return properties;
	}
	public void setProperties(PoolProperties properties) {
		this.properties = properties;
	}
	
	public ArrayList<Outgoing> getOutgoing() {
		return outgoing;
	}
	public void setOutgoing(ArrayList<Outgoing> outgoing) {
		this.outgoing = outgoing;
	}
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public PoolProperties getProps() {
		return properties;
	}
	public void setProps(PoolProperties props) {
		this.properties = props;
	}
	public Stencil getStencil() {
		return stencil;
	}
	public void setStencil(Stencil stencil) {
		this.stencil = stencil;
	}
	public ArrayList<LaneLevel> getChildShapes() {
		return childShapes;
	}
	public void setChildShapes(ArrayList<LaneLevel> childShapes) {
		this.childShapes = childShapes;
	}
	
	
	
}
