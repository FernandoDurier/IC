package general.language.common;


public enum SupportedLanguages 
{
	GERMAN("de-DE"),
	ENGLISH("en-US"),
	PORTUGUESE("pt-BR");
	
	private String code;
	private SupportedLanguages(String code)
	{
		this.code = code;
	}
	
	public String getCode(){ return code; }
}