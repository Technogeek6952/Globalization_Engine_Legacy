package com.julianEngine.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.JPanel;

import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.utility.Log;

public class Frame extends JPanel{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	private static final long serialVersionUID = 264307615572595722L;
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	int width; //width of frame
	int height; //height of frame
	ArrayList<Shape> shapes = new ArrayList<Shape>(); //container for all the shapes to be rendered
	double renderTimeout = 20; //how long should the program wait before ending the render (to limit FPS and CPU impact) - default is 20 (20ms per render - about 50fps)
	long renderTimeoutNano = 1000000000; //nanosecond timeout
	Vector shift = new Vector(0, 0, 0); //vector to shift shapes along if frame is not centered
	Color backgroundColor;
	
	/*--------Code--------------------------*/
	//default constructor - uses default size of 256x256px
	public Frame(){
		this(256, 256); //default size of 256x256px
	}
	
	//full constructor
	public Frame(int width, int height){
		this.width = width; //set width instance variable
		this.height = height; //set height instance variable
		this.setSize(width, height); //set the size of the frame
		this.setVisible(true); //make the frame visible
	}
	
	public void setTargetFPS(int targetFPS){
		renderTimeout = 1000/targetFPS;
		renderTimeoutNano = 1000000000/targetFPS;
	}
	
	public void unlockFPS(){
		renderTimeout = 0;
		renderTimeoutNano = 0;
	}
	
	public Point convertPoint(Point point){
		point.setY(height-point.getY());
		return point;
	}
	
	//Resizes the frame to the specified size
	public void resizeFrame(int width, int height){
		this.width = width; //set width instance variable
		this.height = height; //set height instance variable
		this.setSize(width, height); //set the size of the frame
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
	
	//Render the frame
	public void render(BufferStrategy bufferStrategy){
		long start = System.nanoTime(); //get the system time in nanoseconds when starting the render
		Graphics2D graphics = (Graphics2D)bufferStrategy.getDrawGraphics(); //get a graphics object to draw to the buffer
		//draw stuff
		graphics.setBackground(backgroundColor);
		graphics.clearRect(0, 0, width, height);
		
		shapes.sort(new Comparator<Shape>(){
			public int compare(Shape o1, Shape o2) {
				return (o1.getTopLeftZ()-o2.getTopLeftZ());
			}
		});
		
		for(Shape s:shapes){
			s.draw(graphics, height, shift, this); //Draw every shape we have in our list
		}
		//...
		bufferStrategy.show(); //tell the buffer that we're done drawing, and it can show the frame
		Toolkit.getDefaultToolkit().sync(); //sync the graphics - may reduce fps, but makes animation smoother?
		graphics.dispose(); //release memory from the graphics object
		if(System.nanoTime()-start<renderTimeoutNano){ //if we aren't done with the minimum time for each render, wait
			int nanoWait = (int) (renderTimeoutNano-(System.nanoTime()-start));
			try {
				Thread.sleep((int)nanoWait/1000000, nanoWait%1000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
