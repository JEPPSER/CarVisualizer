package carvisualizer.graphics;

import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ScatterPlot {

	private ArrayList<Car> cars;

	private final int PADDING = 60;

	public ScatterPlot(ArrayList<Car> cars) {
		this.cars = cars;
	}

	public void draw(Canvas canvas, GraphicsContext g, PlotSettings settings) {
		g.scale(settings.scale, settings.scale);

		// Create both axes and draw them
		Axis xAxis = new Axis(cars, canvas.getWidth() - PADDING * 2, Orientation.HORIZONTAL, settings.xAxisAttribute);
		xAxis.draw(g, canvas, settings);
		Axis yAxis = new Axis(cars, canvas.getHeight() - PADDING * 2, Orientation.VERTICAL, settings.yAxisAttribute);
		yAxis.draw(g, canvas, settings);

		ArrayList<Car> filteredCars = filterCars(settings);

		// Draw all entries
		for (int i = 0; i < filteredCars.size(); i++) {
			Car car = filteredCars.get(i);
			if (car.arr[settings.xAxisAttribute] != null && car.arr[settings.yAxisAttribute] != null) {
				double xValue = (double) car.arr[settings.xAxisAttribute];
				double yValue = (double) car.arr[settings.yAxisAttribute];
				double x = PADDING + xAxis.scale * xValue - xAxis.scale * xAxis.start;
				double y = canvas.getHeight() - PADDING - yValue * yAxis.scale + yAxis.scale * yAxis.start;
				g.setFill(car.shapeColor);
				g.fillOval(x - 5 / settings.scale - settings.x, y - 5 / settings.scale + settings.y,
						10 / settings.scale, 10 / settings.scale);
			}
		}

		g.setFill(Color.BLACK);
		g.fillText(String.valueOf(settings.xAxisAttribute), PADDING / 2 - settings.x, PADDING / 2 + settings.y);
		g.fillText(String.valueOf(settings.yAxisAttribute), canvas.getWidth() - PADDING / 2 - settings.x,
				canvas.getHeight() - PADDING / 2 + settings.y);

		g.scale(1 / settings.scale, 1 / settings.scale);
	}

	private ArrayList<Car> filterCars(PlotSettings settings) {
		ArrayList<Car> result = new ArrayList<Car>(cars);
		// Ranges
		for (int i = 0; i < 26; i++) {
			if (settings.ranges.containsKey(i)) {
				for (int j = 0; j < result.size(); j++) {
					if (result.get(j).arr[i] != null) {
						if (!settings.ranges.get(i).contains((double) result.get(j).arr[i])) {
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
