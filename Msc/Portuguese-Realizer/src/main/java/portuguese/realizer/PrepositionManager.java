package portuguese.realizer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PrepositionManager {
	private List<String> prepositions;
	private final String SOURCE = "corpus-preposicoes.txt";
	private static PrepositionManager instance;

	public PrepositionManager() {
		this.prepositions = new ArrayList<String>();
		loadPrepositions();
	}

	public static PrepositionManager getInstance() {
		if (instance == null)
			instance = new PrepositionManager();

		return instance;
	}

	public Collection<String> getAllPrepositions() {
		return prepositions;
	}

	public boolean isPreposition(String word) {
		boolean isPreposition = false;
		if (word != null) {
			String lowCaseWord = word.trim().toLowerCase();
			isPreposition = (Collections.binarySearch(prepositions, word) >= 0);
			if (!isPreposition) {
				String preposition = lowCaseWord.trim();
				preposition = Normalizer.normalize(lowCaseWord,
						Normalizer.Form.NFD);
				preposition = preposition.replaceAll("[^\\p{ASCII}]", "");
				isPreposition = (Collections.binarySearch(prepositions, word) >= 0);
			}
		}

		return isPreposition;
	}

	private void loadPrepositions() {
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream(SOURCE);
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String strLine;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				String preposition = Normalizer
						.normalize(strLine, Normalizer.Form.NFD);
				preposition = preposition.replaceAll("[^\\p{ASCII}]", "");
				preposition = strLine.trim().toLowerCase();
				this.prepositions.add(preposition);
			}

			in.close();
			Collections.sort(prepositions);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
