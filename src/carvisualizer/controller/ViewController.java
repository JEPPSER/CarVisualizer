package carvisualizer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.controlsfx.control.RangeSlider;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import carvisualizer.entities.Range;
import carvisualizer.graphics.ScatterPlot;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewController implements CarVisualizerController {

	@FXML
	private Canvas canvas;
	@FXML
	private TabPane tabPane;
	@FXML
	private HBox root;
	@FXML
	private ScrollPane filterScrollPane;
	@FXML
	private VBox filterVBox;

	private GraphicsContext g;
	private ArrayList<Car> cars;
	private PlotSettings settings;
	private ScatterPlot plot;

	private double prevX;
	private double prevY;
	private boolean isMouseDown = false;
	private final int PADDING = 60;

	@FXML
	public void initialize() {
		AnchorPane.setTopAnchor(filterScrollPane, 0.0);
		AnchorPane.setBottomAnchor(filterScrollPane, 0.0);
		AnchorPane.setLeftAnchor(filterScrollPane, 0.0);
		AnchorPane.setRightAnchor(filterScrollPane, 0.0);
		
		File file = new File("res/cars.csv");
		cars = readCSVFile(file);
		settings = new PlotSettings();
		settings.xAxisAttribute = 21;
		settings.yAxisAttribute = 25;
		plot = new ScatterPlot(cars);
		g = canvas.getGraphicsContext2D();
		initFilters();
		draw();
	}
	
	private void initFilters() {
		initRangeFilter("Risk factor", 0, -1, 1, 1);
		initRangeFilter("Normalized losses", 1, 100, 200, 20);
	}
	
	private void initRangeFilter(String text, int attribute, double low, double high, double tick) {
		Range r = findMinAndMax(attribute);
		double max = r.high;
		double min = r.low;
		Label label = new Label(text);
		RangeSlider slider = new RangeSlider(min, max, low, high);
		slider.setMajorTickUnit(tick);
		slider.setMinorTickCount(0);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setSnapToTicks(true);
		filterVBox.getChildren().addAll(label, slider);
		settings.ranges.put(attribute, new Range(low, high));
		slider.setOnMouseReleased(e -> {
			settings.ranges.put(attribute, new Range(slider.getLowValue(), slider.getHighValue()));
			draw();
		});
	}
	
	private Range findMinAndMax(int attribute) {
		double max = 0;
		double min = 0;
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(0).arr[attribute] != null) {
				max = (double) cars.get(0).arr[attribute];
				min = (double) cars.get(0).arr[attribute];
				break;
			}
		}
		for (int i = 1; i < cars.size(); i++) {
			if (cars.get(i).arr[attribute] != null) {
				double val = (double) cars.get(i).arr[attribute];
				if (val > max) {
					max = val;
				} else if (val < min) {
					min = val;
				}
			}
		}
		return new Range(min, max);
	}

	public void inputListeners() {
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

		scene.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0) {
					// Zoom in
					if (settings.scale < 7) {
						settings.scale *= 1.2;
						settings.x += ((canvas.getWidth() - PADDING * 2) / 8) / settings.scale;
						settings.y -= ((canvas.getHeight() - PADDING * 2) / 8) / settings.scale;
					}
				} else {
					// Zoom out
					if (settings.scale > 1) {
						settings.scale /= 1.2;
						settings.x -= ((canvas.getWidth() - PADDING * 2) / 8) / settings.scale;
						settings.y += ((canvas.getHeight() - PADDING * 2) / 8) / settings.scale;
					}
				}
				draw();
				event.consume();
			}
		});

		canvas.setOnMouseDragged(e -> {
			if (!isMouseDown) {
				isMouseDown = true;
			} else {
				settings.x += prevX - e.getX() / settings.scale;
				settings.y -= prevY - e.getY() / settings.scale;
			}
			prevX = e.getX() / settings.scale;
			prevY = e.getY() / settings.scale;
			draw();
		});

		canvas.setOnMouseReleased(e -> {
			isMouseDown = false;
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
