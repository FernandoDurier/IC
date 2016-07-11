package general.language.common.realizer;

import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.fragments.FragmentType;
import general.language.common.process.ProcessActivityProperty;
import general.language.common.process.ProcessProperty;

public class DsynTProcessor {
	private ProcessProperty processProperties;
	
	public DsynTProcessor(DSynTSentence dsyntSentence){
		ExecutableFragment eFrag = dsyntSentence.getExecutableFragment();
		FragmentType dsyntType = eFrag.getFragmentType();
		if(dsyntType.equals(FragmentType.STANDARD)){
			int originalElementId = (eFrag.getAssociatedActivities().size() > 0) ? eFrag.getAssociatedActivities().get(0) : -1;
			String action = dsyntSentence.getExecutableFragment().getAction();
			String bo = dsyntSentence.getExecutableFragment().getBo();
			String actor = dsyntSentence.getExecutableFragment().getRole();
			String addition = dsyntSentence.getExecutableFragment().getAddition();
			
			this.processProperties = new ProcessActivityProperty(bo, actor, action, addition, originalElementId);
		}
	}
	
	public ProcessProperty getProcessProperties(){
		return this.processProperties;
	}
}
