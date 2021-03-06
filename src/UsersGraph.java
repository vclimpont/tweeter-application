//import java.util.Random;

import org.graphstream.graph.*;

public class UsersGraph extends AbstractGraph{

	private Community community;
	
	public UsersGraph(Community _community)
	{
		super("Community "+_community.getNumber()+" Users");
		community = _community;
	}

	public void buildNodes()
	{
		for(LNode ln : community.getLNodes())
		{
			for(User u : ln.getUsers()) // for each user in this community
			{
				Node n = graph.addNode(u.getId()); // add a new node of this user	
				n.setAttribute("ui.class", u.getCentrality()); // set size and color according to centrality
				n.setAttribute("layout.weight", 3);
			}
		}
	}
		
	public void buildEdges()
	{
		for(LNode ln : community.getLNodes())
		{
			for(User u : ln.getUsers()) // for each user in this community
			{
				for(String id : u.getExternalLinks().keySet())
				{
					User uj = u.getExternalLinks().get(id);
					
					if(uj.getCommunity() != null)
					{						
						if(uj.getCommunity().equals(community)) // if ui and uj are in the same community
						{
							Edge e = graph.addEdge(u.getId()+"."+uj.getId(), u.getId(), uj.getId(), true); // create a normal edge
							e.setAttribute("layout.weight", 3);
						}
						else // if they are in different communities
						{
							String cj = "" + uj.getCommunity().getNumber();
							if(graph.getNode(cj) == null) // if the node of community j is not created yet
							{
								Node n = graph.addNode(cj);
								n.setAttribute("ui.class", "community, "+uj.getCommunity().getCentrality());
								n.setAttribute("layout.weight", 20);
							}
							if(graph.getEdge(u.getId()+"."+cj) == null)
							{								
								Edge e = graph.addEdge(u.getId()+"."+cj, u.getId(), cj, true); // create an edge to represent the link towards this community
								e.setAttribute("ui.class", "community");
								e.setAttribute("layout.weight", 20);
							}
						}
					}
				}
			}
		}
	}

	public void build()
	{
		buildNodes();
		buildEdges();
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}
}
