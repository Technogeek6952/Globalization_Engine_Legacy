package com.julianEngine.graphics.UI;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.shapes.Line;
import com.julianEngine.utility.Log;

public class UIMask implements UIElement, MouseMotionListener {
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
	HashMap<Line, Point> bounds;
	Frame referenceFrame;
	boolean mouseInside = false;
	boolean ready = false;
	boolean listenerReady = false;
	
	/*--------Code--------------------------*/
	public UIMask(HashMap<Line, Point> bounds, Frame frame){
		this.bounds = bounds;
		UIMask ref = this;
		new Thread("Adding Mask Mouse Listener"){
			public void run(){
				frame.addMouseMotionListener(ref);
				listenerReady = true;
				Log.trace("UIMask Ready");
			}
		}.start();
		referenceFrame = frame;
		ready = true;
	}
	
	public void setBounds(HashMap<Line, Point> bounds){
		this.bounds = bounds;
	}
	
	public boolean isPointInside(Point toTest){
		for(Line l:bounds.keySet()){
			if(l.areTwoPointsOnSameSide(bounds.get(l), toTest)){
				//same side - don't worry
			}else{
				return false; //as soon as any check is false, we can exit 
			}
		}
		return true; //if the loop completes without returning, all checks were true, and so the point is inside
	}
	
	public void draw(Graphics graphics, int height, Vector shift, Frame frame) {
		
	}

	public boolean isReady(){
		return ready&&listenerReady;
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
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point mousePoint = referenceFrame.convertPoint(new Point(e.getX(), e.getY(), 0));
		if(isPointInside(mousePoint)){
			
			mouseInside = true;
		}else{
			mouseInside = false;
		}
	}

}
