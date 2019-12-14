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
		
		double m = graph.getEdgeCount();
		double s_ij = 0;
		
		for(int k = 0; k < m; k++)
		{
			Edge e = graph.getEdge(k);
			User ui = base.getUser(e.getNode0().getId());
			initCommunity(ui);
			User uj = base.getUser(e.getNode1().getId());
			initCommunity(uj);
			
			createLEdge(ui.getLNode(), uj.getLNode(), 1);
			
			double ki = ui.getExternalLinksNumber() * 1.0;
			double kj = uj.getExternalLinksNumber() * 1.0;
			
			boolean delta = ui.getCommunity() == uj.getCommunity();
			double df = delta ? 1 : 0;
			
			s_ij += (1 - ((ki * kj)/(2*m))) * df;
		}
		
		modularity = (1/(2*m)) * s_ij;
		System.out.println("INIT MODULARITY : "+modularity);
	}
	
	private void calculateModularity()
	{
		double m = graph.getEdgeCount() * 2.0;
		double s_ij = 0;
		
		for(Integer i : communities.keySet())
		{
			for(LNode ln : communities.get(i)) // Pour chaque node dans chaque communauté
			{
				for(LEdge edge : ln.getEdges())
				{
					LNode lj = edge.getJ();
					double ki = ln.getSumWeight() * 1.0;
					double kj = lj.getSumWeight() * 1.0;
					
					boolean delta = ln.getCommunity() == lj.getCommunity();
					double df = delta ? 1.0 : 0.0;
					
					s_ij += ((edge.getWeight() - ((ki * kj)/m)) * df);
				}
			}	
		}
		
		modularity = (1.0/m) * s_ij;
		System.out.println("NEW MODULARITY : "+modularity);
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
		if(!li.equals(lj))
		{
			lj.addLEdge(li, weight);
		}
	}
	
	
	private void removeFromCommunity(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		//System.out.println("Node has been removed with itr");
		ln.setCommunity(-1);
	}
	
	public void iterate()
	{
		double m2 = graph.getEdgeCount() * 2.0;
		System.out.println("m2 :" + m2);
		
		double maxDelta = 0;
		int maxCommunity = -1;
		int up = 0;
		int cap = 0;
		
		ArrayList<LNode> nodesIterated = new ArrayList<LNode>();
		
		while(cap < 3)
		{
			System.out.println("NEW ITERATION");
			maxDelta = 0;
			
			do
			{
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
				           // System.out.println(ln.getUsers().get(0).getId() + " community : " + ln.getCommunity() + " edges : " + ln.getEdges().size());
							for(LEdge e : ln.getEdges())
							{
								double delta = calculateDeltaModularity(ln, e.getJ().getCommunity(), m2);
								if(delta > maxDelta)
								{
									maxDelta = delta;
									maxCommunity = e.getJ().getCommunity();
								}
							}
							
							if(maxDelta > 0.00001 && maxCommunity != ln.getCommunity())
							{
								removeFromCommunity(itr, ln);
								addToCommunity(maxCommunity, ln);
								calculateModularity();
								up++;
							}
						//	System.out.println("----- \n");
							maxDelta = 0;
			            }			           
			        } 
				}
			}while(up > 0);

			mergeCommunities();
			cap++;
		}
		
		System.out.println("DONE");
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + graph.getEdgeCount());
	}
	
	private double calculateDeltaModularity(LNode li, int targetCommunity, double m2)
	{
		double delta = 0;
		
		/*if(li.getCommunity() == targetCommunity) // it's part of this community already
		{
			return delta;
		}*/
		
		double s_in = 0;
		double ki_in = 0;
		double s_tot = 0;
		double ki = 0;
		
		int[] li_w = li.getSumWeightLinkedToCommunity(targetCommunity);
		
				
		ki_in = li_w[0];
		ki = li_w[1];
		//System.out.println(li.getUsers().get(0).getId() + " ki_in : " + ki_in + " | ki : " + ki);
		
			for(LNode lj : communities.get(targetCommunity))
			{
				int[] lj_w = lj.getSumWeightLinkedToCommunity(targetCommunity);
				s_in += lj_w[0];
				s_tot += lj_w[1];
			}

		s_in = s_in * 1.0;
		s_tot = s_tot *1.0;
		ki = ki * 1.0;
		ki_in = ki_in * 1.0;
			
	//System.out.println("s_in : " + s_in + " | s_tot : " + s_tot);
		double aft = (((s_in + 2*ki_in) / m2) - Math.pow((s_tot + ki) / m2, 2));
		double bfr = ((s_in / m2) - Math.pow(s_tot / m2, 2) - Math.pow(ki / m2, 2));
		delta = aft - bfr;
	/*	System.out.println("before : " + bfr);
		System.out.println("after : " + aft);
		System.out.println(delta + " to community " + targetCommunity + "\n");*/
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
				createLEdge(ln, mergedNodes.get(comm), edges.get(comm));
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
