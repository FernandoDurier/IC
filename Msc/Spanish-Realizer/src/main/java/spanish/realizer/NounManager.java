package spanish.realizer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeMap;

public class NounManager
{
	private TreeMap<String, String> genderMap;
	private TreeMap<String, String> pluralSingularMap;

	private final String GENDER_MAP_SOURCE = "Substantivos.txt";
	private final String PLURAL_SINGULAR_MAP_SOURCE = "Mapeamento_PluralSingular.txt";

	private static NounManager instance;

	private NounManager()
	{
		genderMap = new TreeMap<String, String>();
		pluralSingularMap = new TreeMap<String, String>();

		loadDataFromFile(GENDER_MAP_SOURCE);
		loadDataFromFile(PLURAL_SINGULAR_MAP_SOURCE);
	}

	public static NounManager getInstance()
	{
		if(instance == null)
			instance = new NounManager();

		return instance;
	}

	public void addNounsToGenderMap(ArrayList<String> nounsToBeAdded)
	{
		for(String noun : nounsToBeAdded)
		{
			String transformedNoun = transformToSingularForm(noun);
			int gender = getGender(transformedNoun, false);
			if(gender == LanguageData.GENDER_FEM)
				genderMap.put(transformedNoun, "f");
			else
				genderMap.put(transformedNoun, "m");
		}
	}

	public boolean isPlural(String nounToBeChecked)
	{
		boolean isPluralNoun = false;
		for(String pluralSuffix :  pluralSingularMap.keySet())
			if(nounToBeChecked.endsWith(pluralSuffix))
			{
				isPluralNoun = true;
				break;
			}

		return isPluralNoun;
	}

	public String transformToSingularForm(String pluralNoun)
	{
		for(String pluralSuffix :  pluralSingularMap.keySet())
			if(pluralNoun.endsWith(pluralSuffix))
			{
				//TODO: Improve logic -> If some character sequence equal to the plural suffix appear in the middle of the sentence
				//it will be replaced, not the last one as it should be.
				pluralNoun = pluralNoun.replace(pluralSuffix, pluralSingularMap.get(pluralSuffix));
				break;
			}
		
		return pluralNoun;
	}

	private void loadDataFromFile(String fileName)
	{
		try
		{
			InputStream is = ClassLoader.getSystemResourceAsStream(fileName);
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String strLine;

			while ((strLine = br.readLine()) != null)
			{
				if(fileName.equalsIgnoreCase(GENDER_MAP_SOURCE))
				{
					String[] splitLine = strLine.split(",");
					for (String noun : splitLine)
						genderMap.put(noun.substring(1).toLowerCase(), Character.toString(noun.charAt(0)));
				}
				else
				{
					String[] splitLine = strLine.split("\t");
					pluralSingularMap.put(splitLine[1].trim(), splitLine[0].trim());
				}
			}

			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Determines article of noun based on gender and case.
	 */
	public String getArticle(String noun, boolean isObject, boolean isIndef, boolean isPlural)
	{
		String article = "";

		int articleIndex = getGender(noun, isPlural);

		if(isPlural)
			articleIndex++;

		article = (isObject == true && isIndef) ? LanguageData.indefArticles[articleIndex] : LanguageData.defArticles[articleIndex];

		return article;
	}

	/**
	 * Determines gender of the given noun, which can be Feminine or masculine for Spanish.
	 * Return -1 if some problem is detected.
	 * @param noun The noun to be checked.
	 * @param isPlural A Flag stating if the noun is in its plural form or not.
	 * @return 0 if is masculine, 2 if is feminine and -1 if some problem occur.
	 */
	public int getGender(String noun, boolean isPlural)
	{
		int gender = LanguageData.GENDER_FEM;
		String tempNoun = noun.toLowerCase();

		//Verify if the noun is composed by others nouns, if true, the only one that matters for obtaining the gender is the first one.
		if (tempNoun.contains(" "))
			tempNoun = tempNoun.split(" ")[0];

		if(isPlural)
			tempNoun = transformToSingularForm(tempNoun);

		if(genderMap.containsKey(tempNoun))
			gender = (genderMap.get(tempNoun).equals("f")) ? LanguageData.GENDER_FEM : LanguageData.GENDER_MAS;
		else
		{
			for (String femSuffix: LanguageData.feminineSuffixes)
				if (tempNoun.endsWith(femSuffix))
					gender = LanguageData.GENDER_FEM;

			for (String masSuffix: LanguageData.masculineSuffixes)
				if (tempNoun.endsWith(masSuffix))
					gender = LanguageData.GENDER_MAS;
		}

		return gender;
	}
}
