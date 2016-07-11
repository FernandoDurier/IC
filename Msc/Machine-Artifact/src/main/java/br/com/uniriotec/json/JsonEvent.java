package br.com.uniriotec.json;

import java.util.ArrayList;

public class JsonEvent extends JsonElement{
	public JsonEvent(int id, String label, String type, int laneId, int poolId, ArrayList<Integer> arcs) {
		super(id, label, arcs, laneId, poolId, type);
	}
	
	public String toString() {
		String a = "";
		for (int i: arcs) {
			a = a + " " + i;
		}
		return "Event (" + id + ") - " + "Lane: " + laneId + " " + label + " - " + type;
	}
}
