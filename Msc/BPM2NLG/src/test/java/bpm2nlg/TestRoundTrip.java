package bpm2nlg;

import general.language.common.label.analysis.ILabelHelper;
import general.language.common.process.ProcessActivityProperty;
import general.language.common.process.ProcessElementType;
import general.language.common.process.ProcessProperty;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.RealizedText;
import general.language.common.realizer.SentenceText;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import nlg2bpm.ITestRoundTrip;
import bpm2nlg.text.diff.DiffAnalyzer;
import bpm2nlg.text.diff.SentenceDiff;
import bpm2nlg.text.diff.SentenceOperationType;
import br.com.uniriotec.json.strucuture.Doc;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.ProcessModel;

import com.google.gson.Gson;

public class TestRoundTrip implements ITestRoundTrip {

	public void runRoundTrip(RealizedText realizedText, ProcessModel process) {
		runUpdateTest(realizedText, process);
		SentenceText currentSentence = realizedText.getFirstSentence();
		while(currentSentence != null){
			ProcessElementType elementType = ProcessElementType.UNKNOW;
			ProcessProperty properties = currentSentence.getSentenceProperties();
			if(properties != null){
				elementType = properties.getElementType();
			}
			System.out.println(elementType);
			if(currentSentence.getMainBranchSenteces() != null){
				for(RealizedText text : currentSentence.getMainBranchSenteces()){
					SentenceText sText = text.getFirstSentence();
					while(sText != null){
						ProcessElementType elementType2 = ProcessElementType.UNKNOW;
						ProcessProperty properties2 = sText.getSentenceProperties();
						if(properties2 != null){
							elementType2 = properties2.getElementType();
						}
						System.out.println(elementType2);
						sText = sText.getNextSentence();
					}
				}
			}
			currentSentence = currentSentence.getNextSentence();
		}
	}
	
	private void runUpdateTest(RealizedText realizedText, ProcessModel process){
		String originalText = realizedText.getFormattedText();
		String currentText = originalText.replace("Entao, o aluno realiza a prova.", "Entao, o aluno verifica a prova. Entao, o aluno realiza a prova. ");
		DiffAnalyzer analyzer = new DiffAnalyzer();
		analyzer.mergeText(originalText, currentText);
		
		ILabelHelper lHelper = LanguageConfig.getInstance().getLabelHelper();
		INaturalLanguageProcessor processor = LanguageConfig.getInstance().getLanguageProcessor();
		RealizedText processTextDescription = new RealizedText(lHelper, processor, currentText);
		List<SentenceDiff> diffs = analyzer.getDifferences();
		for(SentenceDiff diff : diffs){
			if(diff.getOperationType().equals(SentenceOperationType.INSERT)){
				int addIndex = diff.getOperationIndexInOriginalText();
				SentenceText newSentenceFound = processTextDescription.getFirstSentence();
				SentenceText originalSentenceFound = realizedText.getFirstSentence();
				for(int i = 0 ; i < addIndex ; i++){ // addIndex-1
					newSentenceFound = newSentenceFound.getNextSentence();
					originalSentenceFound = originalSentenceFound.getNextSentence();
				}
				
				ProcessActivityProperty activityProperties = (ProcessActivityProperty) originalSentenceFound.getSentenceProperties();
				insertActivity(process, activityProperties, newSentenceFound.getNextSentence());
			}
		}
	}
	
	private void insertActivity(ProcessModel process, ProcessActivityProperty originalActivityProperties, SentenceText updatedSentence){
		ProcessActivityProperty updatedActivity = (ProcessActivityProperty)updatedSentence.getSentenceProperties();
		
		int activityId = originalActivityProperties.getOriginalProcessElementId();
		Activity activity = process.getActivity(activityId);
		
		try {
			process.addActivity(updatedActivity, activity);
			testConvertToJson(process.toDocumentModel(), "activity-updated-Process.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private void testActivityUpdate(RealizedText realizedText, ProcessModel process){
//		// TODO Auto-generated method stub
//		SentenceText sentence = realizedText.getSentences().get(1);
//		String oldAction = sentence.getBussinessAction();
//		String newAction = "fazer";
//		sentence.updateActivity(newAction, sentence.getBusinessActor(), sentence.getBusinessObject(), sentence.getAddition());
//		String plainSentence = sentence.getPlainSentence();
//		String updatedAction = sentence.getDsyntSentence().getExecutableFragment().getAction();
//		System.out.println(plainSentence);
//		System.out.println(updatedAction);
//		 

//		String activityDescription = activity.getLabel();
//		System.out.println(activityDescription);
//		
//		activityDescription = activityDescription.toLowerCase().replace(oldAction, newAction);
//		System.out.println(activityDescription);
//		
//		activity.setLabel(activityDescription);
//		System.out.println(activity.getLabel());
//		

//	}
//	
//	private void testActivityRemoval(RealizedText realizedText, ProcessModel process){
//		SentenceText sentence = realizedText.getSentences().get(1);
//		int activityId = sentence.getDsyntSentence().getExecutableFragment().getAssociatedActivities().get(0);
//		
//		try {
//			process.removeActivity(activityId);
//			testConvertToJson(process.toDocumentModel(), "activity-removed-Process.json");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
	private void testConvertToJson(Doc modelDoc, String fileName){
		Gson gson = new Gson();
		String json = gson.toJson(modelDoc);
		
		try {
			//write converted json data to a file named "file.json"
			FileWriter writer = new FileWriter("C:\\Users\\Raphael\\" + fileName);
			writer.write(json);
			writer.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
		System.out.println(json);
	}
}
