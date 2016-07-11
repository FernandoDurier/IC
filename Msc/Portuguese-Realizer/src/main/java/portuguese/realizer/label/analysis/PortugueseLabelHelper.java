package portuguese.realizer.label.analysis;

import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.ILabelProperties;
import general.language.common.label.analysis.LabelException;
import general.language.common.localization.Localization;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import portuguese.realizer.LanguageData;
import portuguese.realizer.NounManager;
import portuguese.realizer.PrepositionManager;
import portuguese.realizer.StopWordManager;
import portuguese.realizer.VerbManager;
import portuguese.realizer.util.POSRecord;

public class PortugueseLabelHelper implements ILabelHelper 
{
	public static int POS_NOUN = 0;
	public static int POS_VERB = 1;
	public static int POS_ADJ = 2;
	public static int POS_ADV = 3;
	
	private ArrayList<String> nouns;
	private HashMap<String,POSRecord> posMap;
	private final String FLORESTA_CORPUS = "Floresta_POS-Map.txt";
	private final String NOUN_CORPUS = "corpus-substantivos.txt";
	
	public PortugueseLabelHelper() 
	{
		nouns = new ArrayList<String>();
		posMap = new HashMap<String, POSRecord>();
		posMap = loadPOSMap();
		
		//Adds the nouns found in the corpus and mapping to its gender.
		NounManager.getInstance().addNounsToGenderMap(nouns); 
	}
	
	/*private String tagString(String input) {
		String[] split = input.split(" ");
		String output = "";
		for (String s: split) {
			output = output + " " + s + "/" + convertPOS(getPOS(s)); 
		}
		return output.trim();
	}*/
	
	private int getPOS(String word) {
		if (word != null){ 
			word = word.toLowerCase().trim();
			word = Normalizer.normalize(word, Normalizer.Form.NFD);
			word = word.replaceAll("[^\\p{ASCII}]", "");
			if(posMap.containsKey(word)) {
				POSRecord record = posMap.get(word.toLowerCase().trim());
				if (record.hasOnlyTagType()) {
					if (record.getNounTags() > 0) {
						return POS_NOUN;
					}
					if (record.getVerbTags() > 0) {
						return POS_VERB;
					}
					if (record.getAdjTags() > 0) {
						return POS_ADJ;
					}
					if (record.getAdvTags() > 0) {
						return POS_ADV;
					}
				} else {
					return record.getMaxTagType();
				}	
			} 
		}
		return -1;
	}
	
