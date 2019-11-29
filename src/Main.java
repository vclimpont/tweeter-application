import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	public final int THEME_DARK = 0;
	public final int THEME_LIGHT = 1;
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private BorderPane mainViewLayout;
    private AnchorPane statsPanelLayout;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Projet Java");
        
        initRootLayout();
        initMainView();
        initStatsPanelView();
        
        
	}
	
	private void initStatsPanelView() {
		try {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("StatsPanelView.fxml"));
	        statsPanelLayout = (AnchorPane) loader.load();
	        statsPanelLayout.setPrefHeight(rootLayout.getPrefWidth());

            // Listener which check when the rootLayout height change
            rootLayout.heightProperty().addListener((InvalidationListener) observable -> {
            	// Set the statsPanelLayout height depending on rootLayout
            	statsPanelLayout.setPrefHeight(rootLayout.getHeight());
            });
            
	        mainViewLayout.setRight(statsPanelLayout);
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void initMainView() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainView.fxml"));
            mainViewLayout = (BorderPane) loader.load();

            // Listener which check when the rootLayout height change
            rootLayout.heightProperty().addListener((InvalidationListener) observable -> {
            	// Set the mainViewLayout height depending on rootLayout
            	mainViewLayout.setPrefHeight(rootLayout.getHeight());
            });
            // Listener which check when the rootLayout width change
            rootLayout.widthProperty().addListener((InvalidationListener) observable -> {
            	// Set the mainViewLayout width depending on rootLayout
            	mainViewLayout.setPrefWidth(rootLayout.getWidth());
            });
            
            rootLayout.setCenter(mainViewLayout);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            

            MainController controller = loader.getController();
            controller.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		launch(args);

				
		UsersBase base = new UsersBase();
		UsersGraph graph = new UsersGraph(base);
	/*	
		// Add some users
		for(int i = 0; i < 10; i++)
		{
			base.addUser(new User(""+i));
		}
		
		// Add some links to users
		Random rand = new Random();
		for(User u : base.getUsers())
		{
			int i = rand.nextInt(10 - 0 + 1) + 0;
			while(i > 0)
			{
				int j = rand.nextInt(10);
				if(u.getId().compareTo(""+j) != 0)
				{
					u.addExternalLink(base.getUser(""+j));
				}
				i--;
			}
		}
		*/
		// Find the maximum amount of links for 1 user
		base.setMaxLinks();
		// Build nodes and edges
		graph.build();
		// Display the graph
		graph.displayGraph(); 
	}
	
	public void readTweets(String filename) {
		BufferedReader csvReader;
		String row;
		int i = 1;
		try {
			csvReader = new BufferedReader(new FileReader("./Data/"+filename));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split("\t");
			    System.out.println(i + " - " + data[1]);
			    i++;
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void changeTheme(int theme) {

		rootLayout.getStylesheets().clear();
		mainViewLayout.getStylesheets().clear();
		statsPanelLayout.getStylesheets().clear();
		
		if(theme == THEME_DARK) {
			rootLayout.getStylesheets().add("/Resources/darkTheme.css");
			mainViewLayout.getStylesheets().add("/Resources/darkTheme.css");
			statsPanelLayout.getStylesheets().add("/Resources/darkTheme.css");
		} else if(theme == THEME_LIGHT) {
			rootLayout.getStylesheets().add("/Resources/lightTheme.css");
			mainViewLayout.getStylesheets().add("/Resources/lightTheme.css");
			statsPanelLayout.getStylesheets().add("/Resources/lightTheme.css");
		}
		
	}
	
	public void quit() {
		primaryStage.close();
	}
}
