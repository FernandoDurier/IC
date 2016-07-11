package bpm2nlg.text.planning;

import general.language.common.ModifierRecord;
import general.language.common.ModifierRecord.ModifierTarget;
import general.language.common.ModifierRecord.ModifierType;
import general.language.common.dsynt.DSynTConditionSentence;
import general.language.common.dsynt.DSynTMainSentence;
import general.language.common.dsynt.DSynTSentence;
import general.language.common.fragments.ConditionFragment;
import general.language.common.fragments.ExecutableFragment;
import general.language.common.fragments.FragmentType;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;
import general.language.common.rpst.RPST;
import general.language.common.rpst.RPSTNode;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.uniriotec.graph.process.ProcessGraphControlFlow;
import br.com.uniriotec.graph.process.ProcessGraphNode;
import br.com.uniriotec.process.model.Activity;
import br.com.uniriotec.process.model.Annotation;
import br.com.uniriotec.process.model.Arc;
import br.com.uniriotec.process.model.Event;
import br.com.uniriotec.process.model.EventType;
import br.com.uniriotec.process.model.ProcessModel;

public class TextToIntermediateConverter {
	
	private RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst;
	private ProcessModel process;
	private ILabelHelper lHelper;
	private boolean imperative;
	private String imperativeRole;
	
	/**
	 * Create a new instance of the class with the properties set to the respective values received by parameter.
	 * @param rpst The RPST process format.
	 * @param process The process model to work with.
	 * @param lHelper The ILabelHelper language specific implementation.
	 * @param imperativeRole The imperative role (actor) of the process model.
	 * @param imperative A boolean that indicates whether it's going to be treated as imperative or not.
	 */
	public TextToIntermediateConverter(RPST<ProcessGraphControlFlow,ProcessGraphNode> rpst, ProcessModel process, ILabelHelper lHelper, String imperativeRole, boolean imperative) {
		this.rpst = rpst;
		this.process = process;
		this.lHelper = lHelper;
		this.imperative = imperative;
		this.imperativeRole = imperativeRole;
	}
	
	//*********************************************************************************************
	//										OR - SPLIT
	//*********************************************************************************************
	
