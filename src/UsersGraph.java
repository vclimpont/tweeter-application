//import java.util.Random;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class UsersGraph {

	private Graph graph;
	private UsersBase base;	
	private LouvainAlgorithm louv;
	
	public UsersGraph(UsersBase _base)
	{
		base = _base;
		initGraph("Tweeter users relationship", "url('file://.//GraphStyle//stylesheet.css')");
		
		louv = new LouvainAlgorithm(base, graph);
	}
	
	public void initGraph(String name, String stylesheet)
	{
		graph = new SingleGraph(name);
		graph.setAttribute("ui.stylesheet", stylesheet);
		graph.setAttribute("layout.stabilization-limit", 0.01);
		graph.setAttribute("layout.quality", 0);
		graph.setAttribute("layout.weight", 10);
		graph.removeAttribute("ui.antialias");
		graph.removeAttribute("ui.quality");
	}

	public void buildNodes()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUser(id);
			if(!u.getCentrality().equals("blue"))
			{
				Node n = graph.addNode(u.getId());
				
				n.setAttribute("ui.class", u.getCentrality());
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
						System.out.println("Added edge " + u.getId() + " --> " + lu.getId());
					}
				}	
			}
		}
	}

	public void build()
	{
		buildNodes();
		buildEdges();
		louv.initModularity();
		louv.iterate();
	}
	
	/*public void displayGraph()
	{
		Viewer viewer = graph.display();
		ViewPanel view = viewer.getDefaultView();
		view.resizeFrame(800, 600);
		viewer.enableAutoLayout(new LinLog());
		//view.getCamera().setViewCenter(0, 0, 0);
		//view.getCamera().setViewPercent(0.5);
		view.requestFocusInWindow();
	}*/
	
	public Graph getGraph()
	{
		return graph;
	}
	
	public LouvainAlgorithm getLouv()
	{
		return louv;
	}

}
