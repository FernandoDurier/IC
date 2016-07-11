package bpm2nlg.text.planning;

import general.language.common.ModifierRecord;
import general.language.common.ModifierRecord.ModifierTarget;
import general.language.common.ModifierRecord.ModifierType;
import general.language.common.Pair;
import general.language.common.dsynt.DSynTConditionSentence;
import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.AbstractFragment;
import general.language.common.fragments.ConditionFragment;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.fragments.FragmentType;
import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.LabelException;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;
import general.language.common.rpst.RPST;
import general.language.common.rpst.RPSTNode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import br.com.uniriotec.Machine.Artifact.ProcessModelConverter;
import br.com.uniriotec.graph.process.GraphProcess;
import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.Annotation;
import br.com.uniriotec.process.model.Event;
import br.com.uniriotec.process.model.EventType;
import br.com.uniriotec.process.model.ProcessModel;

public class TextPlanner {
	
	private RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst;
	private ProcessModel process;
	private TextToIntermediateConverter textToIMConverter;
	private ArrayList <ConditionFragment> passedFragments;
	private ModifierRecord passedMod = null; // used for AND-Splits
	private ArrayList<ModifierRecord> passedMods; // used for Skips 

	private boolean tagWithBullet = false;
	private boolean start = true;
	@SuppressWarnings("unused")
	private boolean end = false;
	private boolean isAlternative = false;
	
	private int lastBulletNumber = -1;
	private int lastSenLevelNumber = -1;
	
	private ArrayList<DSynTSentence> sentencePlan;
	private ArrayList<Pair<Integer,DSynTSentence>> activitiySentenceMap;
	private ILabelHelper lHelper;
	private ILabelDeriver lDeriver;
	private String[] quantifiers;
	
	private boolean imperative;
	private String imperativeRole;
	
	/**
	 * Create a new instance of the class. Sets the properties with the values received by parameter and initialize all other properties with default values.
	 * @param rpst The RPST process format.
	 * @param process The process model to work with.
	 * @param lDeriver The ILabelDeriver language specific implementation.
	 * @param lHelper The ILabelHelper language specific implementation.
	 * @param imperativeRole The imperative role (actor) of the process model.
	 * @param imperative A boolean that indicates whether it's going to be treated as imperative or not.
	 * @param isAlternative A boolean that indicates whether it's going to be treated as imperative or not.
	 * @throws FileNotFoundException
	 * @throws LabelException
	 */
	public TextPlanner(RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst, ProcessModel process, ILabelDeriver lDeriver, ILabelHelper lHelper, String imperativeRole, boolean imperative, boolean isAlternative) throws FileNotFoundException, LabelException {
		this.rpst = rpst;
		this.process = process;
		this.lHelper = lHelper;
		this.lDeriver = lDeriver;
		this.imperative = imperative;
		this.imperativeRole = imperativeRole;
		this.isAlternative = isAlternative;
		textToIMConverter = new TextToIntermediateConverter(rpst, process, lHelper, imperativeRole, imperative);
		passedFragments = new ArrayList<ConditionFragment>();
		sentencePlan = new ArrayList<DSynTSentence>();
		activitiySentenceMap = new ArrayList<Pair<Integer,DSynTSentence>>();
		passedMods = new ArrayList<ModifierRecord>();
		quantifiers = lHelper.getQuantifiers();
		lastBulletNumber = -1;
		lastSenLevelNumber = -1;
	}
	
	private void addPreStatements(ConverterRecord convRecord, int level)
	{
		if (convRecord != null && convRecord.preStatements != null)
		{
			for (DSynTSentence preStatement: convRecord.preStatements) 
			{
				if (passedFragments.size()>0) 
				{
					DSynTConditionSentence dsyntSentence = new DSynTConditionSentence(preStatement.getExecutableFragment(), passedFragments.get(0));
					if (passedFragments.size() > 1) 
					{
						for (int i = 1; i < passedFragments.size(); i++) 
						{
							dsyntSentence.addCondition(passedFragments.get(i), true);
							dsyntSentence.getConditionFragment().addCondition(passedFragments.get(i));
						}
					}
					passedFragments.clear();
					sentencePlan.add(dsyntSentence);
				} 
				else 
				{
					preStatement.getExecutableFragment().sen_level = level;
					if (passedMods.size() > 0 ) 
					{
						preStatement.getExecutableFragment().addMod(passedMods.get(0).getLemma(), passedMods.get(0));	
						preStatement.getExecutableFragment().sen_hasConnective = true;
						passedMods.clear();
					}
					sentencePlan.add(new DSynTMainSentence(preStatement.getExecutableFragment()));
				}
			}
		}
	}
	
