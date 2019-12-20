import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InfoCommunityController {

	@FXML
	private Label communityLabel;
	@FXML
	private Button openButton;
	private Main main;
	private UsersBase base;
	private UsersGraph graph;
	
	private boolean isCommunityOpened = false;

	public void initInfoCommunity(Main m, Node n) {
		this.main = m;
		if(n != null)
			communityLabel.setText(n.getId());
	}
	
	public void initInfoCommunity(Main m, String communityName) {
		isCommunityOpened = true;
		this.main = m;
		communityLabel.setText(communityName);
		openButton.setText("Retour");
	}
	
	@FXML
	private void openCommunity() {
		//TODO : Check if the node is a community node, or a user node
		//		>Open the user graph of the community if it's a community Node
		//		>Return to the community graph if it's a user Node
		
		// Temporary : get an other graph
		
		
		if(isCommunityOpened) {
			main.setCommunityGraph();
		} else {
			readCsv("C:\\Users\\Valentin L\\Desktop\\climat_.csv");
			main.resetGraph(base, graph);
			main.setCommunityInfoPanel(communityLabel.getText());
		}

        main.changeTheme(main.getTheme());
	}
	
	private void readCsv(String filePath) {
		base = new UsersBase();
		graph = new UsersGraph(base);
		
		BufferedReader csvReader;
		String row;
		try {
			csvReader = new BufferedReader(new FileReader(filePath));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split("\t");
			    
			    base.rowDataToUser(data);
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set the centrality of users based of the amount of links
		base.setUsersCentrality();
		// Build nodes and edges
		graph.build();	
	}
}
