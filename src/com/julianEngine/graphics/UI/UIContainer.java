package com.julianEngine.graphics.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.Color;

import com.julianEngine.Engine2D;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

import javafx.scene.paint.CycleMethod;

public class UIContainer implements Shape, Parent{
	
	Frame m_frame;
	Point m_topLeft;
	int m_width;
	int m_height;
	Parent parent;
	BufferedImage buffer = null;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	Color background = new Color(0, 0, 0, 0);
	
	public UIContainer(Point tl, int width, int height){
		m_frame = new Frame(width, height);
		m_frame.setBackground(new Color(0, 0, 0, 0)); //transparent background
		m_topLeft = tl;
		m_width = width;
		m_height = height;
	}

	public void setBackground(Color c){
		//m_frame.setBackground(c);
		background = c;
	}
	
	public void addShape(Shape s){
		shapes.add(s);
		s.setParent(this);
	}
	
	public Frame getFrame(){
		return m_frame;
	}
	
	@Override
	public void setParent(Parent p) {
		parent = p;
	}

	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		forceDraw = true;
		if(buffer==null||forceDraw){
			//set up frame
			m_frame.setShapes(shapes);
			//draw buffer
			buffer = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D bufferGFX = buffer.createGraphics();
			m_frame.drawFrame(bufferGFX, true);
		}
		Point gfxPoint = parent.getGFXPoint(m_topLeft);
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		((Graphics2D)graphics).setColor(background);
		graphics.fillRoundRect(xPos, yPos, m_width, m_height, m_width/8, m_height/8);
		graphics.drawImage(buffer, xPos, yPos, xPos+m_width, yPos+m_height, 
				0, 0, buffer.getWidth(), buffer.getHeight(), null);
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
	public void centerX(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void centerY(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public Point getGFXPoint(Point p) {
		Point newPoint = new Point();
		newPoint.setX(p.getX());
		newPoint.setY(m_height-p.getY());
		return newPoint;
	}

	@Override
	public Frame getContainingFrame() {
		return m_frame;
	}

	@Override
	public Point getRealPointForRelativePoint(Point p) {
		Point thisOrigin = new Point();
		thisOrigin.setX(m_topLeft.getX());
		thisOrigin.setY(m_topLeft.getY()-m_height);
		Vector toOrigin = Point.subtractPointFromPoint(thisOrigin, new Point(0, 0, 0));
		Point realPoint = p.addVector(toOrigin);
		
		return realPoint;
	}
}
