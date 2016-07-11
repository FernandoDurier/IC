package english.realizer;

import general.language.common.dsynt.DSynTConditionSentence;
import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ConditionFragment;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.Identation;
import general.language.common.realizer.SurfaceRealizer;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;

import com.cogentex.real.api.RealProMgr;

public class EnglishSurfaceRealizer extends SurfaceRealizer
{
	private RealProMgr realproManager;
	
	public EnglishSurfaceRealizer(ILabelHelper labelHelper, INaturalLanguageProcessor languageProcessor) {
		super(labelHelper, languageProcessor);
		realproManager = new RealProMgr();
	}
	
	int c = 0;
	
	public String realizeMapSentence(DSynTSentence s, HashMap<Integer, String> map) 
	{
		Document xmldoc = s.getDSynT();
		realproManager.realize(xmldoc);
		ArrayList<Integer> ids = s.getExecutableFragment().getAssociatedActivities();
		if (s.getClass().toString().endsWith("DSynTConditionSentence")) {
			DSynTConditionSentence cs = (DSynTConditionSentence) s;
			ids.addAll(cs.getConditionFragment().getAssociatedActivities());
			ArrayList<ConditionFragment> sentences = cs.getConditionFragment().getSentenceList();
			if (sentences != null) {
				for (ConditionFragment cFrag: sentences) {
					ids.addAll(cFrag.getAssociatedActivities());
				}
			}
		} else {
			DSynTMainSentence ms = (DSynTMainSentence) s;
			ArrayList<ExecutableFragment> sentences = ms.getExecutableFragment().getSentencList();
			if (sentences != null) {
				for (ExecutableFragment eFrag: sentences) {
					ids.addAll(eFrag.getAssociatedActivities());
				}
			}
		}
		String output = "";
		c++;
		String idAttr = "";
		for (int i = 0; i< ids.size(); i++) {
			if (i>0) {
				idAttr = idAttr + ",";
			}
			idAttr = idAttr + map.get(ids.get(i));
		}
		
		return output + "<phrase ids=\"" + idAttr + "\"> " + realproManager.getSentenceString() + " </phrase>";
	}
	
	public String realizeSentence(DSynTSentence s, int level, int lastLevel) 
	{
		Document xmldoc = s.getDSynT();
		realproManager.realize(xmldoc);
		String output = "";
		if (level != lastLevel || s.getExecutableFragment().sen_hasBullet) {
			output = output + Identation.LINE_BREAK;
			for (int i = 1; i <= level; i++) {
				output = output + Identation.TABULATION;
			}
		}
		if (s.getExecutableFragment().sen_hasBullet == true) {
			output = output + Identation.BULLET;
		}
		c++;
		return output + realproManager.getSentenceString();
	}
	
	public String postProcessText(String surfaceText) 
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
	}
	
	/*private String realizeFragment(ConditionFragment cFrag) {
		Document xmldoc = new DSynTConditionSentence(new ExecutableFragment("", "", "", ""), cFrag).getDSynT();
		realproManager.realize(xmldoc);
		return realproManager.getSentenceString();
	}*/
}
