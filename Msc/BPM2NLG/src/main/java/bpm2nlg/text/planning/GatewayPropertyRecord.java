package bpm2nlg.text.planning;

import general.language.common.rpst.RPST;
import general.language.common.rpst.RPSTEdge;
import general.language.common.rpst.RPSTNode;

import java.util.ArrayList;

import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.process.model.Arc;
import br.com.uniriotec.process.model.ProcessModel;

public class GatewayPropertyRecord {
	
	private int outgoingArcs = 0;
	private int maxPathDepth = 0;
	private int maxPathActivityNumber = 0;
	private boolean isGatewayLabeled = false;
	private boolean hasLabeledArcs = false;
	private boolean hasYNArcs = false;
	private RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node;
	private RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst;
	private ProcessModel process;
	
	
	public GatewayPropertyRecord(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node,  RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst, ProcessModel process) {
		this.node = node;
		this.rpst = rpst;
		this.process = process;
		setGatewayPropertyRecord();
	}
	
	/**
	 * Evaluates and determines according values for Gateway. 
	 */
	private void setGatewayPropertyRecord() {
		
		// Outgoing arcs
		for (RPSTEdge<ProcessGraphControlFlow, ProcessGraphNode> conn: rpst.getEdges()) {
			if (conn.getSource().getId() == node.getId()) {
				outgoingArcs++;
			}
		}
		
		// maxPathDepth / maxPathActivityNumber
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> paths = (ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>>) rpst.getChildren(node);
		for (RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> pnode: paths) {
			int depth = PlanningHelper.getDepth(pnode, rpst);
			int number = rpst.getChildren(pnode).size()-1;
			if (depth > maxPathDepth) {
				maxPathDepth = depth;
			}
			if (number > maxPathActivityNumber) {
				maxPathActivityNumber = number;
			}
		}
		
		// Labeling
		isGatewayLabeled = node.getEntry().getName().equals("") == false;
		hasLabeledArcs = true;
		for (Arc arc: process.getArcs().values()) {
			if (arc.getSource().getId() == Integer.valueOf(node.getEntry().getId())) {
				if (arc.getLabel().equals("") == true) {
					hasLabeledArcs = false;
				}
			}
		}
		
		hasYNArcs = true;
		if (process.getArcs().values().size() == 2) {
			for (Arc arc: process.getArcs().values()) {
				if (arc.getLabel().toLowerCase().equals("yes") && arc.getLabel().toLowerCase().equals("no")) {
					hasLabeledArcs = false;
				}
			}
		}
	}
	
	public int getOutgoingArcs() {
		return outgoingArcs;
	}


	public int getMaxPathDepth() {
		return maxPathDepth;
	}


	public int getMaxPathActivityNumber() {
		return maxPathActivityNumber;
	}


	public boolean isGatewayLabeled() {
		return isGatewayLabeled;
	}


	public boolean hasLabeledArcs() {
		return hasLabeledArcs;
	}


	public boolean hasYNArcs() {
		return hasYNArcs;
	}

}
