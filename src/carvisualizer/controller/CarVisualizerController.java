package carvisualizer.controller;

import carvisualizer.StageManager;

public interface CarVisualizerController {
	String VIEW = "/View.fxml";

	StageManager stageManager = StageManager.getInstance();
}
