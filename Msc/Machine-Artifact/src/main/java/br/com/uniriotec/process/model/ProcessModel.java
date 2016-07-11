package br.com.uniriotec.process.model;

import general.language.common.process.ProcessActivityProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.uniriotec.json.strucuture.Doc;

public class ProcessModel 
{
	private int id;
	private String name;
	private String language;
	private HashMap <Integer,Arc> arcs;
	private HashMap <Integer, Activity> activities;
	private HashMap <Integer,Event> events;
	private HashMap <Integer,Gateway> gateways;
	private ArrayList<String> lanes;
	private ArrayList<String> pools;
	private HashMap<Integer,ProcessModel> alternativePaths;
	
	/**
	 * Links to the original Doc that was created reading a JSON file using Gson API.
	 * This original Doc has all the info about the process 
	 * and can be updated in order to update the Json file.
	 */
	private Doc originalDocumentModel;
	/**
	 * Links to the original resources ID of the Doc that was created reading a JSON file using Gson API.
	 * This original Doc has all the info about the process 
	 * and can be updated in order to update the Json file. <br>
	 * NOTE: This jsonId matches the ProcessModel's elements. 
	 * I.e., given a specific jsonId you can retrieve the respective element 
	 * from the process model (e.g., activity, lane, event, etc) and vice-versa.
	 */
	private HashMap<Integer, String> jsonIdToDocResourceIdMap;
	
	/***
	 * Given a specific label for a lane, you can retrieve the 
	 * respective lane object that matches this label.
	 */
	private HashMap<String, Lane> laneLabelToLaneMap;
	
	/***
	 * Given a specific label for a pool, you can retrieve the 
	 * respective pool object that matches this label.
	 */
	private HashMap<String, Pool> poolLabelToPoolMap;
	
	public static final int VOS = 0;
	public static final int AN = 1;
	public static final int INVESTIGATE = 2;
	
	/**
	 * Creates a new instance of the ProcessModel class, with the given id and name received by parameter.
	 * <b>WARNING:</b> This constructor should be used only when the PM is created from a JSON file.
	 * @param id The id for the new process model object.
	 * @param name The process model name
	 */
	public ProcessModel(int id, String name, Doc originalDocumentModel) {
		this.id = id;
		this.name = name;
		arcs = new HashMap<Integer, Arc>();
		activities = new HashMap<Integer, Activity>();
		events = new HashMap<Integer, Event>();
		gateways = new HashMap<Integer, Gateway>();
		lanes = new ArrayList<String>();
		pools = new ArrayList<String>();
		alternativePaths = new HashMap<Integer, ProcessModel>();
		this.originalDocumentModel = originalDocumentModel;
		this.jsonIdToDocResourceIdMap = new HashMap<Integer, String>();
		this.laneLabelToLaneMap = new HashMap<String, Lane>();
		this.poolLabelToPoolMap = new HashMap<String, Pool>();
	}
	
	/**
	 * Creates a new instance of the ProcessModel class, with the given id and name received by parameter.<br>
	 * <b>WARNING:</b> This constructor should <b>NOT</b> be used when the PM is created from a JSON file.
	 * @param id The id for the new process model object.
	 * @param name The process model name
	 */
	public ProcessModel(int id, String name) {
		this.id = id;
		this.name = name;
		arcs = new HashMap<Integer, Arc>();
		activities = new HashMap<Integer, Activity>();
		events = new HashMap<Integer, Event>();
		gateways = new HashMap<Integer, Gateway>();
		lanes = new ArrayList<String>();
		pools = new ArrayList<String>();
		alternativePaths = new HashMap<Integer, ProcessModel>();
	}
	
	public void addAlternativePath(ProcessModel path, int id) {
		alternativePaths.put(id, path);
	}
	
	public int getElemAmount() {
		return activities.size() + gateways.size() + events.size();
	}
	
	
	public ArrayList<String> getPools() {
		return pools;
	}

	public void addPool(Pool pool) {
		String poolLabel = pool.getName();
		
		this.pools.add(pool.getName());
		this.poolLabelToPoolMap.put(poolLabel.toLowerCase(), pool);
	}

	public void addArc(Arc arc) {
		arcs.put(arc.getId(), arc);
	}
	
	public void addActivity(Activity activity) {
		activities.put(activity.getId(), activity);
	}
	
	public void addEvent(Event event) {
		events.put(event.getId(), event);
	}
	
	public void addGateway(Gateway gateway) {
		gateways.put(gateway.getId(), gateway);
	}

