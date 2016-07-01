package com.julianEngine.core;

import com.julianEngine.graphics.Frame;

public interface Parent extends Shape{
	public void addShape(Shape s);
	public Point getGFXPoint(Point p);
	public Frame getContainingFrame();
	public Point getRealPointForRelativePoint(Point p);
}
