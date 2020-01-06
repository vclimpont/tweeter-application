import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
	public void setNodeTransparency(Node elem, boolean isTransparent) {
		boolean isCommunity = false;
		String color;
		
		ArrayList<String> classes = new ArrayList<>(Arrays.asList(((String)(elem.getAttribute("ui.class"))).split(", ")));
		
		if(classes.contains("community")) {
			classes.remove("community");
			isCommunity = true;
		}
		color = classes.get(0).split("_")[0];
		if(isTransparent == true) {
			elem.setAttribute("ui.class", (isCommunity ? "community, ":"") + color + "_transparent");
		} else {
			elem.setAttribute("ui.class", (isCommunity ? "community, ":"") + color);
		}
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
	
	public void hideUnselectedNode(Node selectedNode) {
		HashMap<String, Node> neighbours = new HashMap<>();
		
		graph.edges().forEach((Edge e)->{
			setEdgeTransparency(e, true);
		});
		
		// Loop over linked edges
		selectedNode.edges().forEach(e->{
			setEdgeTransparency(e, false);
			if(e.getNode0().getId() == selectedNode.getId()) {
				neighbours.put(e.getNode1().getId(), e.getNode1());
			} else {
				neighbours.put(e.getNode0().getId(), e.getNode0());
			}
		});
		graph.nodes().forEach((Node n)->{
			if(n.getId() == selectedNode.getId() || neighbours.get(n.getId()) != null)
				setNodeTransparency(n, false);
			else
				setNodeTransparency(n, true);
		});
	}
	
	public void showAllNode() {
		// For each nodes
		graph.nodes().forEach((Node n)->{
			setNodeTransparency(n, false);
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
