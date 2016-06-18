package com.julianEngine.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.graphics.Camera;
import com.julianEngine.graphics.Frame;
import com.julianEngine.utility.Log;

public class World implements Parent{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	static HashMap<Integer, World> worlds = new HashMap<Integer, World>();
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	int worldID;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	ArrayList<Camera> attachedCameras = new ArrayList<Camera>();
	int activeCamera = 0;
	
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
	
	//automatically searches for a world ID that is unused, and creates a world for it
	public static World getNewWorld(){
		int id = 0;
		while(worlds.containsKey(id))
			id++;
		try {
			return new World(id);
		} catch (IDAlreadyInUseException e) {
			e.printStackTrace();
		}
		return null;
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
		s.setParent(this);
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
	
	public static void waitForWorldToBeReady(World world, WorldWaitListener listener){
		boolean ready = false;
		while(!ready&&world.getShapes().size()!=0){
			ready = true;
			//int readyObjects = 0;
			for(Shape s:world.getShapes()){
				if(!s.isReady()){
					Log.debug("Still waiting for ready status from: "+s);
					ready = false;
					break;
				}else{
					//readyObjects++;
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void waitForWorldToBeReady(World world){
		waitForWorldToBeReady(world, null);
	}
	
	public interface WorldWaitListener{
		public void worldChecked(int totalObjects, int readyObjects);
	}

	//Shape/parent
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTopLeftX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTopLeftY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTopLeftZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Point getTopLeft() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void move(Vector path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnchored(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void centerX(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void centerY(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setParent(Parent p) {
		// TODO Auto-generated method stub
		
	}

	public Camera getActiveCamera(){
		if(attachedCameras.size()>0){
			return attachedCameras.get(activeCamera);
		}else{
			return null;
		}
	}
	
	public void setActiveCamera(Camera c){
		if(attachedCameras.contains(c)){
			activeCamera = attachedCameras.indexOf(c);
		}else{
			
		}
	}
	//Parent
	@Override
	public Point getGFXPoint(Point p) {
		Camera activeCamera = getActiveCamera();
		if(activeCamera!=null){
			Frame renderFrame = activeCamera.getFrame();
			return renderFrame.convertPointJEGFXtoJGFX(p);
		}else{
			return new Point(0, 0, 0);
		}
	}

	@Override
	public Frame getContainingFrame() {
		Camera activeCamera = getActiveCamera();
		if(activeCamera!=null){
			Frame renderFrame = activeCamera.getFrame();
			return renderFrame;
		}else{
			return null;
		}
	}
}
