package general.language.common.fragments;

import general.language.common.ModifierRecord;
import general.language.common.ModifierRecord.ModifierTarget;
import general.language.common.ModifierRecord.ModifierType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractFragment {
	
	private String action;
	private String bo;
	private String role;
	private String addition;
	private FragmentType fragmentType;
	
	private HashMap<String, ModifierRecord> modList;
	//TODO: Language specific code, generalize via lHelper
	public static List<String> DEM_PRONOUNS;
	static {
		DEM_PRONOUNS = new ArrayList<String>();
		DEM_PRONOUNS.add("this");
		DEM_PRONOUNS.add("that");
		DEM_PRONOUNS.add("these");
		DEM_PRONOUNS.add("those");
	}
	//TODO: Language specific code, generalize via lHelper
	public static List<String> PRONOUNS;
	static {
		PRONOUNS = new ArrayList<String>();
		PRONOUNS.add("he");
		PRONOUNS.add("she");
		PRONOUNS.add("it");
	}
	
	public boolean bo_replaceWithPronoun = false;
	public boolean bo_isSubject = false;
	public boolean bo_isPlural = false;
	public boolean bo_hasArticle = true;
	public boolean bo_hasIndefArticle = false;

	public boolean verb_IsPassive = false;
	public boolean verb_isParticiple = false;
	public boolean verb_isPast = false;
	public boolean verb_isNegated = false;
	public boolean verb_isImperative = false;
	public boolean verb_isReflexive = false;
	
	public boolean add_hasArticle = true;

	public boolean sen_isCoord = true;
	public boolean sen_hasConnective = false;
	public boolean sen_hasBullet = false;
	public int sen_bulletNumber = -1;
	public int 	   sen_level = 0;
	public boolean sen_hasComma = false;
	public boolean sen_hasColon = false;
	public boolean sen_before = false;
	
	public boolean role_isImperative = false;
	
	//TODO: Understand this attribute and how to use it
	public String connective = "";
	
	private ArrayList <Integer> associatedActivities = new  ArrayList<Integer>();
	
	public ArrayList <Integer> getAssociatedActivities() {
		return associatedActivities;
	}
	
	public void addAssociation(Integer id) {
		associatedActivities.add(id);
	}
	
	public AbstractFragment() {
		action = "";
		bo = "";
		role = "";
		addition= "";
		modList = new HashMap<String, ModifierRecord>();
		fragmentType = FragmentType.STANDARD;
		
	}

	public AbstractFragment(String action, String bo, String role, String addition) {
		this.action = action;
		this.bo = bo;
		this.role = role;
		this.addition = addition;
		modList = new HashMap<String, ModifierRecord>();
		fragmentType = FragmentType.STANDARD;
	}
	
	public AbstractFragment(String action, String bo, String role, String addition, HashMap<String, ModifierRecord> modList) {
		this.action = action;
		this.bo = bo;
		this.role = role;
		this.addition = addition;
		this.modList = modList;
		fragmentType = FragmentType.STANDARD;
	}
	
	public boolean hasBO() {
		if (bo.equals("") == false || bo_replaceWithPronoun == true) {
			return true;
		} else {
			return false;
		}
	}
	
	public FragmentType getFragmentType() {
		return fragmentType;
	}

	public void setFragmentType(FragmentType type) {
		this.fragmentType = type;
	}

	public ArrayList<String> getAllMods() {
		return new ArrayList<String>(modList.keySet());
	}
	
	public void setModList(HashMap<String, ModifierRecord> modList) {
		this.modList = modList;
	}
	
	public void addMod(String mod, ModifierRecord modRecord) {
		modList.put(mod, modRecord);
	}
	
	/*public ArrayList<Pair<String,String>> getModOptions(String mod) {
		return modList.get(mod).getAttributes();
	}*/
	
	public ModifierType getModType(String mod) {
		return modList.get(mod).getType();
	}
	
	public ModifierTarget getModTarget(String mod) {
		return modList.get(mod).getTarget();
	}

	public boolean hasMods() {
		if (modList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public void setBo(String bo) {
		this.bo = bo;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	public String getAction() {
		return action;
	}

	public String getBo() {
		return bo;
	}

	public String getRole() {
		return role;
	}

	public String getAddition() {
		return addition;
	}
}
