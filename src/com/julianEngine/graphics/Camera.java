package com.julianEngine.graphics;

import java.awt.image.BufferStrategy;

import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.graphics.shapes.Rectangle;
import com.julianEngine.graphics.shapes.Text;

public class Camera {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	Point cameraPosition;
	int currentID = 0;
	Rectangle bounds;
	Text fpsText;
	boolean showFPS = false;
	boolean hideFPS = false;
	boolean changedWorld = false;
	boolean update = false;
	
	/*--------Code--------------------------*/
	public Camera(){
		World.getWorldForID(currentID).attachCamera(this);
	}
	
	public void moveToWorld(int id){
		World.getWorldForID(currentID).removeCamera(this);
		World.getWorldForID(id).attachCamera(this);
		currentID = id;
		cameraPosition = new Point(0, 0, 0);
		changedWorld = true;
	}
	
	public void update(){ //forces the shapes to be updated
		update = true;
	}
	
	public void moveCameraToPoint(Point newPosition){
		cameraPosition = newPosition;
	}
	
	public void moveCamera(Vector path){
		Point newPosition = cameraPosition.addVector(path);
		if (bounds.isPointInside(newPosition)){
			cameraPosition = newPosition;
		}
	}
	
	public void setBounds(Rectangle bounds){
		this.bounds = bounds;
	}
	
	//Does calculations, then sends 2d shapes to the frame to be rendered
	public void renderPerspective(Frame destination, BufferStrategy bufferStrategy){
		World world = World.getWorldForID(currentID);
		Vector toOrigin = Point.subtractPointFromPoint(new Point(0, 0, 0), cameraPosition);
		toOrigin.setY(-1*toOrigin.getY());
		synchronized(destination){
			if(changedWorld || update){
				destination.setShapes(world.getShapes());
			}
			
			destination.setShift(toOrigin);
			destination.render(bufferStrategy);
		}
	}
}
