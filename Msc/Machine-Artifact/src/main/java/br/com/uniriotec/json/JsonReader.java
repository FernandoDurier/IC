package br.com.uniriotec.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import br.com.uniriotec.json.strucuture.Doc;
import br.com.uniriotec.json.strucuture.ElementLevel;
import br.com.uniriotec.json.strucuture.LaneLevel;
import br.com.uniriotec.json.strucuture.PoolLevel;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.ActivityType;
import br.com.uniriotec.process.model.Arc;
import br.com.uniriotec.process.model.Element;
import br.com.uniriotec.process.model.Event;
import br.com.uniriotec.process.model.EventType;
import br.com.uniriotec.process.model.Gateway;
import br.com.uniriotec.process.model.GatewayType;
import br.com.uniriotec.process.model.Lane;
import br.com.uniriotec.process.model.Pool;
import br.com.uniriotec.process.model.ProcessModel;

public class JsonReader 
{
	/***
	 * Maps the resourceId (string) from the physical document (file .json)
	 * to the generated id (int) for the JSON objects (e.g., JsonEvent, JsonLane, etc).<br>
	 * I.e., create the follwing mapping: .json -> JsonElement.
	 */
	private HashMap<String, Integer> keyMap;
	
	private ArrayList<String> fileNames;
	private HashMap<Integer, Integer> shapeMap;
	private ArrayList<JsonTask> tasks;
	private ArrayList<JsonEvent> events;
	private HashMap<Integer,JsonArc> arcs;
	private HashMap<Integer,JsonElement> elems;
	private ArrayList<JsonGateway> gateways;
	private ArrayList<JsonLane> lanes;
	private ArrayList<JsonPool> pools;
	private ArrayList<JsonPool> subProcesses;
	
	private boolean wasCorrect = true;
	private int idCounter;
	
	/**
	 * Links to the original Doc that was created reading a JSON file using Gson API.
	 * This original Doc has all the info about the process 
	 * and can be updated in order to update the Json file.
	 */
	private final Doc originalDocumentModel;

	/**
	 * Creates a new instance of the JsonReader class to work with.
	 * Initializes all the object's properties with default values.
	 */
	public JsonReader(Doc originalDocumentModel) 
	{
		this.originalDocumentModel = originalDocumentModel;
		initializeReaderProperties();
	}
	
	/**
	 * Gets all the json file names in the given path, plus the previously saved files names.
	 * @param path The path that contains the json files.
	 * @return A list of the json file names.
	 */
	public ArrayList<String> getAllFileNames(String path) 
	{
		getAllJsonFiles(path);
		return fileNames;
	}

	public boolean wasCorrect() {
		return wasCorrect;
	}
	
	/**
	 * 
	 * @param file The .json file to be converted.
	 * @return A string that represents the json file.
	 * @throws IOException An exception that contains the IO error that ocurred while trying to read the file.
	 */
	public static String getJsonStringFromFile(String file) throws IOException
	{
		BufferedReader fr = new BufferedReader(new FileReader(file));
		String readLineData;
		String jsonStringRepresantation = "";
		while ((readLineData = fr.readLine()) != null) {
			jsonStringRepresantation += readLineData;
		}
		
		fr.close();
		
		return jsonStringRepresantation;
	}

	/**
	 * Maps all the <code>JsonPool</code> and <code>JsonLane</code> to the corresponding process model element and adds them to the model. 
	 * @param model The process model to add elements to.
	 * @param poolMap The mapping of all the current pools by their respective id.
	 * @param laneMap The mapping of all the current lanes by their respective id.
	 * @see JsonPool
	 * @see JsonLane
	 * @see Pool
	 * @see Lane
	 */
	private void addJsonPoolAndLaneToModel(ProcessModel model, HashMap<Integer, Pool> poolMap, HashMap<Integer, Lane> laneMap)
	{
		// Map Pools
		for (JsonPool jPool : pools) 
		{
			Pool pool = new Pool(jPool.getId(), jPool.getLabel());
			model.addPool(pool);
			poolMap.put(jPool.getId(), pool);

			// Map Lanes
			for (JsonLane jLane : lanes) {
				Lane lane = new Lane(jLane.getId(), jLane.getLabel(), pool);
				model.addLane(lane);
				laneMap.put(jLane.getId(), lane);
			}
		}
	}
	
