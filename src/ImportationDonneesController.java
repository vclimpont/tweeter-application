import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImportationDonneesController {

	@FXML
    private Button button;
	@FXML
    private Label label;
	
	private Main main;
	private FileChooser fileChooser;
	
	public ImportationDonneesController() {}

    @FXML
    private void open() {
    	fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("News", "*.csv"));
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		
		if(selectedFile != null) {
			label.setText(selectedFile.getName());
		}
		main.readTweets(selectedFile.getName());
    }

	public void setMain(Main main) {
		this.main = main;
	}
}
