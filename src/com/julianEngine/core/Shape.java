package com.julianEngine.core;

import java.awt.Graphics;

import com.julianEngine.graphics.Frame;

public interface Shape extends Child{
	public void draw(Graphics graphics, Vector shift, boolean forceDraw);
	public int getTopLeftX();
	public int getTopLeftY();
	public int getTopLeftZ();
	public Point getTopLeft();
	public void move(Vector path);
	public void setAnchored(boolean b);
	public void centerX(Frame frame);
	public void centerY(Frame frame);
	public boolean isReady();
}
