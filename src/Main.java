import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Graph graph = new SingleGraph("Tutorial1");
		graph.addAttribute("ui.stylesheet", "url('file:///..//GraphStyle//stylesheet.css')");
		
		for(int i = 0; i < 30; i++)
		{
			Node n = graph.addNode(""+i);
			n.addAttribute("ui.color", i/29.0);
			n.addAttribute("ui.size", 20+i);
		}
		for(int i = 0; i < 30; i++)
		{
			if(i == 29)
			{
				graph.addEdge("290", "29", "0", true);
			}
			else
			{
				graph.addEdge(""+i+""+(i+1), ""+i, ""+(i+1), true);
			}
		}
		
		graph.display();
	}

}
