package general.language.common.realizer;

import general.language.common.label.analysis.ILabelHelper;
import general.language.common.process.ProcessActivityProperty;
import general.language.common.process.ProcessElementType;
import general.language.common.process.ProcessEventProperty;
import general.language.common.process.ProcessGatewayProperty;

public interface INaturalLanguageProcessor {
	ProcessActivityProperty extractActivityProperties(String plainSentence);
	ProcessEventProperty extractEventProperties(String plainSentence);
	ProcessGatewayProperty extractGatewayProperties(String plainSentence);
	ProcessGatewayProperty extractAndGatewayProperties(String plainSentence);
	
	/***
	 * Activity pattern: 
	 * <ul> 
	 * 	<li> {actor} + {action} + {bo} + [addition] </li> 
	 * </ul>
	 * Gateway pattern (XOR): 
	 * <ul>
	 * 	<li> {IF} + {condition} + {THEN} + {activity}. [OTHERWISE], [another activity] </li>
	 * </ul>
	 * 
	 * @param plainSentence
	 * @return
	 */
	ProcessElementType identifySentenceType(String plainSentence);
	
	void configureLabelHelper(ILabelHelper lHelper); 
}
