package general.language.common.rpst;

import general.language.common.graph.AbstractDirectedEdge;
import general.language.common.graph.AbstractMultiDirectedGraph;
import general.language.common.graph.IDirectedEdge;
import general.language.common.graph.IVertex;


public class RPSTEdge<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractDirectedEdge<RPSTNode<E,V>> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected RPSTEdge(AbstractMultiDirectedGraph g, RPSTNode source, RPSTNode target) {
		super(g, source, target);
	}
}
