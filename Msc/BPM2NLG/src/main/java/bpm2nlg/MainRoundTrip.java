package bpm2nlg;

import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import general.language.common.process.ProcessActivityProperty;
import general.language.common.realizer.RealizedText;
import general.language.common.realizer.SentenceText;
import nlg2bpm.ITestRoundTrip;
import br.com.uniriotec.json.strucuture.Doc;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.ProcessModel;

public class MainRoundTrip implements ITestRoundTrip {

	public void runRoundTrip(RealizedText realizedText, ProcessModel process) {
		testActivityUpdate(realizedText, process);
		testActivityRemoval(realizedText, process);
	}
	
	private void testActivityUpdate(RealizedText realizedText, ProcessModel process){
		SentenceText sentence = realizedText.getFirstSentence().getNextSentence();
		ProcessActivityProperty activityProperties = (ProcessActivityProperty) sentence.getSentenceProperties();
		String oldAction = activityProperties.getBusinessAction();
		String newAction = "Fazer";
		//sentence.updateActivity(newAction, activityProperties.getBusinessActor(), activityProperties.getBusinessObject(), activityProperties.getBusinessAddition());
		String plainSentence = sentence.getPlainSentence();
		String updatedAction = activityProperties.getBusinessAction();
		System.out.println(plainSentence);
		System.out.println(updatedAction);
		
		int activityId = activityProperties.getOriginalProcessElementId();
		Activity activity = process.getActivity(activityId);
		String activityDescription = activity.getLabel();
		System.out.println(activityDescription);
		
		activityDescription = activityDescription.toLowerCase().replace(oldAction, newAction);
		System.out.println(activityDescription);
		
		activity.setLabel(activityDescription);
		System.out.println(activity.getLabel());
		
		try {
			process.updateActivity(activity);
			testConvertToJson(process.toDocumentModel(), "activity-updated-Process.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testActivityRemoval(RealizedText realizedText, ProcessModel process){
		SentenceText sentence = realizedText.getFirstSentence().getNextSentence().getNextSentence();
		ProcessActivityProperty activityProperties = (ProcessActivityProperty) sentence.getSentenceProperties();
		int activityId = activityProperties.getOriginalProcessElementId();
		
		try {
			process.removeActivity(activityId);
			testConvertToJson(process.toDocumentModel(), "activity-removed-Process.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testConvertToJson(Doc modelDoc, String fileName){
		Gson gson = new Gson();
		String json = gson.toJson(modelDoc);
		
		try {
			FileWriter writer = new FileWriter("C:\\Users\\Raphael\\Documents\\Round-trip\\" + fileName);
			writer.write(json);
			writer.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
		System.out.println(json);
	}
}
