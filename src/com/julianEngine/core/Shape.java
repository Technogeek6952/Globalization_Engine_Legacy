package com.julianEngine.core;

import java.awt.Graphics;

import com.julianEngine.graphics.Frame;

public interface Shape {
	public void draw(Graphics graphics, int height, Vector shift, Frame frame);
	public int getTopLeftX();
	public int getTopLeftY();
	public int getTopLeftZ();
	public Point getTopLeft();
	public void move(Vector path);
	public void setAnchored(boolean b);
	public boolean isReady();
}
