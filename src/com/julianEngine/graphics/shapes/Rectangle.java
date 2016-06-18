package com.julianEngine.graphics.shapes;

import java.awt.Graphics;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

import java.awt.Color;

public class Rectangle implements Shape{
	/*--------Public Static Variables-------*/
	/*--------Private Static Variables------*/
	/*--------Public Instance Variables-----*/
	/*--------Private Instance Variables----*/
	private Color color;
	private Point topLeft;
	private double length;
	private double height;
	private boolean anchored = false;
	private boolean ready = false;
	private Parent parent;
	
	/*--------Code--------------------------*/
	public Rectangle(Point topLeft, double lenght, double height, Color color){
		this.topLeft = topLeft;
		this.length = lenght;
		this.height = height;
		this.color = color;
		ready = true;
	}
	
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		if(anchored){
			
		}
		graphics.setColor(color);
		//int xPos = Math.round((float)topLeft.getX() + ((anchored)?0:(float)shift.getX()));
		//int yPos = Math.round((float)(winHeight - topLeft.getY()) + ((anchored)?0:(float)shift.getY()));
		
		Point gfxPoint = parent.getGFXPoint(topLeft);
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		
		graphics.drawRect(xPos, yPos, Math.round((float)length), Math.round((float)height));
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public void moveRectangle(Point newTopLeft){
		topLeft = newTopLeft;
	}
	
	public int getTopLeftX() {
		return (int) topLeft.getX();
	}
	
	public int getTopLeftY() {
		return (int) topLeft.getY();
	}
	
	public int getTopLeftZ() {
		return (int) topLeft.getZ();
	}
	
	public Point getTopLeft() {
		return topLeft;
	}
	
	public void move(Vector path) {
		topLeft = topLeft.addVector(path);
	}
	
	public boolean isPointInside(Point point){
		if((point.getX() >= topLeft.getX())&&(point.getY() <= topLeft.getY())&&(point.getX() <= topLeft.getX()+length)&&(point.getY() >= topLeft.getY()-height)){
			return true;
		}else{
			return false;
		}
	}
	
	public void setAnchored(boolean b) {
		anchored = b;
	}
	
	@Override
	public void centerX(Frame frame) {
		int xPos = (int) ((frame.getWidth()-this.length)/2);
		topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
	}

	@Override
	public void centerY(Frame frame) {
		int yPos = (int) ((frame.getWidth()-this.height)/2);
		topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		this.parent = p;
	}
}
