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
	private AbstractGraph graph;
	private Node clickedNode;
	
	private boolean isCommunityOpened = false;

	public void initInfoCommunity(Main m, Node n) {
		this.main = m;
		if(n != null)
			clickedNode = n;
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
	
		if(isCommunityOpened) {
			main.setCommunityGraph();
		} else {
			
			Community c = main.getCommunitiesGraph().getCommunities().get(Integer.parseInt(clickedNode.getId()));
			UsersGraph graph = new UsersGraph(c);
			graph.build();
			
			main.resetGraph(base, graph);
			main.setCommunityInfoPanel(communityLabel.getText());
		}

        main.changeTheme(main.getTheme());
	}
}
