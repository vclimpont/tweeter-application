import java.util.ArrayList;
import java.util.HashMap;

public class LNode {

	private ArrayList<User> users;
	private Community community;
	private ArrayList<LEdge> edges;
	
	public LNode(Community _community)
	{
		community = _community;
		users = new ArrayList<User>();
		edges = new ArrayList<LEdge>();
	}
	
	public LNode(Community _community, ArrayList<User> _users)
	{
		community = _community;
		users = _users;
		edges = new ArrayList<LEdge>();
	}
	
	/**
	 * returns the total degree of this node and the degree of this node linked to nodes in the given community
	 * @param _community : a community number
	 */
	public int[] getSumWeightLinkedToCommunity(int _community)
	{
		int[] w = {0,0}; // in, tot / kin, ki
		for(LEdge e : edges)
		{
			if(e.getJ().getCommunity().getNumber() == _community)
			{
				w[0] += e.getWeight();
			}
			w[1] += e.getWeight();
		}
		return w;
	}
	
	/**
	 * returns the total degree of this node
	 */
	public int getSumWeight()
	{
		int w = 0; // ki
		for(LEdge e : edges)
		{
			w += e.getWeight();
		}
		return w;
	}
	
	/**
	 * merge LEdges of this node by adding the weight of each LEdge in the given mergedEdges hashmap
	 * @param mergedEdges : Hashmap of <community number (Integer), weight associated to this community (Integer)>
	 */
	public void mergeEdges(HashMap<Integer, Integer> mergedEdges)
	{
		for(LEdge e : edges)
		{
			int commJ = e.getJ().getCommunity().getNumber();
			Integer w = mergedEdges.get(commJ);
			if(w == null)
			{
				mergedEdges.put(commJ, e.getWeight());
			}
			else
			{
				w += e.getWeight();
				mergedEdges.put(commJ, w);
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
	
	public void addLEdge(LNode j, int w)
	{
		edges.add(new LEdge(this, j, w));
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
		for(User u : this.users)
		{
			u.setCommunity(community);
		}
	}

	public ArrayList<LEdge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<LEdge> edges) {
		this.edges = edges;
	}
	
}
