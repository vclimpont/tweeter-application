import java.util.ArrayList;
import java.util.Iterator;

public class Community {
	
	private ArrayList<LNode> lnodes;
	private int number;
	private String centrality;
	private String name;
	
	private int somme_in;
	private int somme_tot;
	
	public Community(int _number)
	{
		number = _number;
		lnodes = new ArrayList<LNode>();
		name = "";
	}
	
	/**
	 * initialize somme_in (sum of degrees of each node linked to this community) and somme_tot (sum of degrees of each node in this community)
	 */
	public void initSums() {
		somme_in = 0;
		somme_tot = 0;
		for(LNode ln : lnodes)
		{
			int[] w = ln.getSumWeightLinkedToCommunity(number);
			somme_in += w[0];
			somme_tot += w[1];
		}
	}
	
	public void addLNode(LNode ln)
	{
		lnodes.add(ln);
	}
	
	public void removeLNode(LNode ln)
	{
		lnodes.remove(ln);
	}
	
	public void removeLNodeItr(Iterator<?> itr, LNode ln)
	{
		itr.remove();
		ln.setCommunity(null);
	}

	public ArrayList<LNode> getLNodes() {
		return lnodes;
	}

	public void setLNodes(ArrayList<LNode> lnodes) {
		this.lnodes = lnodes;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * returns sum of the degrees of nodes in this community and linked to nodes in this community
	 */
	public int getSomme_in() {
		return somme_in;
	}

	public void setSomme_in(int somme_in) {
		this.somme_in = somme_in;
	}

	/**
	 * returns sum of the degrees of nodes in this community 
	 */
	public int getSomme_tot() {
		return somme_tot;
	}

	public void setSomme_tot(int somme_tot) {
		this.somme_tot = somme_tot;
	}
	
	/**
	 * returns the number of users in this community
	 */
	public int getNbOfUsers()
	{
		int s = 0;
		for(LNode ln : lnodes)
		{
			s += ln.getUsers().size();
		}
		
		return s;
	}
	
	/**
	 * Set the centrality of the community depending on the number of users in it
	 */
	public void setCentrality()
	{
		int nbUsers = getNbOfUsers();
		if(nbUsers < 5)
		{
			centrality = "blue";
		}
		else if(nbUsers < 30)
		{
			centrality = "green";
		}
		else if(nbUsers < 100)
		{
			centrality = "yellow";
		}
		else if(nbUsers < 300)
		{
			centrality = "orange";
		}
		else
		{
			centrality = "red";
		}
	}
	
	/**
	 * Set the name of this community depending on the most centralized user in the community.
	 */
	public void setName()
	{
		String uId = "";
		int maxLinks = 0;
		
		for(LNode ln : lnodes)
		{
			for(User u : ln.getUsers())
			{
				int uLinks = u.getInternalLinksNumber();
				if(uLinks > maxLinks)
				{
					maxLinks = uLinks;
					uId = u.getId();
				}
			}
		}
		
		name = "Communauté de " + uId;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getCentrality()
	{
		return centrality;
	}	
}