	public HashMap<Integer, Arc> getArcs() {
		return arcs;
	}

	public HashMap<Integer, Activity> getActivites() {
		return activities;
	}

	public HashMap<Integer, Event> getEvents() {
		return events;
	}

	public HashMap<Integer, Gateway> getGateways() {
		return gateways;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public Activity getActivity(int id) {
		return activities.get(id);
	}
	
	public void addLane(Lane lane) 
	{
		String cleanedLane = cleanString(lane.getName());
		
		this.laneLabelToLaneMap.put(cleanedLane.toLowerCase(), lane);
		
		lanes.add(cleanedLane);
	}
	
	public ArrayList<String> getLanes() {
		return lanes;
	}
	
	public int getNewId() 
	{
		int base = 0;
		
		for (int i: arcs.keySet()) 
			if (i > base) 
				base = i;
		
		for (int i: activities.keySet()) 
			if (i > base) 
				base = i;
		
		for (int i: gateways.keySet()) 
			if (i > base) 
				base = i;
			
		for (int i: events.keySet()) 
			if (i > base) 
				base = i;
			
		base++;
		return base;
	}
	
	public HashMap<Integer, ProcessModel> getAlternativePaths() 
	{
		return alternativePaths;
	}

	public void addElem(Element elem) 
	{
		if (elem.getClass().toString().endsWith("Gateway"))
			gateways.put(elem.getId(), (Gateway) elem);
		
		if (elem.getClass().toString().endsWith("Activity")) 
			activities.put(elem.getId(), (Activity) elem);
		
		if (elem.getClass().toString().endsWith("Event")) 
			events.put(elem.getId(), (Event) elem);
	}
	
	public Element getElem(int id) 
	{
		if (events.containsKey(id)) 
			return events.get(id);
		if (gateways.containsKey(id)) 
			return gateways.get(id);
		if (activities.containsKey(id)) 
			return activities.get(id);
		
		return null;
	}
	
	public Arc getArc(int id) 
	{
		return arcs.get(id);
	}
	
	/**
	 * Remove an <code>Arc</code> by its id.
	 * @param id The id of the <code>Arc</code> to be removed.
	 */
	public void removeArc(int id) 
	{
		if (arcs.containsKey(id) == false ) 
			System.out.println("NO ARC: " + id);
		
		this.arcs.remove(id);
	}
	
	/**
	 * Remove an element (Event, Gateway or Activity) by its id. If no such id is found, then no action is taken. 
	 * @param id The id of the element to be removed.
	 * @see Event
	 * @see Activity
	 * @see Gateway
	 */
	public void removeElem(int id) 
	{
		if (events.containsKey(id)) 
			events.remove(id);
		if (gateways.containsKey(id)) 
			gateways.remove(id);
		if (activities.containsKey(id)) 
			activities.remove(id);
	}
	
	/**
	 * Normalize the process model using some specific metrics:
	 * - Removes unnecessary arcs (arcs that have no targets) <br>
	 * - Checks whether an activity has more than one arc pointing to her. If there is, then a XOR gateway is created in order to centralize the arcs target point in the gateway instead of the activity itself. 
	 * After, it creates a new arc to point from the created gateway to the activity.
	 * - Checks whether an activity has more than one arc going out from her. If there is, then a new gateway is created in order to centralize the arcs source point in the gateway instead of the activity itself. 
	 * After that, it creates a new arc to point from the activity to the created gateway.
	 */
	public void normalize() 
	{
		// Clean arcs
		/*for(int i = arcs.keySet().size()-1 ; i >= 0 ; i--)
		{
			if (arcs.get(i).getTarget() == null)
				arcs.remove(i);
		}*/
		
		ArrayList<Integer> toBeDeleted = new ArrayList<Integer>();
		  for (int key: arcs.keySet()) 
			if (arcs.get(key).getTarget() == null) 
				toBeDeleted.add(key);
				
		for (int key: toBeDeleted) 
			arcs.remove(key);
		
		for (int activityKey: activities.keySet()) 
		{
			//int count = 0;
			ArrayList<Arc> arcsPointingToActivity = new ArrayList<Arc>();
			
			// Count arcs (incoming) to make the check
			for (Arc arc: arcs.values()) 
				if (arc.getTarget().getId() == activityKey) 
					arcsPointingToActivity.add(arc);
					//count++;
			
			if (/*count*/ arcsPointingToActivity.size() > 1) //The activity has more than one arc pointing to her, so a gateway is needed to centralize the arcs.
			{
				Activity a = activities.get(activityKey);
				int gwId = getNewId();
				Gateway xorGateway =  new Gateway(gwId , "", a.getLane(), a.getPool(), GatewayType.XOR);
				gateways.put(gwId, xorGateway);
				// Modify target of incoming arcs to new gateway
				for (Arc arc: arcsPointingToActivity /*arcs.values()*/) 
					//if (arc.getTarget().getId() == activityKey)
						arc.setTarget(xorGateway);
				
				// Create new arc from gateway to activity
				int arcId = getNewId();
				Arc arc = new Arc(arcId, "", xorGateway, a);
				arcs.put(arcId, arc);
				System.out.println("Gateway for incoming arcs inserted (" + activityKey + ") :" + gwId);
			}
			
			ArrayList<Arc> arcsPointingFromActivity = new ArrayList<Arc>();
			// Count arcs (outgoing) to make the check
			for (Arc arc: arcs.values()) 
				if (arc.getSource().getId() == activityKey) 
					arcsPointingFromActivity.add(arc);
				
			if (arcsPointingFromActivity.size() > 1) //The activity has more than one arc leaving her, so a gateway is needed to centralize the arcs.
			{
				Activity a = activities.get(activityKey);
				int gwId = getNewId();
				boolean isAND = true;
				for (Arc arc: arcs.values()) 
					if (arc.getType().equals("VirtualFlow")) 
						isAND = false;
				
				int gatewayType = (isAND == true) ? GatewayType.AND : GatewayType.XOR;
				Gateway gateway =   new Gateway(gwId , "", a.getLane(), a.getPool(), gatewayType);
				  
				gateways.put(gwId, gateway);
				System.out.println("Gateway for outgoing arcs inserted: " + gwId);
				
				// Modify target of incoming arcs to new gateway
				for (Arc arc: arcsPointingFromActivity) 
					arc.setSource(gateway);
				
				// Create new arc from gateway to activity
				int arcId = getNewId();
				Arc arc = new Arc(arcId, "", a, gateway);
				arcs.put(arcId, arc);
			}
		}
	}
	
	/**
	 * Normalize the end events of the process model. Centralize the end of the process model into one single end event.<br>
	 * Checks whether more than one end event exist. If more than one is detected, then a new end event is created. A new arc is created, for each old event, to link the old end event to the new end event. 
	 */
	public void normalizeEndEvents() 
	{
		ArrayList<Integer> endEvents = new ArrayList<Integer>();
		
		for (Event e: events.values()) 
			if (EventType.isEndEvent(e.getType())) 
				endEvents.add(e.getId());
		
		if (endEvents.size() > 1) 
		{
			System.out.println("Multiple End Events detected");
			int endEventId = getNewId();
			
			Event endEvent = new Event(endEventId, "", events.get(endEvents.get(0)).getLane(), events.get(endEvents.get(0)).getPool(), EventType.END_EVENT);
			events.put(endEventId, endEvent);
			
			// For each end event, create an arc to the new end event
			for (int mEndEventId: endEvents) {
				int arcId = getNewId();
				Arc arc = new Arc(arcId, "", events.get(mEndEventId), endEvent);
				arcs.put(arcId, arc);
			}
		}
	}
	
	/**
	 * Prints out in the console window the information contained in the process model: The activities, events, gateways and arcs.
	 */
	public void print()
	{
		System.out.println("Process Model: "  + this.name + " (" + this.getId() + ")");
		for (Activity a: activities.values()) {
			System.out.println("Activity (" + a.getId() + ") " + a.getLabel());
		}
		for (Event e: events.values()) {
			System.out.println("Event (" + e.getId() + ") " + e.getLabel() + " - Type: "  + e.getType());
		}
		for (Gateway g: gateways.values()) {
			System.out.println("Gatewyay (" + g.getId() + ")" + " " + g.getType());
		}
		for (Arc arc: arcs.values()) {
			System.out.println("Arc: (s: " + arc.getSource().getId() + " t: " + arc.getTarget().getId() + ")" + "- " + arc.getId());
		}
	}
	
	public void addActivity(ProcessActivityProperty processActivityProperties) throws Exception {
		handleAddActivityCommon(processActivityProperties);
		updateJsonFile();
	}
	
	public void addActivity(ProcessActivityProperty processActivityProperties, Element nextElement) throws Exception {
		if(nextElement != null){
			handleAddActivityCommon(processActivityProperties);
			// create new arc
			// Sets newArc.source = activity
			// Sets newArc.target = nextElement (points to the next element x+1)
		} else {
			addActivity(processActivityProperties);
		}
		
		String docResourceId = this.jsonIdToDocResourceIdMap.get(nextElement.getId());
		this.originalDocumentModel.addTaskBefore(docResourceId, processActivityProperties.getLabel());
	}
	
	public void addActivity(Arc previousArc, ProcessActivityProperty processActivityProperties) throws Exception {
		if(previousArc != null){
			handleAddActivityCommon(processActivityProperties);
			// create new arc
			// Sets newArc.source = activity
			// Sets newArc.target = previousArc.target (points to the next element x+1, previous x)
			// Sets previousArc.target = activity
		} else {
			addActivity(processActivityProperties);
		}
		
		updateJsonFile();
	}
	
	public void updateActivity(Activity updatedActivity) throws Exception{
		//TODO implement this method correctly: receive the updated String by parameter and change activity here
		Integer activityId = updatedActivity.getId();
		String docResourceId = this.jsonIdToDocResourceIdMap.get(activityId);
		this.originalDocumentModel.updateTask(docResourceId, updatedActivity.getLabel());
	}
	
	public void removeActivity(Integer activityId) throws Exception{
		Arc sourceArc = getSourceFor(activityId);
		Arc targetArc = getTargetFor(activityId);
		String docResourceId = this.jsonIdToDocResourceIdMap.get(activityId);
		
		this.originalDocumentModel.removeTask(docResourceId);
		activities.remove(activityId);
		
		if(sourceArc == null){
			removeArc(targetArc.getId());
		} else if(targetArc == null){
			removeArc(sourceArc.getId());
		} else {
			Element targetElement = targetArc.getTarget();
			removeArc(targetArc.getId());
			sourceArc.setTarget(targetElement);
		}
	}
	
	public void initializeMapping(Map<String, Integer> keyMap){
		for(String key : keyMap.keySet()){
			Integer valueMapped = keyMap.get(key);
			this.jsonIdToDocResourceIdMap.put(valueMapped, key);
		}
	}
	
	public Doc toDocumentModel(){
		return this.originalDocumentModel;
	}

	/**
	 * Removes the following unnecessary information from the string: 'glossary://', ';;' and everything before the '/'.
	 * @param str The String to be cleaned
	 * @return A cleaned copy of the given String
	 */
	private String cleanString(String str) 
	{
		String temp = str;
		if (temp.contains("glossary://")) {
			temp = temp.replace("glossary://", "");
			temp = temp.substring(temp.indexOf("/") + 1, temp.length());
			temp = temp.replace(";;", "");
		}
		return temp;
	}
	
	private Arc getSourceFor(Integer elemId){
		Arc sourceArc = null;
		for(Arc arc : arcs.values()){
			if(arc.getSource().getId() == elemId){
				sourceArc = arc;
				break;
			}
		}
		
		return sourceArc;
	} 
	
	private Arc getTargetFor(Integer elemId){
		Arc sourceArc = null;
		for(Arc arc : arcs.values()){
			if(arc.getTarget().getId() == elemId){
				sourceArc = arc;
				break;
			}
		}
		
		return sourceArc;
	} 
	
	private Activity handleAddActivityCommon(ProcessActivityProperty processActivityProperties) throws Exception {
		if(poolLabelToPoolMap.size() == 0){
			throw new Exception("The process model is incomplete. There is no pool associated to this PM, please create a pool first in order to be able to add new activities.");
		} else if(processActivityProperties == null){
			throw new IllegalArgumentException("Parameter 'processActivityProperties' cannot be null.");
		}
		
		String actor = processActivityProperties.getBusinessActor();
		String activityLabel = processActivityProperties.getLabel();
		
		Lane associatedLane = laneLabelToLaneMap.get(actor.toLowerCase());
		Pool associatedPool = poolLabelToPoolMap.values().iterator().next();
		
		Activity activity = new Activity(getNewId(), activityLabel, associatedLane, associatedPool, ActivityType.NONE);
		addActivity(activity);
		
		return activity;
	}
	
	private void updateJsonFile() throws Exception {
		//throw new UnsupportedOperationException();
	}
	
//	private int findJsonIdFor(String elementId){
//		int poolJsonId = -1;
//		for(int id : jsonIdToDocResourceIdMap.keySet()){
//			if(elementId.equals(jsonIdToDocResourceIdMap.get(id))){
//				poolJsonId = id;
//			}
//		}
//		
//		return poolJsonId;
//	}
}
