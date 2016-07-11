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
import java.util.ArrayList;

import net.didion.jwnl.JWNLException;
import nlg2bpm.ITestRoundTrip;
import bpm2nlg.sentence.planning.DiscourseMarker;
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

public class BpmnlgMain {
	
	private ILabelHelper lHelper;
	private ILabelDeriver lDeriver;
	private IProcessModelFacade processModelFacade;
	private ITestRoundTrip concreteRoundTripTest;
	
	public BpmnlgMain(){
		processModelFacade = new ProcessMachineArtifactFacade();
		//this.concreteRoundTripTest = new ConcreteRoundTripTest();
		this.concreteRoundTripTest = new MainRoundTrip();
	}
	
	/***
	 * Constructor created for test purpose only! 
	 * Do not use for real execution.
	 * @param concreteRoundTripTest test implementation
	 */
	@Deprecated
	public BpmnlgMain(ITestRoundTrip concreteRoundTripTest){
		this.concreteRoundTripTest = concreteRoundTripTest;
		processModelFacade = new ProcessMachineArtifactFacade();
	}
	
	public RealizedText convertToModelToText(String modelPath) throws Exception{
		RealizedText convertedModel = null;
		
		// Load and generate from JSON files in directory
		ProcessModel model = processModelFacade.readProcessModelFromJson(modelPath);
		System.out.println("Lingua detectada: " + SupportedLanguages.valueOf(model.getLanguage().toUpperCase()));
		
		try  
		{
			// Set up language configuration and label parsing classes
			SupportedLanguages currentLanguage = SupportedLanguages.valueOf(model.getLanguage().toUpperCase());
			LanguageConfig.getInstance().setCurrentLanguage(currentLanguage);
		}
		catch(IllegalArgumentException argumentException)
		{
			//If some problem occurs, sets the language to English as default.  
			LanguageConfig.getInstance().setCurrentLanguage(SupportedLanguages.ENGLISH);
		}
		finally
		{
			lHelper = LanguageConfig.getInstance().getLabelHelper();
			lDeriver  = LanguageConfig.getInstance().getLabelDeriver(); 
			
			convertedModel = toText(model);
		}
		
		return convertedModel;
	}
	
	/**
	 *  Function for generating text from a model. The according process model must be provided to the function.
	 * @throws JWNLException 
	 */
	private RealizedText toText(ProcessModel model) throws IOException, ClassNotFoundException, LabelException 
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
//		ReferringExpressionGenerator refExpGenerator = new ReferringExpressionGenerator(lHelper);
//		sentencePlan  = refExpGenerator.insertReferringExpressions(sentencePlan, model, false);
		
		// Discourse Marker 
		DiscourseMarker discourseMarker = new DiscourseMarker();
		sentencePlan = discourseMarker.insertSequenceConnectives(sentencePlan);
		
		// Realization
		ISurfaceRealizer surfaceRealizer = LanguageConfig.getInstance().getSurfaceRealizer();
		RealizedText realizedText = surfaceRealizer.realizePlan(sentencePlan);
		
		// Cleaning
		if (imperative) {
			String surfaceText = surfaceRealizer.cleanTextForImperativeStyle(realizedText.getFormattedText(), imperativeRole, model.getLanes());
			realizedText.setText(surfaceText);
		}
		
		// TODO: Remove this line after, class was injected for test purpose only
		concreteRoundTripTest.runRoundTrip(realizedText, model);
		
		return realizedText;
	}
}
