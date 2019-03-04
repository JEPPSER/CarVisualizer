package carvisualizer.graphics;

import java.util.ArrayList;

import carvisualizer.entities.Car;
import carvisualizer.entities.PlotSettings;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Axis {

	private final int PADDING = 60;

	private Orientation orientation;
	public double tickSpacing;
	public double iteration;
	public double scale;
	public double start;
	public int ticks;

	public Axis(ArrayList<Car> cars, double length, Orientation orientation, int attribute) {
		this.orientation = orientation;
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
				if ((double) cars.get(i).arr[attribute] < min) {
					min = (double) cars.get(i).arr[attribute];
				}
				if ((double) cars.get(i).arr[attribute] > max) {
					max = (double) cars.get(i).arr[attribute];
				}
			}
		}

		double dif = (max - min);
		ticks = getSignificantDigit(dif);
		double it = 1.0;
		if (ticks < 5) {
			it = 0.5;
			ticks *= 2;
		}
		ticks += 2;

		// Values for the y axis
		tickSpacing = length / ticks;
		double log = Math.floor(Math.log10(dif));
		iteration = Math.pow(10, log);
		iteration *= it;
		scale = tickSpacing / iteration;
		start = Math.floor(min / iteration) * iteration;
	}
	
	public void draw(GraphicsContext g, Canvas canvas, PlotSettings settings) {
		if (orientation == Orientation.HORIZONTAL) {
			drawXAxis(g, canvas, settings);
		} else if (orientation == Orientation.VERTICAL) {
			drawYAxis(g, canvas, settings);
		}
	}
	
	private void drawXAxis(GraphicsContext g, Canvas canvas, PlotSettings settings) {
		for (int i = 1; i < ticks + 1; i++) {
			double y1 = canvas.getHeight() - PADDING - 5 + settings.y;
			double y2 = canvas.getHeight() - PADDING + 5 + settings.y;
			double x = PADDING + tickSpacing * i - settings.x;

			// Grid line
			g.setLineWidth(1.0 / settings.scale);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(x, PADDING + settings.y, x, canvas.getHeight() - PADDING + settings.y);

			// Tick
			g.setLineWidth(2.0 / settings.scale);
			g.setStroke(Color.BLACK);
			g.strokeLine(x, y1, x, y2);
			if (iteration < 1) {
				g.fillText(String.valueOf(start + i * iteration), x - 5, y2 + 20);
			} else {
				g.fillText(String.valueOf((int) (start + i * iteration)), x - 5, y2 + 20);
			}
		}
		g.setStroke(Color.BLACK);
		g.setLineWidth(2.0 / settings.scale);
		g.strokeLine(PADDING - settings.x, canvas.getHeight() - PADDING + settings.y,
				canvas.getWidth() - PADDING - settings.x, canvas.getHeight() - PADDING + settings.y);
	}
	
	private void drawYAxis(GraphicsContext g, Canvas canvas, PlotSettings settings) {
		for (int i = 1; i < ticks + 1; i++) {
			double x1 = PADDING - 5 - settings.x;
			double x2 = PADDING + 5 - settings.x;
			double y = (canvas.getHeight() - PADDING) - tickSpacing * i + settings.y;

			// Grid line
			g.setLineWidth(1.0 / settings.scale);
			g.setStroke(Color.GAINSBORO);
			g.strokeLine(PADDING - settings.x, y, canvas.getWidth() - PADDING - settings.x, y);

			// Tick
			g.setLineWidth(2.0 / settings.scale);
			g.setStroke(Color.BLACK);
			g.strokeLine(x1, y, x2, y);
			if (iteration < 1) {
				g.fillText(String.valueOf(start + i * iteration), x1 - 40, y + 5);
			} else {
				g.fillText(String.valueOf((int) (start + i * iteration)), x1 - 40, y + 5);
			}
		}
		g.setStroke(Color.BLACK);
		g.setLineWidth(2.0 / settings.scale);
		g.strokeLine(PADDING - settings.x, PADDING + settings.y, PADDING - settings.x,
				canvas.getHeight() - PADDING + settings.y);
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
