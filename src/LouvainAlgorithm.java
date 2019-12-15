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
		
		double m = graph.getEdgeCount() * 1.0;
		double s_ij = 0;
		
		for(int k = 0; k < m; k++)
		{
			Edge e = graph.getEdge(k);
			User ui = base.getUser(e.getNode0().getId());
			initCommunity(ui);
			User uj = base.getUser(e.getNode1().getId());
			
			if(uj.getId().equals(ui.getId())) // it's a loop edge
			{
				createLEdge(ui.getLNode(), ui.getLNode(), 1);
			}
			else
			{
				initCommunity(uj);
				createLEdge(ui.getLNode(), uj.getLNode(), 1);
			}
			
			if(ui.getCommunity() == uj.getCommunity()) // actually if ui == uj on initialization
			{
				s_ij += (1.0 - (e.getNode0().getInDegree() * e.getNode1().getOutDegree()/m));
			}
		}
		
		modularity = (1/m) * s_ij;
		System.out.println("INIT MODULARITY : "+modularity);
	}
	
	private void calculateModularity()
	{
		double m = graph.getEdgeCount() * 1.0;
		double s_ij = 0;
		
		for(Integer i : communities.keySet())
		{
			for(LNode li : communities.get(i)) // Pour chaque node dans chaque communauté
			{
				for(Integer j : communities.keySet())
				{
					for(LNode lj : communities.get(j))
					{
						if(li.getCommunity() == lj.getCommunity())
						{
							LEdge edge = li.getLEdgeTowards(lj);
							if(!li.equals(lj) || edge != null) // to consider li == lj only if there is a loop edge
							{
								double arc = (edge == null ? 0 : edge.getWeight());
								double di_in = li.getSumWeightIn() * 1.0;
								double dj_out = lj.getSumWeight() * 1.0;
								//System.out.println(arc + " " +li.getUsers().get(0).getId()+ " " +lj.getUsers().get(0).getId());
							
								double ij = (arc - ((di_in * dj_out)/m));
								//System.out.println("ij : " + ij);
								s_ij += ij;
							}
						}
					}
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
		lj.addLEdgeIn(li, weight);
	}
	
	
	private void removeFromCommunity(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		//System.out.println("Node has been removed with itr");
		ln.setCommunity(-1);
	}
	
	public void iterate()
	{
		double maxDelta = 0;
		int maxCommunity = -1;
		int up = 0;
		int cap = 0;
		ArrayList<LNode> nodesIterated = new ArrayList<LNode>();
		
		while(cap < 3)
		{
			System.out.println("NEW ITERATION");

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
			            if(!nodesIterated.contains(ln)) // already iterated on this node
			            {
			            	nodesIterated.add(ln);
			            	//System.out.println(ln.getUsers().get(0).getId() + " community : " + ln.getCommunity() + " edges : " + ln.getEdges().size());
							for(LEdge e : ln.getEdges())
							{
								double delta = calculateDeltaModularity(ln, e.getJ().getCommunity());
								//System.out.println("Delta value : " + delta);
								if(delta > maxDelta)
								{
									maxDelta = delta;
									maxCommunity = e.getJ().getCommunity();
								}
							}
							for(LEdge e : ln.getEdgesIn())
							{
								double delta = calculateDeltaModularity(ln, e.getI().getCommunity());
								//System.out.println("Delta value : " + delta);
								if(delta > maxDelta)
								{
									maxDelta = delta;
									maxCommunity = e.getI().getCommunity();
								}
							}
							
							if(maxDelta > 0 && maxCommunity != ln.getCommunity())
							{
								removeFromCommunity(itr, ln);
								addToCommunity(maxCommunity, ln);
								//calculateModularity();
								//System.out.println(cap	);
								up++;
							}
							//System.out.println("----- \n");
							maxDelta = 0;
			            }
			           
			        } 
				}
				System.out.println(up);
			}while(up > 0);

			calculateModularity();
			mergeCommunities();
			cap++;
		}
		
		System.out.println("DONE");
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + graph.getEdgeCount());
		calculateModularity();
	}
	
	
	private double calculateDeltaModularity(LNode li, int targetCommunity)
	{
		double delta = 0.0;
		if(li.getCommunity() == targetCommunity)
		{
			return delta;
		}
		
		double after = calculateDeltaModularityOnCommunity(li, targetCommunity);
		double before = calculateDeltaModularityOnCommunity(li, li.getCommunity());
		
		return after - before;
	}
	
	
	private double calculateDeltaModularityOnCommunity(LNode li, int targetCommunity)
	{
		double delta = 0;
		
		double m = graph.getEdgeCount() * 1.0;
		
		int din[] = li.getSumWeightLinkedToCommunityIn(targetCommunity);
		int dout[] = li.getSumWeightLinkedToCommunity(targetCommunity);
		
		double di_c = (din[0] + dout[0]) * 1.0;
		double di_out = dout[1] * 1.0;
		double di_in = din[1] * 1.0;
		
		double s_tot_in = 0.0;
		double s_tot_out = 0.0;
		
		for(LNode lj : communities.get(targetCommunity))
		{
			if(!lj.equals(li) || li.getCommunity() != targetCommunity)
			{
				s_tot_out += lj.getSumWeight();
				s_tot_in += lj.getSumWeightIn();
			}
		}
		
		//System.out.println("di_c : " + di_c + " di_out : " + di_out + " di_in : " + di_in);
		//System.out.println("s_tot_in : " + s_tot_in + " s_tot_out : " + s_tot_out);
		
		delta = (di_c / m) - (((di_out * s_tot_in) + (di_in * s_tot_out)) / Math.pow(m, 2));
		
		//System.out.println(delta + " to community " + targetCommunity + "\n");
		return delta;
	}
	
	private void mergeCommunities()
	{
		removeEmptyCommunities();
		
		HashMap<Integer, HashMap<Integer,Integer>> mergedEdgesPerCommunity = new HashMap<Integer, HashMap<Integer,Integer>>();
		HashMap<Integer, LNode> mergedNodes = new HashMap<Integer, LNode>();
		
		for(Integer i : communities.keySet()) // for all non empty communities
		{
			mergeNodesAndEdges(communities.get(i), i, mergedEdgesPerCommunity, mergedNodes);
		}
		
		for(Integer i : mergedNodes.keySet()) // Add merged edges to merged nodes
		{
			LNode ln = mergedNodes.get(i); // super node in community i
			HashMap<Integer, Integer> edges = mergedEdgesPerCommunity.get(ln.getCommunity());
			for(Integer comm : edges.keySet())
			{	
				createLEdge(ln, mergedNodes.get(comm), edges.get(comm)); // j , weight
			}
		}
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).clear();
			communities.get(i).add(mergedNodes.get(i));
		}
		
		for(Integer i : communities.keySet())
		{
			System.out.println("Community : " + i + " size : " + communities.get(i).size());
			LNode node = communities.get(i).get(0);
			for(User u : node.getUsers())
			{
				System.out.print("\t" + u.getId());
			}
			System.out.println();
			for(LEdge out : node.getEdges())
			{
				System.out.println("\t out : " + out.getI().getCommunity() + " -> " + out.getJ().getCommunity() + " : "+ out.getWeight());
			}
			for(LEdge in : node.getEdgesIn())
			{
				System.out.println("\t in : " + in.getJ().getCommunity() + " <- " + in.getI().getCommunity() + " : "+ in.getWeight());
			}
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
			users.addAll(ln.getUsers()); // merge all users from all nodes in this community
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
