import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class UsersGraph {

	private Graph graph;
	private UsersBase base;
	
	public UsersGraph(UsersBase _base)
	{
		base = _base;
		graph = new SingleGraph("Tutorial1");
		graph.addAttribute("ui.stylesheet", "url('file:///..//GraphStyle//stylesheet.css')");
	}

	public void buildNodes()
	{
		for(User u : base.getUsers())
		{
			graph.addNode(u.getId());
			System.out.println("Added node : " + u.getId());
		}
	}
	
	public void buildEdges()
	{
		for(User u : base.getUsers())
		{
			for(User lu : u.getLinkedUsers())
			{
				graph.addEdge(u.getId()+lu.getId(), u.getId(), lu.getId());
				System.out.println("Added edge " + u.getId() + " --> " + lu.getId());
			}
		}
	}
}
