package carvisualizer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import carvisualizer.entities.ScatterPlot;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ViewController implements CarVisualizerController {
	
	@FXML private Canvas canvas;
	@FXML private TabPane tabPane;
	@FXML private HBox root;
	
	private GraphicsContext g;
	private ArrayList<Car> cars;
	private PlotSettings settings;
	private ScatterPlot plot;
	
	@FXML public void initialize() {
		File file = new File("res/cars.csv");
		cars = readCSVFile(file);
		settings = new PlotSettings();
		settings.xAxisAttribute = 21;
		settings.yAxisAttribute = 25;
		plot = new ScatterPlot(cars);
		g = canvas.getGraphicsContext2D();
		draw();
	}
	
	public void changeSizeListener() {
		Scene scene = canvas.getScene();
		Stage stage = (Stage) scene.getWindow();

		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			canvas.setWidth(scene.getWidth() - tabPane.getWidth());
			draw();
		});

		stage.heightProperty().addListener((obs, oldVal, newVal) -> {
			canvas.setHeight(scene.getHeight());
			draw();
		});
	}
	
	private void draw() {
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		plot.draw(canvas, g, settings);
	}
	
	private ArrayList<Car> readCSVFile(File file) {
		ArrayList<Car> entries = new ArrayList<Car>();
		try {
			String data = new String(Files.readAllBytes(Paths.get(file.getPath())));
			String[] lines = data.split("\n");
			for (int i = 1; i < lines.length; i++) {
				lines[i] = lines[i].replaceAll("\r", "");
				String[] values = lines[i].split(",");
				
				// Converts "?" to null
				for (int j = 0; j < values.length; j++) {
					if (values[j].equals("?")) {
						values[j] = null;
					}
				}
				
				Car car = new Car(values);
				entries.add(car);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return entries;
	}
}
