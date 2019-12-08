import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
	
	private double modularity;
	private HashMap<Integer, ArrayList<User>> communities;
	
	private int[][] c = {{-100000,-100000},{-100000,100000},{100000,-100000},{100000,100000}};
	
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
		modularity = 0;
		communities = new HashMap<Integer, ArrayList<User>>();
	}

	public void buildNodes()
	{
		for(String id : base.getUsers().keySet())
		{
			Random r = new Random();
			User u = base.getUser(id);
			//u.setCommunity(r.nextInt(4)); // 0 à 3
			
			Node n = graph.addNode(u.getId());
			
			n.addAttribute("ui.class", u.getCentrality());
			//int x = r.nextInt((c[u.getCommunity()][0] + 50000 - (c[u.getCommunity()][0] - 50000)) + 1) + (c[u.getCommunity()][0] - 50000);
			//int y = r.nextInt((c[u.getCommunity()][1] + 50000 - (c[u.getCommunity()][1] - 50000)) + 1) + (c[u.getCommunity()][1] - 50000);
			//n.setAttribute("xyz", x, y, 0);
			//System.out.println("Added node : " + u.getId() + " at x = "+x+" | y = "+y);
		}
	}
		
	public void buildEdges()
	{
		for(String id : base.getUsers().keySet())
		{
			User u = base.getUser(id);
				for(String idl : u.getExternalLinks().keySet())
				{
					User lu = u.getExternalLinks().get(idl);
					graph.addEdge(u.getId()+"."+lu.getId(), u.getId(), lu.getId(), true);
					System.out.println("Added edge " + u.getId() + " --> " + lu.getId());
				}
		}
	}
	
	private void initModularity()
	{
		int m = graph.getEdgeCount();
		double s_ij = 0;
		
		for(Edge e : graph.getEdgeSet())
		{
			double ki = base.getUser(e.getNode0().getId()).getExternalLinksNumber() * 1.0;
			double kj = base.getUser(e.getNode1().getId()).getExternalLinksNumber() * 1.0;
			
			s_ij += (1 - (ki * kj)/(2.0 * m));
		}
		
		modularity = (1/(2.0 * m)) * s_ij;
	}
	
	public void maximizeModularity()
	{

	}
	
	public void build()
	{
		buildNodes();
		buildEdges();
		initModularity();
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
