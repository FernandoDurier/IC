package general.language.common.label.analysis;

import java.util.ArrayList;
import java.util.HashSet;

public interface ILabelProperties 
{
	public ArrayList<String> getMultipleBOs();
	public void addBOs(String bo);
	public String getAction();
	public void setAction(String action);
	public String getAdditionalInfo();
	public void setAdditionalInfo(String additionalInfo);
	public String getBusinessObject() ;
	public void setBusinessObject(String businessObject);
	public int getIndexPrep() ;
	public void setIndexPrep(int indexPrep);
	public int getIndexPrepSplit();
	public void setIndexPrepSplit(int indexPrepSplit);
	public int getIndexOf() ;
	public void setIndexOf(int indexOf);
	public int getIndexConjunctionSplit() ;
	public void setIndexConjunctionSplit(int indexConjunctionSplit) ;
	public int getIndexConjunction();
	public void setIndexConjunction(int indexConjunction);
	public boolean isVerb() ;
	public void setVerb(boolean isVerb) ;
	public boolean hasPreposition();
	public void setHasPreposition(boolean hasPreposition);
	public boolean hasPrepositionOf();
	public void setHasPrepositionOf(boolean hasPrepositionOf);
	public boolean hasConjunction();
	public void setHasConjunction(boolean hasConjunction);
	public boolean hasPhrasalVerb();
	public void setHasPhrasalVerb(boolean hasPhrasalVerb);
	public boolean isGerundStyle() ;
	public void setGerundStyle(boolean isGerundStyle) ;
	public boolean isIrregularStyle() ;
	public void setIrregularStyle(boolean isIrregularStyle) ;
	public HashSet<String> getActions() ;
	public void setActions(HashSet<String> actions) ;
	public void addToActions(String toBeAdded);
	public ArrayList<String> getMultipleActions();
	public void setMultipleActions(ArrayList<String> multipleActions) ;
	public void addToMultipleActions(String toBeAdded);
	public HashSet <String> getPrepositions() ;
}
