import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainController {

	@FXML
	private MenuItem theme_buttonDark;
	@FXML
	private MenuItem theme_lightDefault;
	

	private Main main;
	private FileChooser fileChooser;
	private File lastOpened;


    @FXML
    private void open() {
    	fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("News", "*.csv"));
		
		fileChooser.setInitialDirectory(lastOpened);
		File selectedFile = fileChooser.showOpenDialog(new Stage());
		
		if(selectedFile != null) {
			main.readData(selectedFile.getPath());
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

	@FXML
	private void showHelp() {
	    main.showHelp();
	}
	@FXML
	private void showlegend() {
	    main.showLegend();
	}
}
