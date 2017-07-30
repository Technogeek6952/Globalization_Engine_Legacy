package com.julianEngine.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.julianEngine.Engine2D;
import com.julianEngine.core.Parent.HookListener;
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
	CoordinateSpace relativeSpace;
	
	/*--------Code--------------------------*/
	//full constructor
	public World(int id) throws IDAlreadyInUseException{
		if(!worlds.containsKey(id)){
			worldID = id;
			worlds.put(id, this);
			//create a temporary coordinate space rooted in the frame root. This is because some parts of the code expect all worlds - weather loaded or not - to have a valid coordinate system
			//rooted in the main frame root. It doesn't matter if the origin is in the right spot until the world is actually loaded and drawn, so we don't need to load the
			//height of the frame here, especially since this constructor can be called before that height is set, leading to an error prone situation
			relativeSpace = new CoordinateSpace(Engine2D.frameRootSystem, false, true, 0, 0, 1);
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
		this.relativeSpace = new CoordinateSpace(Engine2D.frameRootSystem, false, true, c.getFrame().getSideBorder(), c.getFrame().getHeight()+c.getFrame().getTitleBorder(), 1); //the new system will have a flipped y axis, with an origin at the bottem left
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
	
	//HOOKS
	Map<String, List<HookListener>> hookListeners = new HashMap<String, List<HookListener>>(); // maps hookID to listeners
	
	@Override
	public void triggerHook(String hookID, HookData data){
		if (hookListeners.get(hookID)!=null){
			for (HookListener l:hookListeners.get(hookID)){
				l.hookTriggered(hookID, data);
			}
		}
	}
	
	@Override
	public void addHookListener(String hookID, HookListener listener){
		if (!hookListeners.containsKey(hookID)){
			hookListeners.put(hookID, new ArrayList<HookListener>());
		}
		hookListeners.get(hookID).add(listener);
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
	/*
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
	*/
	
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

	/*
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
	*/
	
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

	@Override
	public CoordinateSpace getRelativeSpace(){
		return relativeSpace;
	}
	
	@Override
	public CoordinateSpace getDrawingSpace(){
		return new CoordinateSpace(this.getRelativeSpace(), false, true, 0, Engine2D.getInstance().rootFrame.getHeight(), 1);
	}
}
