package br.com.uniriotec.json.strucuture;

import java.util.ArrayList;

public class Bounds {
	Point lowerRight;
	Point upperLeft;
	ArrayList<Point> dockers;
	
	public Bounds(){}
	
	public Bounds(Bounds source){
		if(source.lowerRight != null){
			this.lowerRight = new Point(source.lowerRight);
			this.upperLeft = new Point(source.upperLeft);
		}
		
		if(source.dockers != null){
			this.dockers = new ArrayList<Point>(source.dockers.size());
			for(Point point : source.dockers){
				Point newPoint = new Point(point);
				this.dockers.add(newPoint);
			}
		}
	}
	
	public ArrayList<Point> getDockers() {
		return dockers;
	}
	public void setDockers(ArrayList<Point> dockers) {
		this.dockers = dockers;
	}
	public Point getLowerRight() {
		return lowerRight;
	}
	public void setLowerRight(Point lowerRight) {
		this.lowerRight = lowerRight;
	}
	public Point getUpperLeft() {
		return upperLeft;
	}
	public void setUpperLeft(Point upperLeft) {
		this.upperLeft = upperLeft;
	}
}
