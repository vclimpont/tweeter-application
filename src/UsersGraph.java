import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

public class UsersGraph {

	private Graph graph;
	private UsersBase base;	
	
	public UsersGraph(UsersBase _base)
	{
		base = _base;
		graph = new SingleGraph("Tweeter users relationship");
		graph.addAttribute("ui.stylesheet", "url('file:///..//GraphStyle//stylesheet.css')");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
	}

	public void buildNodes()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUsers().get(id);
			Node n = graph.addNode(u.getId());
			
			double k = (u.getInternalLinksNumber() * 1.0) / base.getMaxLinks();
			setColor(n, k);
			setSize(n, k);
			System.out.println("Added node : " + u.getId());	
		}
	}
		
	public void buildEdges()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUsers().get(id);
			for(String idl : u.getExternalLinks().keySet())
			{
				User lu = u.getExternalLinks().get(idl);
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
		n.addAttribute("ui.size", 10 + 10 * alpha);
	}
	
	public void build()
	{
		buildNodes();
		buildEdges();
	}
	
	public void displayGraph()
	{
		Viewer viewer = graph.display();
		ViewPanel view = viewer.getDefaultView();
		view.resizeFrame(800, 600);
		view.getCamera().setViewCenter(0, 0, 0);
		view.getCamera().setViewPercent(0.50);
	}
}
