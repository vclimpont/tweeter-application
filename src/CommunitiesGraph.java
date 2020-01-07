import java.util.HashMap;

import org.graphstream.graph.Node;

public class CommunitiesGraph extends AbstractGraph {
	
	private HashMap<Integer, Community> communities; // associate each community to a number

	public CommunitiesGraph(HashMap<Integer, Community> _communities)
	{
		super("Communities Graph");
		communities = _communities;
	}
	
	/**
	 * Add a node to the graph for each community in communities
	 */
	private void buildNodes()
	{
		for(Integer i : communities.keySet())
		{
			Node n = graph.addNode(""+i);
			
			// Set the class of the node in the css file
			n.setAttribute("ui.class", "community, "+communities.get(i).getCentrality());
			n.setAttribute("layout.weight", 10);
		}
	}
	
	/**
	 * Build an edge for each LEdge in each community
	 */
	private void buildEdges()
	{
		for(Integer i : communities.keySet())
		{			
			for(LNode ln : communities.get(i).getLNodes()) // for each nodes of each communities
			{
				for(LEdge e : ln.getEdges()) // add their edge 
				{				
					String cj = "" + e.getJ().getCommunity().getNumber();					
					if(graph.getEdge(cj+"."+i) == null)
					{
						graph.addEdge(i+"."+cj, ""+i, ""+cj, true).setAttribute("layout.weight", 10);
					}
				}
			}
		}
	}
	
	/**
	 * Build nodes and edges of the graph
	 */
	public void buildGraph()
	{
		buildNodes();
		buildEdges();
	}
	
	/**
	 * clear the communities graph
	 */
	public void clear()
	{
		graph.clear();
		initAttributes();
		communities.clear();
	}

	public HashMap<Integer, Community> getCommunities() {
		return communities;
	}

	public void setCommunities(HashMap<Integer, Community> communities) {
		this.communities = communities;
	}
	
}
