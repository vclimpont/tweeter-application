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
	
	private int theme = THEME_LIGHT;
	
	private Stage primaryStage;
    private BorderPane rootLayout;
    private StackPane mainViewLayout;
    private AnchorPane statsPanelLayout;
    
	private FxViewPanel panelGraph;
	private FxViewer viewerGraph;

	private UsersBase base;
  	private UsersGraph graph;
	private UsersBase communityBase;
  	private UsersGraph communityGraph;

	private StatsPanelController statController;
	private BorderPane communityInfoPane;
	private BorderPane infoPane;
	
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
	
	public void initView() {

		communityBase = base = new UsersBase();
		communityGraph = graph = new UsersGraph(base);
		
      	setCommunityGraph();
        
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
	}
	
	public void setCommunityGraph() {

		base = communityBase;
		graph = communityGraph;
		
		// Create a graph viewer, which will contains the graph
		viewerGraph = new FxViewer(graph.getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);		
		// Let graphStream manage the placement of the nodes
		viewerGraph.enableAutoLayout();
				
		panelGraph = (FxViewPanel) viewerGraph.addDefaultView(false, new FxGraphRenderer());
        panelGraph.setPrefHeight(mainViewLayout.getHeight());
        
        panelGraph.addEventFilter(MouseEvent.MOUSE_PRESSED, new MousePressGraph(this));
        
		mainViewLayout.getChildren().add(panelGraph);
        mainViewLayout.setAlignment(Pos.CENTER);
	}
	
	private void initStatsPanelView() {
		try {
			FXMLLoader loader = new FXMLLoader();
	        loader.setLocation(Main.class.getResource("StatsPanelView.fxml"));
	        statsPanelLayout = (AnchorPane) loader.load();

	        statController = loader.getController();
            statController.initGraph(graph.getGraph());
            statController.initButtonText();
              
            mainViewLayout.getChildren().add(statsPanelLayout);
            mainViewLayout.setAlignment(Pos.TOP_RIGHT);
            
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
	
	public void resetGraph(UsersBase b, UsersGraph g) {

		// Update data based on those from the community
		this.base = b;
		this.graph = g;
		
		// Remove old panel which contains graph 
		mainViewLayout.getChildren().remove(panelGraph);
		
		viewerGraph = new FxViewer(graph.getGraph(), FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);		
		viewerGraph.enableAutoLayout();
				
		panelGraph = (FxViewPanel) viewerGraph.addDefaultView(false, new FxGraphRenderer());

        panelGraph.addEventFilter(MouseEvent.MOUSE_PRESSED, new MousePressGraph(this));
        
		mainViewLayout.getChildren().add(panelGraph);
        mainViewLayout.setAlignment(Pos.CENTER);
        
        // Set the theme for the new panel
        changeTheme(this.theme);
	}
	
	public void setCommunityInfoPanel(String communityName) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("InfoCommunityView.fxml"));
			communityInfoPane = (BorderPane) loader.load();
			FxViewPanel.positionInArea(communityInfoPane, 10, 10, 0, 0, 0, Insets.EMPTY, HPos.LEFT, VPos.CENTER, true);
			InfoCommunityController icc = (InfoCommunityController) loader.getController();
			icc.initInfoCommunity(this, communityName);
			panelGraph.getChildren().add(communityInfoPane);
	        changeTheme(this.theme);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getTheme() {
		return this.theme;
	}
	
	public void changeTheme(int theme) {
		
		this.theme = theme;
		
		rootLayout.getStylesheets().clear();
		if(infoPane != null)
			infoPane.getStylesheets().clear();
		if(communityInfoPane != null)
			communityInfoPane.getStylesheets().clear();
		
		if(theme == THEME_DARK) {
			rootLayout.getStylesheets().add("/Resources/darkTheme.css");
			if(infoPane != null)
				infoPane.getStylesheets().add("/Resources/darkTheme.css");
			if(communityInfoPane != null)
				communityInfoPane.getStylesheets().add("/Resources/darkTheme.css");
			graph.getGraph().setAttribute("ui.stylesheet", "url('file://.//GraphStyle//darkGraph.css')");
		} else if(theme == THEME_LIGHT) {
			rootLayout.getStylesheets().add("/Resources/lightTheme.css");
			if(infoPane != null)
				infoPane.getStylesheets().add("/Resources/lightTheme.css");
			if(communityInfoPane != null)
				communityInfoPane.getStylesheets().add("/Resources/lightTheme.css");
			graph.getGraph().setAttribute("ui.stylesheet", "url('file://.//GraphStyle//lightGraph.css')");
		}
	}
	
	public void quit() {
		primaryStage.close();
        Platform.exit();
        System.exit(0);
	}
	
	private boolean isNodeCommunity(Node n) {
		String nodeClass;
		if(n != null) {
			nodeClass = (String) n.getAttribute("ui.class");
			return nodeClass.contains("community");
		}
		return false;
	}

	class MousePressGraph implements EventHandler<MouseEvent> {

		private Main m;
		
		public MousePressGraph(Main main) {
			m = main;
		}

		@Override
		public void handle(MouseEvent event) {
			MouseEvent me = ((MouseEvent) event);
			// Find the node we click on
			Node n = (Node) panelGraph.findGraphicElementAt(EnumSet.of(InteractiveElement.NODE), me.getX(), me.getY());
			// IF n == null -> means we did'nt click on a node
			if(n != null) {

				if(infoPane != null) {
					panelGraph.getChildren().remove(infoPane);
				}
				
		        try {

					FXMLLoader loader = new FXMLLoader();
					
					// if the selected node represents a community
					if(!isNodeCommunity(n)) {
						loader.setLocation(Main.class.getResource("InfoCommunityView.fxml"));
						infoPane = loader.load();
						InfoCommunityController communityController = (InfoCommunityController) loader.getController();
						communityController.initInfoCommunity(m, n);
					} else { // if not, it represents a user
						loader.setLocation(Main.class.getResource("InfoUserView.fxml"));
						infoPane = loader.load();
						InfoUserController userController = (InfoUserController) loader.getController();
						userController.initInfoUser(n);
					}
					FxViewPanel.positionInArea(infoPane, me.getX(), me.getY(), 0, 0, 0, Insets.EMPTY, HPos.LEFT, VPos.CENTER, true);
					
					panelGraph.getChildren().add(infoPane);
					
					infoPane.getStylesheets().clear();
					if(theme == THEME_DARK) {
						infoPane.getStylesheets().add("/Resources/darkTheme.css");
					} else if(theme == THEME_LIGHT) {
						infoPane.getStylesheets().add("/Resources/lightTheme.css");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				graph.hideUnselectedNode(n);
				isHiddenNode = true;
			} else {
				panelGraph.getChildren().remove(infoPane);
				infoPane = null;
				if(isHiddenNode == true) {
					graph.showAllNode();
				}
				isHiddenNode = false;
			}
		}
	}
}