	/**
	 * Maps all the <code>JsonElement</code> to the corresponding process model element  
	 * For each element, checks its specific type and then apply the following mapping rule: <br>
	 * JsonTask -> Activity, JsonEvent -> Event, JsonGateway -> Gateway. After that, adds the element as the child of the respective Pool and Lane elements.
	 * @param model The process model to add elements to.
	 * @param poolMap The mapping of all the current pools by their respective id.
	 * @param laneMap The mapping of all the current lanes by their respective id.
	 * @param elementMap The mapping of all the current elements by their respective id.
	 * @see JsonTask
	 * @see JsonEvent
	 * @see JsonGateway
	 */
	private void addJsonElemententsToModel(ProcessModel model, HashMap<Integer, Pool> poolMap, HashMap<Integer, Lane> laneMap, HashMap<Integer, Element> elementMap)
	{
		// Iterate over all Json elements to create the according model objects
		for (JsonElement elem: elems.values()) 
		{
			if (elem.getClass().toString().endsWith("JsonTask")) {
				JsonTask jTask = (JsonTask) elem;
				Activity activity = new Activity(jTask.getId(), jTask.getLabel().replaceAll("\n", " "),laneMap.get(jTask.getLaneId()), poolMap.get(jTask.getPoolId()), ActivityType.TYPE_MAP.get(jTask.getType()));
				model.addActivity(activity);
				elementMap.put(jTask.getId(), activity);
			}
			if (elem.getClass().toString().endsWith("JsonEvent")) {
				JsonEvent jEvent = (JsonEvent) elem;
				Event event = new Event(jEvent.getId(), jEvent.getLabel(), laneMap.get(jEvent.getLaneId()), poolMap.get(jEvent.getPoolId()),getEventType(jEvent));
				model.addEvent(event);
				elementMap.put(jEvent.getId(), event);
			}
			if (elem.getClass().toString().endsWith("JsonGateway")) {
				JsonGateway jGateway = (JsonGateway) elem;
				Gateway gateway = new Gateway(jGateway.getId(),jGateway.getLabel(), laneMap.get(jGateway.getLaneId()),poolMap.get(jGateway.getPoolId()), GatewayType.TYPE_MAP.get(jGateway.getType()));
				model.addGateway(gateway);
				elementMap.put(jGateway.getId(), gateway);
			}	
		}
	}
	
	/**
	 * Creates a process model based on the intermediate Json structure. The intermediate Json process model structure must be already done for this method to work 
	 * (use the 'getIntermediateProcessFromFile' method to create this intermediate structure). <br>
	 * The method reads and convert all the specific Json process model elements in the following order:
	 *     1) Convert and add the Pool elements and add their respective Lane children. 
	 *     2) Convert and add the general elements (Events, Gateways, Tasks)
	 *     3) Creates the necessary arcs to link the elements to each other
	 * @param processModelName The name of the new Business Process Model
	 * @return The created Business Process Model
	 */
	public ProcessModel getProcessModelFromIntermediate(String processModelName) 
	{
		ProcessModel model = new ProcessModel(-1, processModelName, originalDocumentModel);
		HashMap<Integer, Element> idElementMap = new HashMap<Integer, Element>();
		HashMap<Integer, Lane> laneMap = new HashMap<Integer, Lane>();
		HashMap<Integer, Pool> poolMap = new HashMap<Integer, Pool>();

		addJsonPoolAndLaneToModel(model, poolMap, laneMap);
		addJsonElemententsToModel(model, poolMap, laneMap, idElementMap);
		
		HashMap<Integer,Integer> externalPathInitiators = new HashMap<Integer, Integer>();
		
		// Iterate over all elements to create the according arcs
		for (JsonElement elem: elems.values()) 
		{
			for (int outId: elem.getArcs()) 
			{
				// if considered outgoing id does not belong to an arc, create a new one (in order to connect attached event)
				if (elems.containsKey(outId)) 
				{
					Activity activity = ((Activity) idElementMap.get(elem.getId()));
					activity.addAttachedEvent(outId);
					
					// Attached event leads to alternative path
					if (elem.getArcs().size() > 1) 
					{
						System.out.println("Attached Event with alternative Path detected");
						((Event) model.getElem(outId)).setIsAttachedTo(elem.getId());
						((Event) model.getElem(outId)).setAttached(true);
						externalPathInitiators.put(outId, elem.getId());
						
					// Attached event goes back to standard path	
					}
					else
					{
						Arc arc = new Arc(getId(), "", idElementMap.get(elem.getId()) , idElementMap.get(outId), "VirtualFlow");
						Event attEvent = ((Event) idElementMap.get(outId));
						attEvent.setAttached(true);
						attEvent.setIsAttachedTo(elem.getId());
						model.addArc(arc);
					}
				} 
				else if (arcs.keySet().contains(outId)) // Considered outgoing id exists as arc 
				{
					JsonArc jArc = arcs.get(outId);
					Arc arc = new Arc(outId, jArc.getLabel(), idElementMap.get(elem.getId()) , idElementMap.get(jArc.getTarget()), "SequenceFlow");
					model.addArc(arc);
				} 
				else
				{
					System.out.println("No according Arc found: " + outId);
				}
			}
		}
		
		removeAllExternalPathInitiatorsFromModel(model, externalPathInitiators);
		
		model.initializeMapping(keyMap);
		return model;
	}
	
