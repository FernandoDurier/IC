package general.language.common.realizer;

public enum Identation {
	TABULATION("\t"),
	LINE_BREAK("\n"),
	BULLET("- ");
	
	private Identation(String identationValue){
		this.identationValue = identationValue;
	}
	
	private String identationValue;
	
	@Override
	public String toString(){
		return this.identationValue;
	}
}
