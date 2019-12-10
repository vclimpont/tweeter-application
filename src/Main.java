import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	
	public final int THEME_DARK = 0;
	public final int THEME_LIGHT = 1;
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private StackPane mainViewLayout;
    private AnchorPane statsPanelLayout;
    

	private FxViewPanel panelGraph;
	private FxViewer viewerGraph;
	
	private UsersBase base;
  	private UsersGraph graph;

	private StatsPanelController statController;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Projet Java");
        
        initRootLayout();
        initMainView();
        initView();
        initStatsPanelView();
        
 		
 		// Force the application to quit after closing the window
 		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
 		    @Override
 		    public void handle(WindowEvent t) {
 		    	quit();
 		    }
 		});
 		
		primaryStage.show();
	}
	
	private void initView() {

        base = new UsersBase();
      	graph = new UsersGraph(base);
		
      	// Create a graph viewer, which will contains the graph
		viewerGraph = new FxViewer(graph.getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);		
		// Let graphStream manage the placement of the nodes
		viewerGraph.enableAutoLayout();
		
		panelGraph = (FxViewPanel) viewerGraph.addDefaultView(false, new FxGraphRenderer());
		
        mainViewLayout.getChildren().add(panelGraph);
        mainViewLayout.setAlignment(Pos.CENTER);
        
        panelGraph.setPrefHeight(mainViewLayout.getHeight());

		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void initStatsPanelView() {
		try {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("StatsPanelView.fxml"));
	        statsPanelLayout = (AnchorPane) loader.load();
	        statsPanelLayout.setPrefHeight(rootLayout.getPrefWidth());


	        statController = loader.getController();
            statController.initGraph(graph.getGraph());
            statController.initButtonText();
            
            
            mainViewLayout.getChildren().add(statsPanelLayout);
            mainViewLayout.setAlignment(Pos.CENTER_RIGHT);

            // Listener which check when the rootLayout height change
            rootLayout.heightProperty().addListener((InvalidationListener) observable -> {
            	// Set the statsPanelLayout height depending on rootLayout
            	statsPanelLayout.setPrefHeight(rootLayout.getHeight());
            });
            
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void initMainView() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainView.fxml"));
            mainViewLayout = (StackPane) loader.load();

        	mainViewLayout.setPrefHeight(rootLayout.getHeight());
        	
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

            MainController controller = loader.getController();
            controller.setMain(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void main(String[] args) {
		
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		launch(args);
	}
	
	public void readData(String filename) {
		BufferedReader csvReader;
		String row;
		try {
			csvReader = new BufferedReader(new FileReader("./Data/"+filename));
			while ((row = csvReader.readLine()) != null) {
			    String[] data = row.split("\t");
			    
			    base.rowDataToUser(data);
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set the centrality of users based of the amount of links
		base.setUsersCentrality();
		// Build nodes and edges
		graph.build();
		// Display the graph
		//graph.displayGraph();
 		// Set stats in the panel
 		statController.setStats(base);
	}
	
	public void changeTheme(int theme) {

		rootLayout.getStylesheets().clear();
		statsPanelLayout.getStylesheets().clear();
		
		if(theme == THEME_DARK) {
			rootLayout.getStylesheets().add("/Resources/darkTheme.css");
		} else if(theme == THEME_LIGHT) {
			rootLayout.getStylesheets().add("/Resources/lightTheme.css");
		}
		
	}
	
	public void quit() {
		primaryStage.close();
        Platform.exit();
        System.exit(0);
	}
}
