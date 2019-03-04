package carvisualizer.entities;

public class Range {
	
	public double high;
	public double low;
	
	public Range(double low, double high) {
		this.low = low;
		this.high = high;
	}
	
	public boolean contains(double val) {
		if (val >= this.low && val <= this.high) {
			return true;
		}
		return false;
	}
}
