import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class UsersGraph {

	private Graph graph;
	private UsersBase base;	
	
	public UsersGraph(UsersBase _base)
	{
		base = _base;
		graph = new SingleGraph("Tweeter users relationship");
		graph.addAttribute("ui.stylesheet", "url('file:///..//GraphStyle//stylesheet.css')");
	}

	public void buildNodes()
	{
		for(User u : base.getUsers())
		{
			Node n = graph.addNode(u.getId());
			
			double k = (u.getInternalLinksNumber() * 1.0) / base.getMaxLinks();
			setColor(n, k);
			setSize(n, k);
			System.out.println("Added node : " + u.getId() + " k = " + k + " " + u.getInternalLinksNumber() + " " + base.getMaxLinks());	
		}
	}
		
	public void buildEdges()
	{
		for(User u : base.getUsers())
		{
			for(User lu : u.getExternalLinks())
			{
				graph.addEdge(u.getId()+"."+lu.getId(), u.getId(), lu.getId(), true);
				System.out.println("Added edge " + u.getId() + " --> " + lu.getId());
			}
		}
	}
	
	private void setColor(Node n, double alpha)
	{
		n.addAttribute("ui.color", alpha);
	}
	
	private void setSize(Node n, double alpha)
	{
		n.addAttribute("ui.size", 30 + 20 * alpha);
	}
	
	public void build()
	{
		buildNodes();
		buildEdges();
	}
	
	public void displayGraph()
	{
		graph.display();
	}
}
