package carvisualizer.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.controlsfx.control.RangeSlider;

import carvisualizer.entities.Car;
import carvisualizer.entities.CategorySet;
import carvisualizer.entities.PlotSettings;
import carvisualizer.entities.Point;
import carvisualizer.entities.Range;
import carvisualizer.graphics.ScatterPlot;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
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
	@FXML
	private MenuButton xAxisMenu;
	@FXML
	private MenuButton yAxisMenu;
	@FXML
	private CheckBox fishEyeCheckBox;

	private GraphicsContext g;
	private ArrayList<Car> cars;
	private PlotSettings settings;
	private ScatterPlot plot;

	private double prevX;
	private double prevY;
	private boolean isDragging = false;
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
		initAxesMenus();
		draw();
	}
	
	private void initAxesMenus() {
		int[] axisAttributes = { 0, 1, 9, 10, 11, 12, 13, 16, 18, 19, 20, 21, 22, 23, 24, 25 };
		for (int i = 0; i < axisAttributes.length; i++) {
			final int index = i;
			MenuItem miX = new MenuItem(Car.NAMES[axisAttributes[i]]);
			MenuItem miY = new MenuItem(Car.NAMES[axisAttributes[i]]);
			xAxisMenu.getItems().add(miX);
			yAxisMenu.getItems().add(miY);
			miX.setOnAction(e -> {
				settings.xAxisAttribute = axisAttributes[index];
				draw();
			});
			miY.setOnAction(e -> {
				settings.yAxisAttribute = axisAttributes[index];
				draw();
			});
		}
	}
	
	private void initFilters() {
		initCategoryFilter("Make", 2);
		initCategoryFilter("Fuel type", 3);
		initCategoryFilter("Aspiration", 4);
		initCategoryFilter("Number of doors", 5);
		initCategoryFilter("Body style", 6);
		initCategoryFilter("Drive wheels", 7);
		initCategoryFilter("Engine location", 8);
		initCategoryFilter("Engine type", 14);
		initCategoryFilter("Number of cylinders", 15);
		initCategoryFilter("Fuel system", 17);
		initRangeFilter("Risk factor", 0);
		initRangeFilter("Normalized losses", 1);
		initRangeFilter("Wheel base", 9);
		initRangeFilter("Length", 10);
		initRangeFilter("Width", 11);
		initRangeFilter("Height", 12);
		initRangeFilter("Curb weight", 13);
		initRangeFilter("Engine size", 16);
		initRangeFilter("Bore", 18);
		initRangeFilter("Stroke", 19);
		initRangeFilter("Compression ratio", 20);
		initRangeFilter("Horsepower", 21);
		initRangeFilter("Peak rpm", 22);
		initRangeFilter("City mpg", 23);
		initRangeFilter("Highway mpg", 24);
		initRangeFilter("Price", 25);
	}
	
	private void initCategoryFilter(String text, int attribute) {
		CategorySet cs = findCategories(attribute);
		VBox vbox = new VBox();
		TitledPane tp = new TitledPane(text, vbox);
		tp.setExpanded(false);
		
		for (int i = 0; i < cs.categories.size(); i++) {
			CheckBox cb = new CheckBox(cs.categories.get(i));
			cb.setSelected(true);
			vbox.getChildren().add(cb);
			cb.setOnMouseClicked(e -> {
				CategorySet set = new CategorySet();
				for (int j = 0; j < vbox.getChildren().size(); j++) {
					CheckBox checkBox = (CheckBox) vbox.getChildren().get(j);
					if (checkBox.isSelected()) {
						set.categories.add(checkBox.getText());
					}
				}
				settings.categories.put(attribute, set);
				draw();
			});
		}
		
		filterVBox.getChildren().add(tp);
	}
	
	private void initRangeFilter(String text, int attribute) {
		Range r = findMinAndMax(attribute);
		double max = r.high;
		double min = r.low;
		double tick = (max - min) / 5;
		Label label = new Label(text);
		RangeSlider slider = new RangeSlider(min, max, min, max);
		slider.setMinHeight(30);
		slider.setMaxHeight(30);
		slider.setMajorTickUnit(tick);
		slider.setMinorTickCount(0);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setSnapToTicks(true);
		filterVBox.getChildren().addAll(label, slider);
		settings.ranges.put(attribute, new Range(min, max));
		slider.setOnMouseReleased(e -> {
			settings.ranges.put(attribute, new Range(slider.getLowValue(), slider.getHighValue()));
			draw();
		});
	}
	
	private CategorySet findCategories(int attribute) {
		CategorySet result = new CategorySet();
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).arr[attribute] != null) {
				String str = (String) cars.get(i).arr[attribute];
				if (!result.categories.contains(str)) {
					result.categories.add(str);
				}
			}
		}
		return result;
	}
	
	private Range findMinAndMax(int attribute) {
		double max = 0;
		double min = 0;
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).arr[attribute] != null) {
				max = (double) cars.get(i).arr[attribute];
				min = (double) cars.get(i).arr[attribute];
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

		canvas.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
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
		
		canvas.setOnMouseMoved(e -> {
			settings.mouseX = e.getX();
			settings.mouseY = e.getY();
			if (!settings.fishEyePlaced) {
				settings.fishEyeX = e.getX();
				settings.fishEyeY = e.getY();
			}
			draw();
		});

		canvas.setOnMouseDragged(e -> {
			if (!isMouseDown) {
				isMouseDown = true;
			} else {
				isDragging = true;
				settings.x += prevX - e.getX() / settings.scale;
				settings.y -= prevY - e.getY() / settings.scale;
			}
			if (!settings.fishEyePlaced) {
				settings.fishEyeX = e.getX();
				settings.fishEyeY = e.getY();
			}
			settings.mouseX = e.getX();
			settings.mouseY = e.getY();
			prevX = e.getX() / settings.scale;
			prevY = e.getY() / settings.scale;
			draw();
		});

		canvas.setOnMouseReleased(e -> {
			isMouseDown = false;
			if (!settings.fishEyePlaced && settings.isFishEye && !isDragging) {
				settings.fishEyePlaced = true;
			} else if (settings.fishEyePlaced && settings.isFishEye && !isDragging) {
				Point mousePos = new Point(e.getX(), e.getY());
				Point fishEyePos = new Point(settings.fishEyeX, settings.fishEyeY);
				settings.pointClicked = true;
				if (mousePos.distanceTo(fishEyePos) < 150) {
					settings.fishEyePlaced = false;
				}
			} else if (!isDragging) {
				settings.pointClicked = true;
			}
			isDragging = false;
			draw();
		});
		
		fishEyeCheckBox.setOnAction(e -> {
			settings.fishEyePlaced = false;
			settings.isFishEye = fishEyeCheckBox.isSelected();
			settings.selectedCar = null;
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
