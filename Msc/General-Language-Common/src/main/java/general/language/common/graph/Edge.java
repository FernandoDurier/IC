package general.language.common.graph;

/**
 * Graph edge implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Edge extends AbstractEdge<Vertex>
{
	@SuppressWarnings("rawtypes")
	protected Edge(AbstractMultiGraph g, Vertex v1, Vertex v2) {
		super(g, v1, v2);
	}	
}
