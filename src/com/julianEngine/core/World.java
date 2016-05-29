package com.julianEngine.core;

import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.graphics.Camera;

public class World {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	static HashMap<Integer, World> worlds = new HashMap<Integer, World>();
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	int worldID;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	ArrayList<Camera> attachedCameras = new ArrayList<Camera>();
	
	/*--------Code--------------------------*/
	//full constructor
	public World(int id) throws IDAlreadyInUseException{
		if(!worlds.containsKey(id)){
			worldID = id;
			worlds.put(id, this);
		}else{
			throw new IDAlreadyInUseException();
		}
	}
	
	public static World getWorldForID(int id){
		return worlds.get(id);
	}
	
	public int getID(){
		return worldID;
	}
	
	public void attachCamera(Camera c){
		attachedCameras.add(c);
	}
	
	public void removeCamera(Camera c){
		attachedCameras.remove(c);
	}
	
	public void addShape(Shape s){
		shapes.add(s);
		updateAttachedCameras();
	}
	
	public void removeShape(Shape s){
		shapes.remove(s);
		updateAttachedCameras();
	}
	
	public void updateAttachedCameras(){
		for(Camera c:attachedCameras){
			c.update();
		}
	}
	
	public ArrayList<Shape> getShapes(){
		return shapes;
	}
	
	/**
	 * Exception thrown by constructor if the world ID is already being used
	 */
	public static class IDAlreadyInUseException extends Exception{
		/*--------Public Static Variables-------*/
		
		/*--------Private Static Variables------*/
		private static final long serialVersionUID = -3065046282863783477L;
		
		/*--------Public Instance Variables-----*/
		
		/*--------Private Instance Variables----*/
		
		/*--------Code--------------------------*/
		
	}
}
