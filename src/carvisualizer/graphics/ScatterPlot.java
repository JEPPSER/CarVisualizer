package carvisualizer.graphics;

import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import carvisualizer.entities.Point;
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
				Point p = adjustForFishEye(x, y, settings, g);
				g.setFill(car.shapeColor);
				g.fillOval(p.x - 5 / settings.scale - settings.x, p.y - 5 / settings.scale + settings.y,
						10 / settings.scale, 10 / settings.scale);
			}
		}

		g.setFill(Color.BLACK);
		g.fillText(Car.NAMES[settings.yAxisAttribute], PADDING / 2 - settings.x, PADDING / 2 + settings.y);
		g.fillText(Car.NAMES[settings.xAxisAttribute], canvas.getWidth() - PADDING * 2 - settings.x,
				canvas.getHeight() - PADDING / 4 + settings.y);

		g.scale(1 / settings.scale, 1 / settings.scale);
	}

	private Point adjustForFishEye(double x, double y, PlotSettings settings, GraphicsContext g) {
		if (!settings.isFishEye) {
			return new Point(x, y);
		}
		Point fishEyePos = new Point(settings.fishEyeX / settings.scale + settings.x, settings.fishEyeY / settings.scale - settings.y);
		Point carPos = new Point(x, y);
		double newX = x;
		double newY = y;
		double dist = fishEyePos.distanceTo(carPos);
		double lensRadius = 150 / settings.scale;
		if (dist <= lensRadius) {
			double angle = fishEyePos.angleTo(carPos);
			newX = fishEyePos.x + Math.pow(dist, 0.5) * Math.pow(lensRadius, 0.5) * Math.cos(angle);
			newY = fishEyePos.y + Math.pow(dist, 0.5) * Math.pow(lensRadius, 0.5) * Math.sin(angle);
		}
		g.setFill(Color.BLACK);
		g.setLineWidth(1 / settings.scale);
		g.strokeOval(fishEyePos.x - settings.x - lensRadius, fishEyePos.y + settings.y - lensRadius, lensRadius * 2, lensRadius * 2);
		return new Point(newX, newY);
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
