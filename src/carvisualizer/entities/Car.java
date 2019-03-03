package carvisualizer.entities;

import javafx.scene.paint.Color;

public class Car {
	
	/*
	 * 0: riskFactor
	 * 1: normalizedLosses
	 * 2: make
	 * 3: fuelType
	 * 4: aspiration
	 * 5: numOfDoors
	 * 6: bodyStyle
	 * 7: driveWheels
	 * 8: engineLocation
	 * 9: wheelBase
	 * 10: length
	 * 11: width
	 * 12: height
	 * 13: curbWeight
	 * 14: engineType
	 * 15: numOfCylinders
	 * 16: engineSize
	 * 17: fuelSystem
	 * 18: bore
	 * 19: stroke
	 * 20: compressionRatio
	 * 21: horsePower
	 * 22: peakRpm
	 * 23: cityMpg
	 * 24: highwayMpg
	 * 25: price
	 */

	public double shapeSize;
	public Color shapeColor;
	public String shape;
	public Object[] arr = new Object[26];

	public Car(String[] values) {
		for (int i = 0; i < arr.length; i++) {
			if (values[i] == null) {
				arr[i] = null;
			} else {
				if (isInteger(values[i])) {
					arr[i] = Double.parseDouble(values[i]);
				} else if (isDouble(values[i])) {
					arr[i] = Double.parseDouble(values[i]);
				} else {
					arr[i] = values[i];
				}
			}
		}

		// Set shape
		if (arr[3] == "gas") {
			shape = "rectangle";
		} else if (arr[3] == "diesel") {
			shape = "circle";
		}

		shapeSize = (double) arr[13] / 100;
		if ((double) arr[0] == -3) {
			shapeColor = new Color(0, 1, 0, 0.8);
		} else if ((double) arr[0] == -2) {
			shapeColor = new Color(0.33, 1, 0, 0.8);
		} else if ((double) arr[0] == -1) {
			shapeColor = new Color(0.75, 1, 0, 0.8);
		} else if ((double) arr[0] == 0) {
			shapeColor = new Color(1, 1, 0, 0.8);
		} else if ((double) arr[0] == 1) {
			shapeColor = new Color(1, 0.75, 0, 0.8);
		} else if ((double) arr[0] == 2) {
			shapeColor = new Color(1, 0.33, 0, 0.8);
		} else if ((double) arr[0] == 3) {
			shapeColor = new Color(1, 0, 0, 0.8);
		} else {
			shapeColor = new Color(0, 0, 0, 0.8);
		}
	}
	
	private boolean isInteger(String value) {
		for (int i = 0; i < value.length(); i++) {
			if (!(i == 0 && value.charAt(i) == '-')) {
				if (!Character.isDigit(value.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isDouble(String value) {
		int pointCounter = 0;
		for (int i = 0; i < value.length(); i++) {
			if (!(i == 0 && value.charAt(i) == '-')) {
				if (value.charAt(i) == '.') {
					pointCounter++;
				} else if (!Character.isDigit(value.charAt(i))) {
					return false;
				}
			}	
		}
		if (pointCounter != 1) {
			return false;
		}	
		return true;
	}
}