	private void removeAllExternalPathInitiatorsFromModel(ProcessModel model, HashMap<Integer,Integer> externalPathInitiators)
	{
		// remove all external path initiators
		for (int exPI: externalPathInitiators.keySet()) 
		{
			ProcessModel alternativePath = new ProcessModel(exPI, "", originalDocumentModel);

			// Create start event
			Event startEvent = new Event(getId(), "", model.getElem(exPI).getLane(), model.getElem(exPI).getPool(), EventType.START_EVENT);
			alternativePath.addEvent(startEvent);
			
			// Reallocate elems to alternative path
			createAlternativePath(exPI, true, model, alternativePath, exPI);
			
			// Add arc from artifical start to real start elem
			Event realStart = (Event) alternativePath.getElem(exPI);
			alternativePath.addArc(new Arc(getId(), "", startEvent, realStart));
			
			// Add path to model
			model.addAlternativePath(alternativePath, exPI);
		}
	}
	
	public String printContent(int id) {
		String s = "";
		for (JsonTask jTask : tasks) {
			s = s + id + "\t" + "Task" + "\t" + jTask.getType() + "\t"+ wasCorrect + "\n";
		}
		for (JsonEvent jEvent : events) {
			s = s + id + "\t" + "Event" + "\t" + jEvent.getType() + "\t"+ wasCorrect + "\n";
		}
		for (JsonGateway jGateway : gateways) {
			s = s + id + "\t" + "Gateway" + "\t" + jGateway.getType() + "\t"+ wasCorrect + "\n";
		}
		for (JsonArc jArc : arcs.values()) {
			s = s + id + "\t" + "Arc" + "\t" + jArc.getType() + "\t"+ wasCorrect + "\n";
		}
		/* Currently the subProcess type is not supported. Improve the implemantation in order to cover this type of data.
		 * for (JsonPool subP : subProcesses) {
			s = s + id + "\t" + "Subprocess" + "\t" + " " + "\t" + wasCorrect+ "\n";
		}*/
		return s;
	}

	/**
	 * Read from Json and sets some of the process elements: arcs (SequenceFlow or MessageFlow), pools, lanes and elements.
	 * It creates an intermediate Json process representation of the <code>Doc</code> object, with Json mapped process elements, which can be: <br>
	 * <code>JsonPool</code>, <code>JsonArc</code>, <code>JsonLane</code>
	 * @see JsonPool
	 * @see JsonLane
	 * @see JsonArc
	 */
	public void getIntermediateProcessFromFile()throws TransformerException, ParserConfigurationException 
	{
		int id = 0;
		int currentLaneId = -1; 
		int currentPoolId = -1;

		// Pool level (top level elements)
		for (PoolLevel pool : originalDocumentModel.getChildShapes()) 
		{
			id = getId(pool.getResourceId());

			if (pool.getStencil().toString().equals("Pool")) //Checks whether the pool is a Pool element
			{
				//Create a JsonPool to represent the Pool element (e.g. pool element can be Hotel, Restaurant, Factory)
				currentPoolId = id;
				String temp = cleanString(pool.getProps().getName());
				JsonPool jPool = new JsonPool(id, temp);
				pools.add(jPool);
			}
			else if (pool.getStencil().toString().equals("SequenceFlow")) //Checks whether the pool is a SequenceFlow element
			{
				//Create a JsonArc to represent the SequenceFlow element
				int targetId = getId(pool.getTarget().getResourceId());
				JsonArc jArc = new JsonArc(id, targetId, currentLaneId, pool.getProps().getName(), "SequenceFlow");
				arcs.put(id,jArc);
			}
			else if (pool.getStencil().toString().equals("MessageFlow")) //Checks whether the pool is a MessageFlow element 
			{
				//Create a JsonArc to represent the MessageFlow element
				JsonArc jArc = new JsonArc(id, shapeMap.get(id), currentLaneId,pool.getProps().getName(), "MessageFlow");
				arcs.put(id,jArc);
			}

			// Add the lanes which are directed associated (child) of the current pool (e.g. an actor of the process model, like "Customer")
			for (LaneLevel lane : pool.getChildShapes()) 
			{
				id = getId(lane.getResourceId());
				currentLaneId = id;
				String cleanedLaneName = cleanString(lane.getProps().getName());
				JsonLane jlane = new JsonLane(id, cleanedLaneName, currentPoolId);
				lanes.add(jlane);

				// Add the lane's children elements (e.g. tasks, gateways)
				for (ElementLevel elem : lane.getChildShapes()) {
					addElems(elem, currentLaneId, currentPoolId);
				}
			}
		}
	}
	
