import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public abstract class AbstractGraph {
	
	protected Graph graph;
	
	public AbstractGraph(String name)
	{
		graph = new SingleGraph(name);
		initAttributes();
	}
	
	public void initAttributes()
	{
		graph.setAttribute("ui.stylesheet", "url('file://.//GraphStyle//lightGraph.css')");
		graph.setAttribute("layout.stabilization-limit", 0.01);
		graph.setAttribute("layout.quality", 0);
		graph.setAttribute("layout.weight", 10);
	}
	

	/**
	 * Set the node transparent or not, depending on the 2nd param
	 * @param elem : a Node
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setNodeTransparency(Node elem, boolean isTransparent, UsersBase base) {
		/*String centrality = base.getUser(elem.getId()).getCentrality();
		if(isTransparent == true) {
			elem.setAttribute("ui.class", centrality + "_transparent");
		} else {
			elem.setAttribute("ui.class", centrality);
		}*/
	}

	/**
	 * Set the edge transparent or not, depending on the 2nd param
	 * @param elem : an Edge
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setEdgeTransparency(Edge elem, boolean isTransparent) {
		if(isTransparent == true) {
			elem.setAttribute("ui.class", "transparent");
		} else {
			elem.removeAttribute("ui.class");
		}
	}
	
	public void hideUnselectedNode(Node selectedNode, UsersBase base) {
		User selectedUser = base.getUser(selectedNode.getId());
		
		// Set the selected node to visible
		setNodeTransparency(selectedNode, false, base);
		
		// For each nodes in the graph
		graph.nodes().forEach((Node n)->{
			if(!n.getId().equals(selectedNode.getId())) {
				User u = base.getUser(n.getId());
				if(!selectedUser.getInternalLinks().containsKey(u.getId()) &&
					!selectedUser.getExternalLinks().containsKey(u.getId())) {
					setNodeTransparency(n, true, base);
				} else {
					setNodeTransparency(n, false, base);
				}
			}
		});

		graph.edges().forEach((Edge e)->{
			// Hide edges if they aren't linked to the selected node
			if(!e.getSourceNode().getId().equals(selectedNode.getId()) &&
				!e.getTargetNode().getId().equals(selectedNode.getId())) {
				setEdgeTransparency(e, true);
			} else {
				setEdgeTransparency(e, false);
			}
		});
	}
	
	public void showAllNode(UsersBase base) {
		// For each nodes
		graph.nodes().forEach((Node n)->{
			setNodeTransparency(n, false, base);
		});
		// Remove the class attribute for each edges
		graph.edges().forEach(e->setEdgeTransparency(e, false));
	}
	
	
	public Graph getGraph() {
		return graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	

}
