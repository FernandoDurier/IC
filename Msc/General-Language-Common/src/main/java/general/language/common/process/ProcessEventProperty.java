package general.language.common.process;

public class ProcessEventProperty extends ProcessProperty {
	
	private ProcessElementType eventType;
	private String eventIdentifier;
	private String addition;
	
	public ProcessEventProperty(ProcessElementType eventType, String eventIdentifier, String addition){
		this.eventType = eventType;
		this.eventIdentifier = eventIdentifier;
		this.addition = addition;
	}
	
	@Override
	public ProcessElementType getElementType() {
		return eventType;
	}
	
	public String getEventIdentifier() {
		return eventIdentifier;
	}

	public String getAddition() {
		return addition;
	}
}
