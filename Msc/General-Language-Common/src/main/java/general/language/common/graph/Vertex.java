package general.language.common.graph;

/**
 * Basic graph vertex implementation
 * 
 * @author Artem Polyvyanyy
 */
public class Vertex extends GObject implements IVertex
{	
	public Vertex() {
		super();
	}

	public Vertex(String name, String desc) {
		super(name, desc);
	}

	public Vertex(String name) {
		super(name);
	}
}
