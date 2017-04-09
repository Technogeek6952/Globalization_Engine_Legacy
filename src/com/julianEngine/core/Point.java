package com.julianEngine.core;

public class Point extends Tuple{
	public Point(){
		this(0, 0, 0); //Base point is origin
	}
	
	public Point(double x, double y, double z){
		super(x, y, z);
	}
	
	public void addVectorToThis(Vector vector){
		setTupleArray(addVector(vector).getTupleArray());
	}
	
	public Point addVector(Vector vector){
		return addVectorToPoint(vector, this);
	}
	
	public static Point addVectorToPoint(Vector vector, Point point){
		return new Point(point.getX() + vector.getX(), point.getY() + vector.getY(), point.getZ() + vector.getZ());
	}
	
	public void subtractVectorFromThis(Vector vector){
		setTupleArray(subtractVector(vector).getTupleArray());
	}
	
	public Point subtractVector(Vector vector){
		return subtractVectorFromPoint(vector, this);
	}
	
	public static Point subtractVectorFromPoint(Vector vector, Point point){
		return new Point(point.getX() - vector.getX(), point.getY() - vector.getY(), point.getZ() - vector.getZ());
	}
	
	public Vector vectorTo(Point point){
		return subtractPointFromPoint(this, point);
	}
	
	public static Vector subtractPointFromPoint(Point point1, Point point2){
		return new Vector(point1.getX() - point2.getX(), point1.getY() - point2.getY(), point1.getZ() - point2.getZ());
	}
	
	
	public void move(Vector path){
		addVector(path);
	}
}
