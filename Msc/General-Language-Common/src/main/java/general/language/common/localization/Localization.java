package general.language.common.localization;

import general.language.common.SupportedLanguages;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class Localization 
{
	/**
	 * The enum used for the representation of the Dictionary keys. 
	 * Each key should have its corresponding translation for the avaiable languages.
	 * @author Edward
	 */
	public enum Messages
	{
		AFTERWARDS, ALLOW, ALL_THE_X_BRANCHES, AND, AS_LONG_AS, 
		BOTH_BRANCH,
		CONDUCT, CONTAIN, CONTINUE,
		DECIDE,
		EXECUTE,
		FINALLY, FINISH, FOLLOWING, FOR, FOR_THE_FOLLOWING_DEVIATIONS,
		IF, IN_ADDITION, IN_CASE, IN_CONCURRENCY_TO_THE_LATTER_STEP, IN_CONCURRENCY_TO_THE_LATTER_X_STEP, INTO_X_PARALLEL_BRANCHES, IT,
		LOOP,
		NO,
		ONCE, ONE_OR_MORE_OF_THE, ONE_OF_THE_FALLOWING_BRANCHES, OTHERWISE,
		PATHS, PROCESS, PROCESS_INSTANCE,
		REPEAT,
		SPLIT, START, STEP, SUBSEQUENTLY,
		THE, THEIR, THEN, THE_PROCESS_BEGIN_WHEN,
		UNSTRCUTURED_PART, UNSTRCUTURED_PART_WHICH_CAN_BE_EXECUTED_AS_FOLLOW,
		WHETHER, WHEN, WITH_DECISION,
		YES, YOU;
		
		public static boolean isMessageValid(String msg)
		{
			boolean isMessageValid = true;
			
			try
			{
				Messages.valueOf(msg);
			}
			catch(IllegalArgumentException invalidMessage)
			{
				isMessageValid = false;
			}
			
			return isMessageValid;
		}
	}
	
	private final String DICTIONARY_PORTUGUESE = "Dictionary_Portuguese.txt";
	private final String DICTIONARY_ENGLISH = "Dictionary_English.txt";
	private final String DICTIONARY_GERMAN = "Dictionary_German.txt";
        private final String DICTIONARY_SPANISH = "Dictionary_Spanish.txt";
	private HashMap<SupportedLanguages, HashMap<Messages, String>> cachedDictionaries;
	private HashMap<Messages, String> currentDictionary;
	
	/**
	 * Private constructor that don't allow instances to be created without the getInstance method.
	 * Inicialize the cache for the dictionaries. 
	 */
	private Localization()
	{
		cachedDictionaries = new HashMap<SupportedLanguages, HashMap<Messages,String>>();
	}
	
	private static Localization instance;
	/**
	 * The acessor method that defines the singleton. 
	 * Method is responsible for avoiding that multiple instances of the class are created.
	 * @return The Localization instance to be manipulated.
	 */
	public static Localization getInstance()
	{
		if(instance == null)
			instance = new Localization();
		
		return instance;
	}
	
	/**
	 * Sets a String with the translation for the messages received by parameter.<br>
	 * For each Key (message), look for the respective translation in the dictionary. 
	 * Then it concats with the current result string. <br>
	 * @param messages One or more messages (Dictionary key)
	 * @return A String based in the concatenation of the translations for the given messages.
	 */
	public String getLocalizedMessage(Messages...messages)
	{
		String mergedLocalizedMessages = "";
		
		if(messages != null && messages.length > 0)
		{
			for(Messages msg : messages)
				mergedLocalizedMessages += getLocalizedMessage(msg) + "#";
		}
		
		return mergedLocalizedMessages.trim();
	}
	
	/**
	 * Look for the given message (key) in the dictionary for the current language. 
	 * @param message The dictionary key
	 * @return The translated message for the given dictionary Key.
	 */
	public String getLocalizedMessage(Messages message)
	{
		String retVal = "(The translation for the message '" + message + "' wasn't found in the dictionary)";
		
		if(currentDictionary.containsKey(message))
			retVal = currentDictionary.get(message);
		
		return retVal;
	}
	
	/**
	 * Sets the language, which must be one of the supported languages, for the localization framework.
	 * @param language The localization language to be set
	 * @throws LocalizationException An exception containing the erros that happened while trying to read the respective language dictionary file.
	 */
	public void setLocalization(SupportedLanguages language) throws LocalizationException
	{
		if(cachedDictionaries.containsKey(language))
			currentDictionary = cachedDictionaries.get(language);
		else
		{
			String fileName = "";
			
			switch(language)
			{
				case GERMAN:
					fileName = DICTIONARY_GERMAN;
					break;
					
				case PORTUGUESE:
					fileName = DICTIONARY_PORTUGUESE;
					break;
					
                                 case ENGLISH:
					fileName = DICTIONARY_ENGLISH;
					break;
                                 case SPANISH:
					fileName = DICTIONARY_SPANISH;
                                        break;
			}
			
			parseDictonary(fileName, language);
			cachedDictionaries.put(language, currentDictionary);
		}
	}
	
	public Collection<String> getDiscourseMarkers(){
		Messages[] markers = {Messages.AFTERWARDS, Messages.SUBSEQUENTLY, Messages.THEN, Messages.FINALLY};
		Collection<String> markersList = new ArrayList<String>(markers.length);
		for(Messages marker : markers){
			markersList.add(getLocalizedMessage(marker).toLowerCase());
		}
		return markersList;
	}
	
	private void parseDictonary(String fileName, SupportedLanguages language) throws LocalizationException
	{
		currentDictionary = new HashMap<Messages, String>();
		ArrayList<String> warnings = new ArrayList<String>();
		
		try 
		{
			InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = reader.readLine();
			
			while(line != null)
			{
				String[] splittedLine = line.split("=");
				String formattedMessage = splittedLine[0].trim().toUpperCase();
				if(Messages.isMessageValid(formattedMessage))
				{
					Messages message = Messages.valueOf(formattedMessage);
					if(!currentDictionary.containsKey(message))
					{
						String formattedTranslation = splittedLine[1].trim().toLowerCase();
						currentDictionary.put(message, formattedTranslation);
					}
					else
						warnings.add("WARNING: The key/message '" + formattedMessage + "' is duplicated in the file. Please, erase the duplicated keys.");
				}
				else
					warnings.add("WARNING: The key/message '" + formattedMessage + "' is not a valid entry. Please remove it from the dictionary file.");
				
				line = reader.readLine();
			}
			
			cachedDictionaries.put(language, currentDictionary);
		}
		catch (FileNotFoundException e) 
		{
			warnings.add("ERROR: A FileNotFoundException has ocurred, details: " + e.getMessage());
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			warnings.add("ERROR: An IOException has ocurred, details: " + e.getMessage());
			e.printStackTrace();
		}
		
		if(!warnings.isEmpty())
		{
			String formttedWarning = "There was some problems while trying to parse the dictionary (File name: " + fileName + ", Language: " + language + "): \n";
			for(String warning : warnings)
				formttedWarning += "- " + warning + "\n";
			
			throw new LocalizationException(formttedWarning);
		}
	}
}