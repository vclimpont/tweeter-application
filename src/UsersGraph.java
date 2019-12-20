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
		graph.setAttribute("ui.stylesheet", "url('file://.//GraphStyle//lightGraph.css')");
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
		louv.initModularity();
		louv.iterate();
	}

	/**
	 * Set the node transparent or not, depending on the 2nd param
	 * @param elem : a Node
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setNodeTransparency(Node elem, boolean isTransparent) {
		String centrality = base.getUser(elem.getId()).getCentrality(); 
		if(isTransparent == true) {
			elem.setAttribute("ui.class", centrality + "_transparent");
		} else {
			elem.setAttribute("ui.class", centrality);
		}
	}

	/**
	 * Set the edge transparent or not, depending on the 2nd param
	 * @param elem : an Edge
	 * @param isTransparent : should this element be transparent ?
	 */
	public void setEdgeTransparency(Edge elem, boolean isTransparent) {
		if(isTransparent == true) {
			elem.setAttribute("ui.class", "transparent");
		} else {
			elem.removeAttribute("ui.class");
		}
	}
	
	public void hideUnselectedNode(Node selectedNode) {
		User selectedUser = base.getUser(selectedNode.getId());
		
		// Set the selected node to visible
		setNodeTransparency(selectedNode, false);
		
		// For each nodes in the graph
		graph.nodes().forEach((Node n)->{
			if(!n.getId().equals(selectedNode.getId())) {
				User u = base.getUser(n.getId());
				if(!selectedUser.getInternalLinks().containsKey(u.getId()) &&
					!selectedUser.getExternalLinks().containsKey(u.getId())) {
					setNodeTransparency(n, true);
				} else {
					setNodeTransparency(n, false);
				}
			}
		});

		graph.edges().forEach((Edge e)->{
			// Hide edges if they aren't linked to the selected node
			if(!e.getSourceNode().getId().equals(selectedNode.getId()) &&
				!e.getTargetNode().getId().equals(selectedNode.getId())) {
				setEdgeTransparency(e, true);
			} else {
				setEdgeTransparency(e, false);
			}
		});
	}
	
	public void showAllNode() {
		// For each nodes
		graph.nodes().forEach((Node n)->{
			setNodeTransparency(n, false);
		});
		// Remove the class attribute for each edges
		graph.edges().forEach(e->setEdgeTransparency(e, false));
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
