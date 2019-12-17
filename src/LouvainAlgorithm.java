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
	private HashMap<Integer, Community> communities;

	private double modularity;
	private int nbCommunities;
	
	public LouvainAlgorithm(UsersBase _base, Graph _graph)
	{
		base = _base;
		graph = _graph;
		communities = new HashMap<Integer, Community>();
		modularity = 0;
		nbCommunities = 1;
	}
	
	public void initCommunities()
	{
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
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).initSums();
		}
		
		displayCommunities();
		calculateModularity();
	}
	
	public void displayCommunities()
	{
		for(Integer i : communities.keySet())
		{
			System.out.println("Community : " + i);
			for(LNode ln : communities.get(i).getLNodes())
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
			double intra_w = communities.get(i).getSomme_in()/(2.0 * m);
			double inter_w = Math.pow(communities.get(i).getSomme_tot()/(2.0 * m), 2);

			Q += (intra_w - inter_w);
		}
		modularity = Q;
		//System.out.println("MODULARITY : " + modularity);
	}
	
	private void addToCommunity(int community, LNode ln)
	{
		Community c = communities.get(community);
		if(c == null)
		{
			c = new Community(community);
			communities.put(community, c);
		}
		communities.get(community).addLNode(ln);
		ln.setCommunity(communities.get(community));
		//System.out.println(ln.getUsers().get(0).getId() + " : "+community);
	}
	

	private void initCommunity(User u) // initialize community of each user
	{
		if(u.getCommunity() == null) // community is not set yet
		{
			Community c = new Community(nbCommunities);
			u.setCommunity(c);
			createLNode(c, u);
			nbCommunities++;
		}
	}
	
	private void createLNode(Community community, User u)
	{
		LNode ln = new LNode(community);
		ln.addUser(u);
		u.setLNode(ln);
		addToCommunity(community.getNumber(), ln);
	}
	
	private void createLEdge(LNode li, LNode lj, int weight)
	{
		li.addLEdge(lj, weight);
		lj.addLEdge(li, weight);
	}
	
	
	private void removeFromCommunity(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		//System.out.println("Node has been removed with itr");
		ln.setCommunity(null);
	}
	
	public void iterate()
	{
		double m = graph.getEdgeCount();		
		double maxDelta = 0;
		int maxCommunity = -1;
		int up = 0;
		int cap = 0;
		
		while(cap < 3)
		{
			System.out.println("NEW ITERATION");
			double Q = 0;
			calculateModularity();
			do
			{
				Q = modularity;
				up = 0;
				for(Integer i : communities.keySet())
				{
			        Iterator<?> itr = communities.get(i).getLNodes().iterator(); // for all nodes of community i
			        while (itr.hasNext()) 
			        { 
			            LNode ln = (LNode)itr.next(); 
			            int[] wi = ln.getSumWeightLinkedToCommunity(i);
						maxCommunity = i;
						int kinMax = wi[0];
						int kiMax = wi[1];
			            
		            	//System.out.println(ln.getUsers().get(0).getId() + " community : " + ln.getCommunity() + " edges : " + ln.getEdges().size());
						maxDelta = calculateDeltaModularity(ln, i, wi[0], wi[1], m);
						int[] w = {0,0};
			            for(LEdge e : ln.getEdges()) // for each neighbor of ln i 
						{
			            	int edgeCommunityNb = e.getJ().getCommunity().getNumber();
			            	if(edgeCommunityNb != i)
			            	{
			            		w = ln.getSumWeightLinkedToCommunity(edgeCommunityNb);
								double delta = calculateDeltaModularity(ln, edgeCommunityNb, w[0], w[1], m);
								if(delta > maxDelta)
								{
									maxDelta = delta;
									maxCommunity = edgeCommunityNb;
									kinMax = w[0];
									kiMax = w[1];
								}
			            	}
						}
						
						if(maxDelta > 0 && maxCommunity != ln.getCommunity().getNumber())
						{
							communities.get(i).removeLNodeItr(itr, ln);
							addToCommunity(maxCommunity, ln);
							
							communities.get(i).setSomme_in(communities.get(i).getSomme_in() - wi[0]);
							communities.get(i).setSomme_tot(communities.get(i).getSomme_tot() - wi[1]);
							communities.get(maxCommunity).setSomme_in(communities.get(maxCommunity).getSomme_in() + kinMax);
							communities.get(maxCommunity).setSomme_tot(communities.get(maxCommunity).getSomme_tot() + kiMax);
							up++;
						}
						//System.out.println("----- \n");
						maxDelta = 0;
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
	
	private double calculateDeltaModularity(LNode li, int targetCommunity, int kin, int ki, double m)
	{
		double delta = 0;
		double m2 = m * 2.0;
		
		double s_tot = communities.get(targetCommunity).getSomme_tot();
		double s_in = communities.get(targetCommunity).getSomme_in();
		
		//double intra_w = w[0]/(2.0 * m);
		//double inter_w = (tot * w[1])/(2.0 * Math.pow(m, 2));
		
		double intra_w = ((s_in + (2.0 * kin)) / m2) - Math.pow(((s_tot + ki) / m2),2);
		double inter_w = (s_in / m2) - Math.pow((s_tot / m2), 2) - Math.pow((ki / m2), 2);
		
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
			mergeNodesAndEdges(communities.get(i).getLNodes(), i, mergedEdgesPerCommunity, mergedNodes);
		}
		
		for(Integer i : mergedNodes.keySet()) // Add merged edges to merged nodes
		{
			LNode ln = mergedNodes.get(i);
			HashMap<Integer, Integer> edges = mergedEdgesPerCommunity.get(ln.getCommunity().getNumber());
			for(Integer comm : edges.keySet())
			{
				ln.addLEdge(mergedNodes.get(comm), edges.get(comm));
			}
		}
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).getLNodes().clear();
			communities.get(i).addLNode(mergedNodes.get(i));
		}
		
		//displayCommunities();
	}
	
	private void removeEmptyCommunities()
	{
		// Remove empty communities
		Iterator<Entry<Integer, Community>> itr = communities.entrySet().iterator();
        while (itr.hasNext()) { 
        	@SuppressWarnings("rawtypes")
			Map.Entry community = (Map.Entry)itr.next(); 
        	if(((Community)community.getValue()).getLNodes().isEmpty())
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
		
		mergedNodes.put(community, new LNode(communities.get(community), users));
		mergedEdgesPerCommunity.put(community, mergedEdges);
	}

	public double getModularity()
	{
		return modularity;
	}
	
	
}
