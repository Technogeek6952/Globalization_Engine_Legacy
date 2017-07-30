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

public abstract class UIMask implements UIElement, MouseListener, MouseMotionListener{
	//FIXME: long loading times caused by masks waiting for listeners
	//This can probably be fixed by one listener being created at the start of the program, and it can service all masks that have been
	//created.
	protected ArrayList<UIMaskListener> listeners = new ArrayList<UIMaskListener>();
	
	Parent parent;
	
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
		//TODO: draw mask method
	}

	@Override
	public int getTopLeftX() {
		return 0;
	}

	@Override
	public int getTopLeftY() {
		return 0;
	}

	@Override
	public int getTopLeftZ() {
		return 0;
	}

	@Override
	public Point getTopLeft() {
		return null;
	}

	@Override
	public void move(Vector path) {
	}

	@Override
	public void setAnchored(boolean b) {
	}

	@Override
	public void centerX(Frame frame) {
	}

	@Override
	public void centerY(Frame frame) {
	}

	@Override
	public boolean isReady() {
		return false;
	}

	//Child overrides
	@Override
	public void setParent(Parent p) {
		this.parent = p;
	}

	//MouseMotionListener & MouseListener Overrides
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
	//Listeners
	public interface UIMaskListener{
		public void maskClicked();
		public void mouseEnteredMask();
		public void mouseLeftMask();
	}
}
