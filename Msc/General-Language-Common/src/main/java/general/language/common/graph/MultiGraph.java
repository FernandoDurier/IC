package general.language.common.graph;

/**
 * Multi graph implementation
 * 
 * @author Artem Polyvyanyy
 */
public class MultiGraph extends AbstractMultiGraph<Edge,Vertex>
{
	/*
	 * (non-Javadoc)
	 * @see de.hpi.bpt.graph.abs.AbstractMultiGraph#addEdge(de.hpi.bpt.hypergraph.abs.IVertex, de.hpi.bpt.hypergraph.abs.IVertex)
	 */
	@Override
	public Edge addEdge(Vertex v1, Vertex v2)
	{
		if (v1==null || v2==null) return null;
		Edge e = new Edge(this,v1,v2);
		return e;
	}
}
