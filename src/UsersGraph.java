//import java.util.Random;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class UsersGraph {

	private Graph graph;
	private UsersBase base;	
	private LouvainAlgorithm louv;
	
	//private int[][] c = {{-100000,-100000},{-100000,100000},{100000,-100000},{100000,100000}};
	
	public UsersGraph(UsersBase _base)
	{
		base = _base;
		graph = new SingleGraph("Tweeter users relationship");
		graph.setAttribute("ui.stylesheet", "url('file://.//GraphStyle//stylesheet.css')");
		graph.setAttribute("layout.stabilization-limit", 0.01);
		graph.setAttribute("layout.quality", 0);
		graph.setAttribute("layout.weight", 10);
		graph.removeAttribute("ui.antialias");
		graph.removeAttribute("ui.quality");
		
		louv = new LouvainAlgorithm(base, graph);
	}

	public void buildNodes()
	{
		for(String id : base.getUsers().keySet())
		{
			//Random r = new Random();
			User u = base.getUser(id);
			//u.setCommunity(r.nextInt(4)); // 0 � 3
			if(!u.getCentrality().equals("blue"))
			{
				Node n = graph.addNode(u.getId());
				
				n.setAttribute("ui.class", u.getCentrality());
				//int x = r.nextInt((c[u.getCommunity()][0] + 50000 - (c[u.getCommunity()][0] - 50000)) + 1) + (c[u.getCommunity()][0] - 50000);
				//int y = r.nextInt((c[u.getCommunity()][1] + 50000 - (c[u.getCommunity()][1] - 50000)) + 1) + (c[u.getCommunity()][1] - 50000);
				//n.setAttribute("xyz", x, y, 0);
				//System.out.println("Added node : " + u.getId() + " at x = "+x+" | y = "+y);
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
		louv.initCommunities();
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

}
