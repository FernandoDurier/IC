package general.language.common;

import java.util.ArrayList;

public class ModifierRecord {
	public enum ModifierTarget {
		VERB,
		ROLE,
		BUSINESS_OBJECT,
		ADDITION,
	};
	
	public enum ModifierType {
		ADJECTIVE,
		ADVERB,
		QUANTIFIER,
		PREPOSITION,
	};
	
	private ModifierType type;
	private ModifierTarget target;
	private ArrayList<Pair<String,String>> attributes;
	private String lemma;
	
	public ModifierRecord(ModifierType type, ModifierTarget target) {
		this.type = type;
		this.target = target;
		attributes = new ArrayList<Pair<String,String>>();
	}
	
	public void addAttribute(String attName, String attValue) {
		attributes.add(new Pair<String, String>(attName, attValue));
	}
	
	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public ModifierType getType() {
		return type;
	}
	
	public ModifierTarget getTarget() {
		return target;
	}

	public ArrayList<Pair<String, String>> getAttributes() {
		return attributes;
	}
}
