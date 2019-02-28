package carvisualizer.entities;

import javafx.scene.paint.Color;

public class Car {
	
	int riskFactor;
	int normalizedLosses;
	String make;
	String fuelType;
	String aspiration;
	String numOfDoors;
	String bodyStyle;
	String driveWheels;
	String engineLocation;
	double wheelBase;
	double length;
	double width;
	double height;
	int curbWeight;
	String engineType;
	String numOfCylinders;
	int engineSize;
	String fuelSystem;
	double bore;
	double stroke;
	int compressionRatio;
	int horsePower;
	int peakRpm;
	int cityMpg;
	int highwayMpg;
	int price;
	
	double shapeSize;
	Color shapeColor;
	String shape;
	
	public Car(String[] values) {
		riskFactor = Integer.parseInt(values[0]);
		normalizedLosses = Integer.parseInt(values[1]);
		make = values[2];
		fuelType = values[3];
		aspiration = values[4];
		numOfDoors = values[5];
		bodyStyle = values[6];
		driveWheels = values[7];
		engineLocation = values[8];
		wheelBase = Double.parseDouble(values[9]);
		length = Double.parseDouble(values[10]);
		width = Double.parseDouble(values[11]);
		height = Double.parseDouble(values[12]);
		curbWeight = Integer.parseInt(values[13]);
		engineType = values[14];
		numOfCylinders = values[15];
		engineSize = Integer.parseInt(values[16]);
		fuelSystem = values[17];
		bore = Double.parseDouble(values[18]);
		stroke = Double.parseDouble(values[19]);
		compressionRatio = Integer.parseInt(values[20]);
		horsePower = Integer.parseInt(values[21]);
		peakRpm = Integer.parseInt(values[22]);
		cityMpg = Integer.parseInt(values[23]);
		highwayMpg = Integer.parseInt(values[24]);
		price = Integer.parseInt(values[25]);
		
		// Set shape
		if (fuelType == "gas") {
			shape = "rectangle";
		} else if (fuelType == "diesel") {
			shape = "circle";
		}
		
		shapeSize = curbWeight / 100;
		if (riskFactor == -3) {
			shapeColor = new Color(0, 255, 0, 0.8);
		} else if (riskFactor == -2) {
			shapeColor = new Color(85, 255, 0, 0.8);
		} else if (riskFactor == -1) {
			shapeColor = new Color(190, 255, 0, 0.8);
		} else if (riskFactor == 0) {
			shapeColor = new Color(255, 255, 0, 0.8);
		} else if (riskFactor == 1) {
			shapeColor = new Color(255, 190, 0, 0.8);
		} else if (riskFactor == 2) {
			shapeColor = new Color(255, 85, 0, 0.8);
		} else if (riskFactor == 3) {
			shapeColor = new Color(255, 0, 0, 0.8);
		} else {
			shapeColor = new Color(0, 0, 0, 0.8);
		}
	}
}