	/**
	 * 
	 */
	public ConverterRecord convertORSimple(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, GatewayExtractor gwExtractor, boolean labeled) 
	{
		ConverterRecord record = null; 
		
		if (labeled == true) 
			System.out.println("INFO: LABELED OR-SPLIT NOT COVERED");
		

		String[] activityAttributes = Localization.getInstance().getLocalizedMessage(
				Messages.EXECUTE, Messages.PATHS, Messages.ONE_OR_MORE_OF_THE, Messages.FOLLOWING).split("#");
		//String gatewayAndTextualDescription = String.format("%s %s %s", activityAttributes[2], activityAttributes[3], activityAttributes[1]); 
		
		ExecutableFragment eFrag = new ExecutableFragment(activityAttributes[0], activityAttributes[1], "", "");
		eFrag.bo_isSubject = true;
		eFrag.verb_IsPassive = true;
		eFrag.add_hasArticle = true;
		eFrag.sen_isCoord = true;
		eFrag.bo_isPlural = true;
		eFrag.setFragmentType(FragmentType.AND_GATEWAY);
		HashMap<String, ModifierRecord> modList = new HashMap<String, ModifierRecord>();
		ModifierRecord mr1 = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.ROLE);
		mr1.addAttribute("adv-type", "sentential");
		modList.put(activityAttributes[2], mr1);
		ModifierRecord mr2 = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.BUSINESS_OBJECT);
		modList.put(activityAttributes[3], mr2);
		eFrag.setModList(modList);
		// Load OR Template from File
		//ExecutableFragment eFrag = (ExecutableFragment) XMLHandler.getInstance().getFromXML("OrSplit_Unlabled.xml");
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		ArrayList<DSynTSentence> preStatements = new ArrayList<DSynTSentence>();
		preStatements.add(new DSynTMainSentence(eFrag));
		record = new ConverterRecord(null, null, preStatements, null);
		return record;
	}

	//*********************************************************************************************
	//										XOR - SPLIT
	//*********************************************************************************************
	
	public ArrayList <DSynTSentence> convertXORSimple(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, GatewayExtractor gwExtractor) {
		
		ExecutableFragment eFragYes = null;
		ExecutableFragment eFragNo = null;
		String role = "";
		
		ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>> pNodeList = new ArrayList<RPSTNode<ProcessGraphControlFlow,ProcessGraphNode>>();
		pNodeList.addAll(rpst.getChildren(node));
		for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> pNode: pNodeList) {
			for (RPSTNode<ProcessGraphControlFlow,ProcessGraphNode> tNode: rpst.getChildren(pNode)) {
				if (tNode.getEntry() == node.getEntry()) {
					for (Arc arc: process.getArcs().values()) {
						if (arc.getSource().getId() == Integer.valueOf(tNode.getEntry().getId()) && arc.getTarget().getId() == Integer.valueOf(tNode.getExit().getId())) {
							if (arc.getLabel().toLowerCase().equals(Localization.getInstance().getLocalizedMessage(Messages.YES))) {
								Activity a = process.getActivity(Integer.valueOf(tNode.getExit().getId()));
								Annotation anno = a.getAnnotations().get(0);
								String action = anno.getActions().get(0);
								String bo = anno.getBusinessObjects().get(0);
								role = a.getLane().getName();
								
								String addition = anno.getAddition();
								eFragYes = new ExecutableFragment(action, bo, role, addition);
								eFragYes.addAssociation(Integer.valueOf(node.getExit().getId()));
							}
							if (arc.getLabel().toLowerCase().equals(Localization.getInstance().getLocalizedMessage(Messages.NO))) {
								Activity a = process.getActivity(Integer.valueOf(tNode.getExit().getId()));
								Annotation anno = a.getAnnotations().get(0);
								String action = anno.getActions().get(0);
								String bo = anno.getBusinessObjects().get(0);
								
								role = a.getLane().getName();
								
								String addition = anno.getAddition();
								eFragNo = new ExecutableFragment(action, bo, role, addition);
								
								ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
								modRecord.addAttribute("adv-type", "sentential");
								eFragNo.addMod(Localization.getInstance().getLocalizedMessage(Messages.OTHERWISE), modRecord);
								eFragNo.sen_hasConnective = true;
								eFragNo.addAssociation(Integer.valueOf(node.getExit().getId()));
							}
						}
					}
				}
			}
		}
		
		ConditionFragment cFrag = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_IF, gwExtractor.getModList());
		cFrag.bo_replaceWithPronoun = true;
		cFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		// If imperative mode
		if (imperative == true && imperativeRole.equals(role) == true) {
			eFragNo.setRole("");
			eFragNo.verb_isImperative = true;
			eFragYes.setRole("");
			eFragYes.verb_isImperative = true;
		}
		DSynTConditionSentence dsyntSentence1 = new DSynTConditionSentence(eFragYes, cFrag);
		DSynTMainSentence dsyntSentence2 = new DSynTMainSentence(eFragNo);
		ArrayList<DSynTSentence> sentences = new ArrayList<DSynTSentence>();
		sentences.add(dsyntSentence1);
		sentences.add(dsyntSentence2);
		return sentences;
	}
	
	public ConverterRecord convertXORGeneral(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		
		// One of the following branches is executed.  (And then use bullet points for structuring)
		
		String[] activityAttributes = Localization.getInstance().getLocalizedMessage(
				Messages.EXECUTE, Messages.ONE_OF_THE_FALLOWING_BRANCHES).split("#");
		
		ExecutableFragment eFrag = new ExecutableFragment(activityAttributes[0], activityAttributes[1], "", "");
		eFrag.bo_isSubject = true;
		eFrag.verb_IsPassive = true;
		eFrag.add_hasArticle = true;
		eFrag.sen_isCoord = true;
		eFrag.setFragmentType(FragmentType.JOIN);
		//ExecutableFragment eFrag = (ExecutableFragment) XMLHandler.getInstance().getFromXML("XorSplit_General.xml");
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		ArrayList<DSynTSentence> preStatements = new ArrayList<DSynTSentence>();
		preStatements.add(new DSynTMainSentence(eFrag));
		
		activityAttributes = Localization.getInstance().getLocalizedMessage(
				Messages.EXECUTE, Messages.ONE_OF_THE_FALLOWING_BRANCHES).split("#");
		
		// Statement about negative case (process is finished)
		ConditionFragment post = new ConditionFragment(activityAttributes[0], activityAttributes[1], "", "", 1);
		post.setFragmentType(FragmentType.JOIN);
		post.bo_isSubject = true;
		post.verb_IsPassive = true;
		post.verb_isPast = true;
		post.add_hasArticle = true;
		post.sen_isCoord = true;
		post.sen_headPosition = true;
		//ConditionFragment post = (ConditionFragment) XMLHandler.getInstance().getFromXML("XorJoin_General.xml");
		post.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		return new ConverterRecord(null, post, preStatements, null, null);
	}
	

	//*********************************************************************************************
	//										LOOP - SPLIT
	//*********************************************************************************************
	
	/**
	 * Converts a loop construct with labeled entry condition into two sentences. 
	 */
	public ConverterRecord convertLoop(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> firstActivity) {
		
		// Labeled Case
		if (node.getExit().getName().equals("") == false) {
			// Derive information from the gateway
			GatewayExtractor gwExtractor = new GatewayExtractor(node.getExit(), lHelper);
			
			// Generate general statement about loop
//			String  role = process.getGateways().get(Integer.valueOf(node.getEntry().getId())).getLane().getName();
			String role = getRole(node);
			
			ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.REPEAT), Localization.getInstance().getLocalizedMessage(Messages.STEP), role, "");
			eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
			ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.BUSINESS_OBJECT);
			eFrag.addMod("latter", modRecord);
			eFrag.bo_isPlural = true;

			ExecutableFragment eFrag2 = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), "", "", "");
			eFrag2.addAssociation(Integer.valueOf(node.getEntry().getId()));
			eFrag.addSentence(eFrag2);
			if (role.equals("")) {
				eFrag.verb_IsPassive = true;
				eFrag.bo_isSubject = true;
				eFrag2.verb_IsPassive = true;
				eFrag2.setBo("it");
				eFrag2.bo_isSubject = true;
				eFrag2.bo_hasArticle = false;
			}
			
			Activity a = process.getActivity(Integer.valueOf(firstActivity.getExit().getId()));
			ExecutableFragment eFrag3 = new ExecutableFragment(a.getAnnotations().get(0).getActions().get(0),a.getAnnotations().get(0).getBusinessObjects().get(0) , "", "");
			eFrag3.addAssociation(a.getId());	
			eFrag3.sen_isCoord = false;
			eFrag3.verb_isParticiple = true;
			ModifierRecord modRecord2 = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			modRecord2.addAttribute("adv-type", "sentential");
			eFrag3.addMod("with", modRecord2);
			eFrag2.addSentence(eFrag3);
			
			ConditionFragment cFrag = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_AS_LONG_AS, new HashMap<String, ModifierRecord>(gwExtractor.getModList()));
			cFrag.verb_IsPassive = true;
			cFrag.bo_isSubject = true;
			cFrag.sen_headPosition = true;
			cFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
			
			// Determine postcondition  
			gwExtractor.negateGatewayLabel();
			ConditionFragment post = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_ONCE, gwExtractor.getModList());
			post.verb_IsPassive = true;
			post.bo_isSubject = true;
			post.setFragmentType(FragmentType.JOIN);
			post.addAssociation(Integer.valueOf(node.getEntry().getId()));
			
			// If imperative mode
			if (imperative == true && imperativeRole.equals(role) == true) {
				eFrag.setRole("");
				eFrag.verb_isImperative = true;
				eFrag2.verb_isImperative = true;
			}
			
			ArrayList<DSynTSentence> postStatements = new ArrayList<DSynTSentence>();
			postStatements.add(new DSynTConditionSentence(eFrag, cFrag));
			return new ConverterRecord(null, post, null, postStatements);
		}
		
		// Unlabeled case
		else {
			
			// Generate general statement about loop
//			String  role = process.getGateways().get(Integer.valueOf(node.getEntry().getId())).getLane().getName();
		
			
			ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.REPEAT), Localization.getInstance().getLocalizedMessage(Messages.STEP), "", "");
			ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.BUSINESS_OBJECT);
			eFrag.addMod("latter", modRecord);
			eFrag.bo_isPlural = true;
			eFrag.bo_isSubject = true;
			eFrag.verb_IsPassive = true;
			ExecutableFragment eFrag2 = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), "", "", "");
			eFrag.addSentence(eFrag2);

			Activity a = process.getActivity(Integer.valueOf(firstActivity.getExit().getId()));
			String role = a.getLane().getName();
			if (role.equals("")) {
				role = a.getPool().getName();
			}
			eFrag2.setRole(role);
			ExecutableFragment eFrag3 = new ExecutableFragment(a.getAnnotations().get(0).getActions().get(0),a.getAnnotations().get(0).getBusinessObjects().get(0), "", a.getAnnotations().get(0).getAddition());
			eFrag3.sen_isCoord = false;
			eFrag3.verb_isParticiple = true;
			ModifierRecord modRecord2 = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			modRecord2.addAttribute("adv-type", "sentential");
			eFrag3.addMod("with", modRecord2);
			eFrag2.addSentence(eFrag3);
			
			ConditionFragment cFrag = new ConditionFragment("be", "dummy", "", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			cFrag.addMod("required", new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB));
			cFrag.bo_replaceWithPronoun = true;
