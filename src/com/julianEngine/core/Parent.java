package com.julianEngine.core;

import com.julianEngine.graphics.Frame;

public interface Parent extends Shape{
	public void addShape(Shape s);
	public void removeShape(Shape s);
	public void preLoad(); //called before the camera switches to the world, or called from the parent, can be used to update values before the user sees the screen
	public Point getGFXPoint(Point p);
	public Frame getContainingFrame();
	public Frame getFrame();
	public Point getRealPointForRelativePoint(Point p);
	public Point getRelativePointForRealPoint(Point p);
	public Point getOrigin();
	public World getWorld();
	public double getZoom();
	
	public void triggerHook(String hookID, byte[] data);
	public void addHookListener(String hookID, HookListener listener);
	
	public static interface HookListener{
		public void hookTriggered(String hookID, byte[] data);
	}
}
