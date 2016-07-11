package general.language.common.label.analysis;

public interface ILabelDeriver 
{
//	/**
//	 * Returns the computed action of the processed label.
//	 */
//	public ArrayList<String> returnActions() ;
//	
//	/**
//	 * Returns the computed business object of the processed label.
//	 */
//	public ArrayList<String> returnBusinessObjects();
//	
//	/**
//	 * Returns the computed addition.
//	 */
//	public String returnAddition();
//	
//	/**
//	 * Investigates label and determines action and business object.
//	 */
//	public void processLabel(Activity label, String labelStyle); 
//	public void deriveFromActionNounLabels(ILabelProperties props, String label, String[] labelSplit) throws LabelException;
//	public void deriveFromDES(String label, String[] labelSplit, Activity activity, ILabelProperties props) throws LabelException;
	
	/**
	 * Derives and add the identified languages properties for the given label. E.g. type of verb, if the label has conjunction, the BO of the label, the Action and etc...
	 * @param label The label to be analyzed
	 * @param labelSplit the splitted form of the original label
	 * @param props Language specific properties object
	 * @throws LabelException
	 */
	public void deriveFromVOS(String label, String[] labelSplit, ILabelProperties props) throws LabelException;
}
