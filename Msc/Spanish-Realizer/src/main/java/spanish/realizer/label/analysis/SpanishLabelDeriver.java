package spanish.realizer.label.analysis;

import general.language.common.label.analysis.ILabelDeriver;
import general.language.common.label.analysis.ILabelHelper;
import general.language.common.label.analysis.ILabelProperties;
import general.language.common.label.analysis.LabelException;

public class SpanishLabelDeriver implements ILabelDeriver
{
	private ILabelHelper lHelper;

	public SpanishLabelDeriver(ILabelHelper labelHelper)
	{
		lHelper = labelHelper;
	}

	public void deriveFromVOS(String label, String[] labelSplit, ILabelProperties props) throws LabelException
	{
		// Check for phrasal verb
		if (labelSplit.length > 1) {
			if (checkForPhrasalVerb(lHelper.getInfinitiveOfAction(labelSplit[0]) + " " + labelSplit[1])) {
				props.setHasPhrasalVerb(true);
			}
		}

		// Check for conjunction
		if (label.contains(" y ") || label.contains("/"))
		{
			for (int i = 1; i < labelSplit.length; i++)
			{
				if (labelSplit[i].equals("y") || labelSplit[i].equals("/"))
				{
					props.setHasConjunction(true);
					props.setIndexConjunctionSplit(i);
				}
			}
		}

		boolean assigned = false;

		// A1 <AND> A2 BO (A2 is also given as imperative verb)
		if (props.hasPhrasalVerb() == false && props.hasConjunction() == true)
		{
			props.setAction(labelSplit[0] + " y " + labelSplit[props.getIndexConjunctionSplit()+1]);

			props.addToMultipleActions(labelSplit[0]);
			props.addToMultipleActions(labelSplit[props.getIndexConjunctionSplit()+1]);

			// First BO
			String temp = "";
			for (int j = 1; j <= props.getIndexConjunctionSplit()-1; j++)
			{
				temp = temp + " " + labelSplit[j];
			}
			temp = temp.trim();
			props.addBOs(temp);

			// Second BO
			temp = "";
			for (int j = props.getIndexConjunctionSplit()+2; j <= labelSplit.length - 1; j++) {
				temp = temp + " " + labelSplit[j];
			}
			temp = temp.trim();
			props.addBOs(temp);

			assigned = true;
		}

		// If label only contains a single action
		if (assigned == false)
		{
			props.setHasConjunction(false);
		}

		if (props.hasPhrasalVerb() == true && props.hasConjunction() == false)
		{
			props.setAction(labelSplit[0] + " " + labelSplit[1]);
			for (int j = 2; j <= labelSplit.length - 1; j++) {
				props.setBusinessObject(props.getBusinessObject() + " " + labelSplit[j]);
			}
		}

		if (props.hasPhrasalVerb() == false && props.hasConjunction() == false)
		{
			props.setAction(labelSplit[0]);
			for (int j = 1; j <= labelSplit.length - 1; j++) {
				props.setBusinessObject(props.getBusinessObject() + " " + labelSplit[j]);
			}
		}

		props.setBusinessObject(props.getBusinessObject().trim());

		// Separate addition
		String[] boSplit = props.getBusinessObject().split(" ");
		props.setBusinessObject("");
		int temp = -1;
		for (int j = 0; j <= boSplit.length - 1; j++) {
			if (props.getPrepositions().contains(boSplit[j]) == true) {
				temp = j;
				break;
			}
			props.setBusinessObject(props.getBusinessObject() + " " + boSplit[j]);
		}
		props.setBusinessObject(props.getBusinessObject().trim());

		if (temp > -1)
		{
			for (int j = temp; j <= boSplit.length - 1; j++) {
				props.setAdditionalInfo(props.getAdditionalInfo() + " " + boSplit[j]);
			}
			props.setAdditionalInfo(props.getAdditionalInfo().trim());
		}
	}

	/**
	 * Returns true if given word is a two word verb e.g. "Agree with, be back, be over, blow up, carry on, come in, come back..."
	 * @param word considered verb which might contain 2 words
	 * @return true if given word is a phrasal verb
	 * @throws JWNLException
	 */
	private boolean checkForPhrasalVerb(String word) throws LabelException
	{
		//TODO: This method must be implemented...
		return false;
	}
}
