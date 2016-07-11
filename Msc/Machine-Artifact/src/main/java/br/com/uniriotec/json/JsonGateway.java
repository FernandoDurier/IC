package br.com.uniriotec.json;

import java.util.ArrayList;

public class JsonGateway extends JsonElement {
	
	public JsonGateway (int id, String label, ArrayList<Integer> arcs, int laneId, int poolId, String type) {
		super(id, label, arcs, laneId, poolId, type);
	}
	
	public String toString() {
		String a = "";
		for (int i: arcs) {
			a = a + " " + i;
		}
		a = a.trim();
		return "Gateway (" + id + ") - " + "Lane: " + laneId + " " + label + " - " + type + " - " + a;
	}
}


