package com.julianEngine.graphics;

import java.awt.image.BufferStrategy;
import java.util.Stack;

import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.graphics.shapes.Rectangle;

public class Camera {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	Point cameraPosition;
	int currentID = 0;
	Rectangle bounds;
	float fps;
	boolean showFPS = false;
	boolean changedWorld = false;
	boolean update = false;
	boolean render = false;
	Frame frame;
	private World currentWorld;
	Stack<World> worldHistory = new Stack<World>();
	
	/*--------Code--------------------------*/
	public Camera(Frame frame){
		World.getWorldForID(currentID).attachCamera(this);
		currentWorld = World.getWorldForID(currentID);
		currentWorld.load();
		this.frame = frame;
	}
	
	public void moveToWorld(int id){
		moveToWorld(id, true);
	}
	
	public void moveToWorld(int id, boolean save){
		if(World.getWorldForID(id)!=null){
			if(save)
				worldHistory.push(currentWorld); //if we are going to change worlds, push first
			World.getWorldForID(currentID).removeCamera(this);
			World.getWorldForID(id).attachCamera(this);
			World.getWorldForID(id).setActiveCamera(this);
			currentID = id;
			cameraPosition = new Point(0, 0, 0);
			changedWorld = true;
			currentWorld = World.getWorldForID(currentID);
			currentWorld.load();
		}
	}
	
	public void moveBack(){
		moveToWorld(getPreviousWorld().getID(), false);
	}
	
	public World getPreviousWorld(){
		return (worldHistory.isEmpty())?null:worldHistory.pop();
	}
	
	public void clearWorldHistory(){
		worldHistory.clear();
	}
	
	public World getWorld(){
		return World.getWorldForID(currentID);
	}
	
	public void update(){ //forces the shapes to be updated
		update = true;
	}
	
	public void forceRender(){
		render = true;
	}
	
	public void moveCameraToPoint(Point newPosition){
		cameraPosition = newPosition;
	}
	
	public void moveCamera(Vector path){
		Point newPosition = cameraPosition.addVector(path);
		//if (bounds.isPointInside(newPosition)){
			cameraPosition = newPosition;
		//}
	}
	
	public void setBounds(Rectangle bounds){
		this.bounds = bounds;
	}
	
	public void showFPS(boolean b){
		showFPS = b;
	}
	
	public void setFPS(float fps){
		this.fps = fps;
	}
	
	public Frame getFrame(){
		return frame;
	}
	
	public void setFrame(Frame frame){
		this.frame = frame;
	}
	
	public void setActiveCamera(){
		World.getWorldForID(currentID).setActiveCamera(this);
	}
	
	//Does calculations, then sends 2d shapes to the frame to be rendered
	public void renderPerspective(Frame destination, BufferStrategy bufferStrategy){
		destination.showFPS = showFPS;
		destination.fps = fps;
		World world = World.getWorldForID(currentID);
		Vector toOrigin = Point.subtractPointFromPoint(new Point(0, 0, 0), cameraPosition);
		toOrigin.setY(-1*toOrigin.getY());
		toOrigin.setX(0);
		toOrigin.setY(0);
		toOrigin.setZ(0);
		synchronized(destination){
			if(changedWorld || update){
				destination.setShapes(world.getShapes());
			}
			
			destination.setShift(toOrigin);
			
			destination.render(bufferStrategy, render);
		}
	}
}
