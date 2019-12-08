import java.util.ArrayList;
import java.util.HashMap;

public class LNode {

	private ArrayList<User> users;
	private int community;
	private ArrayList<LEdge> edges;
	
	public LNode(int _community)
	{
		community = _community;
		users = new ArrayList<User>();
		edges = new ArrayList<LEdge>();
	}
	
	public LNode(int _community, ArrayList<User> _users)
	{
		community = _community;
		users = _users;
		edges = new ArrayList<LEdge>();
	}
	
	public int[] getSumWeightLinkedToCommunity(int _community)
	{
		int[] w = {0,0}; // in, tot / kin, ki
		for(LEdge e : edges)
		{
			if(e.getJ().getCommunity() == _community)
			{
				w[0] += e.getWeight();
			}
			w[1] += e.getWeight();
		}
		return w;
	}
	
	public int getSumWeight()
	{
		int w = 0; // in, tot / kin, ki
		for(LEdge e : edges)
		{
			w += e.getWeight();
		}
		return w;
	}
	
	public void mergeEdges(HashMap<Integer, Integer> mergedEdges)
	{
		for(LEdge e : edges)
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

	public int getCommunity() {
		return community;
	}

	public void setCommunity(int community) {
		this.community = community;
	}

	public ArrayList<LEdge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<LEdge> edges) {
		this.edges = edges;
	}
	
}
