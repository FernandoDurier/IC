package spanish.realizer;

import general.language.common.dsynt.DSynTConditionSentence;
import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.AbstractFragment;
import general.language.common.fragments.ConditionFragment;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.Identation;
import general.language.common.realizer.SurfaceRealizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SpanishSurfaceRealizer extends SurfaceRealizer
{
	/**
	 * Standard pattern (input): <Role> + <Action> + <BO> + <ADD>
	 * (Gerente solicita assinatura para presidente)
	 * Passive pattern: <BO> + <Verb to be 3�ps/pp> + <Action in participle> + <'por' preposition> + <Role> + <ADD>
	 * (Assinatura � solicitada pelo gerente para presidente)
	 * Condicional pattern:
	 */

	private NounManager nounManager;
	private String sentence  = "";
	//int c = 0;

	public SpanishSurfaceRealizer(ILabelHelper labelHelper, INaturalLanguageProcessor languageProcessor)
	{
		super(labelHelper, languageProcessor);
		nounManager = NounManager.getInstance();
	}

	public String getSentenceString()
	{
		sentence = normalize(sentence);
		return sentence;
	}

	public void realizeSentence(ExecutableFragment fragment)
	{
		sentence = realizeFragment(fragment, false, false);
		sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1,sentence.length()) + ".";
	}

	public void realizeSentence(ConditionFragment cFrag, ExecutableFragment eFrag)
	{
		sentence = realizeFragment(cFrag, true, false) + ", " + realizeFragment(eFrag, false, false);
		sentence = sentence.substring(0, 1).toUpperCase() + sentence.substring(1,sentence.length()) +".";
	}

	/**
	 * Converts a given fragment to a grammatically correct sentence.
	 */
	private String realizeFragment(AbstractFragment frag, boolean isCondition, boolean verbFirst)
	{
		boolean hasRole = true;

		// Determine role
		String role = "";
		if (!frag.getRole().equals(""))
		{
			role = getCompleteRole(frag);
			role = role.trim();
		}

		// Determine bo (also by taking "bo_isSubject" into consideration)
		String bo = frag.getBo();
		if (role.equals("") && frag.bo_isSubject)
		{
			role = bo;
			bo = "";
			hasRole = false;
		}
		if (!isCondition)  //Old code: if(bo.has_Article && !isCondition)
			bo = getCompleteBusinesssObject(frag);

		bo = bo.trim();
		if(bo.contains("las siguientes maneras"))
			bo = "luego las siguientes formas ";

		// Determine verb
		String verbAddition = "";
		String verbSplitted[] = frag.getAction().split(" ");

		if(verbSplitted.length > 1)
			verbAddition = " de " + verbSplitted[1].trim();

		String verb = VerbManager.getInstance().getVerb(frag.getAction(), frag.verb_IsPassive, frag.verb_isParticiple, frag.verb_isPast, frag.verb_isImperative, frag.bo_isSubject, frag.bo_isPlural) + verbAddition;
		verb = verb.trim();

		if(verb.equalsIgnoreCase("ejecutado"))
			verb += "s";

		// Determine addition
		String addition = frag.getAddition();
		if (frag.add_hasArticle)
		{
			String[] addSplit = addition.split(" ");
			if (addSplit.length > 1)
			{
				if (Arrays.asList(LanguageData.articlePrepositions).contains(addSplit[0]))
				{
					addition = addSplit[0].toLowerCase() + " " + nounManager.getArticle(addSplit[1].toLowerCase(), !frag.bo_isSubject, frag.bo_hasIndefArticle, frag.bo_isPlural);
					for (int i = 1; i < addSplit.length; i++)
						addition = addition + " " + addSplit[i];
				}
			}
		}
		addition = addition.trim();

		String s = "";
		boolean mapped = false;

		//Treating the mods, case necessary
		if(frag.hasMods())
		{
			String modRecordsConcated = "";
			for(String mod : frag.getAllMods())
				modRecordsConcated += mod.replace("[", "").replace("]", "");

			role = modRecordsConcated + " " + role;
		}

		// Case 1: standard active, non-reflexive
		if (!frag.verb_IsPassive && !isCondition && !verbFirst && !frag.verb_isReflexive) {
			s = role + " " + verb  + " " + bo + " " + addition;
			mapped = true;
		}

		// Case 2: active with head verb, non-reflexive
		if (!frag.verb_IsPassive && !isCondition && verbFirst && !frag.verb_isReflexive) {
			s = verb  + " "  + role +  " " + bo + " " + addition;
			mapped = true;
		}

		// Case 3: active conditional sentence
		if (!frag.verb_IsPassive && isCondition && !verbFirst)
		{
			ConditionFragment cFrag = (ConditionFragment)frag;

			if(cFrag.getType() == ConditionFragment.TYPE_IF)
				addition += ", entonces";
			else if(cFrag.getType() == ConditionFragment.TYPE_WHEN)
			{
				verb = frag.getAction();

				if(verbSplitted.length > 1)
					verb = verbSplitted[1] + " de " + verb;
			}
			else if(cFrag.getType() == ConditionFragment.TYPE_ONCE)
			{
				if(verbSplitted.length < 1)
					verb = " tenga " + VerbManager.getInstance().getParticipleForm(frag.getAction());
			}

			s = getConditionWord(cFrag) + " " + role + " " + verb  + " " + bo + " " + addition;

			mapped = true;
		}

		mapped = true;

		// ------------- PASSIVE SENTENCES -------------

		// Determine auxiliary verb

		String auxVerb = VerbManager.getInstance().getVerb("ser",false,frag.verb_isParticiple,frag.verb_isPast,false,frag.bo_isSubject,frag.bo_isPlural);

		if(frag.verb_IsPassive)
		{
			if(nounManager.getGender(frag.getBo(),frag.bo_isPlural) == LanguageData.GENDER_FEM)
				verb = verb.substring(0, verb.length()-1) + 'a';

			if(hasRole)
				s = bo + " " + auxVerb + " " + verb + " por el" + role.substring(0,1)  + role.substring(1,role.length()) + addition;
			else
				s = bo + " " + auxVerb + " " + VerbManager.getInstance().getParticipleForm(VerbManager.getInstance().getInfinitive(verb)) + " " + addition;

			mapped = true;
		}

		if (!mapped)
			System.out.println("NOT MAPPED");

		//------------- CONDICIONAL SENTENCES -------------


		// Case 3: passive conditional sentence, verb with 1 component
		if (frag.verb_IsPassive && isCondition && !verbFirst)
		{
			s = getConditionWord((ConditionFragment) frag)  + " " + role + " " + verb + " " + auxVerb;
			//s = bo + " " + auxVerb + " " + verb + " pel" + role.substring(0,1)  + role.substring(1,role.length()) + addition;
			mapped = true;
		}

		//TODO: Check and apply, if necessary, connective logic
		if (frag.sen_hasConnective == true)
		{
			s = frag.connective + " " + s;
			s = s.trim();
		}

		return clean(s);
	}

	/**
	 * Insert the according article for the bo, respecting its gender.
	 * @param frag Fragment, sentence to be analyzed
	 * @return The bo concatenated with its article
	 */
	private String getCompleteBusinesssObject(AbstractFragment frag)
	{
		String article = nounManager.getArticle(frag.getBo(), !frag.bo_isSubject, frag.bo_hasIndefArticle, frag.bo_isPlural);

		if (!article.equals(""))
			return  article + " " + frag.getBo() + " ";
		 else
			return frag.getBo().substring(0, 1).toUpperCase() + frag.getBo().substring(1,frag.getBo().length());
	}

	/**
	 * Insert the according article for the role, respecting its gender.
	 * @param frag Fragment, sentence to be analyzed
	 * @return The role concatenated with its article
	 */
	private String getCompleteRole(AbstractFragment frag)
	{
		String article = nounManager.getArticle(frag.getRole(), frag.bo_isSubject, frag.bo_hasIndefArticle, false);

		if (!article.equals(""))
			return  article + " " + frag.getRole() + " ";
		 else
			return frag.getRole().substring(0, 1).toUpperCase() + frag.getRole().substring(1,frag.getRole().length());
	}

	public String getConditionWord(ConditionFragment cFrag)
	{
		switch (cFrag.getType())
		{
			case ConditionFragment.TYPE_IF:
				return "si";
			case ConditionFragment.TYPE_AS_LONG_AS:
				return "mientras";
			case ConditionFragment.TYPE_WHEN:
				return "cuando";
			case ConditionFragment.TYPE_ONCE:
				return "una vez que";
		}

		return "";
	}

	private String normalize(String s)
	{
		if(s.contains("de ella"))
			s = s.replace("de ella", "de la");
		if(s.contains(" ."))
			s = s.replace(" .", ".");
		if(s.contains(" ,"))
			s = s.replace(" ,", ",");

		return s;
	}

	private String clean(String s)
	{
		s = s.replaceAll("  ", " ");
		s = s.replaceAll("  ", " ");

		return s;
	}

	public String realizeMapSentence(DSynTSentence s,HashMap<Integer, String> map)
	{
		sentence = "";
		ArrayList<Integer> ids = s.getExecutableFragment().getAssociatedActivities();
		if (s.getClass().toString().endsWith("DSynTConditionSentence")) {
			DSynTConditionSentence cs = (DSynTConditionSentence) s;
			ids.addAll(cs.getConditionFragment().getAssociatedActivities());
			ArrayList<ConditionFragment> sentences = cs.getConditionFragment().getSentenceList();
			if (sentences != null) {
				for (ConditionFragment cFrag: sentences)
				{
					ids.addAll(cFrag.getAssociatedActivities());
					sentence += realizeFragment(cFrag, true, false);
				}
			}
		}
		else
		{
			DSynTMainSentence ms = (DSynTMainSentence) s;
			ArrayList<ExecutableFragment> sentences = ms.getExecutableFragment().getSentencList();
			if (sentences != null) {
				for (ExecutableFragment eFrag: sentences)
				{
					ids.addAll(eFrag.getAssociatedActivities());
					sentence += realizeFragment(eFrag, false, false);
				}
			}
		}

		//c++;
		String idAttr = "";
		for (int i = 0; i< ids.size(); i++) {
			if (i>0) {
				idAttr = idAttr + ",";
			}
			idAttr = idAttr + map.get(ids.get(i));
		}

		return "<phrase ids=\"" + idAttr + "\"> " + getSentenceString() + " </phrase>";
	}

	public String realizeSentence(DSynTSentence s, int level, int lastLevel)
	{
		ExecutableFragment frag = s.getExecutableFragment();
		realizeSentence(frag);

		String output = "";
		if (level != lastLevel || frag.sen_hasBullet) {
			output = output + Identation.LINE_BREAK;
			for (int i = 1; i <= level; i++) {
				output = output + Identation.TABULATION;
			}
		}
		if (frag.sen_hasBullet == true) {
			output = output + Identation.BULLET;
		}
		//c++;
		return output + insertDiscourseMarkerIntoText(s);
	}

	private String insertDiscourseMarkerIntoText(DSynTSentence sentence)
	{
		String completeTextualSentence = getSentenceString();
		if (sentence.getClass().getSimpleName().equalsIgnoreCase(DSynTMainSentence.class.getSimpleName()))
		{
			Document document = ((DSynTMainSentence)sentence).getDSynT();
			Node currentNode = document.getFirstChild();
			for(; currentNode.getFirstChild() != null ; currentNode = currentNode.getFirstChild());

			for(; currentNode != null ; currentNode = currentNode.getNextSibling())
			{
				Node node = currentNode.getAttributes().getNamedItem("adv-type");
				if(node != null && node.getNodeValue().equalsIgnoreCase("sentential"))
				{
					String discourseMarker = currentNode.getAttributes().getNamedItem("lexeme").getNodeValue();
					completeTextualSentence = Character.toUpperCase(discourseMarker.charAt(0)) + discourseMarker.substring(1) + ", " + completeTextualSentence.toLowerCase();
					break;
				}
			}
		}

		return completeTextualSentence;
	}

	public String postProcessText(String surfaceText) {
		return surfaceText;
	}
}
