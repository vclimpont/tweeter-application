import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class StatsPanelController {
	
	@FXML
	private Button shrinkButton;
	@FXML
	private AnchorPane statsPanel;
	@FXML
	private Label label_volume;
	@FXML
	private Label label_ordre;
	@FXML
	private Label label_diametre;
	@FXML
	private Label label_degreMoyen;
	
	private String buttonText;
	
	private boolean isShrinked = true;

	private Graph graph;
	
	public void initGraph(Graph g) {
		graph = g;
	}
	
	public void initButtonText() {
		buttonText = shrinkButton.getText().split("\\s+")[0];
	}
	
	public void setStats(UsersBase base) {
		setOrder(base.getUsers().size());
		setDegree(Toolkit.averageDegree(graph));
		setDiameter(Toolkit.diameter(graph));
	}
	
	private void setOrder(int order) {
		label_ordre.setText(Integer.toString(order));
	}

	private void setDegree(double degree) {
		label_degreMoyen.setText(Double.toString(degree));
	}
	
	private void setDiameter(double diameter) {
		label_diametre.setText(Double.toString(diameter));
	}
	
	/*
	EXEMPLE : déclaration d'animation javafx
		
	final Animation hideStatPanel = new Transition() {
		{ setCycleDuration(Duration.millis(250)); }
		protected void interpolate(double frac) {
			final double curWidth = expandedWidth * (1.0 - frac);
			statsPanel.setPrefWidth(curWidth);
			statsPanel.setTranslateX(-expandedWidth + curWidth);
		}
	};
	final Animation showStatPanel = new Transition() {
		{ setCycleDuration(Duration.millis(250)); }
		protected void interpolate(double frac) {
			final double curWidth = expandedWidth * frac;
			statsPanel.setPrefWidth(curWidth);
			statsPanel.setTranslateX(-expandedWidth + curWidth);
		}
	};
	*/
	
	@FXML
    private void shrink() {
		if(isShrinked) {
			statsPanel.setTranslateX(0);
			shrinkButton.setText(buttonText + " ▼");
		} else {
			statsPanel.setTranslateX(300);
			shrinkButton.setText(buttonText + " ▲");
		}
		isShrinked = !isShrinked;
	}
}
