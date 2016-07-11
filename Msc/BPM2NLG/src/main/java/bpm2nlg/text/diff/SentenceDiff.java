package bpm2nlg.text.diff;

public class SentenceDiff {
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

//	public void updateDeleteDiffOperationIndex(List<SentenceDiff> diffs,
//			SentenceOperationType operationType) {
//		int limitIndex = 0;
//		int changeIndexValue = -1;
//		updateDiffOperationIndex(diffs, limitIndex, changeIndexValue);
//	}
//
//	public void updateInsertDiffOperationIndex(List<SentenceDiff> diffs,
//			SentenceOperationType operationType, int sentenceSize) {
//		int changeIndexValue = 1;
//		updateDiffOperationIndex(diffs, sentenceSize - 1, changeIndexValue);
//	}
//
//	private void updateDiffOperationIndex(List<SentenceDiff> diffs,
//			int limitIndex, int changeIndexValue) {
//		for (SentenceDiff diff : diffs) {
//			int currentIndex = diff.getOperationIndexInOriginalText();
//			if (currentIndex != limitIndex) {
//				diff.setOperationIndexInOriginalText(currentIndex
//						+ changeIndexValue);
//			}
//		}
//	}
}
