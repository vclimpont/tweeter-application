import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class CommunitiesGraph {
	
	private Graph graph;
	private HashMap<Integer, ArrayList<LNode>> communities;

	public CommunitiesGraph(HashMap<Integer, ArrayList<LNode>> _communities)
	{
		graph = new SingleGraph("Communities Graph");
		communities = _communities;
		graph.setAttribute("layout.stabilization-limit", 0.01);
		graph.setAttribute("layout.quality", 0);
		graph.setAttribute("layout.weight", 10);
		graph.removeAttribute("ui.antialias");
		graph.removeAttribute("ui.quality");
	}
	
	public void buildNodes()
	{
		for(Integer i : communities.keySet()) // for each community 
		{
			graph.addNode(""+i); // add a node in the graph 
		}
	}
	
	public void buildEdges()
	{
		for(Integer i : communities.keySet())
		{
			for(LNode ln : communities.get(i)) // for each node in community i
			{
				for(LEdge le : ln.getEdges()) // for each edge 
				{
					String id = i+"."+le.getJ().getCommunity();
					Edge e = graph.getEdge(id); // if there is no such edge built in the graph
					if(e == null)
					{
						graph.addEdge(id, ""+i, ""+le.getJ().getCommunity(), true);
					}
				}
			}
		}
	}
	
	public void build()
	{
		buildNodes();
		buildEdges();
	}

	public HashMap<Integer, ArrayList<LNode>> getCommunities() {
		return communities;
	}

	public void setCommunities(HashMap<Integer, ArrayList<LNode>> communities) {
		this.communities = communities;
	}

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
}
