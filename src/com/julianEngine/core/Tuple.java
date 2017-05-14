package com.julianEngine.core;

public class Tuple {
	private double[] tuple; //(x, y, z)
	
	public Tuple(){
		this(0, 0, 0);
	}
	
	public Tuple(double x, double y, double z){
		tuple = new double[]{x, y, z};
	}
	
	public double getX(){
		return tuple[0];
	}
	
	public double getY(){
		return tuple[1];
	}
	
	public double getZ(){
		return tuple[2];
	}
	
	public double[] getTupleArray(){
		return tuple;
	}
	
	public void setX(double xValue){
		tuple[0] = xValue;
	}
	
	public void setY(double yValue){
		tuple[1] = yValue;
	}
	
	public void setZ(double zValue){
		tuple[2] = zValue;
	}
	
	public void setTupleArray(double[] tupleValue){
		tuple = tupleValue;
	}
}
