package general.language.common.label.analysis;

public interface ILabelHelper 
{
	/**
	 * Function returning verb for given noun
	 * @param noun action given as a noun which has to be transformed into a verb
	 * @return verb (infinitive) derived from given noun
	 */
	String getVerbsFromNoun(String noun);
	
	boolean isDefArticle(String wordToBeAnalyzed);
	boolean isIndefArticle(String wordToBeAnalyzed);
	
	/**
	 * Evaluates whether given word can be a verb
	 * @param wordToBeAnalyzed The word that is to be checked
	 * @return true if the given word can be a verb, false otherwise
	 */
	boolean isVerb(String wordToBeAnalyzed);
	
	/**
	 * Evaluates whether given word can be an adjective 
	 * @param wordToBeAnalyzed The word that is to be checked
	 * @return true if the given word can be an adjective, false otherwise
	 */
	boolean isAdjective(String wordToBeAnalyzed);
	
	/**
	 * Evaluates whether given word can be a preposition
	 * @param wordToBeAnalyzed The word that is to be checked
	 * @return true if the given word can be a preposition, false otherwise
	 */
	boolean isPreposition(String wordToBeAnalyzed);
	
	/**
	 * Evaluates whether given word can be a noun 
	 * @param wordToBeAnalyzed The word that is to be checked
	 * @return true if the given word can be a noun, false otherwise
	 */
	boolean isNoun(String wordToBeAnalyzed);
	
	/**
	 * Evaluates whether given word can be an adverb 
	 * @param wordToBeAnalyzed The word that is to be checked
	 * @return true if the given word can be an adverb, false otherwise
	 */
	boolean isAdverb(String potAdverb);
	
	/**
	 * Evaluates whether given role can be a person 
	 * @param wordToBeAnalyzed The role that is to be checked
	 * @return true if the given role can be a person, false otherwise
	 */
	boolean isPerson(String role) ;
	
	/**
	 * Checks whether "inf" is the infinitive of "original"
	 * @param inf
	 * @param original
	 * @return true if "inf" is the infinitive of "original"
	 */
	boolean isInfinitive (String inf, String original);
	
	/**
	 * Checks whether "verb" is the infinitive form
	 * @param verb
	 * @return true if "verb" is the infinitive form of the word
	 */
	boolean isInfinitive(String verb);
	
	/**
	 *  Returns the infinitive of a potential Action (which might still be a noun)
	 * @param action action of label
	 * @return infinitive of action
	 */
	String getInfinitiveOfAction(String action);
	
	/**
	 * Returns adverb for given adjective
	 * @param adj The adjective
	 * @return the adverb for the given adjective
	 * @throws LabelException
	 */
	String getAdverb(String adj) throws LabelException;
	
	/**
	 * Checks whether given label has a plural noun at the end. A positive results proofs the label to be no AN label.
	 * @param label The label to be analyzed
	 * @param labelSplit the splitted form of the original label
	 * @return true if the label has a plural noun at the end, false otherwise.
	 * @throws LabelException
	 */
	boolean hasPluralNounAtEnd(String label, String[] labelSplit) throws LabelException;
	
	/**
	 * Cleans a given label, i.e. content in brackets, dots etc. are removed
	 * @param label Label to be cleaned
	 * @return the cleaned label
	 */
	String cleanLabel(String label);
	
	/**
	 * Removes the article(s) from the given Business object.
	 * @param bo Business object to have its article(s) removed.
	 * @return The business object without the article(s).
	 */
	void removeArticleFromBO(String bo);
	
	
	/**
	 *  Returns the participle form of the given verb (action)
	 * @param verb The verb (action) of a label
	 * @return participle form of the given verb
	 */
	String getParticiple(String verb);
	
	public String getVOSLabelWithoutAddition(ILabelProperties props, String[] labelSplit, String label);
	
	/**
	 * Checks whether given label begins with an adverb. 
	 * @param labelSplit The splitted label to be analyzed
	 * @return true if the label begins with an adverb, false otherwise
	 * @throws LabelException
	 * 
	 */
	boolean beginsWithAdverb(String[] labelSplit) throws LabelException;
	
	/**
	 * Returns singular for the given noun
	 * @param noun noun which needs to be converted in a singular noun
	 * @return singular of given noun
	 */
	String getSingularOfNoun(String noun);
	
	boolean isPluralForm(String noun);
	
	/**
	 * Evaluates whether a given label is a status label (event style)
	 * @param labelTag tagged label string
	 * @return true if label is a status label
	 */
	boolean isStatusLabel(String labelTag) ;
	
	/**
	 * Evaluates whether a given label follows the descriptive Style
	 * @param labelTag tagged label string
	 * @return true if label follows descriptive style
	 */
	boolean isDescriptiveLabel(String labelTag);
	
	/**
	 * Transforms action in an infinitive (=imperative) and aligns multiple actions to a row of imperatives.
	 * @param label action-noun label
	 * @param labelSplit split action-noun label
	 * @param props property object
	 * @throws Exception 
	 */
	void transformActions(String label, String[] labelSplit, ILabelProperties props) throws Exception ;
	
	/**
	 * Checks given label for conjunction and stores results in props.
	 * @param label action-noun label
	 * @param labelSplit split action-noun label
	 * @param props property object
	 */
	void checkForConjunction(String label, String[] labelSplit, ILabelProperties props);
	
	/**
	 * Checks given label for gerund style and stores results in props.
	 * @param label action-noun label
	 * @param labelSplit split action-noun label
	 * @param props property object
	 * @param activity function object 
	 */
	void checkForGerundStyle(String label, String[] labelSplit, ILabelProperties props);
	
	/**
	 * Checks given label for prepositions and stores results props.
	 * @param label action-noun label
	 * @param labelSplit split action-noun label
	 * @param props property object
	 */
	void checkForPrepositions(String label, String[] labelSplit, ILabelProperties props) throws LabelException;
	
	/**
	 * Returns the respective noun which is associated with the given verb
	 * @param verb The verb to be analyzed
	 * @return The noun associated with the verb 
	 */
	String getNoun(String verb) ;
	
	/**
	 * Returns the quantifiers of the current language
	 * @return An array with the quantifiers for the current language
	 */
	String[] getQuantifiers();
	
	/***
	 * Removes all the stop words from the given label (or any text sentence).
	 * @return label without stop words.
	 */
	String removeStopWords(String text);
}
