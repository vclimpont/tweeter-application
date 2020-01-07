import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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

	private TranslateTransition hideStatPanel;
	private TranslateTransition showStatPanel;

	private double diameter = -1;

	public void initGraph(Graph g) {
		graph = g;

		showStatPanel = new TranslateTransition();
		showStatPanel.setDuration(new Duration(150));
		showStatPanel.setNode(statsPanel);
		showStatPanel.setToX(0);
		
		hideStatPanel = new TranslateTransition();
		hideStatPanel.setDuration(new Duration(150));
		hideStatPanel.setNode(statsPanel); 
		hideStatPanel.setToX(statsPanel.getTranslateX());
	}
	
	public void initButtonText() {
		buttonText = shrinkButton.getText().split("\\s+")[0];
	}
	
	public void resetStats(Graph g) {
		graph = g;
		setVolume(graph.getEdgeCount());
		setOrder(graph.getNodeCount());
		setDegree(Toolkit.averageDegree(graph));


		new Thread( () -> {
			diameter = Toolkit.diameter(graph);
		}).start();
		
		Thread setDiameterLoaderThread = new Thread(new Runnable() {
			@Override
            public void run() {
				Runnable updaterLoader = new Runnable() {
                    @Override
                    public void run() {
                    	if(label_diametre.getText().length() > 3)
                    		label_diametre.setText("");
    					if(diameter == -1)
    						label_diametre.setText(label_diametre.getText()+".");
                    }
                };
				Runnable updaterDiameter = new Runnable() {
                    @Override
                    public void run() {
                    	label_diametre.setText(Double.toString(diameter));
                    }
                };

                while (diameter == -1) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {}
                    Platform.runLater(updaterLoader);
                }
                Platform.runLater(updaterDiameter);
			}
		});

		setDiameterLoaderThread.start();
		

		/*new Thread( () -> {
			setDiameter(Toolkit.diameter(graph));
			isLoadingDiameter = false;
		}).start();
		
		new Thread( () -> {
			while(isLoadingDiameter) {
				try {
					if(diameterLoaderString.length() > 3) {
						diameterLoaderString = "";
					}
					diameterLoaderString = diameterLoaderString + ".";
					label_diametre.setText(diameterLoaderString);
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}).start();*/
	}
	
	private void setVolume(int volume) {
		label_volume.setText(Integer.toString(volume));
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
	
	@FXML
    private void shrink() {
		
		if(!isShrinked) {
			hideStatPanel.play();
			shrinkButton.setText(buttonText + " ▲");
		} else {
			showStatPanel.play();
			shrinkButton.setText(buttonText + " ▼");
		}
		isShrinked = !isShrinked;
		
	}
}
