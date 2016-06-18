package com.julianEngine.graphics.shapes;

import java.awt.Graphics;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

public class ProgressCircle implements Shape{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	
	/*--------Code--------------------------*/
	
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
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void centerX(Frame frame) {
		//int xPos = (frame.getWidth()-this.width)/2;
		//topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
	}

	@Override
	public void centerY(Frame frame) {
		//int yPos = (frame.getWidth()-this.height)/2;
		//topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		// TODO Auto-generated method stub
		
	}
}
