package br.com.uniriotec.process.model;

import java.util.ArrayList;

/**
 * A class that encapsulate an <code>Activity</code> details. Contains the list of BOs, the list of actions and the addition of the activity.
 * @see Activity
 */
public class Annotation {
	
	private ArrayList <String> actions;
	private ArrayList <String> businessObjects;
	private String addition;
	
	public Annotation() {
		actions = new ArrayList<String>();
		businessObjects = new ArrayList<String>();
		addition = "";
	}
	
	public void addAction(String action) {
		actions.add(action);
	}
	
	public void addBusinessObjects(String bo) {
		businessObjects.add(bo);
	}
	
	public void setAddition(String add) {
		addition = add;
	}
	
	
	public ArrayList<String> getActions() {
		return actions;
	}
	
	public ArrayList<String> getBusinessObjects() {
		return businessObjects;
	}
	
	public String getAddition() {
		return addition;
	}
	
	public String toString() {
		String s = actions.toString() + " " + businessObjects.toString() + " " + addition;
		return s;
		
	}
	
	
}
