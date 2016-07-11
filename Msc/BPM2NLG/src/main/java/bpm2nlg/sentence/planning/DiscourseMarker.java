package bpm2nlg.sentence.planning;

import general.language.common.dsynt.DSynTConditionSentence;
import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.dsynt.IntermediateToDSynTConverter;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DiscourseMarker 
{
	private ArrayList<String> SEQ_CONNECTIVES;
	
	public DiscourseMarker()
	{
		SEQ_CONNECTIVES = new ArrayList<String>();
		SEQ_CONNECTIVES.add(Localization.getInstance().getLocalizedMessage(Messages.THEN));
		SEQ_CONNECTIVES.add(Localization.getInstance().getLocalizedMessage(Messages.AFTERWARDS));
		SEQ_CONNECTIVES.add(Localization.getInstance().getLocalizedMessage(Messages.SUBSEQUENTLY));
	}
	
	public ArrayList<DSynTSentence> insertSequenceConnectives(ArrayList<DSynTSentence> textPlan) {
		int index = 0;
		int indexConnectors = 0;
		boolean inserted = false;
		for (DSynTSentence s: textPlan) {
			if (s.getClass().getSimpleName().equalsIgnoreCase("DSynTConditionSentence")) 
			{
				DSynTConditionSentence condS = (DSynTConditionSentence) s;
				if (condS.getExecutableFragment().sen_hasConnective == false && index > 0 && condS.getConditionFragment().sen_headPosition == false) {
					Element verb = condS.getVerb();
					Document doc = condS.getDSynT();
					// Insert sequence connective
					if (index == textPlan.size()-1) {
						IntermediateToDSynTConverter.insertConnective(doc, verb, Localization.getInstance().getLocalizedMessage(Messages.FINALLY));
					} else {
						IntermediateToDSynTConverter.insertConnective(doc, verb, SEQ_CONNECTIVES.get(indexConnectors));
						inserted = true;
					}
				}
			}
			if (s.getClass().getSimpleName().equalsIgnoreCase("DSynTMainSentence"))
			{
				DSynTMainSentence mainS = (DSynTMainSentence) s;
				if (mainS.getExecutableFragment().sen_hasConnective == false && index > 0 && mainS.getExecutableFragment().sen_hasBullet == false) {
					Element verb = mainS.getVerb();
					Document doc = mainS.getDSynT();
					
					// Insert sequence connective
					if (index == textPlan.size()-1) {
						IntermediateToDSynTConverter.insertConnective(doc, verb, Localization.getInstance().getLocalizedMessage(Messages.FINALLY));
					} else {
						IntermediateToDSynTConverter.insertConnective(doc, verb, SEQ_CONNECTIVES.get(indexConnectors));
						inserted = true;
					}
				}
			}
			
			// Adjust indices
			index++;
			if (inserted == true) {
				indexConnectors++;
				if (indexConnectors == SEQ_CONNECTIVES.size()) {
					indexConnectors = 0;
				}
				inserted = false;
			}
		}
		return textPlan;
	}
	
}
