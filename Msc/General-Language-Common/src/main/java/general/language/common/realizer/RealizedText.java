package general.language.common.realizer;

import general.language.common.dsynt.DSynTSentence;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.process.ProcessElementType;
import general.language.common.process.ProcessGatewayProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RealizedText {
	private RealizedText originalText;
	private RealizedText fatherBranch;
	private String plainText;
	private String formattedText;
//	private List<SentenceText> sentences;
	private SentenceText firstSentence;
	private SentenceText currentSentence;
	private final ILabelHelper label;
	private final INaturalLanguageProcessor languageProcessor;
	
	
	public RealizedText(ILabelHelper label, INaturalLanguageProcessor languageProcessor, String formattedText){
		this.label = label;
		this.languageProcessor = languageProcessor;
		this.setText(formattedText);
		this.createSentenceList();
	}
	
	public RealizedText(ILabelHelper label, INaturalLanguageProcessor languageProcessor){
		this.languageProcessor = languageProcessor;
		this.label = label;
	}
	
	public void setText(String surfaceText){
		this.formattedText = surfaceText;
		this.plainText = cleanText(surfaceText);
	}
	
	public String getPlainText() {
		return plainText;
	}
	public String getFormattedText() {
		return formattedText;
	}
//	public List<SentenceText> getSentences() {
//		return sentences;
//	}
	
	public RealizedText getFatherBranch(){
		return fatherBranch;
	}
	
	public void setFatherBranch(RealizedText fatherBranch){
		this.fatherBranch = fatherBranch;
	}
	
	public RealizedText getOriginalText() {
		return originalText;
	}
	
	public SentenceText getFirstSentence(){
		return firstSentence;
	}

	public void setOriginalText(RealizedText originalText) {
		this.originalText = originalText;
	}
	
	public void addSentence(String realizedSentence, DSynTSentence dsyntSentence){
		SentenceText sentence = new SentenceText(realizedSentence, label, languageProcessor, dsyntSentence);
		if(firstSentence == null){
			currentSentence = firstSentence = sentence;
		} else {
			currentSentence.setNextSentence(sentence);
			currentSentence = sentence;
		}
	}
	
	public void addBranchSentences(RealizedText branchSentences){
		currentSentence.addMainBranchSentece(branchSentences);
	}
	
	public boolean isEquivalentTo(String plainText){
		String[] arraySentences = this.plainText.split(".");
		List<String> listSentences = new ArrayList<String>(Arrays.asList(arraySentences));
		
		boolean isEquivalent = true;
		for(String s : plainText.split(".")){
			if(!listSentences.contains(s)){
				isEquivalent = false;
				break;
			}
		}
		return isEquivalent;
	}
	
	public void finalizeGatewayConfiguration(){
		SentenceText sentence = firstSentence; 
		while(sentence != null){
			ProcessElementType elementType = sentence.getSentenceProperties().getElementType(); 
			if(ProcessElementType.GATEWAY_AND.equals(elementType)){
				ProcessGatewayProperty gatewayAnd = (ProcessGatewayProperty) sentence.getSentenceProperties();
				gatewayAnd.setMainBranchSenteces(sentence.getMainBranchSenteces());
			}
			sentence = sentence.getNextSentence();
		}
	}
	
	private String cleanText(String plainText){
		ITextFormatter textFormatter = new TemplateTextFormatter(label);
		String cleanPlainText = textFormatter.removeStopWords(plainText);
		cleanPlainText = textFormatter.removeFormatting(plainText);
		cleanPlainText = textFormatter.replaceReferringExpression(cleanPlainText);
		cleanPlainText = textFormatter.replaceSenteceAggregator(cleanPlainText);
		
		return cleanPlainText;
	}
	
	private void createSentenceList(){
		String[] arraySentences = this.plainText.split("\\.");
		List<SentenceText> sentences = new ArrayList<SentenceText>(arraySentences.length);
		SentenceText previousSentence = new SentenceText(arraySentences[0], null, this.label, this.languageProcessor);
		this.firstSentence = previousSentence;
		for(int i = 1 ; i < arraySentences.length ; i++){
			sentences.add(previousSentence);
			SentenceText currentSentence = new SentenceText(arraySentences[i], null, this.label, this.languageProcessor);
			previousSentence.setNextSentence(currentSentence);
			// move the pointer to the next sentence
			previousSentence = currentSentence;
		}
		
		// add the last sentence
		sentences.add(previousSentence);
	}
}
