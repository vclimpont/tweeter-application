import org.graphstream.graph.Node;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InfoUserController {

	@FXML
	private Label usernameLabel;
	
	public void initInfoUser(Node n) {
		usernameLabel.setText(n.getId());
	}
}
