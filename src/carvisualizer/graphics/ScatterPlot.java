package carvisualizer.graphics;

import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import carvisualizer.entities.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ScatterPlot {

	private ArrayList<Car> cars;

	private final int PADDING = 60;
	private final Color[] COLORS = { Color.BLUE, Color.RED, Color.GREEN, Color.PURPLE, Color.YELLOW, Color.PINK };

	public ScatterPlot(ArrayList<Car> cars) {
		this.cars = cars;
	}

	public void draw(Canvas canvas, GraphicsContext g, PlotSettings settings) {
		g.scale(settings.scale, settings.scale);
		g.setFont(new Font("Arial", 12));

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
				checkIfCarClicked(p, settings, car);
				adjustForLens(x, y, settings, g, car);
				g.fillOval(p.x - 5 / settings.scale - settings.x, p.y - 5 / settings.scale + settings.y,
						10 / settings.scale, 10 / settings.scale);
			}
		}

		if (settings.pointClicked) {
			settings.pointClicked = false;
			settings.selectedCar = null;
		}

		g.setFill(Color.BLACK);
		g.fillText(Car.NAMES[settings.yAxisAttribute], PADDING / 2 - settings.x, PADDING / 2 + settings.y);
		g.fillText(Car.NAMES[settings.xAxisAttribute], canvas.getWidth() - PADDING * 2 - settings.x,
				canvas.getHeight() - PADDING / 4 + settings.y);

		g.scale(1 / settings.scale, 1 / settings.scale);

		drawSelectedCar(canvas, g, settings);
	}

	private void drawSelectedCar(Canvas canvas, GraphicsContext g, PlotSettings settings) {
		if (settings.selectedCar != null) {
			g.setFill(Color.BLACK);
			g.fillRect(canvas.getWidth() - 300, 10, 290, canvas.getHeight() - 20);
			g.setFill(Color.WHITE);
			g.fillRect(canvas.getWidth() - 295, 15, 280, canvas.getHeight() - 30);
			g.setFill(Color.BLACK);
			g.setFont(new Font("Arial", (canvas.getHeight() - 130) / 26));
			String str = "";
			for (int i = 0; i < settings.selectedCar.arr.length; i++) {
				str += Car.NAMES[i] + ": " + settings.selectedCar.arr[i] + "\n";
			}
			g.fillText(str, canvas.getWidth() - 290, 15 + g.getFont().getSize());
		}
	}

	private void checkIfCarClicked(Point p, PlotSettings settings, Car car) {
		Point mousePos = new Point(settings.mouseX / settings.scale + settings.x, settings.mouseY / settings.scale - settings.y);
		if (settings.pointClicked && p.distanceTo(mousePos) <= 5 / settings.scale) {
			settings.pointClicked = false;
			settings.fishEyePlaced = true;
			settings.lensPlaced = true;
			settings.selectedCar = car;
		}
	}
	
	private void adjustForLens(double x, double y, PlotSettings settings, GraphicsContext g, Car car) {
		if (!settings.isLens) {
			g.setFill(car.shapeColor);
			return;
		}
		Point carPos = new Point(x, y);
		Point lensPos = new Point(settings.lensX / settings.scale + settings.x, settings.lensY / settings.scale - settings.y);
		double lensRadius = 150 / settings.scale;
		g.setLineWidth(1 / settings.scale);
		g.strokeOval(lensPos.x - settings.x - lensRadius, lensPos.y + settings.y - lensRadius, lensRadius * 2,
				lensRadius * 2);
		if (carPos.distanceTo(lensPos) <= lensRadius) {
			if (car.arr[settings.lensAttribute] != null) {
				int colorIndex = settings.lensValues.indexOf(car.arr[settings.lensAttribute]) % COLORS.length;
				Color color = new Color(COLORS[colorIndex].getRed(), COLORS[colorIndex].getGreen(), COLORS[colorIndex].getBlue(), 0.8);
				g.setFill(color);	
			} else {
				g.setFill(Color.BLACK);
			}
		} else {
			g.setFill(car.shapeColor);
		}
	}

	private Point adjustForFishEye(double x, double y, PlotSettings settings, GraphicsContext g) {
		if (!settings.isFishEye) {
			return new Point(x, y);
		}
		Point fishEyePos = new Point(settings.fishEyeX / settings.scale + settings.x,
				settings.fishEyeY / settings.scale - settings.y);
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
		g.strokeOval(fishEyePos.x - settings.x - lensRadius, fishEyePos.y + settings.y - lensRadius, lensRadius * 2,
				lensRadius * 2);
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
