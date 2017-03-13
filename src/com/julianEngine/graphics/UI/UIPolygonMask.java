package com.julianEngine.graphics.UI;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import com.julianEngine.Engine2D;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.shapes.Line;

public class UIPolygonMask extends UIMask {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	/*
	 * List of bounds - each entry is a line (representing an edge of a complex shape),
	 * and a point that tells on which side of the line is considered "in"
	 * A point is said to be within the mask if it is on the same side of each line as
	 * the point associated with the given line.
	 */
	private HashMap<Line, Point> bounds;
	private Frame referenceFrame;
	private boolean mouseInside = false;
	private boolean ready = false;
	private boolean listenerReady = false;
	private Parent parent;
	//private Vector shift;
	//private ArrayList<UIMaskListener> listeners = new ArrayList<UIMaskListener>();
	
	/*--------Code--------------------------*/
	public UIPolygonMask(HashMap<Line, Point> bounds, Frame frame, Parent parent){
		super(parent.getWorld());
		this.parent = parent;
		this.bounds = bounds;
		UIPolygonMask ref = this;
		ready = false;
		
		Thread listenerThread = new Thread(){
			public void run(){
				while(!UIPolygonMask.this.ready){
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				frame.addMouseMotionListener(ref);
				frame.addMouseListener(ref);
				listenerReady = true;
				//Log.trace("UIMask Ready");
			}
		};
		listenerThread.start();
		
		//listenerThread.setPriority(Thread.MAX_PRIORITY);
		//referenceFrame = frame;
		referenceFrame = parent.getWorld().getContainingFrame();
		listenerReady = true;
		ready = true;
		
	}
	
	public boolean isMouseInside(){
		return mouseInside;
	}
	
	public void setBounds(HashMap<Line, Point> bounds){
		this.bounds = bounds;
	}
	
	public void useVectorToTest(Vector shift){
		//this.shift = shift;
	}
	
	public boolean isPointInside(Point toTest){
		Point point = toTest;//.addVector(shift!=null?shift:new Vector(0, 0, 0));
		for(Line l:bounds.keySet()){
			if(l.areTwoPointsOnSameSide(bounds.get(l), point)){
				//same side - don't worry
			}else{
				return false; //as soon as any check is false, we can exit 
			}
		}
		return true; //if the loop completes without returning, all checks were true, and so the point is inside
	}
	
	public void draw(Graphics graphics, Vector shift) {
	}

	public boolean isReady(){
		return ready&&listenerReady;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (parent!=null){
			Point mousePoint = CoordinateSpace.convertPointToSystem(new Point(e.getX(),  e.getY(),  0), Engine2D.frameRootSystem, parent.getRelativeSpace());
			if(isPointInside(mousePoint)){
				//mose moved inside mask
				if(mouseInside){
					//if mouse was already inside, do nothing
				}else{
					//if mouse moved into mask, set bool and notify
					mouseInside = true;
					if(listeners.size()>0){
						for(UIMaskListener l:listeners){
							l.mouseEnteredMask();
						}
					}
				}
			}else{
				//mouse moved outside mask
				if(mouseInside){
					//if mouse was previously inside the mask, set bool and notify it left
					mouseInside = false;
					if(listeners.size()>0){
						for(UIMaskListener l:listeners){
							l.mouseLeftMask();
						}
					}
				}else{
					//if mouse was already outside, do nothing
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//Log.trace("Mouse Click detected");
		//Mouse clicked with the game active
		if(mouseInside){
			//if the mouse was inside the mask when it clicked, notify
			if(listeners.size()>0 && Engine2D.getInstance().camera.getWorld().equals(parent.getWorld())){
				for(UIMaskListener l:listeners){
					//note: if another mask is on top of this one, both will be notified when
					//the mouse clicks any overlapping region. The program should have logic to
					//figure out if it needs to ignore maskClicked() calls.
					l.maskClicked();
				}
			}
		}
	}
}
