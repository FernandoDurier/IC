package bpm2nlg;
import general.language.common.SupportedLanguages;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.LabelException;
import general.language.common.realizer.ISurfaceRealizer;
import general.language.common.realizer.RealizedText;
import general.language.common.rpst.RPST;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import net.didion.jwnl.JWNLException;
import bpm2nlg.sentence.planning.DiscourseMarker;
import bpm2nlg.sentence.planning.ReferringExpressionGenerator;
import bpm2nlg.sentence.planning.SentenceAggregator;
import bpm2nlg.text.planning.PlanningHelper;
import bpm2nlg.text.planning.TextPlanner;
import br.com.uniriotec.Machine.Artifact.IProcessModelFacade;
import br.com.uniriotec.Machine.Artifact.ProcessMachineArtifactFacade;
import br.com.uniriotec.Machine.Artifact.ProcessModelConverter;
import br.com.uniriotec.graph.process.GraphProcess;
import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.process.model.ProcessModel;

public class Main {
	
	private static ILabelHelper lHelper;
	private static ILabelDeriver lDeriver;
	private static IProcessModelFacade processModelFacade;
	private static final String ENGLISH_PM = "Hotel_Service_English.json";
	private static final String PORTUGUESE_PM = "Hotel_Service_Portuguese.json";
	
	private static void init(){
		processModelFacade = new ProcessMachineArtifactFacade();
	}
	
	/**
	 * Main function. 
	 */
	public static void main(String[] args) throws Exception{
		init();
		
		URL is = ClassLoader.getSystemResource(ENGLISH_PM);
		ProcessModel model = processModelFacade.readProcessModelFromJson(is.getPath());
		System.out.print(SupportedLanguages.valueOf(model.getLanguage().toUpperCase()));
		
		try  
		{
			// Set up language configuration and label parsing classes
			SupportedLanguages currentLanguage = SupportedLanguages.valueOf(model.getLanguage().toUpperCase());
			LanguageConfig.getInstance().setCurrentLanguage(currentLanguage);
		}
		catch(IllegalArgumentException argumentException)
		{
			//If some problem occurs, sets the language to English as default.  
			try{
				LanguageConfig.getInstance().setCurrentLanguage(SupportedLanguages.ENGLISH);
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		finally
		{
			lHelper = LanguageConfig.getInstance().getLabelHelper();
			lDeriver  = LanguageConfig.getInstance().getLabelDeriver(); 
			
			String surfaceText = toText(model);
			System.out.println(surfaceText);
		}
	}
	
	/**
	 *  Function for generating text from a model. The according process model must be provided to the function.
	 * @throws JWNLException 
	 */
	public static String toText(ProcessModel model) throws IOException, ClassNotFoundException, LabelException 
	{
		String imperativeRole = "Room-Service Manager"; 
		boolean imperative = false;
		
		// Annotate model
		PlanningHelper.annotateModel(model, lDeriver, lHelper);
		
		// Convert to RPST
		ProcessModelConverter pModelConverter = new ProcessModelConverter();
		GraphProcess p = pModelConverter.convertToRPSTFormat(model);
		RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst = new RPST<ProcessGraphControlFlow,ProcessGraphNode>(p);
		
		// Convert to Text
		TextPlanner converter = new TextPlanner(rpst, model, lDeriver, lHelper, imperativeRole, imperative, false);
		converter.convertToText(rpst.getRoot(), 0);
		ArrayList <DSynTSentence> sentencePlan = converter.getSentencePlan();
		
		// Aggregation
		SentenceAggregator sentenceAggregator = new SentenceAggregator();
		sentencePlan = sentenceAggregator.performRoleAggregation(sentencePlan, model);
		
		// Referring Expression
		ReferringExpressionGenerator refExpGenerator = new ReferringExpressionGenerator(lHelper);
		sentencePlan  = refExpGenerator.insertReferringExpressions(sentencePlan, model, false);
		
		// Discourse Marker 
		DiscourseMarker discourseMarker = new DiscourseMarker();
		sentencePlan = discourseMarker.insertSequenceConnectives(sentencePlan);
		
		// Realization
		ISurfaceRealizer surfaceRealizer = LanguageConfig.getInstance().getSurfaceRealizer();
		RealizedText realizedText = surfaceRealizer.realizePlan(sentencePlan);
		String surfaceText = realizedText.getFormattedText() + "\n\n";
		
		// Cleaning
		if (imperative == true) {
			surfaceText = surfaceRealizer.cleanTextForImperativeStyle(surfaceText, imperativeRole, model.getLanes());
		}
		
		return surfaceText;
	}
	
//	private static void debugDsynt(ArrayList <DSynTSentence> sentencePlan){
//		for(int i = 0 ; i < sentencePlan.size() ; i++){
//			ExecutableFragment fragmento = sentencePlan.get(i).getExecutableFragment();
//			String output = String.format("DSynT %d: \n\t- Role: %s\n\t- Action: %s \n\t- Bo: %s \n\t- Addition: %s", i+1, fragmento.getRole(), fragmento.getAction(), fragmento.getBo(), fragmento.getAddition());
//			System.out.println(output);
//		}
//	}
	
	
	/**
	 * Counts words in a String 
	 */
	public static int countWords(String Zeichenkette, char buchstabe)
	{
	   int counter =0;
	   for (int i=0;i<Zeichenkette.length();i++)
	   {
	        if (Zeichenkette.charAt(i) == buchstabe) counter++;
	   }

	return counter;
	}
}
