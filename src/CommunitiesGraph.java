import java.util.HashMap;

import org.graphstream.graph.Node;

public class CommunitiesGraph extends AbstractGraph {
	
	private HashMap<Integer, Community> communities;

	public CommunitiesGraph(HashMap<Integer, Community> _communities)
	{
		super("Communities Graph");
		communities = _communities;
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

	public HashMap<Integer, Community> getCommunities() {
		return communities;
	}

	public void setCommunities(HashMap<Integer, Community> communities) {
		this.communities = communities;
	}
	
}
