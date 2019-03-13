package carvisualizer.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlotSettings {
	
	public int xAxisAttribute;
	public int yAxisAttribute;
	public double x = 0;
	public double y = 0;
	public double scale = 1.0;
	public boolean isFishEye = false;
	public boolean fishEyePlaced = false;
	public boolean isLens = false;
	public boolean lensPlaced = false;
	public int lensAttribute;
	public boolean pointClicked = false;
	public double fishEyeX;
	public double fishEyeY;
	public double mouseX;
	public double mouseY;
	public double lensX;
	public double lensY;
	
	public Car selectedCar;
	public int[] matrixAttributes = { 25, 21, 13, 22 };
	
	public Map<Integer, Range> ranges = new HashMap<>();
	public Map<Integer, CategorySet> categories = new HashMap<>();
	public ArrayList<String> lensValues;
}
