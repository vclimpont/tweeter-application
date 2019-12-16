import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;

import org.graphstream.graph.Node;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.javafx.FxGraphRenderer;
import org.graphstream.ui.view.util.InteractiveElement;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
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
	private FXMLLoader infoUserLoader;
	private AnchorPane userPane;
	
	private boolean isHiddenNode = false;
	
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
        
        panelGraph.addEventFilter(MouseEvent.MOUSE_PRESSED, new MousePressGraph());
        
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
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
	
	public void readData(String filePath) {
		BufferedReader csvReader;
		String row;
		try {
			csvReader = new BufferedReader(new FileReader(filePath));
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
 		// Set stats in the panel
 		statController.resetStats();
	}
	
	public void changeTheme(int theme) {

		rootLayout.getStylesheets().clear();
		
		if(theme == THEME_DARK) {
			rootLayout.getStylesheets().add("/Resources/darkTheme.css");
			graph.getGraph().setAttribute("ui.stylesheet", "url('file://.//GraphStyle//darkGraph.css')");
		} else if(theme == THEME_LIGHT) {
			rootLayout.getStylesheets().add("/Resources/lightTheme.css");
			graph.getGraph().setAttribute("ui.stylesheet", "url('file://.//GraphStyle//lightGraph.css')");
		}
		
	}
	
	public void quit() {
		primaryStage.close();
        Platform.exit();
        System.exit(0);
	}
	

	class MousePressGraph implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			MouseEvent me = ((MouseEvent) event);
			// Find the node we click on
			Node n = (Node) panelGraph.findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), me.getX(), me.getY());
			// IF n == null -> means we did'nt click on a node
			if(n != null) {

				if(userPane != null) {
					panelGraph.getChildren().remove(userPane);
				}
				
		        try {
		    		infoUserLoader = new FXMLLoader();
		            infoUserLoader.setLocation(Main.class.getResource("InfoUserView.fxml"));
					userPane = (AnchorPane) infoUserLoader.load();
					FxViewPanel.positionInArea(userPane, me.getX(), me.getY(), 0, 0, 0, Insets.EMPTY, HPos.LEFT, VPos.CENTER, true);
			        InfoUserController iuc = (InfoUserController) infoUserLoader.getController();
					iuc.initInfoUser(n);
					panelGraph.getChildren().add(userPane);
				} catch (IOException e) {
					e.printStackTrace();
				}
				graph.hideUnselectedNode(n);
				isHiddenNode = true;
			} else {
				panelGraph.getChildren().remove(userPane);
				userPane = null;
				if(isHiddenNode == true) {
					graph.showAllNode();
				}
				isHiddenNode = false;
			}
		}
		
	}
}