	private void passPrecondition(ConverterRecord convRecord)
	{
		if (convRecord != null && convRecord.pre != null) {
			if (passedFragments.size() > 0) {
				if (passedFragments.get(0).getFragmentType() == FragmentType.JOIN) {
					ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
					eFrag.bo_isSubject = true;
					DSynTConditionSentence dsyntSentence = new DSynTConditionSentence(eFrag, passedFragments.get(0));
					sentencePlan.add(dsyntSentence);
					passedFragments.clear();
				}
			}
			passedFragments.add(convRecord.pre);
		}
	}
	
	private void prepareAndConvertToText(ConverterRecord convRecord, RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, int level) throws FileNotFoundException, LabelException
	{
		// Convert to Text
		if (PlanningHelper.isLoop(node,rpst) || PlanningHelper.isSkip(node,rpst)) {
			convertToText(node, level);
		}
		if (PlanningHelper.isXORSplit(node,rpst) || PlanningHelper.isORSplit(node, rpst) || PlanningHelper.isEventSplit(node, rpst)) {
			ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> paths = PlanningHelper.sortTreeLevel(node, node.getEntry(), rpst);
			for (RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> path: paths) {
				tagWithBullet = true;
				convertToText(path, level+1);
			}
		}
		if (PlanningHelper.isANDSplit(node,rpst))
		{
			// Determine path count
			ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> andNodes = PlanningHelper.sortTreeLevel(node, node.getEntry(), rpst);	

			if (andNodes.size() == 2) {
				ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> topNodes = PlanningHelper.sortTreeLevel(node, node.getEntry(), rpst);
				RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> path1 = topNodes.get(0);
				RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> path2 = topNodes.get(1);
				
				// Convert both paths
				convertToText(path1, level);
				passedMod = convRecord.mod;
				convertToText(path2, level);
			} 
			else 
			{
				ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> paths = PlanningHelper.sortTreeLevel(node, node.getEntry(), rpst);
				for (RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> path: paths) 
				{
					tagWithBullet = true;
					convertToText(path, level+1);
				}
			}
		}
	}
	
	private void treatBondNode(ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes, RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node, int level) throws FileNotFoundException, LabelException
	{
		// Converter Record
		ConverterRecord convRecord = null;
		
		//**************************************  LOOP - SPLIT  **************************************
		if (PlanningHelper.isLoop(node,rpst)) {
			convRecord = getLoopConverterRecord(node);
		}	
		//**************************************  SKIP - SPLIT  **************************************
		if (PlanningHelper.isSkip(node,rpst)) {
			convRecord = getSkipConverterRecord(orderedTopNodes, node);
		}	
		//**************************************  XOR - SPLIT  **************************************
		if (PlanningHelper.isXORSplit(node, rpst)) {
			convRecord = getXORConverterRecord(node);
		}
		//**************************************  EVENT BASED - SPLIT  **************************************
		if (PlanningHelper.isEventSplit(node, rpst)) {
			convRecord = getXORConverterRecord(node);
		}
		//**************************************  OR - SPLIT  **************************************
		if (PlanningHelper.isORSplit(node, rpst)) {
			convRecord = getORConverterRecord(node);
		}
		//**************************************  AND - SPLIT  **************************************
		if (PlanningHelper.isANDSplit(node, rpst)) {
			convRecord = getANDConverterRecord(node);
		}	
		
		addPreStatements(convRecord, level);
		passPrecondition(convRecord);
		prepareAndConvertToText(convRecord, node, level);
		
		// Add post statement to sentence plan
		if (convRecord != null && convRecord.postStatements != null) {
			for (DSynTSentence postStatement: convRecord.postStatements) {
				sentencePlan.add(postStatement);
			}
		}
			
		// Pass post fragment
		if (convRecord != null  && convRecord.post != null) {
			passedFragments.add(convRecord.post);
		}
	}
	
