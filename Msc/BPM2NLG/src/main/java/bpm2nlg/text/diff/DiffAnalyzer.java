package bpm2nlg.text.diff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DiffAnalyzer {
	
//	private static DiffAnalyzer instance;
	
//	public static DiffAnalyzer getInstance(){
//		if(instance == null){
//			instance = new DiffAnalyzer();
//		}
//		
//		return instance;
//	}
	
	private final List<SentenceDiff> differences;
	
	public DiffAnalyzer(){
		this.differences = new ArrayList<SentenceDiff>();
	}
	
	public List<SentenceDiff> getDifferences(){
		return this.differences;
	}
	
	public String mergeText(String originalText, String currentText) {
//		String originalText = "O processo comeca quando o Secretario recebe o pedido de compra. Em seguida, os seguintes caminhos são executados. O Gerente atualiza a planilha. O Secretario atualiza o cadastro. Em seguida, o gerente finaliza o pedido. Finalmente, o processo é terminado.";
//		String currentText = "O processo comeca quando o Gerente servico de quarto recebe o pedido. Entao, o processo é dividido em 3 ramificacoes paralelas. O Gerente servico de quarto entrega o pedido para o barman. Em seguida, os seguintes caminhos são executados. O Barman pega o vinho da adega. O Barman prepara as bebida alcoolica. O Gerente servico de quarto entrega o pedido para o garcom. Subsequentemente, o garcom prepara a nota. O Gerente servico de quarto submete o pedido para a cozinha. Entao, a cozinha prepara a comida. O Garcom entrega a para o quarto do hospede. Em seguida, o garcom retorna a para o servico de quarto. Subsequentemente, o garcom debita a da conta do hospede. Finalmente, o processo é terminado.";

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
				differences.add(diff);
			}

			for (int i = diffs.size() - 1; i >= 0; i--) {
				SentenceDiff diff = diffs.get(i);
				if (diff.getOperationType()
						.equals(SentenceOperationType.DELETE)) {
					try {
						int index = originalSentencesList.indexOf(diff
								.getDiffSentence());
						originalSentencesList.remove(index);
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
				}
			}

			System.out
					.println(" ================================ Frases apos realizacao do Merge: ================================ ");
			System.out.println(originalSentencesList.toString());
		}
		
		return "";
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
}
