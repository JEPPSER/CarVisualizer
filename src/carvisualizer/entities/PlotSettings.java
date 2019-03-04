package carvisualizer.entities;

import java.util.HashMap;
import java.util.Map;

public class PlotSettings {
	
	public int xAxisAttribute;
	public int yAxisAttribute;
	public double x = 0;
	public double y = 0;
	public double scale = 1.0;
	
	public Map<Integer, Range> ranges = new HashMap<>();
	public Map<Integer, CategorySet> categories = new HashMap<>();
}
