package br.com.uniriotec.graph.process;

import general.language.common.graph.AbstractDirectedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import br.com.uniriotec.graph.process.ProcessGraphGateway.GatewayType;

/**
 * Basic process model implementation
 * 
 * @author Artem Polyvyanyy
 */
public class GraphProcess extends AbstractDirectedGraph<ProcessGraphControlFlow, ProcessGraphNode> {
	private String name;
	
	/**
	 * Construct an empty process
	 */
	public GraphProcess() {
		this.name = "";
	}
	
	/**
	 * Construct an empty process with name
	 */
	public GraphProcess(String name) {
		this.name = name;
	}

	/**
	 * Create a control flow
	 * @param from Source node
	 * @param to Target node
	 * @return The fresh control flow, or <code>null</code> if control flow between source and target already exists
	 */
	public ProcessGraphControlFlow addControlFlow(ProcessGraphNode from, ProcessGraphNode to) {
		if (from == null || to == null) return null;
		
		Collection<ProcessGraphNode> ss = new ArrayList<ProcessGraphNode>(); ss.add(from);
		Collection<ProcessGraphNode> ts = new ArrayList<ProcessGraphNode>(); ts.add(to);
		
		if (!this.checkEdge(ss, ts)) return null;
		
		return new ProcessGraphControlFlow(this, from, to);
	}
	
	/**
	 * Remove control flow from the process
	 * @param flow Control flow to remove
	 * @return Control flow that was removed from the process, or <code>null</code> if control flow was not removed
	 */
	public ProcessGraphControlFlow removeControlFlow(ProcessGraphControlFlow flow) {
		return this.removeEdge(flow)!=null ? flow : null;
	}
	
	/**
	 * Remove control flow from the process
	 * @param flows A collection of control flows to be removed
	 * @return Control flows that were removed from the process, <code>null</code> if no control flow was removed
	 */
	public Collection<ProcessGraphControlFlow> removeControlFlows(Collection<ProcessGraphControlFlow> flows) {
		return this.removeEdges(flows);
	}
	
	/**
	 * Add task to the process
	 * @param task Task to add
	 * @return Task that was added to the process, <code>null</code> upon failure
	 */
	public ProcessGraphTask addTask(ProcessGraphTask task) {
		return this.addVertex(task)!=null ? task : null;
	}
	
	/**
	 * Remove task from the process
	 * @param task Task to remove
	 * @return Task that was removed from the process, <code>null</code> upon failure
	 */
	public ProcessGraphTask removeTask(ProcessGraphTask task) {
		return this.removeVertex(task)!=null ? task : null;
	}
	
	/**
	 * Add gateway to the process
	 * @param gateway Gateway to add
	 * @return Gateway that was added to the process, <code>null</code> upon failure
	 */
	public ProcessGraphGateway addGateway(ProcessGraphGateway gateway) {
		return this.addVertex(gateway)!=null ? gateway : null;
	}
	
	/**
	 * Remove gateway from the process
	 * @param task Gateway to remove
	 * @return Gateway that was removed from the process, <code>null</code> upon failure
	 */
	public ProcessGraphGateway removeGateway(ProcessGraphGateway gateway) {
		return this.removeVertex(gateway)!=null ? gateway : null;
	}
	
	/**
	 * Get tasks of the process
	 * @return A collection of process tasks
	 */
	public Collection<ProcessGraphTask> getTasks() {
		Collection<ProcessGraphTask> result = new ArrayList<ProcessGraphTask>();
		
		Collection<ProcessGraphNode> nodes = this.getVertices();
		Iterator<ProcessGraphNode> i = nodes.iterator();
		while (i.hasNext()) {
			ProcessGraphNode obj = i.next();
			if (obj instanceof ProcessGraphTask)
				result.add((ProcessGraphTask)obj);
		}
		
		return result;
	}
	
	/**
	 * Get gateways of the process
	 * @return A collection of process gateways
	 */
	public Collection<ProcessGraphGateway> getGateways() {
		Collection<GatewayType> types = new ArrayList<GatewayType>();
		types.add(GatewayType.AND);
		types.add(GatewayType.XOR);
		types.add(GatewayType.OR);
		types.add(GatewayType.UNDEFINED);
		return this.getGateways(types);
	}
	
	/**
	 * Get gateways of the process of certain type 
	 * @param type Gateway type
	 * @return A collection of process gateways of the type specified
	 */
	public Collection<ProcessGraphGateway> getGateways(GatewayType type) {
		Collection<GatewayType> types = new ArrayList<GatewayType>();
		types.add(type);
		return this.getGateways(types);
	}
	
	/**
	 * Get process nodes
	 * @return A collection of process nodes
	 */
	public Collection<ProcessGraphNode> getNodes() {
		return this.getVertices();
	}
	
	/**
	 * Get control flow of the process
	 * @return A collection of process flows of the process
	 */
	public Collection<ProcessGraphControlFlow> getControlFlow() {
		return this.getEdges();
	}
	
	/**
	 * Get process name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Set process name
	 * @param name Process name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	private Collection<ProcessGraphGateway> getGateways(Collection<GatewayType> types) {
		Collection<ProcessGraphGateway> result = new ArrayList<ProcessGraphGateway>();
		
		Collection<ProcessGraphNode> nodes = this.getVertices();
		Iterator<ProcessGraphNode> i = nodes.iterator();
		while (i.hasNext()) {
			ProcessGraphNode obj = i.next();
			if (obj instanceof ProcessGraphGateway) {
				ProcessGraphGateway g = (ProcessGraphGateway) obj;
				if (types.contains(g.getGatewayType()))
					result.add(g);
			}		
		}
		
		return result;
	}
}