//			cFrag.verb_IsPassive = true;
			cFrag.bo_isSubject = true;
			cFrag.sen_headPosition = true;
			
			// Determine postcondition  
			ConditionFragment post = new ConditionFragment(Localization.getInstance().getLocalizedMessage(Messages.FINISH), Localization.getInstance().getLocalizedMessage(Messages.LOOP), "", "", ConditionFragment.TYPE_ONCE, new HashMap<String, ModifierRecord>());
			post.verb_IsPassive = true;
			post.bo_isSubject = true;
			post.setFragmentType(FragmentType.JOIN);
			
			// If imperative mode
			if (imperative == true && imperativeRole.equals(role) == true) {
				eFrag.setRole("");
				eFrag.verb_isImperative = true;
				eFrag2.verb_isImperative = true;
			}
			
			ArrayList<DSynTSentence> postStatements = new ArrayList<DSynTSentence>();
			postStatements.add(new DSynTConditionSentence(eFrag, cFrag));
			return new ConverterRecord(null, post, null, postStatements);
		}
	}
	

	//*********************************************************************************************
	//										SKIP - SPLIT
	//*********************************************************************************************
	
	public ConverterRecord convertSkipGeneralUnlabeled(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		ConditionFragment pre = new ConditionFragment("be", "dummy", "", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
		
		ModifierRecord mod = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
		pre.addMod("necessary", mod);
		pre.bo_replaceWithPronoun = true;
		pre.sen_headPosition = true;
		pre.sen_isCoord = true;
		pre.sen_hasComma = true;
		pre.addAssociation(Integer.valueOf(node.getEntry().getId()));
		return new ConverterRecord(pre, null, null, null);
		
	}
	
	/**
	 * Converts a standard skip construct with labeled condition gateway into two sentences. 
	 */
	public ConverterRecord convertSkipGeneral(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
			// Derive information from the gateway
			GatewayExtractor gwExtractor = new GatewayExtractor(node.getEntry(), lHelper);
			
			// Generate general statement about upcoming decision
			ConditionFragment pre = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_IN_CASE, gwExtractor.getModList());
			pre.verb_IsPassive = true;
			if (gwExtractor.hasVerb == false) {
				pre.verb_IsPassive = false;
			}
			pre.bo_isSubject = true;
			pre.sen_headPosition = true;
			pre.bo_isPlural = gwExtractor.bo_isPlural;
			pre.bo_hasArticle = gwExtractor.bo_hasArticle;
			pre.addAssociation(Integer.valueOf(node.getEntry().getId()));
			return new ConverterRecord(pre, null, null, null);
	}
	
	/**
	 * Converts a standard skip construct with labeled condition gateway, leading to the end of the process, into two sentences. 
	 */
	public ConverterRecord convertSkipToEnd(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		
		// Derive information from the gateway
		GatewayExtractor gwExtractor = new GatewayExtractor(node.getEntry(), lHelper);
//		String role = process.getGateways().get(Integer.valueOf(node.getEntry().getId())).getLane().getName();
		String role = getRole(node);
		
		// Generate general statement about upcoming decision
		ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.DECIDE),"" , role , "");
		ConditionFragment cFrag = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_WHETHER, gwExtractor.getModList());
		cFrag.verb_IsPassive = true;
		cFrag.bo_isSubject = true;
		cFrag.sen_headPosition = false;
		cFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		if (role.equals("")) {
			eFrag.verb_IsPassive = true;
			eFrag.setBo("it");
			eFrag.bo_hasArticle = false;
			eFrag.bo_isSubject = true;
			cFrag.verb_IsPassive = true;
			cFrag.setBo("it");
			cFrag.bo_hasArticle = false;
			cFrag.bo_isSubject = true;
		}
		
		// Statement about negative case (process is finished)
		ExecutableFragment eFrag2 = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.FINISH), Localization.getInstance().getLocalizedMessage(Messages.PROCESS_INSTANCE),"", "");
		eFrag2.verb_IsPassive = true;
		eFrag2.bo_isSubject = true;
		ConditionFragment cFrag2 = new ConditionFragment("be", "case", "this", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
		cFrag2.verb_isNegated = true;
		
		// Determine precondition
		ConditionFragment pre = new ConditionFragment(gwExtractor.getVerb(), gwExtractor.getObject(), "", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
		pre.verb_IsPassive = true;
		pre.sen_headPosition = true;
		pre.bo_isSubject = true;
		ModifierRecord modRecord = new ModifierRecord(ModifierType.PREPOSITION, ModifierTarget.VERB);
		modRecord.addAttribute("adv-type", "sentential");
		pre.addMod("otherwise", modRecord);
		pre.sen_hasConnective = true;
		
		// If imperative mode
		if (imperative == true && imperativeRole.equals(role) == true) {
			eFrag.setRole("");
			eFrag.verb_isImperative = true;
		}
		
		ArrayList<DSynTSentence> preStatements = new ArrayList<DSynTSentence>();
		preStatements.add(new DSynTConditionSentence(eFrag, cFrag));
		preStatements.add(new DSynTConditionSentence(eFrag2, cFrag2));

		return new ConverterRecord(pre, null, preStatements, null);
	}
	

	//*********************************************************************************************
	//										AND - SPLIT
	//*********************************************************************************************
	
	public ConverterRecord convertANDGeneral(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, int activities, ArrayList<ProcessGraphNode> conditionNodes) 
	{
		// The process is split into three parallel branches.  (And then use bullet points for structuring)
				String[] activityAttributes = Localization.getInstance().getLocalizedMessage(
				Messages.SPLIT, Messages.PROCESS, Messages.INTO_X_PARALLEL_BRANCHES).split("#");
		
		ExecutableFragment eFrag = new ExecutableFragment(activityAttributes[0], activityAttributes[1], "", activityAttributes[2]);
		eFrag.bo_isSubject = true;
		eFrag.bo_hasArticle = true;
		eFrag.verb_IsPassive = true;
		eFrag.sen_isCoord = true;
		eFrag.setFragmentType(FragmentType.STANDARD);
		
		eFrag.setAddition(eFrag.getAddition().replace("x",Integer.toString(activities)));
		eFrag.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		ArrayList<DSynTSentence> preStatements = new ArrayList<DSynTSentence>();
		preStatements.add(new DSynTMainSentence(eFrag));
		
		activityAttributes = Localization.getInstance().getLocalizedMessage(
				Messages.EXECUTE, Messages.ALL_THE_X_BRANCHES).split("#");
		
		ConditionFragment post = new ConditionFragment(activityAttributes[0], activityAttributes[1], "", "", 1);
		post.bo_isSubject = true;
		post.bo_isPlural = true;
		post.verb_IsPassive = true;
		post.verb_isPast = true;
		post.add_hasArticle = true;
		post.sen_isCoord = true;
		post.sen_headPosition = true;
		// Statement about negative case (process is finished)
		post.setBo(post.getBo().replace("x",Integer.toString(activities)));
		post.addAssociation(Integer.valueOf(node.getEntry().getId()));
		
		return new ConverterRecord(null, post, preStatements, null, null);
	}
	
	/**
	 * Converts a simple and construct. 
	 */
	public ConverterRecord convertANDSimple(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node, int activities, ArrayList<ProcessGraphNode> conditionNodes) 
	{
		//Creates a ModifierRecord in order to enhance the shown text.
		ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
		modRecord.addAttribute("adv-type", "sentential");
		if (activities == 1) 
		{
			modRecord.setLemma(Localization.getInstance().getLocalizedMessage(Messages.IN_CONCURRENCY_TO_THE_LATTER_STEP) + ", ");
			//modRecord = (ModifierRecord ) XMLHandler.getInstance().getFromXML("AndSplit_Simple_1Activity.xml");
		} 
		else 
		{
			//modRecord = (ModifierRecord) XMLHandler.getInstance().getFromXML("AndSplit_Simple_MoreActivities.xml");
			modRecord.setLemma(
					Localization.getInstance().getLocalizedMessage(Messages.IN_CONCURRENCY_TO_THE_LATTER_X_STEP) + ", "
					.replace("x", Integer.toString(activities))
					);
			//modRecord.setLemma(modRecord.getLemma().replace("x", Integer.toString(activities)));
		}

		// get last element of both branches and combine them to a post condition
		// if one of them is a gateway, include gateway post condition in the and post condition
		
		// Determine postcondition
		ConditionFragment post = null;
		String role ="";
		
		// Check whether postcondition should be passed
		int arcs = 0;
		for (Arc arc: process.getArcs().values()) {
			if (arc.getTarget().getId() == Integer.valueOf(node.getExit().getId())) {
				arcs++;
			}
		}
		
		// Only if no other arc flows into join gateway, join condition is passed
		if (arcs == 2) {
			if (conditionNodes.size() == 1) 
			{
				post = new ConditionFragment(Localization.getInstance().getLocalizedMessage(Messages.FINISH), "", "", "", 1);
				post.bo_hasArticle = true;
				post.verb_isPast = true;
				post.add_hasArticle = true;
				post.sen_isCoord = true;
				post.sen_headPosition = true;
				Activity a = process.getActivity(Integer.valueOf(conditionNodes.get(0).getId()));
				String verb = a.getAnnotations().get(0).getActions().get(0);
				role = getRole(node);
				post.setRole(role);
				post.setBo(lHelper.getNoun(verb));
				post.addAssociation(Integer.valueOf(node.getEntry().getId()));
			} 
			else 
			{
				String[] activityAttributes = Localization.getInstance().getLocalizedMessage(
						Messages.FINISH, Messages.BOTH_BRANCH).split("#");
				
				post = new ConditionFragment(activityAttributes[0], activityAttributes[1], "", "", 1);
				post.bo_isSubject = true;
				post.bo_isPlural = true;
				post.verb_IsPassive = true;
				post.verb_isPast = true;
				post.add_hasArticle = true;
				post.sen_isCoord = true;
				post.sen_headPosition = true;
				post.addAssociation(Integer.valueOf(node.getEntry().getId()));
			}
		}
		
		// If imperative mode
		if (imperative == true && imperativeRole.equals(role) == true) {
			post.role_isImperative = true;
		}
		return new ConverterRecord(null, post, null, null, modRecord);
	}
	
	//*********************************************************************************************
	//											EVENTS
	//*********************************************************************************************
	
	public ConverterRecord convertEvent(Event event) {
		
		ConditionFragment cFrag;
		ExecutableFragment eFrag;
		ArrayList<DSynTSentence> preSentences;
		
		switch (event.getType()) {
		
		//***************************************************************
		// 				INTERMEDIATE (CATCHING) EVENTS
		//***************************************************************
		
		// ERROR EVENT
		case EventType.INTM_ERROR:
			String error = event.getLabel();
			
			if (error.equals("")) {
				cFrag = new ConditionFragment("occur", "error","", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
				cFrag.bo_hasIndefArticle = true;
			} else {
				cFrag = new ConditionFragment("occur", "error '" + error + "'","", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
				cFrag.bo_hasArticle = true;
			}
			cFrag.bo_isSubject = true;
			if (event.isAttached()) {
				cFrag.setAddition("while latter task is executed,");
			}		
			break;
			
		// TIMER EVENT
		case EventType.INTM_TIMER:
			String limit = event.getLabel();
			if (limit.equals("")) {
				cFrag = new ConditionFragment("reach", "the time limit","", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			} else {
				cFrag = new ConditionFragment("reach", "the time limit of " + limit,"", "", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			}
			
			if (event.isAttached()) {
				cFrag.setAddition("while latter task is executed,");
			}		
			configureFragment(cFrag);
			break;
		
		// MESSAGE EVENT (CATCHING)
		case EventType.INTM_MSG_CAT:
			cFrag = new ConditionFragment("receive", "a message","", "", ConditionFragment.TYPE_ONCE, new HashMap<String, ModifierRecord>());
			configureFragment(cFrag);
			break;
			
		// ESCALATION EVENT (CATCHING)
		case EventType.INTM_ESCALATION_CAT:
			cFrag = new ConditionFragment("", "of an escalation","", "", ConditionFragment.TYPE_IN_CASE, new HashMap<String, ModifierRecord>());
			cFrag.bo_hasArticle = false;
			cFrag.bo_isSubject = true;
			break;
		
		//***************************************************************
		// 						START / END EVENTS
		//***************************************************************	
		
		// END EVENT
		case EventType.END_EVENT:
			eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.FINISH), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag.verb_IsPassive = true;
			eFrag.bo_isSubject = true;
			eFrag.bo_hasArticle = true;
			return getEventSentence(eFrag);
			
		// ERROR EVENT
		case EventType.END_ERROR:
			eFrag = new ExecutableFragment("end", Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "with an error");
			eFrag.bo_isSubject = true;
			eFrag.bo_hasArticle = true;
			eFrag.add_hasArticle = false;
			return getEventSentence(eFrag);
			
		case EventType.END_SIGNAL:
			eFrag = new ExecutableFragment("end", Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "with a signal.");
			eFrag.bo_isSubject = true;
			eFrag.bo_hasArticle = true;
			eFrag.add_hasArticle = false;
			return getEventSentence(eFrag);
			
		// START EVENT	
		case EventType.START_MSG:
			cFrag = new ConditionFragment("receive", "message","", "", ConditionFragment.TYPE_ONCE);
			cFrag.bo_isSubject = true;
			cFrag.verb_IsPassive = true;
			cFrag.bo_hasArticle = true;
			cFrag.bo_hasIndefArticle = true;
			eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.START), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag.bo_isSubject = true;
			eFrag.bo_hasArticle = true;
			return getEventSentence(eFrag, cFrag);
		
		//***************************************************************
		// 						THROWING EVENTS
		//***************************************************************	
		
		// MESSAGE EVENT	
		case EventType.INTM_MSG_THR:
			eFrag = new ExecutableFragment("send", "message",event.getLane().getName(), "");
			eFrag.bo_hasIndefArticle = true;
			return getEventSentence(eFrag);
			
		// ESCALATION EVENT	
		case EventType.INTM_ESCALATION_THR:
			eFrag = new ExecutableFragment("trigger", "escalation",event.getLane().getName(), "");
			eFrag.bo_hasIndefArticle = true;
			return getEventSentence(eFrag);
			
		// LINK EVENT	
		case EventType.INTM_LINK_THR:
			eFrag = new ExecutableFragment("send", "signal",event.getLane().getName(), "");
			eFrag.bo_hasIndefArticle = true;
			return getEventSentence(eFrag);

		// MULTIPLE TRIGGER	
		case EventType.INTM_MULTIPLE_THR:
			eFrag = new ExecutableFragment("cause", "multiple trigger",event.getLane().getName(), "");
			eFrag.bo_hasArticle = false;
			eFrag.bo_isPlural = true;
			return getEventSentence(eFrag);
			
		// SIGNAL EVENT	
		case EventType.INTM_SIGNAL_THR:
			eFrag = new ExecutableFragment("send", "signal",event.getLane().getName(), "");
			eFrag.bo_hasArticle = true;
			eFrag.bo_hasIndefArticle = true;
			eFrag.bo_isPlural = true;
			return getEventSentence(eFrag);
			
		default:
			System.out.println("NON-COVERED EVENT " + event.getType());
			return null;	
			
		}
		
		// Handling of intermediate Events (up until now only condition is provided)
		
		// Attached Event
		if (event.isAttached()) {
			preSentences = new ArrayList<DSynTSentence>();
			preSentences.add(getAttachedEventSentence(event, cFrag));
			return new ConverterRecord(null, null, preSentences, null);
			
		// Non-attached Event	
		} else {
			preSentences = new ArrayList<DSynTSentence>();
			preSentences.add(getIntermediateEventSentence(event, cFrag));
			return new ConverterRecord(null, null, preSentences, null);
		}
	}
	
	/**
	 * Returns Sentence for attached Event. 
	 */
	private DSynTConditionSentence getAttachedEventSentence(Event event, ConditionFragment cFrag) {
		ExecutableFragment eFrag = new ExecutableFragment("cancel", "it", "", "");
		eFrag.verb_IsPassive = true;
		eFrag.bo_isSubject = true;
		eFrag.bo_hasArticle = false;
		
		if (event.isLeadsToEnd() == false) {
			ModifierRecord modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			ExecutableFragment eFrag2 = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			modRecord.addAttribute("adv-type", "sent-final");
			modRecord.addAttribute("rheme", "+");
			eFrag2.addMod("as follows", modRecord);
			
			eFrag2.bo_isSubject = true;
			eFrag.addSentence(eFrag2);
		} else {
			ExecutableFragment eFrag2 = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.FINISH), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag2.bo_isSubject = true;
			eFrag2.verb_IsPassive = true;
			eFrag.addSentence(eFrag2);
		}
		DSynTConditionSentence sen = new DSynTConditionSentence(eFrag, cFrag);
		return sen;
	}
	
	// For attached events only
	public DSynTConditionSentence getAttachedEventPostStatement (Event event, String processElementId) {
		ModifierRecord modRecord;
		ModifierRecord modRecord2;
		ExecutableFragment eFrag;
		ConditionFragment cFrag;
		
		switch (event.getType()) {
		
		case EventType.INTM_TIMER:
			modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag.bo_isSubject = true;
			modRecord.addAttribute("adv-type", "sent-final");
			modRecord.addAttribute("rheme", "+");
			eFrag.addMod("normally", modRecord);
			 
			cFrag = new ConditionFragment("complete", "the task","", "within the time limit", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			cFrag.sen_hasConnective = true;
			cFrag.add_hasArticle = false;
			modRecord2 = new ModifierRecord(ModifierType.PREPOSITION, ModifierTarget.VERB);
			modRecord2.addAttribute("adv-type", "sentential");
			cFrag.addMod("otherwise", modRecord2);
			configureFragment(cFrag);
			return new DSynTConditionSentence(eFrag, cFrag);
			
		case EventType.INTM_ERROR:
			modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag.bo_isSubject = true;
			modRecord.addAttribute("adv-type", "sent-final");
			modRecord.addAttribute("rheme", "+");
			eFrag.addMod("normally", modRecord);
			 
			cFrag = new ConditionFragment("complete", "the task","", "without error", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			cFrag.sen_hasConnective = true;
			cFrag.add_hasArticle = false;
			modRecord2 = new ModifierRecord(ModifierType.PREPOSITION, ModifierTarget.VERB);
			modRecord2.addAttribute("adv-type", "sentential");
			cFrag.addMod("otherwise", modRecord2);
			configureFragment(cFrag);
			return new DSynTConditionSentence(eFrag, cFrag);
		
		case EventType.INTM_ESCALATION_CAT:
			modRecord = new ModifierRecord(ModifierType.ADJECTIVE, ModifierTarget.VERB);
			eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
			eFrag.bo_isSubject = true;
			modRecord.addAttribute("adv-type", "sent-final");
			modRecord.addAttribute("rheme", "+");
			eFrag.addMod("normally", modRecord);
			 
			cFrag = new ConditionFragment("complete", "the task","", "without escalation", ConditionFragment.TYPE_IF, new HashMap<String, ModifierRecord>());
			cFrag.sen_hasConnective = true;
			cFrag.add_hasArticle = false;
			modRecord2 = new ModifierRecord(ModifierType.PREPOSITION, ModifierTarget.VERB);
			modRecord2.addAttribute("adv-type", "sentential");
			cFrag.addMod("otherwise", modRecord2);
			configureFragment(cFrag);
			return new DSynTConditionSentence(eFrag, cFrag);	
			
		default: 
			System.out.println("NON-COVERED EVENT " + event.getType());
			return null;
		}
	}
	
	/**
	 * Returns record with sentence for throwing events.
	 */
	private ConverterRecord getEventSentence(ExecutableFragment eFrag) {
		DSynTMainSentence msen = new DSynTMainSentence(eFrag);
		ArrayList<DSynTSentence> preSentences = new ArrayList<DSynTSentence>();
		preSentences.add(msen);
		return new ConverterRecord(null, null, preSentences, null);
	}
	
	private ConverterRecord getEventSentence(ExecutableFragment eFrag, ConditionFragment cFrag) {
		DSynTConditionSentence msen = new DSynTConditionSentence(eFrag, cFrag);
		ArrayList<DSynTSentence> preSentences = new ArrayList<DSynTSentence>();
		preSentences.add(msen);
		return new ConverterRecord(null, null, preSentences, null);
	}
	
	
	/**
	 * Returns sentence for intermediate events.
	 */
	private DSynTConditionSentence getIntermediateEventSentence(Event event, ConditionFragment cFrag) {
		ExecutableFragment eFrag = new ExecutableFragment(Localization.getInstance().getLocalizedMessage(Messages.CONTINUE), Localization.getInstance().getLocalizedMessage(Messages.PROCESS), "", "");
		eFrag.bo_isSubject = true;
		DSynTConditionSentence sen = new DSynTConditionSentence(eFrag, cFrag);
		return sen;
	}
	
	/**
	 * Configures condition fragment in a standard fashion. 
	 */
	private void configureFragment (ConditionFragment cFrag) {
		cFrag.verb_IsPassive = true;
		cFrag.bo_isSubject = true;
		cFrag.bo_hasArticle = false;
	}
	
	/**
	 * Returns role executing current RPST node. 
	 */
	private String getRole(RPSTNode<ProcessGraphControlFlow, ProcessGraphNode> node) {
		String role = process.getGateways().get(Integer.valueOf(node.getExit().getId())).getLane().getName();
		if (role.equals("")) {
			role = process.getGateways().get(Integer.valueOf(node.getExit().getId())).getPool().getName();
		}
		return role;
	}
	
	
	
}
