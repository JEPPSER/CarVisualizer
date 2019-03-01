package carvisualizer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import carvisualizer.entities.Car;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ViewController implements CarVisualizerController {
	
	@FXML private Canvas canvas;
	@FXML private TabPane tabPane;
	@FXML private HBox root;
	
	private GraphicsContext g;
	private ArrayList<Car> cars;
	
	@FXML public void initialize() {
		File file = new File("res/cars.csv");
		cars = readCSVFile(file);
		g = canvas.getGraphicsContext2D();
		g.setFill(Color.RED);
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
		for (int i = 0; i < cars.size(); i++) {
			g.setFill(cars.get(i).shapeColor);
			if (cars.get(i).arr[21] != null && cars.get(i).arr[25] != null) {
				g.fillOval((int) cars.get(i).arr[21], (int) cars.get(i).arr[25] / 100, 7, 7);
			}
		}
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
