package spanish.realizer;

import general.language.common.label.analysis.ILabelHelper;
import general.language.common.localization.Localization;
import general.language.common.localization.Localization.Messages;
import general.language.common.process.ProcessActivityProperty;
import general.language.common.process.ProcessElementType;
import general.language.common.process.ProcessEventProperty;
import general.language.common.process.ProcessGatewayProperty;
import general.language.common.realizer.INaturalLanguageProcessor;
import general.language.common.realizer.ITextFormatter;
import general.language.common.realizer.TemplateTextFormatter;

import java.util.Arrays;
import java.util.List;

public class SpanishLanguageProcessor implements INaturalLanguageProcessor {

	private ILabelHelper labelHelper;
	private ITextFormatter textFormatter;

	/***
	 * Patter Covered: {ACTOR} + {ACTION} + {BO} + [ADDITION], where:<br>
	 * <ul>
	 * <li> ACTOR = noun + (preposition + noun)* </li>
	 * <li> ACTION = verb </li>
	 * <li>BO = noun </li>
	 * <li>ADDITION = Phrase noun</li>
	 * </ul>
	 */
	public ProcessActivityProperty extractActivityProperties(
			String plainSentence) {
		if(plainSentence != null && !plainSentence.trim().isEmpty()){
			ProcessActivityProperty properties = extractActivityFrom(plainSentence);
			if(properties != null){
				return properties;
			} else {
				ProcessElementType sentenceType = identifySentenceType(plainSentence);
				throw new IllegalArgumentException(
						String.format("The parameter 'plainSentence', represented by the string '%s' does not represent an activity sentence, rather it represents a(n) '%s' sentence.",
								plainSentence, sentenceType));
			}
		} else {
			throw new IllegalArgumentException("the parameter 'plainSentence' is invalid. Please submit valid values.");
		}
	}

	public ProcessEventProperty extractEventProperties(
			String plainSentence) {
		return extractEventFrom(plainSentence);
	}

	public ProcessGatewayProperty extractGatewayProperties(
			String plainSentence) {
		return new ProcessGatewayProperty(ProcessElementType.GATEWAY_AND);
	}

	/***
	 * ACTIVITY-PASSIVE = {BO} + [ADDITION] + {ACTION} + {ACTOR}
	 * ACTIVITY = [DISOCURSE_MARKER], {ACTOR} + {ACTION} + {BO} + [ADDITION]
	 * GATEWAY-XOR-1 = {IF} + {CONDITION_CLAUSE} + [THEN] + {ACTIVITY}.
	 * GATEWAY-XOR-2 = {IF} + {CONDITION_CLAUSE} + [THEN] + {ACTIVITY}. OTHERWISE + {ACTIVITY}.
	 * GATEWAY-XOR-3 = {ACTOR} + {CHECK/VERIFY} + {IF} + {CONDITION_CLAUSE} . {IF} + {ACTOR} + {CONDITION_CLAUSE} + {THEN} + {ACTIVITY}. {IF} + {ACTOR} + {NOT} + {CONDITION_CLAUSE} + {THEN} + {ACTIVITY}.
	 * GATEWAY-PARALEL = {DISOCURSE_MARKER} + {the process is divided into x parallel branches:}
	 * GATEWAY-AND = {DISOCURSE_MARKER} + {the following branches are executed:}
	 * EVENT-BEGIN-1 = [The] process begins [when there is] BEGIN_DESCRIPTION.
	 * EVENT-BEGIN-2 = [The] process begins [when] {ACTIVITY}.
	 * EVENT-FINISH-1 = Finally, the process finishes.
	 * EVENT-FINISH-2 = Once all the brances are executed, the process finishes with FINISH_DESCRIPTION.
	 */
	public ProcessElementType identifySentenceType(String plainSentence) {
		ProcessElementType elementType = ProcessElementType.UNKNOW;
		if(plainSentence != null && !plainSentence.trim().isEmpty()){
			if(extractActivityFrom(plainSentence) != null){
				elementType = ProcessElementType.ACTIVITY;
			} else {
				ProcessEventProperty eventProperties = extractEventFrom(plainSentence);
				if(eventProperties != null){
					elementType = eventProperties.getElementType();
				} else {
					// Check for Gateway sentence type..
				}
			}
		}

		return elementType;
	}

	public void configureLabelHelper(ILabelHelper lHelper) {
		this.labelHelper = lHelper;
		this.textFormatter = new TemplateTextFormatter(labelHelper);
	}

	public ProcessGatewayProperty extractAndGatewayProperties(
			String plainSentence) {
		return new ProcessGatewayProperty(ProcessElementType.GATEWAY_AND);
	}

