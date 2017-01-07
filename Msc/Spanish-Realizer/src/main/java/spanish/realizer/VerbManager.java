package spanish.realizer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.TreeMap;

public class VerbManager {
	//teste
	private final String FILE_NAME = "Port_Verbs.txt";
	private final String ALL_VERBS_FILE = "corpus-verbos.txt";

	private String prefix = "";

	private TreeMap <String,String> present3PS;
	private TreeMap <String,String> present3PP;
	private TreeMap <String,String> participle;
	private HashSet<String> infinitives;
	private HashSet<String> allVerbs;

	public static VerbManager getInstance()
	{
		if(instance == null)
			instance = new VerbManager();

		return instance;
	}

	private static VerbManager instance;

	private VerbManager() {
		present3PP = new TreeMap<String, String>();
		present3PS = new TreeMap<String, String>();
		participle = new TreeMap<String, String>();
		infinitives = new HashSet<String>();
		allVerbs = new HashSet<String>();

		loadVerbsFromFile();
		loadAllVerbs();
	}

	public boolean containsVerb(String word)
	{
		return allVerbs.contains(word);
	}

	public boolean isParticiple(String word) {
		if (word.endsWith("os") || word.endsWith("as")) {
			return participle.values().contains(word) ||
			participle.values().contains(word.substring(0, word.length()-2) + "o");
		}
		if (word.endsWith("a")) {
			return participle.values().contains(word) ||
			participle.values().contains(word.substring(0, word.length()-1) + "o");
		}
		return participle.values().contains(word);
	}

	/*public boolean isParticiple(String participleString)
	{
		return (participle.values().contains(participleString.toLowerCase()));
	}*/

	/**
	 * Returns adequate verb form for given DSynT setting.
	 */
	public String getVerb(String infinitive, boolean verb_IsPassive, boolean verb_isParticiple,
			boolean verb_isPast, boolean verb_isImperative, boolean bo_isSubject, boolean bo_isPlural)
	{
		// imperative
		if (verb_isImperative)
			return infinitive;//getImperativeForm(infinitive);

		// passive or participle
		if (verb_IsPassive || verb_isParticiple)
			return getParticipleForm(infinitive);

		// past
		if (verb_isPast)
			return infinitive;//getPastForm(infinitive, bo_isSubject, bo_isPlural);

		// presence
		return getPresenceForm(infinitive, bo_isSubject, bo_isPlural);
	}

	/**
	 * Returns imperative of given verb.
	 * If the imperative form is not found on the database, return the given infinitive form.

	public String getImperativeForm(String infinitive)
	{
		String imperativeForm = imperative.get(infinitive.toLowerCase());

		if (imperativeForm == null)
		{
			String stemmed = stemPrefix(infinitive);
			imperativeForm = (!stemmed.equals("")) ? prefix + getImperativeForm(stemmed) : infinitive;
		}

		return (imperativeForm != null) ? imperativeForm : infinitive;
	}*/

	/**
	 * Returns gerundio of given verb.
	 * If the imperative form is not found on the database, return the given infinitive form.

	public String getGerundioForm(String infinitive)
	{
		String gerundioForm = gerundio.get(infinitive.toLowerCase());

		if (gerundioForm == null)
		{
			String stemmed = stemPrefix(infinitive);
			gerundioForm = (!stemmed.equals("")) ? prefix + getGerundioForm(stemmed) : infinitive;
		}

		return (gerundioForm != null) ? gerundioForm : infinitive;
	}*/

	/**
	 * Returns participle of given verb.
	 * If the participle form is not found on the database, return the given infinitive form.
	 */
	public String getParticipleForm(String infinitive)
	{
		String participleForm = participle.get(infinitive.toLowerCase());

		if (participleForm == null)
		{
			String stemmed = stemPrefix(infinitive);
			participleForm = (!stemmed.equals("")) ? prefix +
					getParticipleForm(stemmed) : infinitive;
		}

		return (participleForm != null) ? participleForm : infinitive;
	}

