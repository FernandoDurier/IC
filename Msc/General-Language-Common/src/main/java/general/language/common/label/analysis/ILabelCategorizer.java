package general.language.common.label.analysis;
//package labelAnalysis;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//public interface ILabelCategorizer 
//{
//	/**
//	 * Returns the label style of the given activity.
//	 * Possible results (English models): 'AN', 'VO' 
//	 * Possible results (Dutch models): 'AN', 'AN (first)', 'VO', 'VO (inf)', 'OI'
//	 * Possible results (Portuguese models): 'AN', 'VO'  
//	 */
//	public String getLabelStyle(Activity activity);
//	
//	/**
//	 * Returns the label style of the given model collection.
//	 * Possible results (English models): 'AN', 'VO' 
//	 * Possible results (Dutch models): 'AN', 'AN (first)', 'VO', 'VO (inf)', 'OI'
//	 * Possible results (Portuguese models): 'AN', 'VO' 
//	 */
//	public HashMap<String,String> getLabelStyle(ArrayList<ArrayList<Activity>> modelCollection);
//	
//	/**
//	 * Get the list of labels which belongs to the VOS (Verb-Object) type.
//	 * @return The list of VOS labels
//	 */
//	public ArrayList<Activity> getVOSLabels(); 
//	
//	/**
//	 * Get the list of labels which belongs to the AN (Action-Noun) type.
//	 * @return The list of AN labels
//	 */
//	public ArrayList<Activity> getANLabels();
//	
//	/**
//	 * Get the list of labels which belongs to the DES (???) type.
//	 * @return The list of DES labels
//	 */
//	public ArrayList<Activity> getDESLabels();
//}
