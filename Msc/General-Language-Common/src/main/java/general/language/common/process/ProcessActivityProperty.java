package general.language.common.process;

public class ProcessActivityProperty extends ProcessProperty {
	private String businessObject;
	private String businessAction;
	private String businessActor;
	private String businessAddition;
	
	/***
	 * Used when the properties are extracted from text. In this case, we dont know the PM element associated.
	 * 
	 * @param bo
	 * @param actor
	 * @param action
	 * @param addition
	 */
	public ProcessActivityProperty(String bo, String actor, String action, String addition){
		this.businessAction = action;
		this.businessActor = actor;
		this.businessObject = bo;
		this.businessAddition = addition;
	}
	
	/***
	 * Used when the properties are extracted from a DsynT, which already have the PM element associated to it.
	 * @param bo
	 * @param actor
	 * @param action
	 * @param addition
	 * @param originalElementId
	 */
	public ProcessActivityProperty(String bo, String actor, String action, String addition, int originalElementId){
		this.businessAction = action;
		this.businessActor = actor;
		this.businessObject = bo;
		this.businessAddition = addition;
		setOriginalProcessElementId(originalElementId);
	}
	
	public String getBusinessObject() {
		return businessObject;
	}
	public String getBusinessAction() {
		return businessAction;
	}
	public String getBusinessActor() {
		return businessActor;
	}
	public String getBusinessAddition() {
		return businessAddition;
	}
	public void setBusinessObject(String businessObject) {
		this.businessObject = businessObject;
	}

	public void setBusinessAction(String businessAction) {
		this.businessAction = businessAction;
	}

	public void setBusinessActor(String businessActor) {
		this.businessActor = businessActor;
	}

	public void setBusinessAddition(String businessAddition) {
		this.businessAddition = businessAddition;
	}
	
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append(businessAction);
		sb.append(" ");
		sb.append(businessObject);
		sb.append(" ");
		sb.append(businessAddition);
		return sb.toString();
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(businessActor);
		sb.append(" ");
		sb.append(businessAction);
		sb.append(" ");
		sb.append(businessObject);
		sb.append(" ");
		sb.append(businessAddition);
		return sb.toString();
	}

	@Override
	public ProcessElementType getElementType() {
		return ProcessElementType.ACTIVITY;
	}
}
