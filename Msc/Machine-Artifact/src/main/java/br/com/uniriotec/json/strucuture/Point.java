package br.com.uniriotec.json.strucuture;

public class Point {
	private double x;
	private double y;
	
	public Point(){}
	
	public Point(Point source){
		this.x = source.x;
		this.y = source.y;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
}
