import org.graphstream.graph.Node;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InfoUserController {

	@FXML
	private Label usernameLabel;
	@FXML
	private Label degreSortant_Label;
	@FXML
	private Label degreEntrant_Label;
	
	
	public void initInfoUser(Node n) {
		usernameLabel.setText("@" + n.getId());
		degreEntrant_Label.setText(Integer.toString(n.getInDegree()));
		degreSortant_Label.setText(Integer.toString(n.getOutDegree()));
	}
}
