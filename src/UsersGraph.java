import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
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
		graph.addAttribute("layout.stabilization-limit", 0.01);
		graph.addAttribute("layout.quality", 0);
		graph.addAttribute("layout.weight", 10);
		//graph.addAttribute("ui.antialias");
		//graph.addAttribute("ui.quality");
	}

	public void buildNodes()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUser(id);
			if(u.getCentrality().equals("blue"))
			{
				//n.addAttribute("ui.hide");
				u.setHidden(true);
			}
			else
			{
				Node n = graph.addNode(u.getId());
				
				n.addAttribute("ui.class", u.getCentrality());

				System.out.println("Added node : " + u.getId());
			}	
		}
	}
		
	public void buildEdges()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUser(id);
			if(!u.getCentrality().equals("blue"))
			{
				for(String idl : u.getExternalLinks().keySet())
				{
					User lu = u.getExternalLinks().get(idl);
					if(!lu.getCentrality().equals("blue"))
					{
						graph.addEdge(u.getId()+"."+lu.getId(), u.getId(), lu.getId(), true);
					}
					System.out.println("Added edge " + u.getId() + " --> " + lu.getId());
				}
			}
		}
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
		viewer.enableAutoLayout(new LinLog());
		//view.getCamera().setViewCenter(0, 0, 0);
		//view.getCamera().setViewPercent(0.5);
		view.requestFocusInWindow();
	}
}
