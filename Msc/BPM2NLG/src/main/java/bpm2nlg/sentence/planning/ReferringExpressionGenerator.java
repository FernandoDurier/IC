package bpm2nlg.sentence.planning;

import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.label.analysis.ILabelHelper;

import java.util.ArrayList;

import br.com.uniriotec.process.model.ProcessModel;

public class ReferringExpressionGenerator {
	
	private ILabelHelper lHelper;
	
	public ReferringExpressionGenerator(ILabelHelper lHelper) {
		this.lHelper = lHelper;
	}
	
	
	public ArrayList<DSynTSentence> insertReferringExpressions(ArrayList<DSynTSentence> textPlan, ProcessModel process, boolean male) {
		
		String prevRole = null;
		ExecutableFragment prevFragment = null;
		DSynTSentence prevSentence = null;
		
		for (int i = 0; i < textPlan.size(); i++) {
			// Determine current role
			String currRole = textPlan.get(i).getExecutableFragment().getRole(); 
			ExecutableFragment currFragment = textPlan.get(i).getExecutableFragment();
			DSynTSentence currSentence = textPlan.get(i);
			
			if (prevRole != null && prevFragment != null && prevSentence != null) {
				
				if (currRole.equals(prevRole) && 
						//TODO: Language specific code, change
						!currRole.equals("") && !currRole.equals("he") && !currRole.equals("she") && !currRole.equals("it") &&
						!currFragment.sen_hasBullet && currFragment.sen_level == prevFragment.sen_level &&
						prevSentence.getExecutableFragment().getListSize() == 0 && 
						!currFragment.sen_hasConnective && !prevFragment.sen_hasConnective &&
					    currSentence.getClass().toString().endsWith("DSynTMainSentence") &&
						prevSentence.getClass().toString().endsWith("DSynTMainSentence")) {
					
					// Insert referring expression
					if (lHelper.isPerson(currRole)) {
						if (male) {
							textPlan.get(i).getExecutableFragment().setRole("he");
						} else {
							textPlan.get(i).getExecutableFragment().setRole("she");
						}
					} else {
						textPlan.get(i).getExecutableFragment().setRole("it");
					}
				
					((DSynTMainSentence) textPlan.get(i)).changeRole();
					System.out.println("Referring Expression inserted: " + textPlan.get(i).getExecutableFragment().getAction() + " - " + textPlan.get(i).getExecutableFragment().getBo());
					prevRole = null;
					prevFragment = null;
					prevSentence = null;
				}
			} else {
				prevRole = currRole;
				prevFragment = currFragment;
				prevSentence = currSentence;	
			}
		}
		return textPlan;
	}
	
	

}
