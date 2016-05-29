package com.julianEngine.graphics.shapes;

import java.awt.Graphics;

import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

public class Line implements Shape{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	Point start;
	Point end;
	boolean ready = false;
	
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
	public void draw(Graphics graphics, int height, Vector shift, Frame frame) {
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

}
