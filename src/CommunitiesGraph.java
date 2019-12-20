import java.util.HashMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class CommunitiesGraph {
	
	private Graph graph;
	private HashMap<Integer, Community> communities;

	public CommunitiesGraph(HashMap<Integer, Community> _communities)
	{
		graph = new SingleGraph("Communities Graph");
		initAttributes();
		communities = _communities;
	}
	
	private void initAttributes()
	{
		graph.setAttribute("ui.stylesheet", "url('file://.//GraphStyle//stylesheet.css')");
		graph.setAttribute("layout.stabilization-limit", 0.01);
		graph.setAttribute("layout.quality", 0);
	}
	
	private void buildNodes()
	{
		for(Integer i : communities.keySet())
		{
			Node n = graph.addNode(""+i);
			n.setAttribute("ui.class", "community, "+communities.get(i).getCentrality());
			n.setAttribute("layout.weight", 10);
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
						graph.addEdge(i+"."+cj, ""+i, ""+cj).setAttribute("layout.weight", 10);;
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
	
	public void clear()
	{
		graph.clear();
		initAttributes();
		communities.clear();
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
