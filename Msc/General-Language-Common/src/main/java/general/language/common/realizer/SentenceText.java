package general.language.common.realizer;

import java.util.ArrayList;
import java.util.List;

import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.fragments.FragmentType;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.process.ProcessElementType;
import general.language.common.process.ProcessEventProperty;
import general.language.common.process.ProcessProperty;

public class SentenceText {
	private String plainSentence;
	private ProcessProperty processSentenceProperties;
	private ILabelHelper labelHelper;
	private SentenceText nextSentence;
	private List<RealizedText> mainBranchSenteces;
	private final INaturalLanguageProcessor languageProcessor;
	
	public SentenceText(String realizedSentence, ILabelHelper labelHelper, INaturalLanguageProcessor languageProcessor, DSynTSentence dsyntSentence){
		this.labelHelper = labelHelper;
		this.plainSentence = removeFormatting(realizedSentence);
		this.languageProcessor = languageProcessor;
		this.languageProcessor.configureLabelHelper(labelHelper);
		
		ExecutableFragment eFrag = dsyntSentence.getExecutableFragment();
		FragmentType dsyntType = eFrag.getFragmentType();
		
		ProcessElementType sentenceType = ProcessElementType.UNKNOW;
		if(FragmentType.AND_GATEWAY.equals(dsyntType)){
			sentenceType = ProcessElementType.GATEWAY_AND;
		} else {
			ITextFormatter textFormatter = new TemplateTextFormatter(labelHelper);
			this.plainSentence = textFormatter.removeBullets(this.plainSentence);
			sentenceType = languageProcessor.identifySentenceType(this.plainSentence);
		}
		
		extractProcessSemanticFromText(sentenceType);
		
		if(processSentenceProperties != null){
			int originalElementId = (eFrag.getAssociatedActivities().size() > 0) ? eFrag.getAssociatedActivities().get(0) : -1;
			processSentenceProperties.setOriginalProcessElementId(originalElementId);
		}
	}
	
	public SentenceText(String realizedSentence, SentenceText nextSentence, ILabelHelper labelHelper, INaturalLanguageProcessor languageProcessor){
		this.labelHelper = labelHelper;
		this.plainSentence = removeFormatting(realizedSentence);
		this.nextSentence = nextSentence;
		this.languageProcessor = languageProcessor;
		this.languageProcessor.configureLabelHelper(labelHelper);
		
		ProcessElementType sentenceType = languageProcessor.identifySentenceType(this.plainSentence);
		extractProcessSemanticFromText(sentenceType);
	}
	
	public List<RealizedText> getMainBranchSenteces() {
		return mainBranchSenteces;
	}
	
	public void addMainBranchSentece(RealizedText branchSentences) {
		if(mainBranchSenteces == null){
			mainBranchSenteces = new ArrayList<RealizedText>();
		}
		
		mainBranchSenteces.add(branchSentences);
	}
	
	public String getPlainSentence() {
		return plainSentence;
	}
	
	public SentenceText getNextSentence(){
		return nextSentence;
	}
	
	public ProcessProperty getSentenceProperties(){
		return processSentenceProperties;
	}
	
	public void setNextSentence(SentenceText nextSentence){
		this.nextSentence = nextSentence;
	}

	public String getTextSentence() {
		return plainSentence;
	}

	public void setTextSentence(String textSentence) {
		this.plainSentence = textSentence;
	}

	private String removeFormatting(String realizedSentence){
		ITextFormatter textFormatter = new TemplateTextFormatter(labelHelper);
		String unformattedSentence = textFormatter.removeStopWords(realizedSentence);
		unformattedSentence = textFormatter.removeFormatting(unformattedSentence);
		unformattedSentence = unformattedSentence.replaceAll("\\.", "");
		
		return unformattedSentence.trim();
	}
	
	private void extractProcessSemanticFromText(ProcessElementType sentenceType){
		if(ProcessElementType.ACTIVITY.equals(sentenceType)){
			this.processSentenceProperties = languageProcessor.extractActivityProperties(this.plainSentence);
		} else if(ProcessElementType.EVENT_BEGIN_WITH_ACTIVITY.equals(sentenceType)){
			ProcessEventProperty eventProperties = languageProcessor.extractEventProperties(this.plainSentence);
			String activity = eventProperties.getAddition();
			// Must update nextSentence
			SentenceText newNextSentence = new SentenceText(activity, nextSentence, labelHelper, languageProcessor);
			this.nextSentence = newNextSentence;
			this.processSentenceProperties = eventProperties;
		} else if(ProcessElementType.EVENT_FINISH.equals(sentenceType)){
			this.processSentenceProperties = languageProcessor.extractEventProperties(this.plainSentence);
		} else if(ProcessElementType.GATEWAY_AND.equals(sentenceType)){
			this.processSentenceProperties = languageProcessor.extractAndGatewayProperties(this.plainSentence);
		}
	}
}
