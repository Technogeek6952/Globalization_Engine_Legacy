package com.julianEngine.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;

import com.julianEngine.Engine2D;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.shapes.Sprite;
import com.julianEngine.utility.Log;

public class Frame extends JPanel{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	private static final long serialVersionUID = 264307615572595722L;
	
	/*--------Public Instance Variables-----*/
	public boolean showFPS = false;
	public float fps = 0;
	
	/*--------Private Instance Variables----*/
	private int width; //width of frame
	private int height; //height of frame
	private ArrayList<Shape> shapes = new ArrayList<Shape>(); //container for all the shapes to be rendered
	//private double renderTimeout = 20; //how long should the program wait before ending the render (to limit FPS and CPU impact) - default is 20 (20ms per render - about 50fps)
	//private long renderTimeoutNano = 1000000000; //nanosecond timeout
	private Vector shift = new Vector(0, 0, 0); //vector to shift shapes along if frame is not centered
	private Color backgroundColor;
	private int sideBorder = 0;
	private int titleBorder = 0;
	private boolean listenerWaiting = false;
	private Lock lock = new ReentrantLock();
	//private ArrayList<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	//private ArrayList<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	/*--------Code--------------------------*/
	//default constructor - uses default size of 256x256px
	public Frame(){
		this(1080, 720); //default size of 1080x720px
	}
	
	//full constructor
	public Frame(int width, int height){
		this.width = width; //set width instance variable
		this.height = height; //set height instance variable
		this.setSize(width, height); //set the size of the frame
		this.setVisible(true); //make the frame visible
	}
	
	public void setTargetFPS(int targetFPS){
		if(targetFPS>0){
			//renderTimeout = 1000/targetFPS;
			//renderTimeoutNano = 1000000000/targetFPS;
		}else{
			unlockFPS();
		}
	}
	
	public void unlockFPS(){
		//renderTimeout = 0;
		//renderTimeoutNano = 0;
	}
	
	public Point convertPointJGFXtoJEGFX(Point point){
		Point newPoint = new Point();
		newPoint.setX(point.getX()-sideBorder);
		newPoint.setY(height-(point.getY()-titleBorder));
		return newPoint;
	}
	
	public Point convertPointJEGFXtoJGFX(Point point){
		Point newPoint = new Point();
		newPoint.setX(point.getX()+sideBorder);
		newPoint.setY((height-point.getY())+titleBorder);
		return newPoint;
	}
	
	public Point convertPointFGFXtoJEGFX(Point point){
		Point newPoint = new Point();
		newPoint.setX(point.getX());
		newPoint.setY(height-point.getY());
		return newPoint;
	}
	
	//Resizes the frame to the specified size
	public void resizeFrame(int width, int height){
		this.width = width; //set width instance variable
		this.height = height; //set height instance variable
		//this.setSize(width, height); //set the size of the frame
		this.setPreferredSize(new Dimension(width, height));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(width, height);
	}
	@Override
	public Dimension getMinimumSize(){
		return new Dimension(width, height);
	}
	@Override
	public Dimension getMaximumSize(){
		return new Dimension(width, height);
	}
	
	public void setShift(Vector shift){
		this.shift = shift;
	}
	
	public void setShapes(ArrayList<Shape> newShapes){
		shapes = newShapes;
	}
	
	public void setBackground(Color newBackground){
		backgroundColor = newBackground;
	}
	
	Sprite cursor;
	public void setCursor(String cursorURI){
		Dimension cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(32, 32);
		cursor = new Sprite(new Point(0, 0, 100), cursorSize.width, cursorSize.height, cursorURI);
		Engine2D.getInstance().rootWorld.addShape(cursor);
		//Engine2D.getInstance().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB), new java.awt.Point(1, 1), "blank"));
	}
	
	@Override
	public void paintComponent(Graphics graphics){
		//First get the render lock
		synchronized(lock){
			//if there are listeners waiting for the lock, and we hold it, release it for 100ms, 
			//and then grab it again, and check again if there are listeners waiting for it
			if(listenerWaiting&&lock.tryLock()){
				lock.unlock();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.warn("Interrupted while waiting for render lock");
					e.printStackTrace();
				}
				lock.lock();
			}
		}
		
		((Graphics2D)graphics).setBackground(backgroundColor);
		graphics.clearRect(0, 0, width, height);
		//draw stuff
		drawFrame((Graphics2D)graphics, true);
		if(showFPS){
			graphics.setColor(Color.YELLOW);
			graphics.setFont(new Font("Ariel", Font.BOLD, 20));
			graphics.drawString(String.format("FPS: %.2f", fps), 3, 50);
		}
		//...
	}
	
	public void drawFrame(Graphics2D graphics, boolean forceDraw){
		((Graphics2D)graphics).setBackground(backgroundColor);
		((Graphics2D)graphics).setColor(backgroundColor);
		graphics.fillRect(0, 9, width, height);
		Point mousePoint = Engine2D.getMouseLocation();
		if (cursor!=null && mousePoint!=null){
			cursor.getTopLeft().setX(mousePoint.getX());
			cursor.getTopLeft().setY(mousePoint.getY());
			cursor.draw(graphics, new Vector(), true);
		}
		synchronized(this){
			shapes.sort(new Comparator<Shape>(){
				public int compare(Shape o1, Shape o2) {
					return (o1.getTopLeftZ()-o2.getTopLeftZ());
				}
			});
			
			synchronized(shapes){
				for(Shape s:shapes){
					//s.setParent(this);
					s.draw(graphics, shift, forceDraw); //Draw every shape we have in our list
				}
			}
		}
	}
}
