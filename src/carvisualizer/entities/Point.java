package carvisualizer.entities;

public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double distanceTo(Point p) {
		return Math.hypot(x - p.x, y - p.y);
	}
	
	public double angleTo(Point p) {
		double angle = Math.atan2(p.y - this.y, p.x - this.x);
		if(angle < 0) {
			angle += Math.PI * 2;
		}
		return angle;
	}
}
