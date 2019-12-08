import java.util.ArrayList;

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
