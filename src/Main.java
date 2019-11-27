import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Graph graph = new SingleGraph("Tutorial1");
		graph.addAttribute("ui.stylesheet", "url('file:///..//tweeter-application//GraphStyle//stylesheet.css')");
		
		for(int i = 0; i < 30; i++)
		{
			graph.addNode(""+i);
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
		/*Node n1 = graph.addNode("A");
		Node n2 = graph.addNode("B");
		Node n3 = graph.addNode("C");
		
		graph.addEdge("AB", "A", "B", true);
		graph.addEdge("BC", "B", "C", true);
		graph.addEdge("CA", "C", "A", true);
		
		n1.addAttribute("ui.color", 1);
		n1.addAttribute("ui.size", 50);
		n2.addAttribute("ui.color", 0.8);
		n2.addAttribute("ui.size", 50*0.8);
		n3.addAttribute("ui.color", 0.6);
		n3.addAttribute("ui.size", 50*0.6);*/
		
		graph.display();
	}

}
