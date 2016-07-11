package general.language.common.tctree;

import general.language.common.graph.AbstractDirectedEdge;
import general.language.common.graph.AbstractMultiDirectedGraph;
import general.language.common.graph.IEdge;
import general.language.common.graph.IVertex;

/**
 * 
 * @author Artem Polyvyanyy
 *
 */
public class TCTreeEdge<E extends IEdge<V>, V extends IVertex> extends AbstractDirectedEdge<TCTreeNode<E,V>> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected TCTreeEdge(AbstractMultiDirectedGraph g, TCTreeNode source, TCTreeNode target) {
		super(g, source, target);
	}
}