	/**
	 * Return past form of given verb.
	 * If the past form is not found on the database, return the given infinitive form.

	private String getPastForm(String infinitive, boolean bo_isSubject, boolean bo_isPlural)
	{
		String pastForm = null;

		//Verify if has to return the singular or the plural form of the verb
		pastForm = (bo_isSubject && bo_isPlural) ? past3PP.get(infinitive.toLowerCase()) : past3PS.get(infinitive.toLowerCase());

		if (pastForm == null)
		{
			String stemmed = stemPrefix(infinitive);
			pastForm = (!stemmed.equals("")) ? prefix + getPastForm(stemmed, bo_isSubject, bo_isPlural) : infinitive;
		}

		return (pastForm != null) ? pastForm : infinitive;
	}*/

	/**
	 * Return present form of given verb.
	 */
	private String getPresenceForm(String infinitive, boolean bo_isSubject, boolean bo_isPlural)
	{
		String presentForm = null;

		//Verify if has to return the singular or the plural form of the verb
		presentForm = (bo_isSubject && bo_isPlural) ? present3PP.get(
				infinitive.toLowerCase()) : present3PS.get(infinitive.toLowerCase());

		if (presentForm == null)
		{
			String stemmed = stemPrefix(infinitive);
			presentForm = (!stemmed.equals("")) ? prefix + getPresenceForm(
					stemmed, bo_isSubject, bo_isPlural) : infinitive;
		}

		return (presentForm != null) ? presentForm : infinitive;
	}

	/**
	 * Removes prefix from verb.
	 */
	private String stemPrefix(String infinitive)
	{
		String stemmed = "";

		for (String prefix: LanguageData.verbalPrefixes)
			if (infinitive.startsWith(prefix))
			{
				stemmed = infinitive.substring(prefix.length());
				this.prefix = prefix;
			}

		return stemmed;
	}

	public boolean isInfinitive(String word) {
		return infinitives.contains(word);
	}

	public boolean is3PS(String word) {
		return present3PS.values().contains(word);
	}

	public String getInfinitive(String conjugatedVerb)
	{
		String infinitiveForm = null;

		if(present3PP.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(present3PP, conjugatedVerb);
		else if(present3PS.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(present3PS, conjugatedVerb);
		/*else if(past3PS.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(past3PS, conjugatedVerb);
		else if(past3PP.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(past3PP, conjugatedVerb);*/
		else if(participle.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(participle, conjugatedVerb);
		/*else if(gerundio.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(gerundio, conjugatedVerb);
		else if(imperative.containsValue(conjugatedVerb))
			infinitiveForm = lookForKey(imperative, conjugatedVerb);*/

		return (infinitiveForm == null) ? conjugatedVerb : infinitiveForm;
	}

	private String lookForKey(TreeMap<String, String> collection, String value)
	{
		String keyFound = null;

		for(String currentKey : collection.keySet())
			if(collection.get(currentKey).equalsIgnoreCase(value))
			{
				keyFound = currentKey;
				break;
			}

		return keyFound;
	}

	private void loadAllVerbs(){
		try{
			InputStreamReader is = new InputStreamReader(
					ClassLoader.getSystemResourceAsStream(ALL_VERBS_FILE), "UTF-8");
			BufferedReader br = new BufferedReader(is);
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				strLine = strLine.trim();
				if(!strLine.isEmpty())
					allVerbs.add(strLine);
			}

			br.close();
			is.close();
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	private void loadVerbsFromFile() {
		int count = 0;
		try {
			InputStreamReader is = new InputStreamReader(
					ClassLoader.getSystemResourceAsStream(FILE_NAME), "UTF-8");
			BufferedReader br = new BufferedReader(is);
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				if (strLine.contains("/")) {
					strLine = strLine.replace("/", "");
				}
				String[] splitLine = strLine.split("\t");
				if (splitLine.length >= 4) {
					infinitives.add(splitLine[1].toLowerCase());
					present3PS.put(splitLine[1].toLowerCase(),
							splitLine[2].toLowerCase().trim());
					present3PP.put(splitLine[1].toLowerCase(),
							splitLine[3].toLowerCase().trim());
					participle.put(splitLine[1].toLowerCase(),
							splitLine[4].toLowerCase().trim());
					count++;
				}
			}
			br.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Verbs loaded: " + count);
	}
}
