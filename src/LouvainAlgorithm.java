import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
		
		for(int k = 0; k < graph.getEdgeCount(); k++)
		{
			Edge e = graph.getEdge(k);
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
	
	private void addToCommunity(int community, LNode ln)
	{
		ArrayList<LNode> nodes = communities.get(community);
		if(nodes == null)
		{
			nodes = new ArrayList<LNode>();
			communities.put(community, nodes);
		}
		nodes.add(ln);
		ln.setCommunity(community);
		System.out.println(ln.getUsers().get(0).getId() + " : "+community);
	}
	
	/*private void addToCommunity(Iterator itr)
	{
		
		System.out.println("Node has been removed with itr");
		((LNode)itr).setCommunity(-1);
	}*/

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
		addToCommunity(community, ln);
	}
	
	private void createLEdge(LNode li, LNode lj, int weight)
	{
		li.addLEdge(lj, weight);
	}
	
	/*private void removeFromCommunity(LNode ln, int community)
	{
		if(communities.get(community).remove(ln))
		{
			System.out.println("Node has been removed");
			ln.setCommunity(-1);
		}
	}*/
	
	private void removeFromCommunity(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		System.out.println("Node has been removed with itr");
		ln.setCommunity(-1);
	}
	
	public void iterate()
	{
		double m2 = 0;
		for(Integer i : communities.keySet())
		{
			for(LNode ln : communities.get(i))
			{
				m2 += ln.getSumWeight();
			}
		}
		m2 = m2 * 2.0;
		
		double maxDelta = 0;
		int maxCommunity = -1;
		boolean up = true;
		int cap = 0;
		
		HashMap<Integer, int[]> sumWeightPerCommunity = new HashMap<Integer, int[]>();
		
		while(up && cap < 10)
		{
			System.out.println("NEW ITERATION");
			sumWeightPerCommunity.clear();
			maxDelta = 0;
			up = false;
			
			for(Integer i : communities.keySet())
			{
		        Iterator<?> itr = communities.get(i).iterator(); // for all nodes of community i
		        while (itr.hasNext()) 
		        { 
		            LNode ln = (LNode)itr.next(); 
					maxCommunity = i;
					for(LEdge e : ln.getEdges())
					{
						double delta = calculateDeltaModularity(ln, e.getJ().getCommunity(), m2, sumWeightPerCommunity);
						if(delta > maxDelta)
						{
							maxDelta = delta;
							maxCommunity = e.getJ().getCommunity();
							up = true;
						}
					}
					if(maxDelta > 0)
					{
						modularity += maxDelta;
						removeFromCommunity(itr, ln);
						addToCommunity(maxCommunity, ln);
					}
					maxDelta = 0;
		        } 
			}
			mergeCommunities();
			cap++;
		}
		
		System.out.println("DONE");
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + graph.getEdgeCount());
	}
	
	private double calculateDeltaModularity(LNode li, int community, double m2, HashMap<Integer, int[]> sumWeightPerCommunity)
	{
		double delta = 0;
		
		if(li.getCommunity() == community) // it's part of this community already
		{
			return delta;
		}
		
		int s_in = 0;
		int ki_in = 0;
		int s_tot = 0;
		int ki = 0;
		
		int[] li_w = li.getSumWeightLinkedToCommunity(community);
		
				
		ki_in = li_w[0];
		ki = li_w[1];
		
		int[] sumCommunity = sumWeightPerCommunity.get(community);
		if(sumCommunity == null)
		{
			for(LNode lj : communities.get(community))
			{
				int[] lj_w = lj.getSumWeightLinkedToCommunity(community);
				s_in += lj_w[0];
				s_tot += lj_w[1];
			}
			sumCommunity = new int[2];
			sumCommunity[0] = s_in;
			sumCommunity[1] = s_tot;
			sumWeightPerCommunity.put(community, sumCommunity); // save sum values per community
		}
		else
		{
			s_in = sumCommunity[0];
			s_tot = sumCommunity[1];
		}

		
		delta = (((s_in + ki_in) / m2) - Math.pow((s_tot + ki) / m2, 2)) - ((s_in / m2) - Math.pow(s_tot / m2, 2) - Math.pow(ki / m2, 2));
		System.out.println(delta);
		return delta;
	}
	
	private void mergeCommunities()
	{
		removeEmptyCommunities();
		
		HashMap<Integer, HashMap<Integer,Integer>> mergedEdgesPerCommunity = new HashMap<Integer, HashMap<Integer,Integer>>();
		HashMap<Integer, LNode> mergedNodes = new HashMap<Integer, LNode>();
		
		for(Integer i : communities.keySet())
		{
			System.out.println(i);
			mergeNodesAndEdges(communities.get(i), i, mergedEdgesPerCommunity, mergedNodes);
		}
		
		for(Integer i : mergedNodes.keySet()) // Add merged edges to merged nodes
		{
			LNode ln = mergedNodes.get(i);
			HashMap<Integer, Integer> edges = mergedEdgesPerCommunity.get(ln.getCommunity());
			for(Integer comm : edges.keySet())
			{
				ln.addLEdge(mergedNodes.get(comm), edges.get(comm));
			}
		}
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).clear();
			communities.get(i).add(mergedNodes.get(i));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void removeEmptyCommunities()
	{
		// Remove empty communities
		Iterator<Entry<Integer, ArrayList<LNode>>> itr = communities.entrySet().iterator();
        while (itr.hasNext()) { 
        	@SuppressWarnings("rawtypes")
			Map.Entry lnodes = (Map.Entry)itr.next(); 
        	if(((ArrayList<LNode>)lnodes.getValue()).isEmpty())
        	{
        		itr.remove();
        	}
        }
	}
	
	private void mergeNodesAndEdges(ArrayList<LNode> lnodes, int community, HashMap<Integer, HashMap<Integer,Integer>> mergedEdgesPerCommunity, HashMap<Integer,LNode> mergedNodes)
	{
		ArrayList<User> users = new ArrayList<User>();
		HashMap<Integer, Integer> mergedEdges = new HashMap<Integer, Integer>(); // target community, weight
		
		for(LNode ln : lnodes)
		{
			users.addAll(ln.getUsers());
			ln.mergeEdges(mergedEdges);
		}
		
		mergedNodes.put(community, new LNode(community, users));
		mergedEdgesPerCommunity.put(community, mergedEdges);
	}

	public double getModularity()
	{
		return modularity;
	}
	
	
}
