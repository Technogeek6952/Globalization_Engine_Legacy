package com.julianEngine.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.julianEngine.Engine2D;
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
	List<PreLoadListener> preLoadListeners = new ArrayList<PreLoadListener>();
	int activeCamera = 0;
	LoadExecutor onLoad = null;
	
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
	
	public void setOnLoad(LoadExecutor executor){
		onLoad = executor;
	}
	
	public void load(){
		if(onLoad!=null)
			onLoad.execute();
	}
	
	//clears all shapes from the world
	public void clear(){
		shapes.clear();
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
	
	public void addShapes(Shape... shapes){
		for(Shape shape:shapes){
			addShape(shape);
		}
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
		//World shouldn't actually draw anything - it just has to be included because we are inheriting the Shape interface from parent
	}

	@Override
	public int getTopLeftX() {
		return 0;
	}

	@Override
	public int getTopLeftY() {
		return 0;
	}

	@Override
	public int getTopLeftZ() {
		return 0;
	}

	@Override
	public Point getTopLeft() {
		return null;
	}

	@Override
	public void move(Vector path) {
	}

	@Override
	public void setAnchored(boolean b) {
	}

	@Override
	public void centerX(Frame frame) {
	}

	@Override
	public void centerY(Frame frame) {
	}

	@Override
	public boolean isReady() {
		return false;
	}
	
	@Override
	public void setParent(Parent p) {
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
		return Engine2D.getInstance().rootFrame;
		/*
		Camera activeCamera = getActiveCamera();
		if(activeCamera!=null){
			Frame renderFrame = activeCamera.getFrame();
			return renderFrame;
		}else{
			return null;
		}
		*/
	}
	
	public interface LoadExecutor{
		//called when the world is loaded - should be used for setting background, playing music, etc
		public void execute();
	}

	@Override
	public Point getRealPointForRelativePoint(Point p) {
		return p;
	}
	
	@Override
	public Point getRelativePointForRealPoint(Point p){
		return p;
	}
	
	@Override
	public Point getOrigin(){
		return new Point();
	}
	
	@Override
	public World getWorld(){
		return this;
	}
	
	@Override
	public double getZoom(){
		return 1;
	}
	
	@Override
	public Frame getFrame(){
		return this.getContainingFrame();
	}
	
	//custom worlds should overload this and call super.preLoad() at the top, to keep this functionality
	@Override
	public void preLoad(){
		for (PreLoadListener listener:preLoadListeners){
			listener.preLoad();
		}
		
		for (Shape s:shapes){
			if (Parent.class.isInstance(s)){
				((Parent)s).preLoad();
			}
		}
	}
	
	public void addPreLoadListener(PreLoadListener listener){
		preLoadListeners.add(listener);
	}
	
	public static interface PreLoadListener{
		public void preLoad();
	}
}
