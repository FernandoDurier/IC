package br.com.uniriotec.graph.process;

import general.language.common.graph.AbstractDirectedEdge;
import general.language.common.graph.AbstractMultiDirectedGraph;

public class ProcessGraphControlFlow extends AbstractDirectedEdge<ProcessGraphNode> {

	private String label = "";
	
	@SuppressWarnings("rawtypes")
	protected ProcessGraphControlFlow(AbstractMultiDirectedGraph g, ProcessGraphNode source, ProcessGraphNode target) {
		super(g, source, target);
	}

	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
}
