package carvisualizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import carvisualizer.controller.CarVisualizerController;
import carvisualizer.controller.ViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StageManager {
	private static StageManager instance;
	private Stage mainStage;
	
	private StageManager() {
		
	}
	
	public static StageManager getInstance() {
		if (instance == null) {
			instance = new StageManager();
		}
		return instance;
	}
	
	private Map<String, CarVisualizerController> controllers = new HashMap<>();
	
	public ViewController getViewController() {
		return (ViewController) controllers.get(CarVisualizerController.VIEW);
	}
	
	public void showLevelEditor(Stage primaryStage) throws IOException {
		mainStage = primaryStage;
		mainStage.setTitle("Car Visualizer");
		
		HBox view = (HBox) loadLayout(CarVisualizerController.VIEW);
		Scene mainScene = new Scene(view);
		
		mainStage.setScene(mainScene);
		mainStage.show();
		
		getViewController().changeSizeListener();
	}
	
	private Parent loadLayout(String layout) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(layout));
		Parent nodeLayout = loader.load();
		
		controllers.put(layout, loader.getController());
		
		return nodeLayout;
	}
}
