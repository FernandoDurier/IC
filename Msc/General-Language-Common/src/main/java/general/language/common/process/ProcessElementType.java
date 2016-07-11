package general.language.common.process;

import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;

public enum ProcessElementType {
	GATEWAY_AND,
	GATEWAY_PARALEL,
	GATEWAY_XOR,
	ACTIVITY,
	EVENT_BEGIN_WITH_DESCRIPTION,
	EVENT_BEGIN_WITH_ACTIVITY,
	EVENT_FINISH,
	EVENT_FINISH_WITH_DESCRIPTION,
	UNKNOW;
	
	private String[] keywords;
	
	ProcessElementType(){}
	
	ProcessElementType(Messages... keywords){
		String[] keys = new String[keywords.length];
		for(int i = 0 ; i < keywords.length ; i++){
			keys[i] = Localization.getInstance().getLocalizedMessage(keywords[i]);
		}
		this.keywords = keys;
	}
	
	public String[] getKeywords(){
		return keywords;
	}
}
