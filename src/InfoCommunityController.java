import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class InfoCommunityController {

	@FXML
	private Label label_volume;
	@FXML
	private Label label_ordre;
	@FXML
	private Label label_diametre;
	@FXML
	private Label label_degreMoyen;
	@FXML
	private Label communityLabel;
	@FXML
	private Button openButton;
	private Main main;
	private UsersBase base;
	private Node clickedNode;
	Community community;
	UsersGraph graphUsers;
	
	private boolean isCommunityOpened = false;

	public void initInfoCommunity(Main m, Node n) {
		this.main = m;
		clickedNode = n;
		community = main.getCommunitiesGraph().getCommunities().get(Integer.parseInt(clickedNode.getId()));

		graphUsers = new UsersGraph(community);
		graphUsers.build();
		
		label_volume.setText(Integer.toString(graphUsers.getGraph().getEdgeCount()));
		label_ordre.setText(Integer.toString(graphUsers.getGraph().getNodeCount()));
		label_degreMoyen.setText(Double.toString(Toolkit.averageDegree(graphUsers.getGraph())));
		label_diametre.setText(Double.toString(Toolkit.diameter(graphUsers.getGraph())));
		
		communityLabel.setText(community.getName());
	}
	
	public void setCommunityOpened(boolean isOpened) {
		isCommunityOpened = isOpened;
		if(isOpened)
			openButton.setText("Retour");
		else
			openButton.setText("Ouvrir");
	}
	
	@FXML
	private void openCommunity() {
		//TODO : Check if the node is a community node, or a user node
		//		>Open the user graph of the community if it's a community Node
		//		>Return to the community graph if it's a user Node
		
		
		if(isCommunityOpened) {
			main.setCommunityGraph();
		} else {
			main.resetGraph(base, graphUsers);
			main.setCommunityInfoPanel(clickedNode);
		}

        main.changeTheme(main.getTheme());
	}
}
