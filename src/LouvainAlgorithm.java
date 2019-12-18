import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LouvainAlgorithm {
	
	private UsersBase base;
	private HashMap<Integer, Community> communities;

	private double modularity;
	private int nbCommunities;
	private double m;
	
	public LouvainAlgorithm(UsersBase _base)
	{
		base = _base;
		communities = new HashMap<Integer, Community>();
		modularity = 0;
		nbCommunities = 1;
		m = 0.0;
	}
	public void initCommunities()
	{		
		for(String id : base.getUsers().keySet()) // for every users in the base
		{
			User ui = base.getUser(id);
			
			if(!ui.getCentrality().equals("blue"))
			{
				for(String idj : ui.getExternalLinks().keySet())
				{
					User uj = ui.getExternalLinks().get(idj);
					if(!uj.getCentrality().equals("blue"))
					{
						initCommunity(ui);
						initCommunity(uj);
						createLEdge(ui.getLNode(), uj.getLNode(), 1);
						
						m++;
					}
				}
			}
		}
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).initSums();
		}
		
		//displayCommunities();
		calculateModularity();
	}
	
	public void displayCommunities()
	{
		for(Integer i : communities.keySet())
		{
			System.out.println("Community : " + i);
			System.out.println("s_tot : " + communities.get(i).getSomme_tot() + " s_in : " + communities.get(i).getSomme_in());
			for(LNode ln : communities.get(i).getLNodes())
			{
				for(User u : ln.getUsers())
				{
					System.out.println("\t"+u.getId());
				}
				System.out.println();
				for(LEdge le : ln.getEdges())
				{
					System.out.println("\t Edge " +le.getI().getCommunity().getNumber()+ " --> "+le.getJ().getCommunity().getNumber() +  " : " + le.getWeight());
				}
			}
		}
	}
	
	private void calculateModularity()
	{
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
		addToCommunity(communities.get(community), ln);
	}
	
	private void addToCommunity(Community community, LNode ln)
	{
		community.addLNode(ln);
		ln.setCommunity(community);
		//System.out.println(ln.getUsers().get(0).getId() + " : "+community.getNumber());
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
	
	
	public void iterate()
	{	
		double maxDelta = 0;
		int maxCommunity = -1;
		int up = 1;
		
		while(up > 0)
		{
			double Q = 0;
			up = -1;
			calculateModularity();
			do
			{
				Q = modularity;
				up++;
				for(Integer i : communities.keySet())
				{
			        Iterator<?> itr = communities.get(i).getLNodes().iterator(); // for all nodes of community i
			        while (itr.hasNext()) 
			        { 
			            LNode ln = (LNode)itr.next(); 
			            int[] wi = ln.getSumWeightLinkedToCommunity(i);
						int kinMax = wi[0];
						int kiMax = wi[1];
						maxCommunity = i;
						maxDelta = calculateDeltaModularity(ln, i, wi[0], wi[1], m);
		            	//System.out.println(ln.getUsers().get(0).getId() + " community : " + ln.getCommunity().getNumber() + " edges : " + ln.getEdges().size());

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
							Community c_old = communities.get(i);
							Community c_new = communities.get(maxCommunity);
							
							c_old.removeLNodeItr(itr, ln);
							addToCommunity(c_new, ln);
							
							// Update s_in, s_tot
							c_old.setSomme_in(c_old.getSomme_in() - (2*wi[0]));
							c_old.setSomme_tot(c_old.getSomme_tot() - wi[1]);
							c_new.setSomme_in(c_new.getSomme_in() + (2*kinMax));
							c_new.setSomme_tot(c_new.getSomme_tot() + kiMax);
						}
						//System.out.println("----- \n");
						maxDelta = 0;
		            }			           
		        } 
				calculateModularity();
			}while(modularity > Q);
			
			mergeCommunities();
		}
		
		for(Integer i : communities.keySet())
		{
			communities.get(i).setCentrality();
		}
		
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + m);
		System.out.println("Modularity : " + modularity);
	}
	
	private double calculateDeltaModularity(LNode li, int targetCommunity, int kin, int ki, double m)
	{
		double delta = 0;
		double m2 = m * 2.0;
		
		double s_tot = communities.get(targetCommunity).getSomme_tot();
		double s_in = communities.get(targetCommunity).getSomme_in();
		//System.out.println("s_tot : " + s_tot + "  s_in : "+ s_in);
		
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
			Community c = communities.get(i);
			c.getLNodes().clear();
			c.addLNode(mergedNodes.get(i));
			c.initSums();
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
	public HashMap<Integer, Community> getCommunities() {
		return communities;
	}
	
	
}
