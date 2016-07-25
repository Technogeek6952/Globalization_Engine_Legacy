package com.julianEngine.graphics.UI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.UI.UIMask.UIMaskListener;
import com.julianEngine.graphics.shapes.Line;
import com.julianEngine.utility.Log;

public class UIContainer implements Shape, Parent{
	
	Frame m_frame;
	Point m_topLeft;
	int m_width;
	int m_height;
	Parent parent;
	BufferedImage buffer = null;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	Color background = new Color(0, 0, 0, 0);
	ArrayList<UIContainerListener> listeners = new ArrayList<UIContainerListener>();
	UIMask mask = null;
	Color borderColor = new Color(0, 0, 0, 0);
	
	public UIContainer(Point t1, int width, int height, World world){
		this(t1, width, height);
	}
	
	public UIContainer(Point tl, int width, int height){
		m_frame = new Frame(width, height);
		m_frame.setBackground(new Color(0, 0, 0, 0)); //transparent background
		m_topLeft = tl;
		m_width = width;
		m_height = height;
	}

	public void setBorderColor(Color c){
		borderColor = c;
	}
	
	public void addListener(UIContainerListener l){
		listeners.add(l);
	}
	
	public void setBackground(Color c){
		//m_frame.setBackground(c);
		background = c;
	}
	
	public void addShape(Shape s){
		shapes.add(s);
		s.setParent(this);
	}
	
	public void addShapes(Shape... shapes){
		for(Shape shape:shapes){
			addShape(shape);
		}
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
		//FIXME: \/some screens with heavy container use get *very* bad FPS, even with no content. This code should probably be optimized\/
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
		graphics.fillRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		((Graphics2D)graphics).setColor(borderColor);
		graphics.drawRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		graphics.drawImage(buffer, xPos, yPos, xPos+m_width, yPos+m_height, 
				0, 0, buffer.getWidth(), buffer.getHeight(), null);
		if(mask!=null&&mask.isMouseInside()){
			Color oldColor = graphics.getColor();
			graphics.setColor(Color.pink);
			graphics.fillRect(xPos, yPos, m_width, m_height);
			graphics.setColor(oldColor);
		}
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
		Point origin = getOrigin();
		return new Point(p.getX()+origin.getX(), p.getY()+origin.getY(), p.getZ()+origin.getZ());
	}
	
	@Override
	public Point getRelativePointForRealPoint(Point p){
		Point origin = getOrigin();
		return new Point(p.getX()-origin.getX(), p.getY()-origin.getY(), p.getZ()-origin.getZ());
	}
	
	@Override
	public Point getOrigin(){
		Point origin = new Point(m_topLeft.getX(), m_topLeft.getY()-m_height, m_topLeft.getZ());
		origin = parent.getRealPointForRelativePoint(origin);
		return origin;
	}
	
	@Override
	public World getWorld(){
		assert parent!=null;
		return parent.getWorld();
	}
	
	public interface UIContainerListener{
		public void containerClicked();
	}
}