	private void initializeReaderProperties()
	{
		fileNames = new ArrayList<String>();
		tasks = new ArrayList<JsonTask>();
		arcs = new HashMap<Integer, JsonArc>();
		gateways = new ArrayList<JsonGateway>();
		events = new ArrayList<JsonEvent>();
		lanes = new ArrayList<JsonLane>();
		keyMap = new HashMap<String, Integer>();
		shapeMap = new HashMap<Integer, Integer>();
		pools = new ArrayList<JsonPool>();
		subProcesses = new ArrayList<JsonPool>();
		elems = new HashMap<Integer, JsonElement>();
		idCounter = 0;
		wasCorrect = true;
	}

	
	
	private void createAlternativePath(int id, boolean isElem, ProcessModel model, ProcessModel alternative, int exPI) {
		if (isElem) {
			JsonElement elem = elems.get(id);
			if (elem.getArcs().size() > 0) {
				for (int arc: elem.getArcs()) {
					createAlternativePath(arc, false,model, alternative, exPI);
					alternative.addElem(model.getElem(id));
					elems.remove(id);
					model.removeElem(id);
					System.out.println("Elem reallocated: " + id + " " + elem.getLabel() + " --> " + exPI);
				}
			} else {
				alternative.addElem(model.getElem(id));
				elems.remove(id);
				model.removeElem(id);
				System.out.println("Elem reallocated: " + id + " " + elem.getLabel() + " --> " + exPI);
			}
		} else {
			createAlternativePath(arcs.get(id).getTarget(), true, model, alternative, exPI);
			alternative.addArc(model.getArc(id));
			arcs.remove(id);
			model.removeArc(id);
			System.out.println("Arc reallocated: " + id + " --> " + exPI);
		}
	}

	private int getEventType(JsonEvent jEvent) {
		try {
			int type = EventType.TYPE_MAP.get(jEvent.getType());
			return type;
		} catch (Exception e) {
			System.out.println("Error: Event Mapping (" + jEvent.getType()+ ")");
		}
		return 5;
	}

	

	private void addElems(ElementLevel elem, Integer currentLaneId,int currentPoolId) {
		int id = getId(elem.getResourceId());

		// Save outgoing elements
		ArrayList<Integer> jArcIDs = new ArrayList<Integer>();
		for (ElementLevel out : elem.getOutgoing()) {
			jArcIDs.add(getId(out.getResourceId()));
		}

		if (elem.getStencil().toString().equals("Task")) {
			JsonTask jTask = new JsonTask(id, elem.getProps().getName(),jArcIDs, currentLaneId, currentPoolId, elem.getProps().getTasktype());
			tasks.add(jTask);
			elems.put(id,jTask);
		}
		if (elem.getStencil().toString().equals("CollapsedSubprocess")) {
			JsonTask jTask = new JsonTask(id, elem.getProps().getName(),jArcIDs, currentLaneId, currentPoolId, "Subprocess");
			tasks.add(jTask);
			elems.put(id,jTask);
		}

		if (elem.getStencil().toString().toLowerCase().contains("event")) {
			JsonEvent jEvent = new JsonEvent(id, elem.getProps().getName(),elem.getStencil().toString(), currentLaneId, currentPoolId,jArcIDs);
			events.add(jEvent);
			elems.put(id,jEvent);
		}

		if (elem.getStencil().toString().toLowerCase().contains("gateway")) {
			JsonGateway jGateway = new JsonGateway(id, elem.getProps().getName(), jArcIDs, currentLaneId,currentPoolId, elem.getStencil().toString());
			gateways.add(jGateway);
			elems.put(id,jGateway);
		}
		if (elem.getStencil().toString().equals("Subprocess")) {
			subProcesses.add(new JsonPool(id, elem.getProps().getName()));
			for (ElementLevel subElem : elem.getChildShapes()) {
				addElems(subElem, currentLaneId, currentPoolId);
			}
		}
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

	private int getId(String rid) {
		int id;
		if (keyMap.containsKey(rid)) {
			id = keyMap.get(rid);
		} else {
			id = idCounter;
			idCounter++;
			keyMap.put(rid, id);
		}
		return id;
	}

	private int getId() {
		int id = idCounter;
		idCounter++;
		keyMap.put("newElem" + id, id);
		return id;
	}

	/**
	 * Gets all the .json files names located in the directory path received by parameter and store them in the fileNames property.
	 * @param directoryPath The directory path that contains the .json files.
	 */
	private void getAllJsonFiles(String directoryPath)
	{
		File directory = new File(directoryPath);
		String[] shortFilesNames = directory.list();
		
		for (String fileName : shortFilesNames)
		{
			if (fileName.endsWith("json")) 
			{
				String completeFileName = directory.getAbsolutePath() + "/" + fileName;
				fileNames.add(completeFileName);
			}
		}
		
		Collections.sort(fileNames);
	}
}