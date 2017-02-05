package com.julianEngine.graphics.shapes;

import java.awt.Color;
import java.awt.Graphics;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

public class ProgressBar implements Shape {
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	private Point topLeft;
	private int width;
	private int height;
	private Color barColor = Color.BLACK;
	private Color borderColor = Color.BLACK;
	private float percentFilled;
	private boolean fillLeftToRight = true;
	private boolean anchored;
	private boolean ready = false;
	private Parent parent;
	
	/*--------Code--------------------------*/
	
	public ProgressBar(Point topLeft, int width, int height){
		this.topLeft = topLeft;
		this.width = width;
		this.height = height;
		this.percentFilled = 0;
		
		ready = true;
	}
	
	public Color getBarColor() {
		return barColor;
	}

	public void setBarColor(Color color) {
		this.barColor = color;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public void setPercentFilled(float fillPercent){
		percentFilled = fillPercent;
	}
	
	public float getPercentFilled(){
		return percentFilled;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		if(anchored){
			
		}
		//int xPos = Math.round((float)topLeft.getX() + ((anchored)?0:(float)shift.getX()));
		//int yPos = Math.round((float)(height - topLeft.getY())+ ((anchored)?0:(float)shift.getY()));
		int filledWidth = Math.round(this.width*this.percentFilled);
		
		Point gfxPoint = parent.getGFXPoint(topLeft);
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		
		graphics.setColor(barColor);
		if(fillLeftToRight){
			graphics.fillRect(xPos, yPos, filledWidth, this.height);
		}else{
			int unfilledWidth = this.width - filledWidth;
			xPos = xPos + unfilledWidth;
			graphics.fillRect(xPos, yPos, filledWidth, this.height);
		}
		
		//Draw border after so it shows up on top
		graphics.setColor(borderColor);
		graphics.drawRect(xPos, yPos, this.width, this.height);
	}

	@Override
	public int getTopLeftX() {
		return (int)topLeft.getX();
	}

	@Override
	public int getTopLeftY() {
		return (int)topLeft.getY();
	}

	@Override
	public int getTopLeftZ() {
		return (int)topLeft.getZ();
	}

	@Override
	public Point getTopLeft() {
		return topLeft;
	}

	@Override
	public void move(Vector path) {
		topLeft.addVector(path);
	}

	@Override
	public void setAnchored(boolean b) {
		anchored = b;
	}
	
	@Override
	public boolean isReady() {
		return ready;
	}

	@Override
	public void centerX(Frame frame) {
		int xPos = (frame.getWidth()-this.width)/2;
		topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
	}

	@Override
	public void centerY(Frame frame) {
		int yPos = (frame.getHeight()+this.height)/2;
		topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		this.parent = p;
	}
}
