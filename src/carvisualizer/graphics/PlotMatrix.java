package carvisualizer.graphics;

import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PlotMatrix {

	private ArrayList<Car> cars;
	
	public PlotMatrix(ArrayList<Car> cars) {
		this.cars = cars;
	}
	
	public void draw(Canvas canvas, GraphicsContext g, PlotSettings settings) {
		ArrayList<Car> filteredCars = filterCars(settings);
		double plotWidth = canvas.getWidth() / 4;
		double plotHeight = canvas.getHeight() / 4;
		for (int i = 0; i < 4; i++) {
			for (int j = i; j < 4; j++) {
				double x = j * plotWidth;
				double y = i * plotHeight;
				g.strokeRect(x, y, plotWidth, plotHeight);
				if (j == i) {
					g.fillText(Car.NAMES[settings.matrixAttributes[i]], x + 5, y + plotHeight / 2);
				} else {
					drawPlot(x, y, plotWidth, plotHeight, g, settings, j, i, filteredCars);
				}
			}
		}
	}
	
	private void drawPlot(double plotX, double plotY, double width, double height, GraphicsContext g, PlotSettings settings, int xAxisIndex, int yAxisIndex, ArrayList<Car> cars) {
		Axis xAxis = new Axis(cars, width, Orientation.HORIZONTAL, settings.matrixAttributes[xAxisIndex]);
		Axis yAxis = new Axis(cars, height, Orientation.VERTICAL, settings.matrixAttributes[yAxisIndex]);
		double xScale = width / (xAxis.max - xAxis.min);
		double yScale = height / (yAxis.max - yAxis.min);
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).arr[settings.matrixAttributes[xAxisIndex]] != null && cars.get(i).arr[settings.matrixAttributes[yAxisIndex]] != null) {
				double x = plotX + ((double) cars.get(i).arr[settings.matrixAttributes[xAxisIndex]] - xAxis.min) * xScale;
				double y = ((double) cars.get(i).arr[settings.matrixAttributes[yAxisIndex]] - yAxis.min) * yScale;
				y = plotY + height - y;
				g.setFill(cars.get(i).shapeColor);
				if (settings.selectedCars.contains(cars.get(i))) {
					g.setFill(Color.BLUE);
				}
				g.fillOval(x - 2, y - 2, 4, 4);
				g.setFill(Color.BLACK);
			}
		}
	}
	
	private ArrayList<Car> filterCars(PlotSettings settings) {
		ArrayList<Car> result = new ArrayList<Car>(cars);
		for (int i = 0; i < 26; i++) {
			if (settings.ranges.containsKey(i)) { // Ranges
				for (int j = 0; j < result.size(); j++) {
					if (result.get(j).arr[i] != null) {
						if (!settings.ranges.get(i).contains((double) result.get(j).arr[i])) {
							result.remove(j);
							j--;
						}
					}
				}
			} else if (settings.categories.containsKey(i)) { // Categories
				for (int j = 0; j < result.size(); j++) {
					if (result.get(j).arr[i] != null) {
						if (!settings.categories.get(i).categories.contains((String) result.get(j).arr[i])) {
							result.remove(j);
							j--;
						}
					}
				}
			}
		}
		return result;
	}
}