	private ProcessActivityProperty extractActivityFrom(String plainSentence){
		ProcessActivityProperty activityProperties = null;
		int i = 0;
		String[] splittedSentence = plainSentence.split("\\s+");
		boolean isFirstWordNoun = labelHelper.isNoun(splittedSentence[i++]);
		String compositeNoun = splittedSentence[i-1];
		if(isFirstWordNoun){
			//check if is composite noun
			for( ; i < splittedSentence.length ; i+=2){ // incrementa pois analisou um par i e i+1, logo proxima iteracao tem que comecar em i+2
				boolean isPreposition = labelHelper.isPreposition(splittedSentence[i]);
				boolean isNoun = labelHelper.isNoun(splittedSentence[i+1]);
				if(isPreposition && isNoun){
					compositeNoun += " " + splittedSentence[i] + " " + splittedSentence[i+1];
				} else{
					break;
				}
			}
		}
		String action = splittedSentence[i++];
		String businessObject = splittedSentence[i];
		boolean isSecondWordVerb = labelHelper.isVerb(action);
		boolean isThirdWordNoun = labelHelper.isNoun(businessObject);

		if(isFirstWordNoun && isSecondWordVerb && isThirdWordNoun){
			if(!labelHelper.isInfinitive(action)){
				action = labelHelper.getInfinitiveOfAction(action);
			}
			int indexBeginAddition = plainSentence.indexOf(businessObject) + businessObject.length();
			String addition = (indexBeginAddition < plainSentence.length()) ? plainSentence.substring(indexBeginAddition + 1) : "";
			activityProperties = new ProcessActivityProperty(businessObject, compositeNoun, action, addition);
		}

		return activityProperties;
	}

	private ProcessEventProperty extractEventFrom(String plainSentence){
		ProcessEventProperty eventProperties = null;
		if(plainSentence != null && !plainSentence.trim().isEmpty()){
			String cleanedPlainSentence = textFormatter.removeStopWords(plainSentence);
			String lowerCaseSentence = cleanedPlainSentence.toLowerCase();

			eventProperties = extractBeginEvent(lowerCaseSentence);
			if(eventProperties == null){
				eventProperties = extractEndEvent(lowerCaseSentence);
			}
		}

		return eventProperties;
	}

	private ProcessEventProperty extractBeginEvent(String eventSentence){
		ProcessEventProperty eventProperties = null;

		String firstKey = Localization.getInstance().getLocalizedMessage(Messages.PROCESS);
		String secondKey = Localization.getInstance().getLocalizedMessage(Messages.START);
		secondKey = VerbManager.getInstance().getVerb(secondKey, false, false, false, false, true, false);

		boolean  isBeginEvent = eventSentence.contains(firstKey) && eventSentence.contains(secondKey);

		if(isBeginEvent){
			int lastEventIdentifierIndex = 0;
			String wordWhen = Localization.getInstance().getLocalizedMessage(Messages.WHEN);
			if(eventSentence.contains(wordWhen)){
				lastEventIdentifierIndex = eventSentence.indexOf(wordWhen) + wordWhen.length();
			} else {
				lastEventIdentifierIndex = eventSentence.indexOf(secondKey) + secondKey.length();
			}

			// decide whether is EVENT-BEGIN-1 or EVENT-BEGIN-2
			ProcessElementType eventType = ProcessElementType.UNKNOW;
			String eventIdentifier = eventSentence.substring(0, lastEventIdentifierIndex);
			String remainingSentece = eventSentence.substring(lastEventIdentifierIndex).trim();
			if(extractActivityFrom(remainingSentece) != null){
				eventType = ProcessElementType.EVENT_BEGIN_WITH_ACTIVITY;
			} else {
				eventType = ProcessElementType.EVENT_BEGIN_WITH_DESCRIPTION;
			}

			eventProperties = new ProcessEventProperty(eventType, eventIdentifier, remainingSentece);
		}

		 return eventProperties;
	}

	private ProcessEventProperty extractEndEvent(String eventSentence){
		ProcessEventProperty eventProperties = null;

		String firstKey = Localization.getInstance().getLocalizedMessage(Messages.PROCESS);
		String secondKey = Localization.getInstance().getLocalizedMessage(Messages.FINISH);

		String finishes = VerbManager.getInstance().getVerb(secondKey, false, false, false, false, true, false);
		String finished = secondKey = VerbManager.getInstance().getVerb(secondKey, true, true, false, false, false, false);

		// splits sentence into words to avoid wrong matching (e.g., termina = terminado, because termina = 'termina' + 'do')
		List<String> splittedEventSentence = Arrays.asList(eventSentence.split("\\s+"));

		String keyFinish = null;
		if(splittedEventSentence.contains(finishes) ){
			keyFinish  = finishes;
		} else if(splittedEventSentence.contains(finished)){
			keyFinish  = finished;
		}

		boolean  isEndEvent = splittedEventSentence.contains(firstKey) && keyFinish != null;
		if(isEndEvent){
			int lastEventIdentifierIndex = 0;
			eventSentence = eventSentence.replaceAll("\\s*\\.\\s*", "");
			ProcessElementType eventType = ProcessElementType.EVENT_FINISH;
			lastEventIdentifierIndex = eventSentence.indexOf(keyFinish) + keyFinish.length();
			String eventIdentifier = eventSentence.substring(0, lastEventIdentifierIndex);
			String remainingSentece = eventSentence.substring(lastEventIdentifierIndex).trim();

			// decide whether is EVENT-END-1 or EVENT-END-2
			if(remainingSentece.length() > 0){
				eventType = ProcessElementType.EVENT_FINISH_WITH_DESCRIPTION;
			}
			eventProperties = new ProcessEventProperty(eventType, eventIdentifier, remainingSentece);
		}

		return eventProperties;
	}
}
