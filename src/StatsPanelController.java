import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class StatsPanelController {
	@FXML
	private Button shrinkButton;
	@FXML
	private AnchorPane statsPanel;
	
	private int expandedWidth = 300;
	private boolean isShrinked = true;
	
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
	
	
	@FXML
    private void shrink() {
		if(isShrinked) {
			statsPanel.setTranslateX(0);
			shrinkButton.setText(">");
		} else {
			statsPanel.setTranslateX(300);
			shrinkButton.setText("<");
		}
		isShrinked = !isShrinked;
	}
}
