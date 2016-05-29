package com.julianEngine.graphics.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.shapes.Line;
import com.julianEngine.graphics.shapes.Rectangle;
import com.julianEngine.graphics.shapes.Text;
import com.julianEngine.utility.Log;

public class UIButton implements UIElement{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	Point topLeft;
	Color color;
	Color hColor;
	boolean anchored = false;
	Rectangle boundingBox;
	int width;
	int height;
	ArrayList<UIButtonListener> listeners = new ArrayList<UIButtonListener>();
	boolean selected;
	UIMask buttonMask;
	Text UIText;
	boolean ready = false;
	
	/*--------Code--------------------------*/
	public UIButton(Point topLeft, String text, Color color, Frame frame){
		this(topLeft, text,color, 0, 0, frame);
		ready=false;
		this.width = UIText.getWidth();
		this.height = UIText.getHeight();
		HashMap<Line, Point> bounds = new HashMap<Line, Point>();
		Point center = new Point(topLeft.getX()+(width/2),topLeft.getY()-(height/2), 0);
		Point topRight = new Point(topLeft.getX()+width, topLeft.getY(), 0);
		Point bottomLeft = new Point(topLeft.getX(), topLeft.getY()-height, 0);
		Point bottomRight = new Point(topLeft.getX()+width, topLeft.getY()-height, 0);
		//top
		bounds.put(new Line(topLeft, topRight), center);
		//left
		bounds.put(new Line(topLeft, bottomLeft), center);
		//right
		bounds.put(new Line(topRight, bottomRight), center);
		//bottom
		bounds.put(new Line(bottomLeft, bottomRight), center);
		buttonMask.setBounds(bounds);
		ready=true;
	}
	
	public UIButton(Point topLeft, String text, Color color, int width, int height, Frame frame){
		this.topLeft = topLeft;
		this.color = color;
		this.hColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
		this.width = width;
		this.height = height;
		UIText = new Text(topLeft, text, frame);
		UIText.setColor(color);
		HashMap<Line, Point> bounds = new HashMap<Line, Point>();
		Point center = new Point(topLeft.getX()+(width/2),topLeft.getY()-(height/2), 0);
		Point topRight = new Point(topLeft.getX()+width, topLeft.getY(), 0);
		Point bottomLeft = new Point(topLeft.getX(), topLeft.getY()-height, 0);
		Point bottomRight = new Point(topLeft.getX()+width, topLeft.getY()-height, 0);
		//top
		bounds.put(new Line(topLeft, topRight), center);
		//left
		bounds.put(new Line(topLeft, bottomLeft), center);
		//right
		bounds.put(new Line(topRight, bottomRight), center);
		//bottom
		bounds.put(new Line(bottomLeft, bottomRight), center);
		buttonMask = new UIMask(bounds, frame);
		ready=true;
	}
	
	public void setColor(Color newColor){
		color = newColor;
	}
	
	public void addUIButtonListener(UIButtonListener newListener){
		listeners.add(newListener);
	}
	
	public void draw(Graphics graphics, int height, Vector shift, Frame frame) {
		UIText.move(UIText.getTopLeft().vectorTo(new Point(topLeft.getX(), topLeft.getY(), 0)));
		UIText.draw(graphics, height, shift, frame);
		int xPos = Math.round((float)topLeft.getX() + ((anchored )?0:(float)shift.getX()));
		int yPos = Math.round((float)(height - topLeft.getY()) + ((anchored)?0:(float)shift.getY())+this.height);
		graphics.drawRect(xPos, yPos, this.width, this.height);
		if(buttonMask.mouseInside){
			graphics.setColor(hColor);
			graphics.fillRect(xPos, yPos, width, this.height);
		}
	}
	
	public boolean isReady(){
		return ready&&buttonMask.isReady()&&UIText.isReady();
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
	
	public void setAnchored(boolean b) {
		anchored = b;
	}
	
	public interface UIButtonListener{
		public void buttonClicked();
		public void buttonMousedOver();
		public void buttonLostMouse();
	}
}