	private HashMap<String,POSRecord> loadPOSMap() {
		HashMap<String,POSRecord> posMap = new HashMap<String, POSRecord>();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(FLORESTA_CORPUS);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null) 
			{
				String[] split = strLine.split("\t");
				String word = split[0].trim().toLowerCase(); 
				POSRecord pos = new POSRecord(Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3]), Integer.valueOf(split[4]));
				String wordWithoutAccent = Normalizer.normalize(word, Normalizer.Form.NFD);
				wordWithoutAccent = wordWithoutAccent.replaceAll("[^\\p{ASCII}]", "");
				posMap.put(wordWithoutAccent, pos);
			
				if(pos.hasOnlyTagType() && pos.getNounTags() > 0) //Add the noun to the list of nouns
					nouns.add(word);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			Collection<String> nouns = loadAllNouns();
			for(String noun : nouns){
				String nounWithoutAccent = Normalizer.normalize(noun, Normalizer.Form.NFD);
				nounWithoutAccent = nounWithoutAccent.replaceAll("[^\\p{ASCII}]", "");
				POSRecord pos = new POSRecord(1, 0, 0, 0);
				posMap.put(nounWithoutAccent, pos);
			}
		}
		return posMap;
	}
	
	private Collection<String> loadAllNouns(){
		List<String> nouns = new ArrayList<String>();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(NOUN_CORPUS);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String noun = strLine.trim().toLowerCase();
				nouns.add(noun);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nouns;
	}
	
	/*private String convertPOS(int code) {
		if (code == 0) {
			return "N"; 
		}
		if (code == 1) {
			return "V"; 
		}
		if (code == 2) {
			return "ADJ"; 
		}
		if (code == 3) {
			return "ADV"; 
		}
		return "-1";
	}*/
	
	public String getVerbsFromNoun(String noun) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isVerb(String word) 
	{
		boolean isVerb = false;
		
		isVerb = (getPOS(word) == PortugueseLabelHelper.POS_VERB);
		if(!isVerb)
			isVerb = VerbManager.getInstance().containsVerb(word);
		
		return isVerb;
	}

	public boolean isNoun(String word) {
		return (getPOS(word) == PortugueseLabelHelper.POS_NOUN);
	}

	public boolean isAdverb(String word) 
	{
		return (getPOS(word) == PortugueseLabelHelper.POS_ADV);
	}
	
	public boolean isAdjective(String word) 
	{
		if (getPOS(word) == PortugueseLabelHelper.POS_ADJ) {
			return true;
		}
		if (word.endsWith("as") || word.endsWith("os")) {
			if (getPOS(word.substring(0, word.length()-2) + "o") == PortugueseLabelHelper.POS_ADJ) {
				return true;
			}
		}
		if (word.endsWith("a")) {
			if (getPOS(word.substring(0, word.length()-1) + "o") == PortugueseLabelHelper.POS_ADJ) {
				return true;
			}
		}
		return false;
	}

	public boolean isInfinitive(String inf, String original) {
		return (inf.equalsIgnoreCase(VerbManager.getInstance().getInfinitive(original)));
	}

	public String getInfinitiveOfAction(String action) {
		return VerbManager.getInstance().getInfinitive(action);
	}

	public boolean hasPluralNounAtEnd(String label, String[] labelSplit) throws LabelException 
	{
		boolean hasPlural = false;
		
		if(label != null && labelSplit != null)
		{
			String formattedLabel = label.trim().toLowerCase();
			if(formattedLabel.endsWith("s"))
			{
				String lastWord = labelSplit[labelSplit.length-1].trim().toLowerCase();
				if(isNoun(lastWord))
				{
					hasPlural = NounManager.getInstance().isPlural(lastWord);
				}
			}
		}
		
		return hasPlural;
	}

	public String cleanLabel(String label) 
	{
		// Remove content in brackets (*), [*] and clean from numbers etc. 
		if (label.indexOf(")") > -1 && label.indexOf(" (") > -1 && label.indexOf(")") == (label.length()-1)) {
			label = label.substring(0,label.indexOf(" ("));
		}
		if (label.indexOf(" [") > -1) {
			label = label.substring(0,label.indexOf(" ["));
		}
		if (label.endsWith(".")) {
			label = label.substring(0,label.length()-1);
		}
		if (label.endsWith(".")) {
			label = label.substring(0,label.length()-1);
		}
		if (label.contains("\"")) {
			label = label.replace("\"", "");
		}
		return label;
	}

	public String getParticiple(String verb) {
		return VerbManager.getInstance().getParticipleForm(verb);
	}

	public String getVOSLabelWithoutAddition(ILabelProperties props, String[] labelSplit, String label) 
	{
		if (props.getIndexPrepSplit() > 0) {
			String BO = "";
			for (int j=1; j<=props.getIndexPrepSplit()-1; j++) {
				BO = BO + " " + labelSplit[j];
			}
			BO = BO.trim();
			return labelSplit[0] + " " + BO;
		} else {
			return label;
		}
	}

	public boolean beginsWithAdverb(String[] labelSplit) throws LabelException 
	{
		boolean beginsWithAdverb = false;
		if(labelSplit != null)
		{
			String formattedFirstWord = labelSplit[0].trim().toLowerCase();
			beginsWithAdverb = isAdverb(formattedFirstWord);
		}
		
		return beginsWithAdverb;
	}
	
	public boolean isStatusLabel(String labelTag) {
		String[] splitLabel = labelTag.split(" ");
		if  (labelTag.contains("VBD")) {
			if (isVerb(splitLabel[0]) == false || isInfinitive(getInfinitiveOfAction(splitLabel[0]), splitLabel[0])==false) {
				return true;	
			}
		}
		if (labelTag.contains("VBD")) {
			String[] splitTag = labelTag.split(" ");
			for (int i = 0; i < splitTag.length; i++) {
				if (splitTag[i].contains("VBD") == true) {
					if ((i+1) > splitTag.length && splitTag[i+1].contains("VBN") == true) {
						return true;
					}
				}
			}	
		}
		return false;
	}

	public boolean isDescriptiveLabel(String labelTag)
	{
		String[] splitLabel = labelTag.split(" ");
		if  (labelTag.contains("VBZ")) {
			if (isVerb(splitLabel[0]) == false || isInfinitive(getInfinitiveOfAction(splitLabel[0]), splitLabel[0])==false) {
				return true;	
			}
		}
		return false;
	}

	public void transformActions(String label, String[] labelSplit, ILabelProperties props) throws Exception 
	{
		// If no multiple actions...
		if (props.getMultipleActions().size() == 0) 
		{
			// Transform to infinitive
			props.setAction(getSingularOfNoun(props.getAction()));
			props.setAction(getInfinitiveOfAction(props.getAction()));
			
			if (!props.getAction().equals("")) 
			{
				// Assign infinitive to action
				props.setAction(props.getAction().trim());
				
				// Make first letter upper case
				props.setAction(props.getAction().substring(0,1).toUpperCase() + props.getAction().substring(1,props.getAction().length()));
				props.addToActions(props.getAction());
			}
			
		} 
		else // If there are multiple actions... 
		{
			// Clear action variable
			props.setAction("");
			
			// For each of the identified actions ...
			for (int j=0; j < props.getMultipleActions().size(); j++) 
			{
				// Transform to infinitive
				String tempAction = props.getMultipleActions().get(j);
				tempAction = getSingularOfNoun(tempAction);
				tempAction = getInfinitiveOfAction(tempAction);
				
				// Organize in a row of actions separated with a ',' and the last action is connected with an and 'e'
				if (j<props.getMultipleActions().size()-2) {
					props.setAction(props.getAction() + tempAction + ", ");	
				} 
				else if (j< props.getMultipleActions().size()-1) {
					props.setAction(props.getAction() + tempAction + " e ");	
				} 
				else {
					props.setAction(props.getAction() + tempAction);
				}
				props.addToActions(props.getAction());
			}
		}
		
	}

	public void checkForConjunction(String label, String[] labelSplit, ILabelProperties props)
	{
		if (label.contains(" e ")) {
			props.setIndexConjunction(label.indexOf(" e "));
			props.setHasConjunction(true);
			for (int j = 0; j < labelSplit.length; j++) {
				if (labelSplit[j].equals("e")) {
					props.setIndexConjunctionSplit(j);
					break;
				}
			}
		}	
		if (label.contains(" / ") && label.indexOf(" / ") < props.getIndexPrep()) {
			props.setIndexConjunction(label.indexOf(" / "));
			props.setHasConjunction(true);
			for (int j = 0; j < labelSplit.length; j++) {
				if (labelSplit[j].equals("/")) {
					props.setIndexConjunctionSplit(j);
					break;
				}
			}
		}	
		if (label.contains(",")) {
			props.setIndexConjunction(label.indexOf(","));
			props.setHasConjunction(true);
			for (int j = 0; j < labelSplit.length; j++) {
				if (labelSplit[j].contains(",")) {
					props.setIndexConjunctionSplit(j);
					break;
				}
			}
		}			
	}

	public void checkForGerundStyle(String label, String[] labelSplit, ILabelProperties props) 
	{
		// Check if first verb can be a verbLabelProperties
		String cleanedVerb = labelSplit[0].trim().toLowerCase();
		if(isVerb(cleanedVerb))
		{
			props.setVerb(true);
			props.setGerundStyle(labelSplit[0].endsWith("ndo"));
		}
	}

	public void checkForPrepositions(String label, String[] labelSplit, ILabelProperties props) throws LabelException
	{
		// Check each word in the label whether it is a preposition
		if(label != null && labelSplit != null)
		{
			for (int j = 1; j < labelSplit.length; j++) 
			{
				if (props.getPrepositions().contains(labelSplit[j]))
				{
					String[] ofPrepositions = {"de","do","da"};
					if (Arrays.asList(ofPrepositions).contains(labelSplit[j])) 
					{
						props.setHasPrepositionOf(true);
						props.setIndexOf(j);
					}
					
					props.setIndexPrep(label.indexOf(" " + labelSplit[j]));
					props.setHasPreposition(true);
					props.setIndexPrepSplit(j);
					
					// if conjunction is positioned in addition, ignore conjunction
					if (props.getIndexPrep() < props.getIndexConjunction()) 
						props.setHasConjunction(false);
					
					break;
				}
			}
		}
	}
	
	public boolean isPerson(String role) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getAdverb(String adj) throws LabelException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getSingularOfNoun(String noun) 
	{
		return NounManager.getInstance().transformToSingularForm(noun);
	}
	
	public String getNoun(String verb) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getQuantifiers() {
		return  new String[]{"um", "o","a", "todo", "qualquer", "mais", "maioria", "nenhum", "algum", "tal", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez"};
	}

	public boolean isDefArticle(String string) {
		return Arrays.asList(LanguageData.defArticles).contains(string);
	}

	public void removeArticleFromBO(String bo) 
	{
		//TODO: Method hasn't been tested...
		String[] splittedBo = bo.split(" ");
		if(splittedBo.length > 1 && isArticle(splittedBo[0]))
			bo = bo.substring(splittedBo[0].length());
	}
	
	public boolean isPluralForm(String noun) 
	{
		return NounManager.getInstance().isPlural(noun);
	}
	
	private boolean isArticle(String word)
	{
		boolean isArticle = Arrays.asList(LanguageData.defArticles).contains(word);
		if(!isArticle)
			isArticle = Arrays.asList(LanguageData.indefArticles).contains(word);
		
		return isArticle;
	}

	public boolean isIndefArticle(String wordToBeAnalyzed) 
	{
		return (Arrays.asList(LanguageData.indefArticles).contains(wordToBeAnalyzed));
	}

	public String removeStopWords(String text) {
		String cleanText = text;
		if(text != null && !text.trim().isEmpty()){
			
			Collection<String> discourseMarkers = Localization.getInstance().getDiscourseMarkers();
			for(String marker : discourseMarkers){
				if(cleanText.toLowerCase().contains(marker)){
					cleanText = cleanText.replaceAll("(?i)" + marker, "");
				}
			}
			
			String[] words = cleanText.split("\\s+");
			
			//necessary to avoid bug in some cases (e.g., 'O processo comeca' does not match with \\s+O\\s+ because does begin with white space)
			cleanText = " " + cleanText; 
			
			for(String word : words){
				if(!word.trim().isEmpty()){
					
					// remove attached periods (e.g., .entao. -> entao)
					word = word.replaceAll("\\.", "");
					// remove attached commas (e.g., ,entao, -> entao)
					word = word.replaceAll(",", "");
					
					if(discourseMarkers.contains(word.toLowerCase())){
						String regex = String.format("(?i)%s%s%s", "\\s*", word, "\\s*");
						cleanText = cleanText.replaceAll(regex, " ");
					} else if(StopWordManager.getInstance().isStopWord(word)){
						String regex = String.format("(?i)%s%s%s", "\\s+", word, "\\s+");
						cleanText = cleanText.replaceAll(regex, " ");
					} 
				}
			}
			
			// remove trailing and excessive white spaces
			cleanText = cleanText.trim().replaceAll("\\s+", " ");
			
			// remove empty comas (see method documentation for details)
			cleanText = cleanTextFromEmptyComas(cleanText);
		}
		
		return cleanText;
	}

	public boolean isInfinitive(String verb) {
		return VerbManager.getInstance().isInfinitive(verb);
	}

	public boolean isPreposition(String wordToBeAnalyzed) {
		return PrepositionManager.getInstance().isPreposition(wordToBeAnalyzed);
	}
	
	/***
	 * Remove empty commas from the text input. 
	 * Any occurrence of commas ',' after a period '.' will be removed from the string. 
	 * Example of usage: <br>
	 *  <ol>
	 *  <li><b>Input:</b> Previous sentence here. , next sentence here.</li>
	 *  <li><b>Output:</b> Previous sentence here. next sentence here.</li> 
	 *  </ol>
	 *  
	 * @param text Input text to be cleaned
	 * @return cleaned text without empty commas
	 */
	private String cleanTextFromEmptyComas(String text){
		String cleannedText = text;
		Pattern regex = Pattern.compile("\\s*(\\p{Punct})*\\s*,");
		Matcher regexMatcher = regex.matcher(text);
		try {
			if (regexMatcher.find()) {
				cleannedText = regexMatcher.replaceAll("$1");
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return cleannedText;
	}
}
