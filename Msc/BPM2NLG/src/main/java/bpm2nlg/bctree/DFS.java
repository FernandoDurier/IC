package bpm2nlg.bctree;

import general.language.common.graph.IEdge;
import general.language.common.graph.IGraph;
import general.language.common.graph.IVertex;

import java.util.Hashtable;
import java.util.Iterator;


public abstract class DFS<E extends IEdge<V>, V extends IVertex> {

    private Iterator<V> nodes = null;
    protected Hashtable<V,NodeAttrs> attrs = null;
    protected IGraph<E,V> graph;

    protected class NodeAttrs {
        boolean visited;

        public NodeAttrs() {
            visited = false;
        }
    }

    public DFS(IGraph<E,V> graph) {
        nodes = graph.getVertices().iterator();
        attrs = new Hashtable<V,NodeAttrs>(graph.getVertices().size());
        this.graph = graph;
        while (nodes.hasNext()) {
            prepareNode((V)nodes.next());
        }
    }

    protected void prepareNode(V node) {
        attrs.put(node, new NodeAttrs());
    }
    
    protected boolean visited(V node) {
        return ((NodeAttrs)attrs.get(node)).visited;
    }

    protected void process(V node) {
        NodeAttrs attributes = (NodeAttrs)attrs.get(node);
        attributes.visited = true;
        
        for (V i : this.graph.getAdjacent(node))
        	if (!visited(i))
                process(i);
    }

}
