import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class LouvainAlgorithm {
	
	private UsersBase base;
	private HashMap<Integer, Community> communities;
	private Graph modelGraph;

	private double modularity;
	private int nbCommunities;
	private double m;
	
	public LouvainAlgorithm(UsersBase _base)
	{
		base = _base;
		modelGraph = new SingleGraph("model");
		communities = new HashMap<Integer, Community>();
		modularity = 0;
		nbCommunities = 1;
		m = 0.0;
	}
	
	/**
	 * create the graph based on users relationships and initialize users communities excluding blue centrality's users 
	 */
	public void initCommunities()
	{			
		for(String id : base.getUsers().keySet()) // for every users in the base
		{
			User ui = base.getUser(id);
			
			try {
				modelGraph.addNode(ui.getId()); // try to add the user in the model graph
			}
			catch(Exception e){}
			
			if(!ui.getCentrality().equals("blue")) // if the user is not "blue" centralized
			{
				for(String idj : ui.getExternalLinks().keySet()) 
				{
					User uj = ui.getExternalLinks().get(idj);
					// initialize this user's community and the community of every users linked to him who are not "blue" centralized
					if(!uj.getCentrality().equals("blue"))
					{
						initCommunity(ui);
						initCommunity(uj);
						createLEdge(ui.getLNode(), uj.getLNode(), 1);
						
						// increments the number of edges
						m++;
					}
				}
			}
			
			// try to add every users linked to this user to the model graph and add edges
			for(String idj : ui.getExternalLinks().keySet()) 
			{
				User uj = ui.getExternalLinks().get(idj);
				try {
					modelGraph.addNode(uj.getId());
				}
				catch(Exception e){}
				modelGraph.addEdge(ui.getId()+"."+uj.getId(), ui.getId(), uj.getId(), true);
			}
		}
		
		// for every communities (basically every users who are not "blue" centralized here)
		for(Integer i : communities.keySet())
		{
			communities.get(i).initSums();
		}
		
		calculateModularity();
	}
	
	
	/**
	 * print each community
	 */
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
	
	/**
	 * calculates modularity based on the given formula : https://pdfs.semanticscholar.org/9fa0/3cde48aee448bef4de225cc1f4943ab72095.pdf
	 */
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
	}
	
	/**
	 * add a LNode to a community represented by his number id
	 * @param community : number of the community
	 * @param ln : LNode to add in the community
	 */
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
	
	/**
	 * add LNode to the given community
	 * @param community : the community
	 * @param ln : LNode to add in the community
	 */
	private void addToCommunity(Community community, LNode ln)
	{
		community.addLNode(ln);
		ln.setCommunity(community);
		//System.out.println(ln.getUsers().get(0).getId() + " : "+community.getNumber());
	}
	

	/**
	 * Initialize the community of the given user
	 * @param u : a user
	 */
	private void initCommunity(User u)
	{
		if(u.getCommunity() == null) // community is not set yet
		{
			Community c = new Community(nbCommunities);
			u.setCommunity(c);
			createLNode(c, u);
			nbCommunities++;
		}
	}
	
	/**
	 * Create a new LNode from a user
	 * @param community : the community that contains the new LNode
	 * @param u : a user
	 */
	private void createLNode(Community community, User u)
	{
		LNode ln = new LNode(community);
		ln.addUser(u);
		u.setLNode(ln);
		addToCommunity(community.getNumber(), ln);
	}
	
	/**
	 * create a new LEdge
	 * @param li : an origin LNode
	 * @param lj : a target LNode
	 * @param weight : weight of the LEdge
	 */
	private void createLEdge(LNode li, LNode lj, int weight)
	{
		li.addLEdge(lj, weight);
		lj.addLEdge(li, weight);
	}
	
	/**
	 * main Louvain algorithm iteration
	 * each iteration picks up a node and tries to improve global modularity by removing it from his community
	 * and by adding it to the community of one of its neighbors.
	 * If improvements happen (basically if the delta modularity calculated is > 0) then the node is moved to the community
	 * that maximizes the modularity.
	 * When the modularity can't be improved anymore, nodes of the same community are merged into super node that represents the community.
	 * Edges linked to other communities are also weighted are merged, and edges linked to same communities create loops.
	 */
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
				for(Integer i : communities.keySet()) // for each community
				{
			        Iterator<?> itr = communities.get(i).getLNodes().iterator(); // for all nodes of community i
			        while (itr.hasNext()) 
			        { 
			            LNode ln = (LNode)itr.next(); 
			            
			            // Calculates the delta modularity lost if ln i is removed from its current community
			            int[] wi = ln.getSumWeightLinkedToCommunity(i);
						int kinMax = wi[0];
						int kiMax = wi[1];
						maxCommunity = i;
						maxDelta = calculateDeltaModularity(ln, i, wi[0], wi[1], m);
						
						int[] w = {0,0};
						
			            for(LEdge e : ln.getEdges()) // for each neighbor of ln i 
						{
			            	int edgeCommunityNb = e.getJ().getCommunity().getNumber();
			            	if(edgeCommunityNb != i)
			            	{
			            		// Calculates the delta modularity obtained by moving ln i into an other community
			            		w = ln.getSumWeightLinkedToCommunity(edgeCommunityNb);
								double delta = calculateDeltaModularity(ln, edgeCommunityNb, w[0], w[1], m);
								
								// If there is a positive gain
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
							
							// Remove ln i from its current community and add it to the community that maximizes to modularity
							c_old.removeLNodeItr(itr, ln);
							addToCommunity(c_new, ln);
							
							// Update s_in, s_tot
							c_old.setSomme_in(c_old.getSomme_in() - (2*wi[0]));
							c_old.setSomme_tot(c_old.getSomme_tot() - wi[1]);
							c_new.setSomme_in(c_new.getSomme_in() + (2*kinMax));
							c_new.setSomme_tot(c_new.getSomme_tot() + kiMax);
						}
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
			communities.get(i).setName();
		}
		
		System.out.println("communities : " + communities.size());
		System.out.println("edges : " + m);
		System.out.println("Modularity : " + modularity);
	}
	
	/**
	 * returns the delta modularity obtained by moving li into the targetCommunity (source of the formula : https://perso.uclouvain.be/vincent.blondel/publications/08BG.pdf)
	 * @param li : the node to move
	 * @param targetCommunity : the community to move into
	 * @param kin : sum of the degrees of nodes in targetCommunity and linked to nodes in targetCommuntiy
	 * @param ki : sum of the degrees of nodes in targetCommunity
	 * @param m : number of edges in the graph
	 * @return delta modularity obtained
	 */
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
	
	/**
	 * Merge communities by merging every nodes of a community into one super node, then merging edges into new weighted edges.
	 * At the end of the process, each community is represented by one super node, edges and merged and weighted, and edges linked to a same community are represented by loops.
	 */
	private void mergeCommunities()
	{
		removeEmptyCommunities();
		
		// HashMap of <Community origin number, <Community target number, Weight of the edge>>
		HashMap<Integer, HashMap<Integer,Integer>> mergedEdgesPerCommunity = new HashMap<Integer, HashMap<Integer,Integer>>();
		
		// HashMap of <Community number, Super node of merged nodes>
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
		
	}
	
	/**
	 * Remove empty communities from the communities HashMap
	 */
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
	
	/**
	 * Merge every nodes in lnodes into a super node.
	 * Merge edges from lnodes into mergedEdges HashMap.
	 * Save the merged edges of the given community into mergedEdgesPerCommunity HashMap.
	 * @param lnodes : list of the nodes in this community
	 * @param community : the community to deal with
	 * @param mergedEdgesPerCommunity : Hashmap of merged edges linked to the given community
	 * @param mergedNodes : the super node that represents the community
	 */
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
	
	/**
	 * @return graph based on every users (including "blue" centralized ones)
	 */
	public Graph getModelGraph()
	{
		return modelGraph;
	}
}
