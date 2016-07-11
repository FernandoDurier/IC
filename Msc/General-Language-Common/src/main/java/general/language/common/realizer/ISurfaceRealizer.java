package general.language.common.realizer;

import general.language.common.dsynt.DSynTSentence;

import java.util.ArrayList;
import java.util.HashMap;

public interface ISurfaceRealizer 
{
	public String realizeSentence(DSynTSentence s, int level, int lastLevel);
	public String realizeMapSentence(DSynTSentence s, HashMap<Integer, String> map);
	public String realizeSentenceMap(ArrayList<DSynTSentence> sentencePlan, HashMap<Integer, String> map);
	public RealizedText realizePlan(ArrayList<DSynTSentence> sentencePlan);
	
	//TODO: Assert the necessity of this method and if it's really necessary generalize it.
	public String postProcessText(String surfaceText);
	public String cleanTextForImperativeStyle(String surfaceText, String imperativeRole, ArrayList<String> roles);
}
