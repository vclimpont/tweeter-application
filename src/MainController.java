import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController {	

	@FXML
	private Label stats_volume;
	@FXML
	private Label stats_ordre;
	@FXML
	private Label stats_diametre;
	@FXML
	private Label stats_degreMoyen;

	@FXML
	private MenuItem theme_buttonDark;
	@FXML
	private MenuItem theme_lightDefault;
	


	private Main main;
	private FileChooser fileChooser;
	private File lastOpened;
	
	public MainController() {}

    @FXML
    private void open() {
    	fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("News", "*.csv"));
		
		fileChooser.setInitialDirectory(lastOpened);
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		
		if(selectedFile != null) {
			main.readTweets(selectedFile.getName());
			lastOpened = selectedFile.getParentFile();
		}
    }
    
    @FXML
    private void quit() {
		main.quit();
    }

	public void setMain(Main main) {
		this.main = main;
	}

	@FXML
	private void setDarkTheme() {
		main.changeTheme(main.THEME_DARK);
	}
	@FXML
	private void setLightTheme() {
		main.changeTheme(main.THEME_LIGHT);
	}
}