	private void treatRigidNodes(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, int level)
	{
		//**************************************  RIGIDS *******************************************
		ArrayList<Integer> validIDs = new ArrayList<Integer>();
		validIDs.addAll(process.getActivites().keySet());
		
		// Transforming RPST subtree to Petri Net
		ArrayList<HashMap<String,Boolean>> runRelations = PlanningHelper.getNetSystemFromRPSTFragment(node, process);
		
		ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTAIN), 
				Localization.getInstance().getLocalizedMessage(Messages.UNSTRCUTURED_PART_WHICH_CAN_BE_EXECUTED_AS_FOLLOW), Localization.getInstance().getLocalizedMessage(Messages.PROCESS),"");
		eFrag.bo_hasIndefArticle = true;
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		sentencePlan.add(new DSynTMainSentence(eFrag));
		
		// Main run
		HashMap<String,Boolean> runRelation = runRelations.get(0);
		for (String rel: runRelation.keySet()) {
			int first = Integer.valueOf(rel.split("-")[0]);
			int second = Integer.valueOf(rel.split("-")[1]);
			if (validIDs.contains(first) && validIDs.contains(second)) {
				if (runRelation.get(rel) == false) {
					System.out.println(second  + " before " + first);
					convertRigidActivityPair(second,first,level, node.getId());
				} else {
					System.out.println(first + " before " + second);
					convertRigidActivityPair(first,second,level, node.getId());
				}
			}
		}
		
		eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.ALLOW), Localization.getInstance().getLocalizedMessage(Messages.UNSTRCUTURED_PART), "", Localization.getInstance().getLocalizedMessage(Messages.FOR_THE_FOLLOWING_DEVIATIONS));
		ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
		modRecord.addAttribute("adv-type", "sentential");
		eFrag.addMod(Localization.getInstance().getLocalizedMessage(Messages.IN_ADDITION), modRecord);
		eFrag.bo_hasArticle = true;
		eFrag.bo_isSubject = true;
		eFrag.sen_hasConnective = true;
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		sentencePlan.add(new DSynTMainSentence(eFrag));
		
		for (int i= 1; i < runRelations.size(); i++) {
			runRelation = runRelations.get(i);
			//int count = 0;
			for (String rel: runRelation.keySet()) {
				int first = Integer.valueOf(rel.split("-")[0]);
				int second = Integer.valueOf(rel.split("-")[1]);
				if (validIDs.contains(first) && validIDs.contains(second)) {
					//count++;
					if (runRelation.get(rel) == false) {
						System.out.println(second  + " before " + first);
						convertRigidActivityPair(second,first,level+1, node.getId());
					} else {
						System.out.println(first + " before " + second);
						convertRigidActivityPair(first,second,level+1, node.getId());
					}
				}
			}
		}
	}
	
	private void treatActivityNodeType(ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes, RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node, int level, int depth) throws FileNotFoundException, LabelException
	{
		//**************************************  ACTIVITIES  **************************************
		convertActivities(node, level, depth);
		
		// Handle End Event
		if (PlanningHelper.isEvent(node.getExit())) {
			Event event = process.getEvents().get((Integer.valueOf(node.getExit().getId())));
			if (event.getType() == EventType.END_EVENT && orderedTopNodes.indexOf(node) == orderedTopNodes.size()-1) {
				// Adjust level and add to sentence plan
				DSynTSentence sen = textToIMConverter.convertEvent(event).preStatements.get(0);
				sen.getExecutableFragment().sen_level = level;
				sentencePlan.add(sen);
			}
		}
	}
	
	private void treatEventTypeNode(ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes, RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node, int level)
	{
		//**************************************  EVENTS  **************************************	
		 Event event = process.getEvents().get((Integer.valueOf(node.getEntry().getId())));
		int currentPosition = orderedTopNodes.indexOf(node);
		// Start Event
		if (currentPosition == 0)
		{
			
			// Start event should be printed
			if (start == true && isAlternative == false) {
				
				// Event is followed by gateway --> full sentence
				if (event.getType() == EventType.START_EVENT && currentPosition < orderedTopNodes.size()-1 && PlanningHelper.isBond(orderedTopNodes.get(currentPosition+1))) {
					start = false;
					ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.START), 
							Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", Localization.getInstance().getLocalizedMessage(Messages.WITH_DECISION));
					eFrag.add_hasArticle = false;
					eFrag.bo_isSubject = true;
					sentencePlan.add(new DSynTMainSentence(eFrag));
				}
				if (event.getType() != EventType.START_EVENT) {
					start = false;
					ConverterRecord convRecord = textToIMConverter.convertEvent(event);
					if (convRecord != null && convRecord.hasPreStatements() == true) {
						sentencePlan.add(convRecord.preStatements.get(0));
					}
				}
			}
		} 
		else // Intermediate Events	
		{
			ConverterRecord convRecord = textToIMConverter.convertEvent(event);
			
			// Add fragments if applicable
			if (convRecord != null && convRecord.pre != null) {
				passedFragments.add(convRecord.pre);
			}
			
			// Adjust level and add to sentence plan (first sentence not indented)
			if (convRecord != null && convRecord.hasPreStatements() == true) {
				for (int i = 0; i <convRecord.preStatements.size(); i++) {
					
					DSynTSentence sen = convRecord.preStatements.get(i);
					
					// If only one sentence (e.g. "Intermediate" End Event)
					if (convRecord.preStatements.size() == 1) {
						sen.getExecutableFragment().sen_level = level;
					}
					
					if (tagWithBullet == true) {
						if(lastSenLevelNumber != sen.getExecutableFragment().sen_level){
							lastBulletNumber = 1;
							lastSenLevelNumber = sen.getExecutableFragment().sen_level;
						}
						sen.getExecutableFragment().sen_hasBullet = true;
						sen.getExecutableFragment().sen_level = level;
						sen.getExecutableFragment().sen_bulletNumber = lastBulletNumber;
						tagWithBullet = false;
						lastBulletNumber++;
					}

					if (i>0) {
						sen.getExecutableFragment().sen_level = level;
					}
					sentencePlan.add(sen);
				}
			}
		}

	}
	
	/**
	 * Text Planning Main 
	 * @throws FileNotFoundException 
	 */
	public void convertToText(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> root, int level) throws LabelException, FileNotFoundException 
	{
		// Order nodes of current level with respect to control flow
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes = PlanningHelper.sortTreeLevel(root, root.getEntry(), rpst);

		// For each node of current level
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> node : orderedTopNodes)
		{
			// If we face an end event
			if ((PlanningHelper.isEvent(node.getExit()) && orderedTopNodes.indexOf(node) == orderedTopNodes.size()-1)) 
				end = true;
				
			int depth = PlanningHelper.getDepth(node, rpst);
			if (PlanningHelper.isBond(node)) 
			{
				treatBondNode(orderedTopNodes, node, level);
			} 
			else if (PlanningHelper.isRigid(node)) 
			{
				treatRigidNodes(node, level);
			}
			else if (PlanningHelper.isTask(node.getEntry())) 
			{
				treatActivityNodeType(orderedTopNodes, node, level, depth);
			} 
			else if (PlanningHelper.isEvent(node.getEntry())) 
			{
				treatEventTypeNode(orderedTopNodes, node, level);
			} 
			else if (depth > 0) 
			{
				convertToText(node, level);
			}
		}	
	}
	
	private void convertRigidActivityPair(int first, int second, int level, String processElementId) {
		Activity activity1 = (Activity) process.getActivity(first);
		Activity activity2 = (Activity) process.getActivity(second);
		Annotation anno1 = activity1.getAnnotations().get(0);
		Annotation anno2 = activity2.getAnnotations().get(0);
	
		ExecutableFragment eFrag = null;
		eFrag = new ExecutableFragment(anno1.getActions().get(0), anno1.getBusinessObjects().get(0), "", anno1.getAddition());
		eFrag.addAssociation(activity1.getId());
		String role = getRole(activity1, eFrag);
		eFrag.setRole(role);
		
		ExecutableFragment eFrag2 = null;
		eFrag2 = new ExecutableFragment(anno2.getActions().get(0), anno2.getBusinessObjects().get(0), "", anno2.getAddition());
		eFrag2.addAssociation(activity2.getId());
		role = getRole(activity2, eFrag2);
		eFrag2.setRole(role);
		eFrag.addSentence(eFrag2);
		eFrag.sen_hasBullet = true;
		eFrag.sen_level = level +1;
		eFrag.sen_before = true;
		
		DSynTMainSentence dsyntSentence = new DSynTMainSentence(eFrag);
		sentencePlan.add(dsyntSentence);
	}
	
	/**
	 * Method converts the activity.
	 * Gets the task in the entry point of the node and search, by the task (activity) id, in the Graph process structure for all the task's details.  
	 * Checks if the task is the fist task of the process (Start of the process). If it's then, creates a fragment (DSynT sentence) to encapsulate a general sentence
	 * stating that the start of the process. In order to create the sentence, search for the localized words in the Localization module. 
	 * @param node
	 * @param level
	 * @param depth
	 * @throws LabelException
	 * @throws FileNotFoundException
	 */
	private void convertActivities(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, int level, int depth) throws LabelException, FileNotFoundException {
		
		boolean planned = false;	
		
		Activity activity = (Activity) process.getActivity(Integer.parseInt(node.getEntry().getId()));
		Annotation anno = activity.getAnnotations().get(0);
		
		ExecutableFragment eFrag = null;
		
		// Start of the process
		if (start == true && isAlternative == false) {
			
			start = false;
			ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			modRecord.addAttribute("starting_point", "+");
			
			String bo = anno.getBusinessObjects().get(0);
			eFrag = new ExecutableFragment(anno.getActions().get(0), bo, "", anno.getAddition());
			eFrag.addAssociation(activity.getId());
			eFrag.addMod(Localization.getInstance().getLocalizedMessage(Messages.THE_PROCESS_BEGIN_WHEN), modRecord);
			
			String role = getRole(activity, eFrag);
			eFrag.setRole(role);
			if (anno.getActions().size() == 2)
			{
				ExecutableFragment eFrag2 = null;
				if (anno.getBusinessObjects().size() == 2) 
				{
					eFrag2 = new ExecutableFragment(anno.getActions().get(1), anno.getBusinessObjects().get(1), "", "");
					eFrag2.addAssociation(activity.getId());
				} 
				else 
				{
					eFrag2 = new ExecutableFragment(anno.getActions().get(1), "", "", "");
					eFrag2.addAssociation(activity.getId());
				}
				correctArticleSettings(eFrag2);
				eFrag.addSentence(eFrag2);
			}
			
			eFrag.bo_hasArticle = lHelper.isPluralForm(bo);
			
			// If imperative mode
			if (imperative == true && imperativeRole.equals(role) == true) {
				eFrag.verb_isImperative = true;
				eFrag.role_isImperative = true;
			}
			correctArticleSettings(eFrag);
			DSynTMainSentence dsyntSentence = new DSynTMainSentence(eFrag);
			sentencePlan.add(dsyntSentence);
			activitiySentenceMap.add(new Pair<Integer,DSynTSentence>(Integer.valueOf(node.getEntry().getId()), dsyntSentence));
			
			planned = true;
		} 
		
		// Standard case
		eFrag = new ExecutableFragment(anno.getActions().get(0), anno.getBusinessObjects().get(0), "", anno.getAddition());
		eFrag.addAssociation(activity.getId());
		String role = getRole(activity, eFrag);
		eFrag.setRole(role);
		if (anno.getActions().size() == 2) {
			ExecutableFragment eFrag2 = null;
			if (anno.getBusinessObjects().size() == 2) {
				eFrag2 = new ExecutableFragment(anno.getActions().get(1), anno.getBusinessObjects().get(1), "", "");
				if (eFrag.verb_IsPassive == true) {
					if (anno.getBusinessObjects().get(0).equals("") == true) {
						eFrag2.verb_IsPassive = true;
						eFrag.setBo(eFrag2.getBo());
						eFrag2.setBo("");
						eFrag.bo_hasArticle = true;
					} else {
						eFrag2.verb_IsPassive = true;
						eFrag2.bo_isSubject = true;
					}
					
				}
			} else {
				eFrag2 = new ExecutableFragment(anno.getActions().get(1), "", "", "");
				if (eFrag.verb_IsPassive == true) {
					eFrag2.verb_IsPassive = true;
				}
			}
			
			correctArticleSettings(eFrag2);
			eFrag2.addAssociation(activity.getId());
			eFrag.addSentence(eFrag2);
		}
		
		eFrag.sen_level = level;
		if (imperative == true && imperativeRole.equals(role) == true) {
			correctArticleSettings(eFrag);	
			eFrag.verb_isImperative = true;
			eFrag.setRole("");
		}
		
		// In case of passed modifications (NOT AND - Split) 
		if (passedMods.size() > 0 && planned == false) {
			correctArticleSettings(eFrag);	
			eFrag.addMod(passedMods.get(0).getLemma(), passedMods.get(0));	
			eFrag.sen_hasConnective = true;
			passedMods.clear();
		}
			
		// In case of passed modifications (e.g. AND - Split) 
		if (passedMod != null && planned == false){
			correctArticleSettings(eFrag);
			eFrag.addMod(passedMod.getLemma(), passedMod);	
			eFrag.sen_hasConnective = true;
			passedMod = null;
		}	
			
		if (tagWithBullet == true) {
			eFrag.sen_hasBullet = true;
			if(lastSenLevelNumber != eFrag.sen_level){
				lastBulletNumber = 1;
				lastSenLevelNumber = eFrag.sen_level;
			}
			eFrag.sen_bulletNumber = lastBulletNumber;
			lastBulletNumber++;
			tagWithBullet = false;
		}
			
		// In case of passed fragments (General handling)
		if (passedFragments.size() > 0 && planned == false) {
			correctArticleSettings(eFrag);
			DSynTConditionSentence dsyntSentence = new DSynTConditionSentence(eFrag, passedFragments.get(0));
			if (passedFragments.size() > 1) {
				for (int i = 1; i < passedFragments.size(); i++) {
					dsyntSentence.addCondition(passedFragments.get(i), true);
					dsyntSentence.getConditionFragment().addCondition(passedFragments.get(i));
				}
			}
			sentencePlan.add(dsyntSentence);
			activitiySentenceMap.add(new Pair<Integer,DSynTSentence>(Integer.valueOf(node.getEntry().getId()), dsyntSentence));
			passedFragments.clear();
			planned = true;
		}
			
		if (planned == false) {
			correctArticleSettings(eFrag);
			DSynTMainSentence dsyntSentence = new DSynTMainSentence(eFrag);
			sentencePlan.add(dsyntSentence);
			activitiySentenceMap.add(new Pair<Integer,DSynTSentence>(Integer.valueOf(node.getEntry().getId()), dsyntSentence));
		}
		

		// If activity has attached Events
		if (activity.hasAttachedEvents()) {
			ArrayList<Integer>attachedEvents = activity.getAttachedEvents();
			HashMap<Integer,ProcessModel>alternativePaths = process.getAlternativePaths();
			for (Integer attEvent: attachedEvents) {
				if (alternativePaths.keySet().contains(attEvent)) {
					System.out.println("Incorporating Alternative " + attEvent);
					// Transform alternative
					ProcessModel alternative = alternativePaths.get(attEvent);
					PlanningHelper.annotateModel(alternative, lDeriver, lHelper);
				
					// Consider complexity of the process
					if (alternative.getElemAmount() <= 3) {
						alternative.getEvents().get(attEvent).setLeadsToEnd(true);	
					}
					ProcessModelConverter pModelConverter = new ProcessModelConverter();
					GraphProcess p = pModelConverter.convertToRPSTFormat(alternative);
					RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst = new RPST<ProcessGraphControlFlow,ProcessGraphNode>(p);
					TextPlanner converter = new TextPlanner(rpst, alternative, lDeriver,lHelper, imperativeRole, imperative, true);
					PlanningHelper.printTree(rpst.getRoot(), 0, rpst);
					converter.convertToText(rpst.getRoot(), level+1);
					ArrayList <DSynTSentence> subSentencePlan = converter.getSentencePlan();
					for (int i = 0; i <subSentencePlan.size(); i++) {
						DSynTSentence sen = subSentencePlan.get(i);
						if (i==0) {
							sen.getExecutableFragment().sen_level = level;
						}
						if (i==1) {
							sen.getExecutableFragment().sen_hasBullet = true;
						}
						sentencePlan.add(sen);
					}
					converter = null;
					
					// Print sentence for subsequent normal execution
					sentencePlan.add(textToIMConverter.getAttachedEventPostStatement(alternative.getEvents().get(attEvent), node.getId()));
				}
			}
		}
		
		
		if (depth > 0) {
			convertToText(node, level);
		}
	}
	
	
	/**
	 * Get ConverterRecord for AND
	 */
	private ConverterRecord getANDConverterRecord(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		// Determine path count
		ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> andNodes = PlanningHelper.sortTreeLevel(node, node.getEntry(), rpst);	

		if (andNodes.size() == 2) {
				
			// Determine last activities of the AND split paths
			ArrayList<ProcessGraphNode> conditionNodes = new ArrayList<ProcessGraphNode>();
			for (RPSTNode <ProcessGraphControlFlow, ProcessGraphNode> n: andNodes) {
				ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> pathNodes = PlanningHelper.sortTreeLevel(n, n.getEntry(), rpst);	
				ProcessGraphNode lastNode = pathNodes.get(pathNodes.size()-1).getEntry();
				if (PlanningHelper.isTask(lastNode)) {
					conditionNodes.add(lastNode);
				}
			}
			return textToIMConverter.convertANDSimple(node, PlanningHelper.getActivityCount(andNodes.get(0), rpst), conditionNodes);

		// General case (paths > 2)
		} else {
			return textToIMConverter.convertANDGeneral(node, andNodes.size(), null);
		}
				
	}	
				
				
	/**
	 * Get ConverterRecord for OR
	 */
	private ConverterRecord getORConverterRecord(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		GatewayPropertyRecord orPropRec = new GatewayPropertyRecord(node, rpst, process);
		
		// Labeled Case
		if (orPropRec.isGatewayLabeled() == true)  {
			return null;
			
		// Unlabeled case
		} else {
			return textToIMConverter.convertORSimple(node, null, false);
		}	
	}
	
	/**
	 * Get ConverterRecord for XOR
	 */
	private ConverterRecord getXORConverterRecord(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		GatewayPropertyRecord propRec = new GatewayPropertyRecord(node, rpst, process);
		
		// Labeled Case with Yes/No - arcs and Max. Depth of 1
		if (propRec.isGatewayLabeled()== true && propRec.hasYNArcs() == true && propRec.getMaxPathDepth() == 1) {
			GatewayExtractor gwExtractor = new GatewayExtractor(node.getEntry(), lHelper);
		
			// Add sentence
			for (DSynTSentence s: textToIMConverter.convertXORSimple(node, gwExtractor)) {
				sentencePlan.add(s);
			}
			return null;
		// General case
		} else {
			return textToIMConverter.convertXORGeneral(node);
		}	
	}
	
	/**
	 * Get ConverterRecord for Loop
	 */
	private ConverterRecord getLoopConverterRecord(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> firstNodeInLoop = PlanningHelper.getNextActivity(node, rpst);
		return textToIMConverter.convertLoop(node,firstNodeInLoop);
	}
	
	/**
	 * Get ConverterRecord for Skip 
	 */
	private ConverterRecord getSkipConverterRecord(ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes, RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		GatewayPropertyRecord propRec = new GatewayPropertyRecord(node, rpst, process);
		
		// Yes-No Case 
		if (propRec.isGatewayLabeled() == true && propRec.hasYNArcs() == true) {
			
			// Yes-No Case which is directly leading to the end of the process
			if (isToEndSkip(orderedTopNodes, node) == true) {
				return textToIMConverter.convertSkipToEnd(node);

			// General Yes/No-Case	
			} else {
				return textToIMConverter.convertSkipGeneral(node);
			}
		
		// General unlabeled Skip
		} else {
			return textToIMConverter.convertSkipGeneralUnlabeled(node);
		}
	}
			
	/**
	 * Evaluate whether skip leads to an end 
	 */
	private boolean isToEndSkip(ArrayList<RPSTNode<ProcessGraphControlFlow, ProcessGraphNode>> orderedTopNodes, RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		int currentPosition = orderedTopNodes.indexOf(node);
		if (currentPosition < orderedTopNodes.size()-1) {
			ProcessGraphNode potEndNode = orderedTopNodes.get(currentPosition+1).getExit();
			if (PlanningHelper.isEndEvent(potEndNode,process) == true) {
				return true;
			} 
		}
		return false;
	}
	
	
	/**
	 * Returns role of a fragment.  
	 */
	private String getRole(Activity a, AbstractFragment frag) {
		if (a.getLane() == null) {
			frag.verb_IsPassive = true;
			frag.bo_isSubject = true;
			if (frag.getBo().equals("")) {
				frag.setBo(Localization.getInstance().getLocalizedMessage(Messages.IT));
				frag.bo_hasArticle = false;
			}
			return "";
		}
		String role = a.getLane().getName();
		if (role.equals("")) {
			role = a.getPool().getName();
		}
		if (role.equals("")) {
			frag.verb_IsPassive = true;
			frag.bo_isSubject = true;
			if (frag.getBo().equals("")) {
				frag.setBo(Localization.getInstance().getLocalizedMessage(Messages.IT));
				frag.bo_hasArticle = false;
			}
		}
		return role;
	}
	
	/**
	 * Checks and corrects the article settings for the bo and the addition. 
	 */
	public void correctArticleSettings(AbstractFragment frag) 
	{
		String bo = frag.getBo();
		
		//Checks if the BO is in the plural form, if it is then save it in the singular form and set the flag 'bo_isPlural' to true. 
		if (frag.bo_hasArticle && lHelper.isPluralForm(bo)) 
		{
			bo = lHelper.getSingularOfNoun(bo);
			frag.setBo(bo);
			frag.bo_isPlural = true;
		}
		else if (bo.contains("&")) 
			frag.bo_isPlural = true;
		
		//Checks if the article flag was set correctly
		if (frag.bo_hasArticle) {
			String[] boSplit = bo.split(" ");
			if (boSplit.length > 1) {
				if (Arrays.asList(quantifiers).contains(boSplit[0].toLowerCase()) || lHelper.isIndefArticle(boSplit[0])
						|| bo.startsWith(Localization.getInstance().getLocalizedMessage(Messages.THEIR)) || bo.startsWith(Localization.getInstance().getLocalizedMessage(Messages.FOR))) 
				{
					 frag.bo_hasArticle = false;
				}
			}
			else
				frag.bo_hasArticle = false;
		}
		
		String[] splitAdd = frag.getAddition().split(" ");
		if (splitAdd.length > 3 && lHelper.isVerb(splitAdd[1]) && splitAdd[0].equals("on") == false) 
			frag.add_hasArticle = false;
		else 
			frag.add_hasArticle = true;
	}

	public ArrayList<Pair<Integer, DSynTSentence>> getActivitiySentenceMap() {
		return activitiySentenceMap;
	}
	
	public ArrayList<DSynTSentence> getSentencePlan() {
		return sentencePlan;
	}

}
