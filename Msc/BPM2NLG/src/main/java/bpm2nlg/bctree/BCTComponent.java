package bpm2nlg.bctree;

import general.language.common.graph.AbstractMultiGraphFragment;
import general.language.common.graph.IEdge;
import general.language.common.graph.IGraph;
import general.language.common.graph.IVertex;

public class BCTComponent<E extends IEdge<V>, V extends IVertex> extends AbstractMultiGraphFragment<E, V> {

	public BCTComponent(IGraph<E, V> g) {
		super(g);
	}

}
