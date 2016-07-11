package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;
import java.util.Iterator;

public class Doc
{
	String resourceId;
	DocProperties properties;
	Stencil stencil;
	ArrayList<PoolLevel>childShapes;
	Bounds bounds;
	StencilSet stencilset;
	ArrayList<String> ssextensions;
	
	public StencilSet getStencilset() {
		return stencilset;
	}
	public void setStencilset(StencilSet stencilset) {
		this.stencilset = stencilset;
	}
	public ArrayList<String> getSsextensions() {
		return ssextensions;
	}
	public void setSsextensions(ArrayList<String> ssextensions) {
		this.ssextensions = ssextensions;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	public DocProperties getProperties() {
		return properties;
	}
	public void setProperties(DocProperties properties) {
		this.properties = properties;
	}

	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public DocProperties getProps() {
		return properties;
	}
	public void setProps(DocProperties props) {
		this.properties = props;
	}
	public Stencil getStencil() {
		return stencil;
	}
	public void setStencil(Stencil stencil) {
		this.stencil = stencil;
	}
	public ArrayList<PoolLevel> getChildShapes() {
		return childShapes;
	}
	public void setChildShapes(ArrayList<PoolLevel> childShapes) {
		this.childShapes = childShapes;
	}
	
	public void print() {
		 System.out.println("Document: " + this.getProps().getName());
		    ArrayList <PoolLevel> pools = this.getChildShapes();
		    for (PoolLevel pool : pools) {
		    	System.out.print("\t"+pool.getResourceId() + " (");
		    	System.out.print(pool.getProps().getName() + ", ");
		    	System.out.println(pool.getStencil() + ")");

		    	ArrayList<LaneLevel> lanes = pool.getChildShapes();
		    	for (LaneLevel lane:lanes) {
		    		System.out.print("\t\t"+lane.getResourceId() + " (");
			    	System.out.print(lane.getProps().getName() + ", ");
			    	System.out.println(lane.getStencil() + ")");
			    	
		    		ArrayList<ElementLevel> elems = lane.getChildShapes();
		    		for (ElementLevel elem: elems) {
		    			System.out.print("\t\t\t"+elem.getResourceId() + " (");
		    	    	System.out.print(elem.getProps().getName() + ", ");
		    	    	System.out.println(elem.getStencil() + ")");
		    		}
		    	}
		    	
		    }
	}
	
	public void addTaskBefore(String taskResourceId, String taskLabel) throws Exception{
		ElementLevel taskElement = searchForTask(taskResourceId);
		ElementLevel clonnedTask = new ElementLevel(taskElement);
		
		// update task label
		clonnedTask.properties.name = taskLabel;
		
		int insertIndex = -1;
		LaneLevel parentLane = null;
		// search for parent lane
		outerloop: for(PoolLevel pool : this.getChildShapes()){
			for(LaneLevel lane : pool.getChildShapes()){
				if(lane.getChildShapes() != null){
					ArrayList<ElementLevel> bpmnElements = lane.getChildShapes();
					for(int i = 0 ; i < bpmnElements.size(); i++){
						taskElement = searchForElement(bpmnElements.get(i), taskResourceId);
						if(taskElement != null){
							parentLane = lane;
							insertIndex = i;
							break outerloop;
						}
					}
				}
			}
		}
		
		// move child taks forward
		ArrayList<ElementLevel> bpmnElements = parentLane.getChildShapes();
		for(int i = bpmnElements.size() ; i > insertIndex ; i--){
			bpmnElements.add(i, bpmnElements.get(i-1));
		}
		
		// insert new task as lane's child
		bpmnElements.add(insertIndex, clonnedTask);
		
		final double valueToMove = 60;
		
		// sets new task X = X-60 so it wont be moved by 'moveElementsHorizontally'
		Bounds clonnedTaskBounds = clonnedTask.getBounds();
		Point clonnedTasklowerRightPoint = clonnedTaskBounds.getLowerRight();
		Point clonnedTaskUpperLeftPoint = clonnedTaskBounds.getUpperLeft();
		clonnedTasklowerRightPoint.setX(clonnedTasklowerRightPoint.getX() - valueToMove);
		clonnedTaskUpperLeftPoint.setX(clonnedTaskUpperLeftPoint.getX() - valueToMove);
		
		// move all bpmn elements to X+60 starting from taskElement 
		Bounds taskElementBounds = taskElement.getBounds();
		Point lowerRightPoint = taskElementBounds.getLowerRight();
		moveElementsHorizontally(lowerRightPoint.getX(), valueToMove);
	}
	
	private ElementLevel searchForTask(String taskResourceId) throws Exception{
		ElementLevel taskElement = null;
		outerloop: for(PoolLevel pool : this.getChildShapes()){
			for(LaneLevel lane : pool.getChildShapes()){
				if(lane.getChildShapes() != null){
					ArrayList<ElementLevel> bpmnElements = lane.getChildShapes();
					for(int i = 0 ; i < bpmnElements.size(); i++){
						taskElement = searchForElement(bpmnElements.get(i), taskResourceId);
						if(taskElement != null){
							break outerloop;
						}
					}
				}
			}
		}
		
		if(taskElement == null){
			throw new Exception("It was not possible to find an equivalent element "
					+ "in the original DocumentModel with the ID: " + taskResourceId);
		}
		
		return taskElement;
	}
	
	public void updateTask(String taskResourceId, String newTaskLabel) throws Exception{
		ElementProperties properties = null;
		outerloop: for(PoolLevel pool : this.getChildShapes()){
			System.out.println("Pool resource id: " + pool.getResourceId());
			for(LaneLevel lane : pool.getChildShapes()){
				System.out.println("Lane resource id: " + lane.getResourceId());
				for(ElementLevel element : lane.getChildShapes()){
					ElementLevel elementFound = searchForElement(element, taskResourceId);
					if(elementFound != null){
						properties = elementFound.getProperties();
						properties.setName(newTaskLabel);
						break outerloop;
					}
				}
			}
		}
		
		if(properties == null){
			throw new Exception("It was not possible to find an equivalent element "
					+ "in the original DocumentModel with the ID: " + taskResourceId);
		} 
	}
	
	public void removeTask(String taskResourceId) throws Exception{
		ElementLevel taskElement = null;
		//ElementLevel sourceElement = null;
		outerloop: for(PoolLevel pool : this.getChildShapes()){
			for(LaneLevel lane : pool.getChildShapes()){
				if(lane.getChildShapes() != null){
					ArrayList<ElementLevel> bpmnElements = lane.getChildShapes();
					for(int i = 0 ; i < bpmnElements.size(); i++){
						taskElement = searchForElement(bpmnElements.get(i), taskResourceId);
						//sourceElement = searchForOutgoingElement(bpmnElements.get(i), taskResourceId);
						if(taskElement != null){
							break outerloop;
						}
					}
				}
			}
		}
		
		PoolLevel sourceSequenceFlow = searchForSourceSequenceFlow(taskResourceId);
		
		if(taskElement == null){
			throw new Exception("It was not possible to find an equivalent element "
					+ "in the original DocumentModel with the ID: " + taskResourceId);
		} else if(sourceSequenceFlow == null) {
			// Search for the next bpmnElement (probably a SequenceFlow)
			ArrayList<ElementLevel> outgoing = taskElement.getOutgoing();
			if(outgoing != null){
				// Remove the sequence flow because its source (task) was removed
				String sequenceFlowId = outgoing.get(0).resourceId;
				removePoolElement(sequenceFlowId);
			}
		} else {
			ArrayList<ElementLevel> outgoing = taskElement.getOutgoing();
			if(outgoing != null){
				// Remove the sequence flow because its source (task) was removed
				String sequenceFlowId = outgoing.get(0).resourceId;
				PoolLevel oldSequenceFlow = removePoolElement(sequenceFlowId);
				// Gets the target element from the sequence flow
				ArrayList<Outgoing> outgoingFromSequenceFlow = oldSequenceFlow.getOutgoing();
				Target target = oldSequenceFlow.getTarget();
				if(outgoingFromSequenceFlow != null){
					Outgoing outgoingElement = outgoingFromSequenceFlow.get(0);
					// Sets the target for the source element (probably a SequenceFlow)
					sourceSequenceFlow.outgoing.clear();
					sourceSequenceFlow.outgoing.add(outgoingElement);
					sourceSequenceFlow.target = target;
				}
			}
		}
		
		removeElement(taskResourceId);
	}
	
	public ArrayList<String> getTasks() 
	{
		ArrayList<String> tasks = new ArrayList<String>();

		// Pool level
		for (PoolLevel pool:this.getChildShapes())
		{
			  String elemName = pool.getStencil().toString();
			  if (elemName.contains(" ")) {
				  elemName = elemName.replace(" ", "");
			  }
			  if (elemName.toLowerCase().equals("task")) {
				  tasks.add(pool.getProps().getName());
			  }
			  
			  // Lane level
			  for (LaneLevel lane: pool.getChildShapes()) {
				  if (lane.getStencil().toString().toLowerCase().equals("task")) {
					  tasks.add(lane.getProps().getName());
				  }
				  
				  // Element level
				  for (ElementLevel elem: lane.getChildShapes()) {
					  if (elem.getStencil().toString().toLowerCase().equals("task")) {
						  tasks.add(elem.getProps().getName());
					  }
				  }
			  }
		  }
		return tasks;
	}
	
	private ElementLevel searchForElement(ElementLevel element, String resourceId){
		ElementLevel elementFound = null;
		System.out.println("Element resource id: " + element.getResourceId());
		
		if(element.getResourceId().equals(resourceId)){
			return element;
		} 
			
		for(ElementLevel elem : element.getChildShapes()){
			if(resourceId.equals(elem.getResourceId())){
				elementFound = elem;
				break;
			} else{
				elementFound = searchForElement(elem, resourceId);
				if(elementFound != null){
					break;
				}
			}
		}
		
		return elementFound;
	}
	
	private ElementLevel searchForOutgoingElement(ElementLevel element, String resourceId){
		ElementLevel elementFound = null;
		if(element.getResourceId().equals(resourceId)){
			return element;
		} 
			
		for(ElementLevel elem : element.getChildShapes()){
			ArrayList<ElementLevel> outgoing = elem.getOutgoing();
			if(outgoing != null && outgoing.size() > 0 && 
					resourceId.equals(outgoing.get(0).getResourceId())){
				elementFound = elem;
				break;
			} else{
				elementFound = searchForElement(elem, resourceId);
				if(elementFound != null){
					break;
				}
			}
		}
		
		return elementFound;
	}
	
	private PoolLevel searchForSourceSequenceFlow(String resourceId){
		PoolLevel sourceSequenceFlow = null;
		for(PoolLevel pool : this.getChildShapes()){
			ArrayList<Outgoing> outgoing = pool.getOutgoing();
			if(outgoing != null && outgoing.size() > 0 && resourceId.equals(outgoing.get(0).getResourceId())){
				sourceSequenceFlow = pool;
				break;
			}
		}
		
		return sourceSequenceFlow;
	}
	
	private PoolLevel removePoolElement(String resourceId){
		PoolLevel elementToBeRemoved = null;
		Iterator<PoolLevel> iterator = this.getChildShapes().iterator();
		while(iterator.hasNext()){
			PoolLevel pool = iterator.next();
			if(resourceId.equals(pool.resourceId)){
				elementToBeRemoved = pool;
				iterator.remove();
				break;
			}
		}
		
		return elementToBeRemoved;
	}
	
	private ElementLevel removeElement(String taskResourceId){
		ElementLevel elementToBeRemoved = null;
		outerloop: for(PoolLevel pool : this.getChildShapes()){
			for(LaneLevel lane : pool.getChildShapes()){
				if(lane.getChildShapes() != null){
					ArrayList<ElementLevel> bpmnElements = lane.getChildShapes();
					Iterator<ElementLevel> iterator = bpmnElements.iterator();
					while(iterator.hasNext()){
						ElementLevel currentElement = iterator.next();
						elementToBeRemoved = searchForElement(currentElement, taskResourceId);
						if(elementToBeRemoved != null){
							iterator.remove();
							//TODO maybe will be necessary to sort the array to clear it from null 
							// (e.g., [0, 1, 2, null, 4, 5] => [0, 1, 2, 4, 5]
							break outerloop;
						}
					}
				}
			}
		}
		return elementToBeRemoved;
	}
	
	private void moveElementsHorizontally(double startingValue, double moveValue){
		for(PoolLevel pool : this.getChildShapes()){
			for(LaneLevel lane : pool.getChildShapes()){
				if(lane.getChildShapes() != null){
					ArrayList<ElementLevel> bpmnElements = lane.getChildShapes();
					Iterator<ElementLevel> iterator = bpmnElements.iterator();
					while(iterator.hasNext()){
						ElementLevel currentElement = iterator.next();
						Bounds elementBounds = currentElement.getBounds();
						Point elementLowerRightPoint = elementBounds.getLowerRight();
						Point elementUpperLeftPoint = elementBounds.getUpperLeft();
						double lowerRightX = elementLowerRightPoint.getX();
						if(lowerRightX >= startingValue){
							double updatedLowerRightX = lowerRightX + moveValue;
							double updatedUpperLeftX = elementUpperLeftPoint.getX() + moveValue;
							elementLowerRightPoint.setX(updatedLowerRightX);
							elementUpperLeftPoint.setX(updatedUpperLeftX);
						}
					}
				}
			}
		}
		
	}
}
