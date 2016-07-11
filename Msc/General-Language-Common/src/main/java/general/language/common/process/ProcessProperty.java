package general.language.common.process;

public abstract class ProcessProperty {
	
	private int originalProcessElementId;
	
	public int getOriginalProcessElementId() {
		return originalProcessElementId;
	}

	public void setOriginalProcessElementId(int originalProcessElementId) {
		this.originalProcessElementId = originalProcessElementId;
	}

	public abstract ProcessElementType getElementType(); 
}
