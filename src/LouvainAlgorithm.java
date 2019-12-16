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
	
	public void initCommunities()
	{
		communities = new HashMap<Integer, ArrayList<LNode>>();
		double m = graph.getEdgeCount();
		
		for(int k = 0; k < m; k++)
		{
			Edge e = graph.getEdge(k);
			User ui = base.getUser(e.getNode0().getId());
			initCommunity(ui);
			User uj = base.getUser(e.getNode1().getId());
			initCommunity(uj);
			
			createLEdge(ui.getLNode(), uj.getLNode(), 1);
		}
		
		displayCommunities();
		calculateModularity();
	}
	
	public void displayCommunities()
	{
		for(Integer i : communities.keySet())
		{
			System.out.println("Community : " + i);
			for(LNode ln : communities.get(i))
			{
				for(User u : ln.getUsers())
				{
					System.out.println("\t"+u.getId());
				}
				System.out.println();
				for(LEdge le : ln.getEdges())
				{
					System.out.println("\t Edge " +le.getI().getCommunity()+ " --> "+le.getJ().getCommunity() +  " : " + le.getWeight());
				}
			}
		}
	}
	
	private void calculateModularity()
	{
		double m = graph.getEdgeCount() * 1.0; //  sum of the weights from all edges in the entire graph
		double Q = 0;
		for(Integer i : communities.keySet())
		{
			for(LNode ln : communities.get(i))
			{
				int[] w = ln.getSumWeightLinkedToCommunity(i);
				double intra_w = w[0]/(2.0 * m);
				double inter_w = Math.pow(w[1]/(2.0 * m), 2);

				Q += (intra_w - inter_w);
			}
		}
		modularity = Q;
		//System.out.println("MODULARITY : " + modularity);
	}
	
	private void addToCommunity(int community, LNode ln)
	{
		ArrayList<LNode> nodes = communities.get(community);
		if(nodes == null)
		{
			nodes = new ArrayList<LNode>();
			communities.put(community, nodes);
		}
		communities.get(community).add(ln);
		ln.setCommunity(community);
		//System.out.println(ln.getUsers().get(0).getId() + " : "+community);
	}
	

	private void initCommunity(User u) // initialize community of each user
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
		//if(!li.equals(lj))
		//{
			lj.addLEdge(li, weight);
		//}
	}
	
	
	private void removeFromCommunity(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		//System.out.println("Node has been removed with itr");
		ln.setCommunity(-1);
	}
	
	public void iterate()
	{
		double m = graph.getEdgeCount();		
		double maxDelta = 0;
		int maxCommunity = -1;
		int up = 0;
		int cap = 0;
		
		ArrayList<LNode> nodesIterated = new ArrayList<LNode>();
		
		while(cap < 3)
		{
			System.out.println("NEW ITERATION");
			double Q = 0;
			calculateModularity();
			do
			{
				Q = modularity;
				nodesIterated.clear();
				up = 0;
				for(Integer i : communities.keySet())
				{
			        Iterator<?> itr = communities.get(i).iterator(); // for all nodes of community i
			        while (itr.hasNext()) 
			        { 
			            LNode ln = (LNode)itr.next(); 
			            if(!nodesIterated.contains(ln))
			            {
			            	nodesIterated.add(ln);
				            
			            	//System.out.println(ln.getUsers().get(0).getId() + " community : " + ln.getCommunity() + " edges : " + ln.getEdges().size());
							maxDelta = calculateDeltaModularity(ln, i, m);
							maxCommunity = i;
				            for(LEdge e : ln.getEdges()) // for each neighbor of ln i 
							{
				            	if(e.getJ().getCommunity() != i)
				            	{
									double delta = calculateDeltaModularity(ln, e.getJ().getCommunity(), m);
									if(delta > maxDelta)
									{
										maxDelta = delta;
										maxCommunity = e.getJ().getCommunity();
									}
				            	}
							}
							
							if(maxDelta > 0 && maxCommunity != ln.getCommunity())
							{
								removeFromCommunity(itr, ln);
								addToCommunity(maxCommunity, ln);
								calculateModularity();
								up++;
							}
							//System.out.println("----- \n");
							maxDelta = 0;
			            }			           
			        } 
				}
				calculateModularity();
				System.out.println(up);
				System.out.println(modularity);
			}while(modularity > Q);

			mergeCommunities();
			cap++;
		}
		
		System.out.println("DONE");
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + graph.getEdgeCount());
		System.out.println("Modularity : " + modularity);
	}
	
	private double calculateDeltaModularity(LNode li, int targetCommunity, double m)
	{
		double delta = 0;
		double m2 = m * 2.0;
		
		int[] w = li.getSumWeightLinkedToCommunity(targetCommunity);
		double s_tot = 0.0;
		double s_in = 0.0;
		
		for(LNode ln : communities.get(targetCommunity))
		{
			int[] s_w = ln.getSumWeightLinkedToCommunity(targetCommunity);
			s_in += s_w[0];
			s_tot += s_w[1];
		}
		
		//double intra_w = w[0]/(2.0 * m);
		//double inter_w = (tot * w[1])/(2.0 * Math.pow(m, 2));
		
		double intra_w = ((s_in + (2.0 * w[0])) / m2) - Math.pow(((s_tot + w[1]) / m2),2);
		double inter_w = (s_in / m2) - Math.pow((s_tot / m2), 2) - Math.pow((w[1] / m2), 2);
		
		delta = intra_w - inter_w;
		
		//System.out.println("to community " + targetCommunity + " delta : " + delta);
		return delta;
	}
	
	private void mergeCommunities()
	{
		removeEmptyCommunities();
		
		HashMap<Integer, HashMap<Integer,Integer>> mergedEdgesPerCommunity = new HashMap<Integer, HashMap<Integer,Integer>>();
		HashMap<Integer, LNode> mergedNodes = new HashMap<Integer, LNode>();
		
		for(Integer i : communities.keySet())
		{
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
		
		//displayCommunities();
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
