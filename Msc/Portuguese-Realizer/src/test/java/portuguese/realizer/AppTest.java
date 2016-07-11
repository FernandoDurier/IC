package portuguese.realizer;

import static org.junit.Assert.fail;
import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.ISurfaceRealizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CRLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import portuguese.realizer.label.analysis.PortugueseLabelDeriver;
import portuguese.realizer.label.analysis.PortugueseLabelHelper;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class AppTest{
	ISurfaceRealizer realizer;
	ILabelDeriver deriver;
	ILabelHelper helper;
	INaturalLanguageProcessor processor;
	
	@Before
	public void initializeTest(){
		processor = new PortugueseLanguageProcessor();
		helper = new PortugueseLabelHelper();
		deriver = new PortugueseLabelDeriver(helper);
		realizer = new PortugueseSurfaceRealizer(helper, processor);
	}
	
	@Test
	public void testa() throws CRLException {

		String originalText = "O processo comeca quando o Secretario recebe o pedido de compra. Em seguida, os seguintes caminhos são executados. O Gerente atualiza a planilha. O Secretario atualiza o cadastro. Em seguida, o gerente finaliza o pedido. Finalmente, o processo é terminado.";
		String currentText = "O processo comeca quando o Gerente servico de quarto recebe o pedido. Entao, o processo é dividido em 3 ramificacoes paralelas. O Gerente servico de quarto entrega o pedido para o barman. Em seguida, os seguintes caminhos são executados. O Barman pega o vinho da adega. O Barman prepara as bebida alcoolica. O Gerente servico de quarto entrega o pedido para o garcom. Subsequentemente, o garcom prepara a nota. O Gerente servico de quarto submete o pedido para a cozinha. Entao, a cozinha prepara a comida. O Garcom entrega a para o quarto do hospede. Em seguida, o garcom retorna a para o servico de quarto. Subsequentemente, o garcom debita a da conta do hospede. Finalmente, o processo é terminado.";

		String originalTextWithoutWhiteSpaces = originalText.replaceAll("\\s+",
				" ");
		String currentTextWithoutWhiteSpaces = currentText.replaceAll("\\s+",
				" ");

		if (!originalTextWithoutWhiteSpaces
				.equalsIgnoreCase(currentTextWithoutWhiteSpaces)) {
			String[] originalSentences = originalTextWithoutWhiteSpaces
					.split("\\.");
			String[] currentSentences = currentTextWithoutWhiteSpaces
					.split("\\.");

			LinkedList<String> originalSentencesList = new LinkedList<String>(
					Arrays.asList(originalSentences));
			LinkedList<String> currentSentencesList = new LinkedList<String>(
					Arrays.asList(currentSentences));

			List<SentenceDiff> diffs = processRemovals(originalSentencesList,
					currentSentencesList);
			diffs.addAll(processInserts(originalSentencesList,
					currentSentencesList));

			for (SentenceDiff diff : diffs) {
				System.out.println(diff);
			}

			for (int i = diffs.size() - 1; i >= 0; i--) {
				SentenceDiff diff = diffs.get(i);
				if (diff.getOperationType()
						.equals(SentenceOperationType.DELETE)) {
					try {
						int index = originalSentencesList.indexOf(diff
								.getDiffSentence());
						originalSentencesList.remove(index);
						// diff.updateDeleteDiffOperationIndex(diffs,
						// diff.getOperationType());
						diffs.remove(i);
					} catch (Exception e) {
						System.out.println("Error!");
						e.printStackTrace();
					}
				}
			}

			for (SentenceDiff diff : diffs) {
				if (diff.getOperationType()
						.equals(SentenceOperationType.INSERT)) {
					try {
						int insertIndex = (diff
								.getOperationIndexInOriginalText() > originalSentencesList
								.size() ? originalSentencesList.size() : diff
								.getOperationIndexInOriginalText());
						originalSentencesList.add(insertIndex,
								diff.getDiffSentence());
					} catch (Exception e) {
						System.out.println("Error!");
						e.printStackTrace();
					}

					// diff.updateInsertDiffOperationIndex(diffs,
					// diff.getOperationType(), originalSentencesList.size());
				}
			}

			System.out
					.println(" ================================ Frases apos realizacao do Merge: ================================ ");
			System.out.println(originalSentencesList.toString());
		}
	}

	private List<SentenceDiff> processInserts(List<String> original,
			List<String> current) {
		List<SentenceDiff> diffs = new ArrayList<SentenceDiff>();

		for (int index = 0; index < current.size(); index++) {
			// Verify if the new sentence exists within the old text
			String currentSentence = current.get(index);
			if (!original.contains(currentSentence)) {
				// If the sentence does not exists, then it was inserted
				SentenceDiff diff = new SentenceDiff(currentSentence, index,
						SentenceOperationType.INSERT);
				diffs.add(diff);
			}
		}

		return diffs;
	}

	private List<SentenceDiff> processRemovals(List<String> original,
			List<String> current) {
		List<SentenceDiff> diffs = new ArrayList<SentenceDiff>();

		for (int index = 0; index < original.size(); index++) {
			// Verify if the old sentence exists within the new text
			String originalSentence = original.get(index);
			if (!current.contains(originalSentence)) {
				// If the sentence does not exists, then it was removed
				SentenceDiff diff = new SentenceDiff(originalSentence, index,
						SentenceOperationType.DELETE);
				diffs.add(diff);
			}
		}

		return diffs;
	}

	private class SentenceDiff {
		private String diffSentence;
		private int operationIndexInOriginalText;
		private SentenceOperationType operationType;

		public SentenceDiff(String diffSentence, int operationIndex,
				SentenceOperationType operationType) {
			this.diffSentence = diffSentence;
			this.operationIndexInOriginalText = operationIndex;
			this.operationType = operationType;
		}

		public String getDiffSentence() {
			return diffSentence;
		}

		public int getOperationIndexInOriginalText() {
			return operationIndexInOriginalText;
		}

		public void setOperationIndexInOriginalText(int newIndex) {
			this.operationIndexInOriginalText = newIndex;
		}

		public SentenceOperationType getOperationType() {
			return operationType;
		}

		public String toString() {
			String outputPattern = "Sentence '%s' was '%s' comparing to the original text. The corret position to perform the operation in the original text is '%d'.";
			return String.format(outputPattern, diffSentence, operationType,
					operationIndexInOriginalText);
		}

		public void updateDeleteDiffOperationIndex(List<SentenceDiff> diffs,
				SentenceOperationType operationType) {
			int limitIndex = 0;
			int changeIndexValue = -1;
			updateDiffOperationIndex(diffs, limitIndex, changeIndexValue);
		}

		public void updateInsertDiffOperationIndex(List<SentenceDiff> diffs,
				SentenceOperationType operationType, int sentenceSize) {
			int changeIndexValue = 1;
			updateDiffOperationIndex(diffs, sentenceSize - 1, changeIndexValue);
		}

		private void updateDiffOperationIndex(List<SentenceDiff> diffs,
				int limitIndex, int changeIndexValue) {
			for (SentenceDiff diff : diffs) {
				int currentIndex = diff.getOperationIndexInOriginalText();
				if (currentIndex != limitIndex) {
					diff.setOperationIndexInOriginalText(currentIndex
							+ changeIndexValue);
				}
			}
		}
	}

	private enum SentenceOperationType {
		DELETE, INSERT
	}
	
	@Test
	public void testaAPIJena(){
		Model m = ModelFactory.createDefaultModel();
		try{
			String pathToFile = ClassLoader.getSystemClassLoader().getResource("Copia.rdfs").getPath();
			
			// create an empty model
			 Model model = ModelFactory.createDefaultModel();

			 // use the FileManager to find the input file
			 InputStream in = FileManager.get().open( pathToFile );
			if (in == null) {
			    throw new IllegalArgumentException("File: " + pathToFile + " not found");
			}

			// read the RDF/XML file
			model.read(in, null);
			System.out.println(model.isEmpty());
			System.out.println(model.size());
			
			Property rdfLabel = m.createProperty("http://www.w3.org/2000/01/rdf-schema#", "label");
			
			ResIterator iter = model.listSubjectsWithProperty(rdfLabel);
			while(iter.hasNext()){
				Resource res = iter.nextResource();
				StmtIterator propriedades = res.listProperties();
				while(propriedades.hasNext()){
					Statement state = propriedades.nextStatement();
					System.out.println(state.toString());
				}
			}
			
			List<RDFNode> elementos = model.listObjects().toList();
			for(RDFNode item : elementos){
				System.out.println(item.toString());
			}
			
			// write it to standard out
			model.write(System.out);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testApiToIgnoreAccents(){
		Collator collator = Collator.getInstance (new Locale ("pt", "BR"));  
	    collator.setStrength(Collator.PRIMARY); // importante!  
        if (collator.compare ("João", "Joao") == 0) {  
            System.out.println ("As duas Giseles sao a mesma pessoa, sa diferem pelos acentos");  
        }
	}
	
	@Test
	public void loadAllVerbsTest(){
		try{
			InputStreamReader is = new InputStreamReader(
					ClassLoader.getSystemResourceAsStream("corpus-verbos.txt"), "UTF-8");
			BufferedReader br = new BufferedReader(is);
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				if(!strLine.trim().isEmpty())
					System.out.println(strLine);
			}
		} catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}

}
