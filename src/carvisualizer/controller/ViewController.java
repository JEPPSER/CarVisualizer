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
import carvisualizer.graphics.PlotMatrix;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ViewController implements CarVisualizerController {

	@FXML
	private Canvas canvas;
	@FXML
	private Canvas matrixCanvas;
	@FXML
	private CheckBox matrixCheckBox;
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
	@FXML
	private CheckBox lensCheckBox;
	@FXML
	private MenuButton lensAttribute;
	@FXML
	private VBox lensLegend;
	
	private final Color[] COLORS = { Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE, Color.YELLOW, Color.PINK };

	private GraphicsContext g;
	private GraphicsContext gMatrix;
	private ArrayList<Car> cars;
	private PlotSettings settings;
	private ScatterPlot plot;
	private PlotMatrix matrix;

	private double prevX;
	private double prevY;
	private boolean isDragging = false;
	private boolean isMouseDown = false;
	private boolean isSelecting = false;
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
		g = canvas.getGraphicsContext2D();
		gMatrix = matrixCanvas.getGraphicsContext2D();
		plot = new ScatterPlot(cars);
		matrix = new PlotMatrix(cars, matrixCanvas, gMatrix, settings);
		initFilters();
		initLens();
		initAxesMenus();
		draw();
	}
	
	private void initLens() {
		int[] lensAttributes = { 2, 3, 4, 5, 6, 7, 8, 14, 15, 17 };
		settings.lensAttribute = 3;
		createLensLegend(3);
		for (int i = 0; i < lensAttributes.length; i++) {
			final int index = lensAttributes[i];
			MenuItem mi = new MenuItem(Car.NAMES[lensAttributes[i]]);
			lensAttribute.getItems().add(mi);
			mi.setOnAction(e -> {
				settings.lensAttribute = index;
				createLensLegend(index);
				draw();
			});
		}
	}
	
	private void createLensLegend(int attribute) {
		lensLegend.getChildren().clear();
		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).arr[attribute] != null && !values.contains(cars.get(i).arr[attribute])) {
				int colorIndex = values.size() % COLORS.length;
				values.add((String) cars.get(i).arr[attribute]);
				HBox hbox = new HBox();
				hbox.setSpacing(5);
				Circle circle = new Circle(10, 10, 5, COLORS[colorIndex]);
				Label label = new Label((String) cars.get(i).arr[attribute]);
				hbox.getChildren().addAll(circle, label);
				lensLegend.getChildren().add(hbox);
			}
		}
		HBox hbox = new HBox();
		hbox.setSpacing(5);
		Circle circle = new Circle(10, 10, 5, Color.BLACK);
		Label label = new Label("No value");
		hbox.getChildren().addAll(circle, label);
		lensLegend.getChildren().add(hbox);
		settings.lensValues = values;
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
			double canvasWidth = stage.getWidth() - tabPane.getWidth();
			if (matrixCanvas.isVisible()) {
				matrixCanvas.setWidth(canvasWidth / 2);
				canvas.setWidth(canvasWidth / 2);
			} else {
				canvas.setWidth(canvasWidth);
			}
			draw();
		});

		stage.heightProperty().addListener((obs, oldVal, newVal) -> {
			canvas.setHeight(scene.getHeight());
			matrixCanvas.setHeight(scene.getHeight());
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
			if (!settings.lensPlaced) {
				settings.lensX = e.getX();
				settings.lensY = e.getY();
			}
			draw();
		});

		canvas.setOnMouseDragged(e -> {
			if (!isMouseDown) {
				isMouseDown = true;
			} else {
				isDragging = true;
				if (e.getButton() == MouseButton.SECONDARY) {
					if (!isSelecting) {
						isSelecting = true;
						settings.selectRectangle = new Rectangle(e.getX() / settings.scale, e.getY() / settings.scale, 0, 0);
					} else {
						settings.selectRectangle.setWidth(e.getX() / settings.scale - settings.selectRectangle.getX());
						settings.selectRectangle.setHeight(e.getY() / settings.scale - settings.selectRectangle.getY());
					}
				} else {
					settings.x += prevX - e.getX() / settings.scale;
					settings.y -= prevY - e.getY() / settings.scale;
				}
			}
			if (!settings.fishEyePlaced) {
				settings.fishEyeX = e.getX();
				settings.fishEyeY = e.getY();
			}
			if (!settings.lensPlaced) {
				settings.lensX = e.getX();
				settings.lensY = e.getY();
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
			} else if (!settings.lensPlaced && settings.isLens && !isDragging) {
				settings.lensPlaced = true;
			} else if (settings.lensPlaced && settings.isLens && !isDragging) {
				Point mousePos = new Point(e.getX(), e.getY());
				Point lensPos = new Point(settings.lensX, settings.lensY);
				settings.pointClicked = true;
				if (mousePos.distanceTo(lensPos) < 150) {
					settings.lensPlaced = false;
				}
			} else if (!isDragging) {
				settings.pointClicked = true;
			}
			isDragging = false;
			isSelecting = false;
			settings.selectRectangle = null;
			draw();
		});
		
		fishEyeCheckBox.setOnAction(e -> {
			if (!fishEyeCheckBox.isSelected()) {
				lensCheckBox.setDisable(false);
			} else {
				lensCheckBox.setDisable(true);
			}
			settings.fishEyePlaced = false;
			settings.isFishEye = fishEyeCheckBox.isSelected();
			settings.selectedCar = null;
			draw();
		});
		
		lensCheckBox.setOnAction(e -> {
			if (!lensCheckBox.isSelected()) {
				fishEyeCheckBox.setDisable(false);
			} else {
				fishEyeCheckBox.setDisable(true);
			}
			settings.lensPlaced = false;
			settings.isLens = lensCheckBox.isSelected();
			settings.selectedCar = null;
			draw();
		});
		
		matrixCheckBox.setOnAction(e -> {
			if (matrixCheckBox.isSelected()) {
				matrixCanvas.setVisible(true);
				stage.setWidth(canvas.getWidth() * 2 + tabPane.getWidth());
				matrixCanvas.setWidth(canvas.getWidth());
			} else {
				matrixCanvas.setVisible(false);
				stage.setWidth(canvas.getWidth() + tabPane.getWidth());
				matrixCanvas.setWidth(0);
			}
			draw();
		});
		
		matrixCanvas.setOnMouseClicked(e -> {
			settings.xAxisAttribute = matrix.getXAttribute(e.getX());
			settings.yAxisAttribute = matrix.getYAttribute(e.getY());
			draw();
		});
	}

	private void draw() {
		g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gMatrix.clearRect(0, 0, matrixCanvas.getWidth(), matrixCanvas.getHeight());
		plot.draw(canvas, g, settings);
		matrix.draw();
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
