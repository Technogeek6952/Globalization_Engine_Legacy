package com.julianEngine.graphics.shapes;

import java.awt.Graphics;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

/*
 * The line object, while technically a shape, is mostly used for calculations and math, and so most Shape methods are not defined
 */
public class Line implements Shape{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	Point start;
	Point end;
	boolean ready = false;
	boolean anchored = false;
	private Parent parent;
	
	/*--------Code--------------------------*/
	public Line(Point start, Point end){
		this.start = start;
		this.end = end;
		ready = true;
	}
	
	public boolean areTwoPointsOnSameSide(Point p1, Point p2){
		//Complex math ahead... (http://math.stackexchange.com/questions/162728/how-to-determine-if-2-points-are-on-opposite-sides-of-a-line)
		//ax + by + c = 0
		double a = (start.getY()-end.getY());
		double b = (end.getX()-start.getX());
		double c = (start.getX()*end.getY())-(end.getX()*start.getY());
		
		double devP1 = (a*p1.getX()) + (b*p1.getY()) + c;
		double devP2 = (a*p2.getX()) + (b*p2.getY()) + c;
		if((devP1>0&&devP2>0)||(devP1<0&&devP2<0)||(devP1==0&&devP2==0)){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isReady(){
		return ready;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		//TODO: implement line draw function
	}

	//Since a line doesn't have a top left, we're just going to use the start point - be aware of this functionality when using the line class
	@Override
	public int getTopLeftX() {
		return (int) start.getX();
	}

	@Override
	public int getTopLeftY() {
		return (int) start.getY();
	}

	@Override
	public int getTopLeftZ() {
		return (int) start.getZ();
	}

	@Override
	public Point getTopLeft() {
		return start;
	}

	@Override
	public void move(Vector path) {
		start.addVector(path);
		end.addVector(path);
	}

	@Override
	public void setAnchored(boolean b) {
		anchored = b;
	}

	@Override
	public void centerX(Frame frame) {
	}

	@Override
	public void centerY(Frame frame) {
	}

	@Override
	public void setParent(Parent p) {
		parent = p;
	}
}
