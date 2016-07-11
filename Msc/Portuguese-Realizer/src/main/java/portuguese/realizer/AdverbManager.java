package portuguese.realizer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import portuguese.realizer.domain.Adverb;

public class AdverbManager 
{
	private List<Adverb> adverbs;
	private final String SOURCE = "Adverbios.txt";
	private static AdverbManager instance;
	
	public AdverbManager() 
	{
		loadAdverbs();
	}
	
	public static AdverbManager getInstance()
	{
		if(instance == null)
			instance = new AdverbManager();
		
		return instance;
	}

	private void loadAdverbs() 
	{
		try 
		{	
			FileInputStream fstream = new FileInputStream(SOURCE);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String strLine;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				String[] splitLine = strLine.split("=");
				String adverbType = splitLine[0].toLowerCase();
				String[] adverbs = strLine.split(";");
				
				for(String adv : adverbs)
				{
					Adverb adverb = new Adverb(adverbType, adv.trim().toLowerCase());
					this.adverbs.add(adverb);
				}
			}
			
			in.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getAdverbType(String adverb) 
	{
		String retVal = null;
		
		for(Adverb advInstance : adverbs)
			if(advInstance.getValue() == adverb.toLowerCase())
			{
				retVal = advInstance.getType();
				break;
			}
		
		return retVal;
	}
	
	public List<String> getAdverbsByType(String adverbType) 
	{
		List<String> adverbList = new LinkedList<String>();
		adverbType = adverbType.toLowerCase();
		
		for(Adverb adv : adverbs)
			if(adv.getType() == adverbType)
				adverbList.add(adv.getValue());
		
		return adverbList;
	}
}
