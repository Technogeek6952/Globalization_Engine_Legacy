package com.julianEngine.core;

import com.julianEngine.graphics.Frame;

public interface Parent extends Shape{
	public void addShape(Shape s);
	public void removeShape(Shape s);
	public Point getGFXPoint(Point p);
	public Frame getContainingFrame();
	public Frame getFrame();
	public Point getRealPointForRelativePoint(Point p);
	public Point getRelativePointForRealPoint(Point p);
	public Point getOrigin();
	public World getWorld();
	public double getZoom();
}
