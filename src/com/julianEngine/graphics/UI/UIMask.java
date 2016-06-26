package com.julianEngine.graphics.UI;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.graphics.Frame;
import com.julianEngine.utility.Log;

public abstract class UIMask implements UIElement, MouseListener, MouseMotionListener{
	protected ArrayList<UIMaskListener> listeners = new ArrayList<UIMaskListener>();
	
	public UIMask(World parent){
		//forces children to also take a world as a constructor argument. Should be used to figure out if the mask is actually visible, and if it should be rendered
	}
	
	public void addUIMaskListener(UIMaskListener l){
		listeners.add(l);
	}
	
	public abstract boolean isPointInside(Point toTest);
	
	public abstract boolean isMouseInside();
	
	//Shape Overrides
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
	public void centerX(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void centerY(Frame frame) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	//Child overrides
	@Override
	public void setParent(Parent p) {
		// TODO Auto-generated method stub
		
	}

	//MouseMotionListener & MouseListener Overrides
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	*/
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//Listeners
	public interface UIMaskListener{
		public void maskClicked();
		public void mouseEnteredMask();
		public void mouseLeftMask();
	}
}

