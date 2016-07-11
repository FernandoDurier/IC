package portuguese.realizer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StopWordManager {
	private static StopWordManager instance;
	private final String STOP_WORDS_SOURCE = "corpus-stop-words.txt";
	private final List<String> stopWords = new ArrayList<String>();
	
	public static StopWordManager getInstance(){
		if(instance == null){
			instance = new StopWordManager();
		}
		
		return instance;
	}
	
	public boolean isStopWord(String word){
		boolean isStopWord = false;
		if(word != null && !word.trim().isEmpty()){
			isStopWord = (Collections.binarySearch(stopWords, word.toLowerCase()) >= 0); 
		}
		return isStopWord;
	}
	
	private StopWordManager(){
		InputStream is = ClassLoader.getSystemResourceAsStream(STOP_WORDS_SOURCE);
		DataInputStream in = new DataInputStream(is);
		BufferedReader br = null;
		try {	
			br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String stopWord;
			while((stopWord = br.readLine()) != null){
				if(stopWord != null && stopWord != "")
					stopWords.add(stopWord.trim().toLowerCase());
			}
			
			Collections.sort(stopWords);
		} catch (Exception e) {
			System.err.println(this.getClass().getName() + ": There was an error while trying to read the source resource for the StopWordsList.");
			e.printStackTrace();
		} finally {
			try{
				if(br != null) 
					br.close();
				in.close();
				is.close();
			} catch(Exception e){
				System.err.println("There was an error while trying to close the open source resources for the StopWordsList.");
				e.printStackTrace();
			}
		}
	}
}
