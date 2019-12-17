import java.util.HashMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class CommunitiesGraph {
	
	private Graph graph;
	private HashMap<Integer, Community> communities;

	public CommunitiesGraph(HashMap<Integer, Community> _communities)
	{
		graph = new SingleGraph("Communities Graph");
		communities = _communities;
	}
	
	private void buildNodes()
	{
		for(Integer i : communities.keySet())
		{
			graph.addNode(""+i);
		}
	}
	
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
						graph.addEdge(i+"."+cj, ""+i, ""+cj);
					}
				}
			}
		}
	}
	
	public void buildGraph()
	{
		buildNodes();
		buildEdges();
	}
	

	public Graph getGraph() {
		return graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public HashMap<Integer, Community> getCommunities() {
		return communities;
	}

	public void setCommunities(HashMap<Integer, Community> communities) {
		this.communities = communities;
	}
	
	
}
