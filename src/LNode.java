import java.util.ArrayList;
import java.util.HashMap;

public class LNode {

	private ArrayList<User> users;
	private int community;
	private ArrayList<LEdge> edgesOut;
	private ArrayList<LEdge> edgesIn;
	
	public LNode(int _community)
	{
		community = _community;
		users = new ArrayList<User>();
		edgesOut = new ArrayList<LEdge>();
		edgesIn = new ArrayList<LEdge>();
	}
	
	public LNode(int _community, ArrayList<User> _users)
	{
		community = _community;
		users = _users;
		edgesOut = new ArrayList<LEdge>();
		edgesIn = new ArrayList<LEdge>();
	}
	
	public int[] getSumWeightLinkedToCommunity(int _community)
	{
		int[] w = {0,0}; // in, tot / kin, ki
		for(LEdge e : edgesOut)
		{
			if(e.getJ().getCommunity() == _community)
			{
				w[0] += e.getWeight();
			}
			w[1] += e.getWeight();
		}
		return w;
	}
	
	public int[] getSumWeightLinkedToCommunityIn(int _community)
	{
		int[] w = {0,0}; // in, tot / kin, ki
		for(LEdge e : edgesIn)
		{
			if(e.getI().getCommunity() == _community)
			{
				w[0] += e.getWeight();
			}
			w[1] += e.getWeight();
		}
		return w;
	}
	
	public int getSumWeight()
	{
		int w = 0; // ki
		for(LEdge e : edgesOut)
		{
			w += e.getWeight();
		}
		return w;
	}
	
	public int getSumWeightIn()
	{
		int w = 0; // ki
		for(LEdge e : edgesIn)
		{
			w += e.getWeight();
		}
		return w;
	}
	
	public void mergeEdges(HashMap<Integer, Integer> mergedEdges)
	{
		for(LEdge e : edgesOut)
		{
			int commJ = e.getJ().getCommunity();
			Integer w = mergedEdges.get(commJ);
			if(w == null)
			{
				mergedEdges.put(commJ, e.getWeight());
			}
			else
			{
				w += e.getWeight();
			}
		}
	}
	
	public void addUser(User u)
	{
		users.add(u);
	}
	
	public void removeUser(User u)
	{
		users.remove(u);
	}
	
	public LEdge addLEdge(LNode j, int w)
	{
		LEdge edge = new LEdge(this, j, w);
		edgesOut.add(edge);
		return edge;
	}
	
	public void addLEdgeIn(LEdge edge)
	{
		edgesIn.add(edge);
	}
	

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public int getCommunity() {
		return community;
	}

	public void setCommunity(int community) {
		this.community = community;
		for(User u : this.users)
		{
			u.setCommunity(community);
		}
	}

	public ArrayList<LEdge> getEdges() {
		return edgesOut;
	}

	public void setEdges(ArrayList<LEdge> edges) {
		this.edgesOut = edges;
	}
	
}
