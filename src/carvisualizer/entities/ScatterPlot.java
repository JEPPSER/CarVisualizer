package carvisualizer.entities;

import java.util.ArrayList;

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
		// Calculating values for x and y axis
		double xMin = (double) cars.get(0).arr[settings.xAxisAttribute];
		double yMin = (double) cars.get(0).arr[settings.yAxisAttribute];
		double xMax = (double) cars.get(0).arr[settings.xAxisAttribute];
		double yMax = (double) cars.get(0).arr[settings.yAxisAttribute];
		for (int i = 1; i < cars.size(); i++) {
			if (cars.get(i).arr[settings.xAxisAttribute] != null) {
				if ((double) cars.get(i).arr[settings.xAxisAttribute] < xMin) {
					xMin = (double) cars.get(i).arr[settings.xAxisAttribute];
				}
				if ((double) cars.get(i).arr[settings.xAxisAttribute] > xMax) {
					xMax = (double) cars.get(i).arr[settings.xAxisAttribute];
				}
			}
			if (cars.get(i).arr[settings.yAxisAttribute] != null) {
				if ((double) cars.get(i).arr[settings.yAxisAttribute] < yMin) {
					yMin = (double) cars.get(i).arr[settings.yAxisAttribute];
				}
				if ((double) cars.get(i).arr[settings.yAxisAttribute] > yMax) {
					yMax = (double) cars.get(i).arr[settings.yAxisAttribute];
				}
			}
		}

		double xDif = (xMax - xMin);
		double yDif = (yMax - yMin);
		int xTicks = getSignificantDigit(xDif);
		double xIt = 1.0;
		if (xTicks < 5) {
			xIt = 0.5;
			xTicks *= 2;
		}
		int yTicks = getSignificantDigit(yDif);
		double yIt = 1.0;
		if (yTicks < 5) {
			yIt = 0.5;
			yTicks *= 2;
		}

		yTicks += 2;
		xTicks += 2;

		// Values for the y axis
		double yTickSpacing = (canvas.getHeight() - PADDING * 2) / yTicks;
		double yLog = Math.floor(Math.log10(yDif));
		double yIteration = Math.pow(10, yLog);
		yIteration *= yIt;
		double yScale = yTickSpacing / yIteration;
		double yStart = Math.floor(yMin / yIteration)
				* yIteration;

		// Values for the x axis
		double xTickSpacing = (canvas.getWidth() - PADDING * 2) / xTicks;
		double xLog = Math.floor(Math.log10(xDif));
		double xIteration = Math.pow(10, xLog);
		xIteration *= xIt;
		double xScale = xTickSpacing / xIteration;
		double xStart = Math.floor(xMin / xIteration)
				* xIteration;

		g.scale(settings.scale, settings.scale);
		// Drawing the y axis
		for (int i = 0; i < yTicks + 1; i++) {
			double x1 = PADDING - 5 - settings.x;
			double x2 = PADDING + 5 - settings.x;
			double y = (canvas.getHeight() - PADDING) - yTickSpacing * i + settings.y;

			// Grid line
			g.setLineWidth(1);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(PADDING - settings.x, y, canvas.getWidth() - PADDING - settings.x, y);

			// Tick
			g.setLineWidth(2);
			g.setStroke(Color.BLACK);
			g.strokeLine(x1, y, x2, y);
			if (yIteration < 1) {
				g.fillText(String.valueOf(yStart + i * yIteration), x1 - 30, y + 5);
			} else {
				g.fillText(String.valueOf((int) (yStart + i * yIteration)), x1 - 30, y + 5);
			}
		}

		// Drawing the x axis
		for (int i = 0; i < xTicks + 1; i++) {
			double y1 = canvas.getHeight() - PADDING - 5 + settings.y;
			double y2 = canvas.getHeight() - PADDING + 5 + settings.y;
			double x = PADDING + xTickSpacing * i - settings.x;

			// Grid line
			g.setLineWidth(1);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(x, PADDING + settings.y, x, canvas.getHeight() - PADDING + settings.y);

			// Tick
			g.setLineWidth(2);
			g.setStroke(Color.BLACK);
			g.strokeLine(x, y1, x, y2);
			if (xIteration < 1) {
				g.fillText(String.valueOf(xStart + i * xIteration), x - 5, y2 + 20);
			} else {
				g.fillText(String.valueOf((int) (xStart + i * xIteration)), x - 5, y2 + 20);
			}
		}

		g.setStroke(Color.BLACK);
		g.setLineWidth(2);
		g.strokeLine(PADDING - settings.x, PADDING + settings.y, PADDING - settings.x,
				canvas.getHeight() - PADDING + settings.y);
		g.strokeLine(PADDING - settings.x, canvas.getHeight() - PADDING + settings.y,
				canvas.getWidth() - PADDING - settings.x, canvas.getHeight() - PADDING + settings.y);

		// Draw all entries
		for (int i = 0; i < cars.size(); i++) {
			if (cars.get(i).arr[settings.xAxisAttribute] != null && cars.get(i).arr[settings.yAxisAttribute] != null) {
				// Converting entry's x and y to canvas x and y.
				double x = PADDING + xScale * (double) cars.get(i).arr[settings.xAxisAttribute] - xScale * xStart;
				double y = canvas.getHeight() - PADDING - (double) cars.get(i).arr[settings.yAxisAttribute] * yScale
						+ yScale * yStart;
				g.setFill(cars.get(i).shapeColor);
				g.fillOval(x - 5 / settings.scale - settings.x, y - 5 / settings.scale + settings.y, 10 / settings.scale, 10 / settings.scale);
			}
		}
		g.setFill(Color.BLACK);

		g.fillText("y", PADDING / 2 - settings.x, PADDING / 2 + settings.y);
		g.fillText("x", canvas.getWidth() - PADDING / 2 - settings.x, canvas.getHeight() - PADDING / 2 + settings.y);

		g.scale(1 / settings.scale, 1 / settings.scale);
	}

	private int getSignificantDigit(double number) {
		String str = String.valueOf(number);
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != '0' && str.charAt(i) != '.') {
				return Integer.parseInt("" + str.charAt(i));
			}
		}
		return 1;
	}
}
