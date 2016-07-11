package bpm2nlg.text.planning;

import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.ILabelProperties;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;
import general.language.common.rpst.RPST;
import general.language.common.rpst.RPSTNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import bpm2nlg.LanguageConfig;
import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphEvent;
import br.com.uniriotec.graph.process.ProcessGraphGateway;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.graph.process.ProcessGraphTask;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.Annotation;
import br.com.uniriotec.process.model.Event;
import br.com.uniriotec.process.model.EventType;
import br.com.uniriotec.process.model.Lane;
import br.com.uniriotec.process.model.ProcessModel;

public class PlanningHelper {
	
	
	/**
	 * Creates an order for the top level of a given RPST Tree.
	 */
	public static ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> sortTreeLevel(RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> lnode, ProcessGraphNode startElem, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		
		if (PlanningHelper.isSplit(lnode, rpst)) {
			ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> unordered = new ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>>();
			
			if (rpst.getChildren((lnode)).size() != 2) {
				unordered.addAll(rpst.getChildren((lnode)));
				return unordered;
			} else {
				unordered.addAll(rpst.getChildren((lnode)));
				if (getDepth(unordered.get(0), rpst) > getDepth(unordered.get(1), rpst)) {
					ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> ordered = new ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>>();
					ordered.add(unordered.get(1));
					ordered.add(unordered.get(0));
					return ordered;	
				} else {
					ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> ordered = new ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>>();
					ordered.addAll(rpst.getChildren((lnode)));
					return ordered;
				}
			}
		}
		
		Collection<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> topNodes = rpst.getChildren((lnode));
		ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> orderedTopNodes = new ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>>();
		
		if (isRigid(lnode)) {
			return orderedTopNodes;
		}
		
		ProcessGraphNode currentElem = startElem;
		while (orderedTopNodes.size() < topNodes.size()) {
			for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: topNodes) {
				if (node.getEntry().equals(currentElem)) {
					orderedTopNodes.add(node);
					currentElem = node.getExit();
					break;
				} 
			}
		}	
		return orderedTopNodes;
	}
	
	/**
	 * Returns String representation of node. 
	 */
	public static String getNodeRepresentation(ProcessGraphNode n) {
		String s = "";
		if (PlanningHelper.isEvent(n)) {
			s = "Event + (" + n.getId() + ")";
		}
		else if (PlanningHelper.isGateway(n)) {
			ProcessGraphGateway g = (ProcessGraphGateway) n;
			if (g.isAND()) {
				s = "AND (" + n.getId() + ")";
			}
			if (g.isXOR()) {
				if (g.getName().equals("")) {
					s = "XOR (" + n.getId() + ")";
				}
				s = g.getName()+ "(XOR," + n.getId() + ")";
				
			}
			if (g.isOR()) {
				if (g.getName().equals("")) {
					s = "OR (" + n.getId() + ")";
				}
				s = g.getName()+ "(OR," + n.getId() + ")";
				
			}
		}
		else {
			s = n.toString();
		}
		return s;
	}
	
	/**
	 * Returns amount of nodes of the next level in the RPST. 
	 */
	public static int getSubLevelCount(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		return rpst.getChildren(node).size();
	}
	
	/**
	 * Returns amount of nodes on the current RPST level. 
	 */
	public static int getNodeCount(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (PlanningHelper.isTrivial(node)) {
			return 0;
		} else {
			Collection<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> children = rpst.getChildren(node);
			int sum = 0;
			for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> child: children) {
				sum = sum + 1 + getNodeCount(child, rpst);
			}
			return sum;
		}
	}
	
	
	/**
	 * Compute depth of a given component.
	 */
	public static int getDepth(RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node,  RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		int depth = getDepthHelper(node, rpst);
		if (depth > 1) {
			return depth -1;
		} else {
			return depth;
		}
	}
	
	/**
	 * Helper for depth computation.
	 */
	public static int getDepthHelper(RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node,  RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (node.getName().startsWith("T")) {
			return 0;
		}
		ArrayList<Integer> depthValues = new ArrayList<Integer>();
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> n: rpst.getChildren(node)){
			depthValues.add(getDepthHelper(n, rpst) + 1);
		}
		return Collections.max(depthValues);
	}
	
	
	/**
	 * Returns type of given bond. 
	 */
	public static String getBondType(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isEventSplit(bond, rpst)) {
			return "EVENTBASED";
		}
		if (isANDSplit(bond, rpst)) {
			return "AND";
		}
		if (isXORSplit(bond, rpst)) {
			return "XOR";
		}
		if (isORSplit(bond, rpst)) {
			return "OR";
		}
		if (isSkip(bond, rpst)) {
			return "Skip";
		}
		if (isLoop(bond, rpst)) {
			return "Loop";
		}
		return "";
	}
	
	/**
	 * Decides whether a given bond is a loop (Arc from exit gateway to entry gateway).
	 */
	public static boolean isLoop(RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isBond(bond) && isGateway(bond.getEntry()) && ((ProcessGraphGateway) bond.getEntry()).isXOR()) {
			for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: rpst.getChildren((bond))) {
				if (isTrivial(node) && node.getEntry().equals(bond.getExit()) && node.getExit().equals(bond.getEntry()) && isGateway(node.getExit())) {
					return true;
				}
				if (bond.getEntry().equals(node.getExit()) && isGateway(node.getExit())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Decides whether a given bond is a skip (Arc from entry gateway to exit gateway).
	 */
	public static boolean isSkip(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isBond(bond) && isGateway(bond.getEntry())&& ((ProcessGraphGateway) bond.getEntry()).isXOR()) {
			for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: rpst.getChildren((bond))) {
				if (isTrivial(node) && node.getEntry().equals(bond.getEntry()) && node.getExit().equals(bond.getExit()) && isGateway(node.getExit())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Decides whether a given bond is a skip (All arcs are outgoing).
	 */
	public static boolean isSplit(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: rpst.getChildren((bond))) {
			if (node.getEntry() != bond.getEntry()) {
				return false;
			}
		}
		return true;
	}
	
		public static boolean isEventSplit(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
			if (isBond(bond) && isGateway(bond.getEntry())) {
				return ((ProcessGraphGateway) bond.getEntry()).isEventBased() && isSplit(bond, rpst);
			}
			return false;
		}
		
		
	/**
	 * Decides whether a given bond is an AND split.
	 */
	public static boolean isANDSplit(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isBond(bond) && isGateway(bond.getEntry())) {
			return ((ProcessGraphGateway) bond.getEntry()).isAND() && isSplit(bond, rpst);
		}
		return false;
	}
	
	/**
	 * Decides whether a given bond is an XOR split.
	 */
	public static boolean isXORSplit(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isBond(bond) && isGateway(bond.getEntry())) {
			return ((ProcessGraphGateway) bond.getEntry()).isXOR() && isSplit(bond, rpst) && !isSkip(bond, rpst);
		}
		return false;
	}
	
	/**
	 * Decides whether a given bond is an OR split.
	 */
	public static boolean isORSplit(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		if (isBond(bond) && isGateway(bond.getEntry())) {
			return ((ProcessGraphGateway) bond.getEntry()).isOR() && isSplit(bond, rpst);
		}
		return false;
	}
	
	/**
	 * Decides whether a given component is a Bond.
	 */
	public static boolean isBond(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		return node.getName().startsWith("B");
	}
	
	/**
	 * Decides whether a given component is a trivial one.
	 */
	public static boolean isTrivial(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		return node.getName().startsWith("T");
	}
	
	/**
	 * Decides whether a given component is a Rigid.
	 */
	public static boolean isRigid(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		return node.getName().startsWith("R");
	}
	
	
	/**
	 * Decides whether a given node is a gateway.
	 */
	public static boolean isGateway(ProcessGraphNode node) {
		return node.getClass().equals(ProcessGraphGateway.class);
	}
	
	/**
	 * Decides whether considered event is an end event
	 */
	public static boolean isEndEvent(Object o, ProcessModel process) {
		if (o.getClass().toString().equals("class de.hpi.bpt.process.Event") == true) {
			Event event = process.getEvents().get(Integer.valueOf(((Event) o).getId()));
			if (event.getType() == EventType.END_EVENT) {
				return true;
			}	
		}
		return false;
	}
	
	/**
	 * Return true if o is an HPI event 
	 */
	public  static boolean isEvent(Object o) {
		return o.getClass().equals(ProcessGraphEvent.class);
	}
	
	/**
	 * Returns true if o is a HPI task  
	 */
	public static boolean isTask(Object o) {
		return o.getClass().equals(ProcessGraphTask.class);
	}
	
	
	/**
	 * Chekcs whether bond stays in the same lane. 
	 */
	public boolean staysInLane(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> bond, Lane lane, ProcessModel process, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(bond, bond.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			if (depth == 0 && PlanningHelper.isTrivial(node)) {
				int id = Integer.valueOf(node.getEntry().getId());
				if (process.getActivites().containsKey(id)) {
					Lane currentLane = process.getActivites().get(id).getLane();	
					if (currentLane.getName().equals(lane.getName()) == false) {
						return false;
					}
				}
			} else {
				boolean stays = staysInLane(node, lane, process, rpst);	  
				if (stays == false) {
					return false;
				}
			}
		}
		return true;
	}	
	
	public static ArrayList<HashMap<String,Boolean>> getNetSystemFromRPSTFragment(RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node, ProcessModel process) {

		/*ArrayList<HashMap<String,Boolean>> runRelations = new ArrayList<HashMap<String,Boolean>>();
		org.jbpt.pm.ProcessModel pm = new org.jbpt.pm.ProcessModel();
		HashMap<Integer,FlowNode> elements = new HashMap<Integer, FlowNode>();
		HashMap<String,String> orignalMapping = new HashMap<String,String> (); 
		
		for (Node n: node.getFragment().getVertices()) {
			orignalMapping.put(n.getId(), n.getName());
			if (process.getGateways().containsKey(Integer.valueOf(n.getId()))) {
				int type = process.getGateways().get(Integer.valueOf(n.getId())).getType();
				
				if (type == GatewayType.AND) {
					org.jbpt.pm.AndGateway gw = new org.jbpt.pm.AndGateway(n.getId());
					gw.setId(n.getId());
					elements.put(Integer.valueOf(n.getId()),gw);
				}
				if (type == GatewayType.XOR) {
					org.jbpt.pm.XorGateway gw = new org.jbpt.pm.XorGateway(n.getId());
					gw.setId(n.getId());
					elements.put(Integer.valueOf(n.getId()),gw);
				}
			} else {
				org.jbpt.pm.Activity a = new org.jbpt.pm.Activity(n.getId());
				a.setId(n.getId());
				elements.put(Integer.valueOf(n.getId()),a);
			}
		}
		
		for (AbstractDirectedEdge arc: node.getFragment().getEdges()) {
			int sID = Integer.valueOf(arc.getSource().getId());
			int tID = Integer.valueOf(arc.getTarget().getId());
			pm.addControlFlow(elements.get(sID), elements.get(tID));
		}
		PetriNet net = null;
		try {
			net = Process2PetriNet.convert(pm);
		} catch (TransformationException e) {
			e.printStackTrace();
		}
		
		NetSystem netSystem = new NetSystem();
		for (Flow flow: net.getEdges()) {
			netSystem.addFlow(flow.getSource(), flow.getTarget());
		}
		
		// Set IDs
		int id = 0;
		HashMap<String, Integer> idMap = new HashMap<String, Integer>();
		HashMap<Integer, String> oldIDs = new HashMap<Integer, String>();
		for (Flow flow: netSystem.getFlow()) {
			String s = flow.getSource().getId();
			String t = flow.getTarget().getId();
			int sID;
			int tID;
			
			if (idMap.containsKey(s)) {
				sID = idMap.get(s);
				if (!oldIDs.containsKey(sID)) {
					oldIDs.put(sID, flow.getSource().getName());
				}
				flow.getSource().setName(Integer.toString(sID));
			} else {
				sID = id;
				if (!oldIDs.containsKey(sID)) {
					oldIDs.put(sID, flow.getSource().getName());
				}
				flow.getSource().setName(Integer.toString(sID));
				id ++;
				idMap.put(s, sID);
			}
			if (idMap.containsKey(t)) {
				tID = idMap.get(t);
				if (!oldIDs.containsKey(tID)) {
					oldIDs.put(tID, flow.getTarget().getName());
				}
				flow.getSource().setName(Integer.toString(tID));
			} else {
				tID = id;
				if (!oldIDs.containsKey(tID)) {
					oldIDs.put(tID, flow.getTarget().getName());
				}
				flow.getSource().setName(Integer.toString(tID));
				id ++;
				idMap.put(t, tID);
			}
		
		}
		
		netSystem.loadNaturalMarking();
		ProcessCover fps = new ProcessCover(netSystem);
		for (org.jbpt.petri.Process p : fps.getCorrectProcesses()) {
			
			System.out.println("-----------------------------------");
			NetSystem ns = new NetSystem();
			for (Flow flow: p.getCausalNet().getEdges()) {
				ns.addFlow(flow.getSource(), flow.getTarget());
			}
			System.out.println(ns);
			BehaviouralProfile<NetSystem, org.jbpt.petri.Node> bp = BPCreatorNet.getInstance().deriveRelationSet(ns);
			
			ArrayList<org.jbpt.petri.Node> nodes = new ArrayList<org.jbpt.petri.Node>();
			for (org.jbpt.petri.Node n : ns.getNodes()) {
				if (orignalMapping.containsKey(n.getName())) {
					String oldProcessID = n.getName();
					for (org.jbpt.pm.Activity a: pm.getActivities()) {
						if (a.getId().equals(oldProcessID)) {
							nodes.add(n);
						}
					}
					for (org.jbpt.pm.Gateway g: pm.getGateways()) {
						if (g.getId().equals(oldProcessID)) {
							nodes.add(n);
						}
					}
				}
			}*/
			
//			TransitiveClosure tc = new TransitiveClosure(p.getCausalNet());
//			ArrayList<org.jbpt.petri.Node> nodes = new ArrayList<org.jbpt.petri.Node>();
//			for (org.jbpt.petri.Node n : p.getCausalNet().getNodes()) {
//				if (orignalMapping.containsKey((p.embedding.get( n ).getId()))) {
//					String oldProcessID = p.embedding.get( n ).getId();
//					for (org.jbpt.pm.Activity a: pm.getActivities()) {
//						if (a.getId().equals(oldProcessID)) {
//							nodes.add(n);
//						}
//					}
//					for (org.jbpt.pm.Gateway g: pm.getGateways()) {
//						if (g.getId().equals(oldProcessID)) {
//							nodes.add(n);
//						}
//					}
//				}
//			}
			
			
			/*
			 * HashMap<String,Boolean> orderInRun = new HashMap<String, Boolean>();
			 for (int i = 0; i<nodes.size(); i++) {
				for (int j = 0; j<nodes.size(); j++) {
					if (i!=j) {
						if (!orderInRun.containsKey(nodes.get(j).getName() + "-" + nodes.get(i).getName())) {
							String nString = nodes.get(j).getName() + "-" + nodes.get(i).getName();
							System.out.println(nString + ":" + bp.getRelationForEntities(nodes.get(i), nodes.get(j)).toString());
							orderInRun.put(nString,true);
							
						}
					}	
				}
			}}
			runRelations.add(orderInRun);*/
		
		return null;//runRelations;
	}
	
	
	/**
	 * Prints Text Structure.
	 */
	public static void printTextStructure(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, int level, ProcessModel process, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			
			if (PlanningHelper.isBond(node)) {
				printIndent(level);
				if (PlanningHelper.isLoop(node, rpst)) {
					System.out.println("LOOP [");
					printTextStructure(node, level+1, process, rpst);
					printIndent(level);
					System.out.println("] (LOOP) ");
				}
				if (PlanningHelper.isSkip(node, rpst)) {
					System.out.println("SKIP [");
					printTextStructure(node, level+1, process, rpst);
					printIndent(level);
					System.out.println("] (SKIP) ");
				}
				if (PlanningHelper.isXORSplit(node, rpst)) {
					System.out.println("XOR [");
					printTextStructure(node, level+1, process, rpst);
					printIndent(level);
					System.out.println("] (XOR) ");
				}
				if (PlanningHelper.isANDSplit(node, rpst)) {
					System.out.println("AND [");
					printTextStructure(node, level+1, process, rpst);
					printIndent(level);
					System.out.println("] (AND) ");
				}
			} else {
				if (PlanningHelper.isTask(node.getEntry())) {
					printIndent(level);
					Activity activity = (Activity) process.getActivity(Integer.parseInt(node.getEntry().getId()));
					Annotation anno = activity.getAnnotations().get(0);
					System.out.println(anno.getActions().get(0) + " " + anno.getBusinessObjects().get(0) + " " + anno.getAddition());
					if (depth > 0) {
						printTextStructure(node, level, process, rpst);
					}
				} else {
					if (depth > 0) {
						printTextStructure(node, level, process, rpst);
					}
				}
			}
		}
	}
	
	/**
	 * Prints intend on screen (standard out).
	 */
	private static void printIndent(int level) {
		for (int i = 0; i < level; i++) {
			System.out.print("\t");
		}
	}
	
	/**
	 * Return next activity. 
	 */
	public static RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> getNextActivity(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			if (depth == 0 && PlanningHelper.isTrivial(node)) {
				return node;
			} else {
				return getNextActivity(node, rpst);	
			}
		}
		return null;
	}
	
	/**
	 * Determines activity count in RPST. 
	 */
	public static int getActivityCount(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		int c = 0;
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			if (depth == 0 && (PlanningHelper.isTask(node.getEntry()))) {
				c++;;
			} else {
				c = c+ getActivityCount(node, rpst);	
			}
		}
		return c;
	}
	
	
	/**
	 * Print a given RPST Tree. 
	 */
	public static void printTree(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, int level, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			for (int i = 0; i < level; i++) {
				System.out.print("\t");
			}
			
			// Determine type of node for presentation purposes
			String entryString = PlanningHelper.getNodeRepresentation(node.getEntry());
			String exitString = PlanningHelper.getNodeRepresentation(node.getExit());
			
			if (PlanningHelper.isBond(node)) {
				System.out.println(node.getName() + " (" + PlanningHelper.getBondType(node, rpst) + "," + depth + ", " + PlanningHelper.getSubLevelCount(node,rpst)  + ") ["+ entryString + " --> " + exitString + "]");
			} else {
				System.out.println(node.getName() + " (" + depth + ", " + PlanningHelper.getSubLevelCount(node,rpst)  + ") ["+ entryString + " --> " + exitString + "]");
			}
			
			if (depth > 0) {
				printTree(node, level+1, rpst);
			}
		}
	}
	

	public static boolean containsRigid(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, int level, RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst) {
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node: orderedTopNodes) {
			int depth = PlanningHelper.getDepth(node, rpst);
			if (isRigid(node)) {
				return true;
			}
			if (depth > 0) {
				printTree(node, level+1, rpst);
			} 
		}
		return false;
	}
	
	/**
	 * Sets the <code>Annotation</code> for each one of the model's activities.
	 * Basically, it centralize all the Activity task related information (BO, Action and Addition) into an Annotation object.<br>
	 * The necessary information is extracted from the label with the aid of the Language tools: <code>ILabelHelper</code> and <code>ILabelDeriver</code>.
	 * @param lDeriver The ILabelDeriver seted to the appropriate language.
	 * @param lHelper The ILabelHelper seted to the appropriate language.
	 * @see Annotation
	 * @see ILabelDeriver
	 * @see ILabelHelper
	 */
	public static void annotateModel(ProcessModel pModel, ILabelDeriver lDeriver, ILabelHelper lHelper) 
	{
		for (Activity a: pModel.getActivites().values())
		{
			ILabelProperties props = LanguageConfig.getInstance().createNewLabelProperties();
			try 
			{
				String label = a.getLabel().toLowerCase().replaceAll("\n", " ");
				label = label.replaceAll("\\s+", " ");
				
				label = cleanString(label);
				
				String[] labelSplit = label.split(" ");
				
				lDeriver.deriveFromVOS(a.getLabel(), labelSplit, props);
			
				Annotation anno = new Annotation();
				
				if (props.hasConjunction() == false) // No Conjunction label 
				{
					if (lHelper.isVerb(labelSplit[0]) == false) // If no verb-object label 
					{
						anno.addAction(Localization.getInstance().getLocalizedMessage(Messages.CONDUCT));
						anno.addBusinessObjects(a.getLabel().toLowerCase());
						a.addAnnotation(anno);
					} 
					else // If verb-object label
					{
						anno.addAction(props.getAction());
						String bo = props.getBusinessObject();
						lHelper.removeArticleFromBO(bo);
						anno.addBusinessObjects((bo));
						String add = props.getAdditionalInfo();
						
						if(!add.isEmpty()){
							List<String> wordsFromAddition = Arrays.asList(add.split(" "));
							
							if (wordsFromAddition.size() > 2) 
							{
								String newAdd = "";
								//Checks weather exists a defined article. If there is, remove it.
								for(String word : wordsFromAddition){
									if(!lHelper.isDefArticle(word)){
										newAdd += word + " ";
									}
								}
								
								add = newAdd.trim();
							} 
						} 
						
						anno.setAddition(add);
						a.addAnnotation(anno);
					}
					
				} 
				else // Conjunction label
				{
					for (String action: props.getMultipleActions()) 
						anno.addAction(action);
					
					for (String bo: props.getMultipleBOs()) 
					{
						lHelper.removeArticleFromBO(bo);
						anno.addBusinessObjects(bo);
					}
					
					anno.setAddition("");
					a.addAnnotation(anno);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Removes the following unnecessary information from the string: 'glossary://', ';;' and everything before the '/'.
	 * @param str The String to be cleaned
	 * @return A cleaned copy of the given String
	 */
	private static String cleanString(String str) 
	{
		String temp = str;
		if (temp.contains("glossary://")) {
			temp = temp.replace("glossary://", "");
			temp = temp.substring(temp.indexOf("/") + 1, temp.length());
			temp = temp.replace(";;", "");
		}
		return temp;
	}

}
