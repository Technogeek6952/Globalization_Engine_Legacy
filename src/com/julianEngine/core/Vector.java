package com.julianEngine.core;

public class Vector extends Tuple{
	public Vector(){
		this(0, 0, 0);
	}
	
	public Vector(double x, double y, double z){
		super(x, y, z);
	}
	
	public void addVectorToThis(Vector vector){
		setTupleArray(addVector(vector).getTupleArray());
	}
	
	public Vector addVector(Vector vector){
		return addVectorToVector(this, vector);
	}
	
	public static Vector addVectorToVector(Vector vector1, Vector vector2){
		return new Vector(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public void subtractVectorFromThis(Vector vector){
		setTupleArray(subtractVector(vector).getTupleArray());
	}
	
	public Vector subtractVector(Vector vector){
		return subtractVectorFromVector(this, vector);
	}
	
	public static Vector subtractVectorFromVector(Vector vector1, Vector vector2){
		return new Vector(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ());
	}
	
	public static double getMagnatudeOfVector(Vector vector){
		return Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getZ(), 2) + Math.pow(vector.getY(), 2));
	}
	
	public static double getDirectionOfVector(){
		return 0;
	}
	
	public static Vector rotateVectorXY(Vector vector, double angle){
		double theta = Math.toRadians(angle);
		return new Vector((Math.cos(theta)*vector.getX()) - (Math.sin(theta)*vector.getY()),
							(Math.sin(theta)*vector.getX()) + (Math.cos(theta)*vector.getY()),
							vector.getZ());
	}
	
	public static Vector rotateVectorXZ(Vector vector, double angle){
		double theta = Math.toRadians(angle);
		return new Vector((Math.cos(theta)*vector.getX()) + (Math.sin(theta)*vector.getZ()),
							vector.getY(),
							(Math.sin(theta)*-vector.getX()) + (Math.cos(theta)*vector.getZ()));
	}
	
	public static Vector rotateVectorYZ(Vector vector, double angle){
		double theta = Math.toRadians(angle);
		return new Vector(vector.getX(),
							(Math.cos(theta)*vector.getY()) - (Math.sin(theta)*vector.getZ()),
							(Math.sin(theta)*vector.getY()) + (Math.cos(theta)*vector.getZ()));
	}
}
