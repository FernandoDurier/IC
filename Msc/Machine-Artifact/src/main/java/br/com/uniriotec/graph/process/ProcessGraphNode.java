package br.com.uniriotec.graph.process;

import general.language.common.graph.Vertex;

public abstract class ProcessGraphNode extends Vertex {

	public ProcessGraphNode() {
		super();
	}

	public ProcessGraphNode(String name, String desc) {
		super(name, desc);
	}

	public ProcessGraphNode(String name) {
		super(name);
	}
}
