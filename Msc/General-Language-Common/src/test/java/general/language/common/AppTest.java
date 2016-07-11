package general.language.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class AppTest {

	@Test
	public void testa() {
		
		String originalText = "O processo comeca quando o Secretario recebe o pedido de compra. Em seguida, os seguintes caminhos são executados. O Gerente atualiza a planilha. O Secretario atualiza o cadastro. Em seguida, o gerente finaliza o pedido. Finalmente, o processo é terminado.";
		String currentText = "O processo comeca quando o Gerente servico de quarto recebe o pedido. Entao, o processo é dividido em 3 ramificacoes paralelas. O Gerente servico de quarto entrega o pedido para o barman. Em seguida, os seguintes caminhos são executados. O Barman pega o vinho da adega. O Barman prepara as bebida alcoolica. O Gerente servico de quarto entrega o pedido para o garcom. Subsequentemente, o garcom prepara a nota. O Gerente servico de quarto submete o pedido para a cozinha. Entao, a cozinha prepara a comida. O Garcom entrega a para o quarto do hospede. Em seguida, o garcom retorna a para o servico de quarto. Subsequentemente, o garcom debita a da conta do hospede. Finalmente, o processo é terminado.";
		
		String originalTextWithoutWhiteSpaces = originalText.replaceAll("\\s+", " ");
		String currentTextWithoutWhiteSpaces = currentText.replaceAll("\\s+", " ");
		
		if(!originalTextWithoutWhiteSpaces.equalsIgnoreCase(currentTextWithoutWhiteSpaces)){
			String[] originalSentences = originalTextWithoutWhiteSpaces.split("\\.");
			String[] currentSentences = currentTextWithoutWhiteSpaces.split("\\.");
			
			LinkedList<String> originalSentencesList = new LinkedList<String>(Arrays.asList(originalSentences));
			LinkedList<String> currentSentencesList = new LinkedList<String>(Arrays.asList(currentSentences));
			
			List<SentenceDiff> diffs = processRemovals(originalSentencesList, currentSentencesList);
			diffs.addAll(
					processInserts(originalSentencesList, currentSentencesList));
			
			for(SentenceDiff diff : diffs){
				System.out.println(diff);
			}
			
			for(int i = diffs.size() -1 ; i >= 0 ; i--){
				SentenceDiff diff = diffs.get(i);
				if(diff.getOperationType().equals(SentenceOperationType.DELETE)){
					originalSentencesList.remove(diff.getOperationIndexInOriginalText());
					diff.updateDeleteDiffOperationIndex(diffs, diff.getOperationType());
					diffs.remove(i);
				} 
			}
			
			for(SentenceDiff diff : diffs){
				if(diff.getOperationType().equals(SentenceOperationType.INSERT)){
					try{
						originalSentencesList.add(diff.getOperationIndexInOriginalText(), diff.getDiffSentence());
					} catch(Exception e){
						System.out.println("Error!");
					}
					
					diff.updateInsertDiffOperationIndex(diffs, diff.getOperationType(), originalSentencesList.size());
				}
			}
			
			System.out.println(" ================================ Frases apos realizacao do Merge: ================================ ");
			System.out.println(originalSentencesList.toString());
		}
	}
	
	private List<SentenceDiff> processInserts(List<String> original, List<String> current){
		List<SentenceDiff> diffs = new ArrayList<SentenceDiff>();
		
		for(int index = 0 ; index < current.size() ; index++){
			// Verify if the new sentence exists within the old text
			String currentSentence = current.get(index);
			if(!original.contains(currentSentence)){
				// If the sentence does not exists, then it was inserted
				SentenceDiff diff = new SentenceDiff(currentSentence, index, SentenceOperationType.INSERT);
				diffs.add(diff);
			}
		}
		
		return diffs;
	}
	
	private List<SentenceDiff> processRemovals(List<String> original, List<String> current){
		List<SentenceDiff> diffs = new ArrayList<SentenceDiff>();
		
		for(int index = 0 ; index < original.size() ; index++){
			// Verify if the old sentence exists within the new text
			String originalSentence = original.get(index);
			if(!current.contains(originalSentence)){
				// If the sentence does not exists, then it was removed
				SentenceDiff diff = new SentenceDiff(null, index, SentenceOperationType.DELETE);
				diffs.add(diff);
			}
		}
		
		return diffs;
	}
	
	private class SentenceDiff{
		private String diffSentence;
		private int operationIndexInOriginalText;
		private SentenceOperationType operationType;
		
		public SentenceDiff(String diffSentence, int operationIndex, 
				SentenceOperationType operationType){
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
		
		public void setOperationIndexInOriginalText(int newIndex){
			this.operationIndexInOriginalText = newIndex;
		}

		public SentenceOperationType getOperationType() {
			return operationType;
		}
		
		public String toString(){
			String outputPattern = "Sentence '%s' was '%s' comparing to the original text. The corret position to perform the operation in the original text is '%d'.";
			return String.format(outputPattern, diffSentence, operationType, operationIndexInOriginalText);
		}
		
		public void updateDeleteDiffOperationIndex(List<SentenceDiff> diffs, SentenceOperationType operationType){
			int limitIndex = 0;
			int changeIndexValue = -1;
			updateDiffOperationIndex(diffs, limitIndex, changeIndexValue);
		}
		
		public void updateInsertDiffOperationIndex(List<SentenceDiff> diffs, SentenceOperationType operationType, int sentenceSize){
			int changeIndexValue = 1;
			updateDiffOperationIndex(diffs, sentenceSize - 1, changeIndexValue);
		}
		
		private void updateDiffOperationIndex(List<SentenceDiff> diffs, int limitIndex, int changeIndexValue){
			for(SentenceDiff diff : diffs){
				int currentIndex = diff.getOperationIndexInOriginalText();
				if(currentIndex != limitIndex){
					diff.setOperationIndexInOriginalText(currentIndex + changeIndexValue);
				}
			}
		}
	}
	
	private enum SentenceOperationType{
		DELETE,
		INSERT
	}
}

