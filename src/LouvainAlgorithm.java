import java.util.ArrayList;
import java.util.HashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

public class LouvainAlgorithm {
	
	private UsersBase base;
	private Graph graph;
	private HashMap<Integer, ArrayList<LNode>> communities;
	private double modularity;
	private int nbCommunities;
	
	public LouvainAlgorithm(UsersBase _base, Graph _graph)
	{
		base = _base;
		graph = _graph;
		communities = new HashMap<Integer, ArrayList<LNode>>();
		modularity = 0;
		nbCommunities = 1;
	}
	
	public void initModularity()
	{
		communities = new HashMap<Integer, ArrayList<LNode>>();
		
		int m = graph.getEdgeCount();
		double s_ij = 0;
		
		for(Edge e : graph.getEdgeSet())
		{
			User ui = base.getUser(e.getNode0().getId());
			setCommunity(ui);
			User uj = base.getUser(e.getNode1().getId());
			setCommunity(uj);
			
			createLEdge(ui.getLNode(), uj.getLNode(), 1);
			
			double ki = ui.getExternalLinksNumber() * 1.0;
			double kj = uj.getExternalLinksNumber() * 1.0;
			
			s_ij += (1 - (ki * kj)/(2.0 * m));
		}
		
		modularity = (1/(2.0 * m)) * s_ij;
	}
	
	private void addToCommunities(int community, LNode ln)
	{
		ArrayList<LNode> nodes = communities.get(community);
		if(nodes == null)
		{
			nodes = new ArrayList<LNode>();
			communities.put(community, nodes);
		}
		nodes.add(ln);
	}

	private void setCommunity(User u)
	{
		if(u.getCommunity() == -1) // community is not set yet
		{
			u.setCommunity(nbCommunities);
			createLNode(nbCommunities, u);
			nbCommunities++;
		}
	}
	
	private void createLNode(int community, User u)
	{
		LNode ln = new LNode(community);
		ln.addUser(u);
		u.setLNode(ln);
		addToCommunities(community, ln);
	}
	
	private void createLEdge(LNode li, LNode lj, int weight)
	{
		li.addLEdge(lj, weight);
	}

	public double getModularity()
	{
		return modularity;
	}
	
	
}
