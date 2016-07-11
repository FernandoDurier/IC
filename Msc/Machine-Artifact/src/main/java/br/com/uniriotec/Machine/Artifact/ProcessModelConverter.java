package br.com.uniriotec.Machine.Artifact;

import java.util.HashMap;

import br.com.uniriotec.graph.process.GraphProcess;
import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphEvent;
import br.com.uniriotec.graph.process.ProcessGraphGateway;
import br.com.uniriotec.graph.process.ProcessGraphGateway.GatewayType;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.graph.process.ProcessGraphTask;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.Arc;
import br.com.uniriotec.process.model.Element;
import br.com.uniriotec.process.model.Event;
import br.com.uniriotec.process.model.Gateway;
import br.com.uniriotec.process.model.ProcessModel;

/***
 * Class responsible for the conversion among different formats for process models.
 * @author Raphael Rodrigues
 *
 */
public class ProcessModelConverter {
	/***
	 * Simulates a cache strategy, specially useful if a lot of conversions 
	 * take place during the application execution.
	 */
	private HashMap<Integer, Element> converterMap;
	private int newElems;
	
	/**
	 * Transforms ProcessModel format to HPI Process Format (writes IDs to labels in order to save the information)
	 */
	public GraphProcess convertToRigidFormat(ProcessModel pm){
		GraphProcess p = new GraphProcess();
		converterMap = new HashMap<Integer, Element>();
		HashMap <Integer, ProcessGraphNode> elementMap = new HashMap<Integer, ProcessGraphNode>();
		
		// Transform activities
		for (Activity a: pm.getActivites().values()) {
			ProcessGraphTask t = new ProcessGraphTask(Integer.toString(a.getId()));
			elementMap.put(a.getId(), t);
			converterMap.put(a.getId(),a);
		}
		
		// Transform events
		for (Event e: pm.getEvents().values()) {
			ProcessGraphTask et = new ProcessGraphTask(Integer.toString(e.getId()));
			elementMap.put(e.getId(), et);
			converterMap.put(e.getId(),e);
		}
		
		// Transform gateway
		for (Gateway g: pm.getGateways().values()) {
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.XOR) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.XOR,Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
				
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.OR) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.OR,Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.AND) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.AND,Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.EVENT) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.EVENT,Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
			converterMap.put(g.getId(),g);
		}
		
		// Transform arcs
		for (Arc arc: pm.getArcs().values()) {
			if (arc.getSource() != null) {
				p.addControlFlow(elementMap.get(arc.getSource().getId()), elementMap.get(arc.getTarget().getId()));
			}	
		}
		return p;
	}
	
	/**
	 * Reconstructs the ProcessModel format from HPI Process Model after Rigid Structuring 
	 */
	public ProcessModel convertFromRigidFormat(GraphProcess p) {
		ProcessModel pm = new ProcessModel(0, "Structured Model");
		
		HashMap<String,Integer> idMap = new HashMap<String, Integer>();
		HashMap<Integer,Element> elemMap = new HashMap<Integer, Element>();
		newElems = 0;
		
		for (ProcessGraphTask t: p.getTasks()) {
			int id = Integer.valueOf(t.getName());
			if (converterMap.containsKey(id)) {
				Element elem = converterMap.get(id);
				
				if (elem.getClass().toString().endsWith("Activity")) { 
					Activity a = (Activity) elem;
					pm.addActivity(a);
					idMap.put(t.getId(), a.getId());
					elemMap.put(a.getId(), a);
				}
				if (elem.getClass().toString().endsWith("Event")) { 
					Event e = (Event) elem;
					pm.addEvent(e);
					idMap.put(t.getId(), e.getId());
					elemMap.put(e.getId(), e);
				}
			} else {
				System.out.println("ERROR: Transformation Problem");
			}
		}
		
		for (ProcessGraphGateway g: p.getGateways()) {
			if (!g.getName().equals("") && converterMap.containsKey(Integer.valueOf(g.getName()))) {
				int id = Integer.valueOf(g.getName());
				Gateway gw = (Gateway) converterMap.get(id);
				pm.addGateway(gw);
			} else {
				if (g.getGatewayType() == GatewayType.XOR) {
					Gateway gw = new Gateway(getId(), "", null, null, br.com.uniriotec.process.model.GatewayType.XOR);
					pm.addGateway(gw);
					idMap.put(g.getId(), gw.getId());
					elemMap.put(gw.getId(), gw);
				}
				if (g.getGatewayType() == GatewayType.OR) {
					Gateway gw = new Gateway(getId(), "", null, null, br.com.uniriotec.process.model.GatewayType.OR);
					pm.addGateway(gw);
					idMap.put(g.getId(), gw.getId());
					elemMap.put(gw.getId(), gw);
				}
				if (g.getGatewayType() == GatewayType.AND) {
					Gateway gw = new Gateway(getId(), "", null, null, br.com.uniriotec.process.model.GatewayType.AND);
					pm.addGateway(gw);
					idMap.put(g.getId(), gw.getId());
					elemMap.put(gw.getId(), gw);
				}
				if (g.getGatewayType() == GatewayType.EVENT) {
					Gateway gw = new Gateway(getId(), "", null, null, br.com.uniriotec.process.model.GatewayType.EVENT);
					pm.addGateway(gw);
					idMap.put(g.getId(), gw.getId());
					elemMap.put(gw.getId(), gw);
				}
			}
		}
		
		for (ProcessGraphControlFlow f: p.getControlFlow()) {
			Element source = elemMap.get(idMap.get(f.getSource().getId()));
			Element target = elemMap.get(idMap.get(f.getTarget().getId()));
			Arc arc = new Arc(getId(), f.getName(), source , target);
			pm.addArc(arc);
		}
		return pm;
	}
	
	/**
	 * Transforms given ProcessModel to HPI format, executing the following steps: <br>
	 *    1) Transform the activities from the process model to a basic and compatible RPST format.<br>
	 *    2) Transform the events from the process model to a basic and compatible RPST format.<br>
	 *    3) Transform the gateways from the process model to a basic and compatible RPST format.<br>
	 *    4) Transform the arcs from the process model to a basic and compatible RPST format.<br>
	 * @return An abstraction of the <code>Process</code> object that represents the model.
	 */
	public GraphProcess convertToRPSTFormat(ProcessModel pm)
	{
		GraphProcess p = new GraphProcess();
		HashMap <Integer, ProcessGraphNode> elementMap = new HashMap<Integer, ProcessGraphNode>();
		
		// Transform activities
		for (Activity a: pm.getActivites().values()) {
			ProcessGraphTask t = new ProcessGraphTask(a.getLabel());
			t.setId(Integer.toString(a.getId()));
			elementMap.put(a.getId(), t);
		}
		
		// Transform events
		for (Event e: pm.getEvents().values()) {
			ProcessGraphEvent et = new ProcessGraphEvent(e.getLabel());
			et.setId(Integer.toString(e.getId()));
			elementMap.put(e.getId(), et);
		}
		
		// Transform gateway
		transformGatewayElement(pm, elementMap);
		
		// Transform arcs
		for (Arc arc: pm.getArcs().values()) {
			if (arc.getSource() != null) {
				p.addControlFlow(elementMap.get(arc.getSource().getId()), elementMap.get(arc.getTarget().getId()));
			}	
		}
		return p;
	}
	
	private void transformGatewayElement(ProcessModel pm, HashMap<Integer, ProcessGraphNode> elementMap)
	{
		for (Gateway g: pm.getGateways().values()) {
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.XOR) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.XOR, g.getLabel());
				gt.setId(Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
				
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.OR) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.OR, g.getLabel());
				gt.setId(Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.AND) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.AND, g.getLabel());
				gt.setId(Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
			if (g.getType() == br.com.uniriotec.process.model.GatewayType.EVENT) {
				ProcessGraphGateway gt = new ProcessGraphGateway(GatewayType.EVENT, g.getLabel());
				gt.setId(Integer.toString(g.getId()));
				elementMap.put(g.getId(), gt);
			}
		}
	}
	
	/**
	 * Calculates new ID 
	 */
	private int getId() {
		int max = -1;
		for (int i: converterMap.keySet()) {
			if (i>max) {
				max = i;
			}
		}
		newElems++;
		return max+newElems;
	}
}
