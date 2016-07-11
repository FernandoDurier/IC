package general.language.common.graph;

/**
 * Directed edge implementation
 * 
 * @author Artem Polyvyanyy
 */
public class DirectedEdge extends AbstractDirectedEdge<Vertex>
{
	@SuppressWarnings("rawtypes")
	protected DirectedEdge(AbstractMultiDirectedGraph g, Vertex source, Vertex target) {
		super(g, source, target);
	}
}
