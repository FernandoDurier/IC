package general.language.common.realizer;

import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SurfaceRealizer implements ISurfaceRealizer
{
	public abstract String realizeSentence(DSynTSentence s, int level, int lastLevel);
	public abstract String realizeMapSentence(DSynTSentence s, HashMap<Integer, String> map);
	private final ILabelHelper lHelper;
	private final INaturalLanguageProcessor languageProcessor;
	
	public SurfaceRealizer(ILabelHelper labelHelper, INaturalLanguageProcessor languageProcessor){
		this.lHelper = labelHelper;
		this.languageProcessor = languageProcessor;
	}
	
	public String realizeSentenceMap(ArrayList<DSynTSentence> sentencePlan, HashMap<Integer, String> map)
	{
		String s = "<text>\n";
		
		for (DSynTSentence dsynt: sentencePlan) 
			s = s + " " + realizeMapSentence(dsynt, map) + "\n";
		
		return s + "</text>";
	}
	
	//Realize Sentence
//	public RealizedText realizePlan(ArrayList<DSynTSentence> sentencePlan) 
//	{
//		String surfaceText = "";
//		int lastLevel = -1;
//		RealizedText realizedText = new RealizedText(lHelper, languageProcessor);
//		for (DSynTSentence dsyntSentence : sentencePlan) {
//			int level = dsyntSentence.getExecutableFragment().sen_level;
//			String textSentence = realizeSentence(dsyntSentence, level, lastLevel);
//			surfaceText = surfaceText + " " + textSentence;
//			realizedText.addSentence(textSentence, dsyntSentence);
//			lastLevel = level;
//		}
//		realizedText.setText(surfaceText + "\n\n");
//		return realizedText;
//	}
	
	private int currentDsyntSentence = 0;
	private int lastLevel = 0;
	private int lastBulletNumber = -1;
	
	public RealizedText realizePlan(ArrayList<DSynTSentence> sentencePlan) {
		String surfaceText = "";
		RealizedText realizedText = new RealizedText(lHelper, languageProcessor);
		surfaceText = processSentencesRecursivly(sentencePlan, realizedText, 0);
		realizedText.setText(surfaceText + "\n\n");
//		realizedText.finalizeGatewayConfiguration();
		return realizedText;
	}
	
	private String processSentencesRecursivly(List<DSynTSentence> sentencePlan, RealizedText currentMainBranch, int mainBranchSenLevel){
		String surfaceText = "";
		
		while(currentDsyntSentence < sentencePlan.size()){
			DSynTSentence dsyntSentence = sentencePlan.get(currentDsyntSentence);
			ExecutableFragment eFrag = dsyntSentence.getExecutableFragment();
			mainBranchSenLevel = eFrag.sen_level;
			System.out.println(eFrag.sen_bulletNumber);
			
			// checks if the secondary branch has finished
			if(lastLevel > mainBranchSenLevel){
				// it means that all secondary paths (branches) are finished, then we must go back to the main branch 
				currentMainBranch = currentMainBranch.getFatherBranch();
			}
			
			 if(mainBranchSenLevel > lastLevel || lastBulletNumber < eFrag.sen_bulletNumber){
				 //if bullet number > 1, then we must return to the previous mainBranch
				 if(eFrag.sen_bulletNumber > 1){
					 currentMainBranch = currentMainBranch.getFatherBranch();
				 }
				 lastBulletNumber = eFrag.sen_bulletNumber;
				 RealizedText newCurrentMainBranch = new RealizedText(lHelper, languageProcessor);
				currentMainBranch.addBranchSentences(newCurrentMainBranch);
				newCurrentMainBranch.setFatherBranch(currentMainBranch);
				if(currentDsyntSentence < sentencePlan.size()-1){
					lastLevel = mainBranchSenLevel;
					surfaceText += processSentencesRecursivly(sentencePlan, newCurrentMainBranch, mainBranchSenLevel);
				}
			} else {
				String textSentence = realizeSentence(dsyntSentence, mainBranchSenLevel, lastLevel);
				surfaceText = surfaceText + " " + textSentence;
				currentMainBranch.addSentence(textSentence, dsyntSentence);
				currentDsyntSentence++;
				lastLevel = mainBranchSenLevel;
			}
		}
		
		return surfaceText;
	}
	
	//TODO: Assert the necessity of this method and if it's really necessary generalize it.
	public abstract String postProcessText(String surfaceText);
	/*public String postProcessText(String surfaceText) 
	{
		surfaceText = surfaceText.replaceAll("If it is necessary", "If it is necessary,");
		surfaceText = surfaceText.replaceAll("one of the branches was executed", "one of the branches was executed,");
		surfaceText = surfaceText.replaceAll("In concurrency to the latter steps", "In concurrency to the latter steps,");
		surfaceText = surfaceText.replaceAll("Once both branches were finished", "Once both branches were finished,");
		surfaceText = surfaceText.replaceAll("Once the loop is finished", "Once the loop is finished,");
		surfaceText = surfaceText.replaceAll("one of the following branches is executed.", "one of the following branches is executed:");
		surfaceText = surfaceText.replaceAll("one or more of the following branches is executed.", "one or more of the following branches is executed:");
		surfaceText = surfaceText.replaceAll("parallel branches.", "parallel branches:");
		surfaceText = surfaceText.replaceAll("The process begins", "The process begins,");
		surfaceText = surfaceText.replaceAll("If it is required", "If it is required,");
		surfaceText = surfaceText.replaceAll(" the a ", " a ");
		surfaceText = surfaceText.replaceAll("branches were executed ", "branches were executed, ");
		
		return surfaceText;
	}*/
	
	//TODO: Improve the generalization, don't use localization instead use the category of the required word.
	//Ex: the = Defined article, you = personal pronoun (3 person of singular)
	public String cleanTextForImperativeStyle(String surfaceText, String imperativeRole, ArrayList<String> roles) 
	{
		if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole)) {
			surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + 
					imperativeRole, Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole)) {
			surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + 
					imperativeRole, Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole.toLowerCase())) {
			surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole.toLowerCase(), Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole.toLowerCase())) {
			surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.THE) + " " + imperativeRole.toLowerCase(), Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		if (surfaceText.contains(imperativeRole.toLowerCase())) {
			surfaceText = surfaceText.replaceAll(imperativeRole.toLowerCase(), Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		if (surfaceText.contains(imperativeRole)) {
			surfaceText = surfaceText.replaceAll(imperativeRole, Localization.getInstance().getLocalizedMessage(Messages.YOU));
		}
		
		for (String role: roles) 
		{
			if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.AND) + " " + role.toLowerCase())) {
				surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.AND) + " " + role.toLowerCase(), getLocalizedAndThe() + role.toLowerCase());
			}
			if (surfaceText.contains(Localization.getInstance().getLocalizedMessage(Messages.AND) + " " + role)) {
				surfaceText = surfaceText.replaceAll(Localization.getInstance().getLocalizedMessage(Messages.AND) + " " + role, getLocalizedAndThe() + role);
			}
		}
		
		return surfaceText;
	}
	
	private String getLocalizedAndThe()
	{
		return Localization.getInstance().getLocalizedMessage(Messages.AND) + " " + Localization.getInstance().getLocalizedMessage(Messages.THE);
	}
}
