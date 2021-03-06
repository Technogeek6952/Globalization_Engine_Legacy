package com.julianEngine.graphics.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import com.julianEngine.Engine2D;
import com.julianEngine.config.UserConfiguration;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;

public class UIBitmapMask extends UIMask{

	private int width;
	private int height;
	private boolean[][] bitMap;
	private Point topLeft;
	private boolean ready = false;
	private boolean listenerReady = false;
	private boolean mouseInside = false;
	private boolean draw = false;
	private Parent parent;
	private Frame referenceFrame;
	private Color highlightColor;
	private boolean parentSet = false;
	private boolean highlight = false;
	
	public UIBitmapMask(Point topLeft, boolean[][] bitMap, Parent parent){
		super(parent.getWorld());
		this.topLeft = topLeft;
		this.bitMap = bitMap;
		width = bitMap.length; //get the size of the first array for the width
		//to avoid an out of bounds exception, only get the size of the second array if the first is greater than 0
		height = (width>0)?bitMap[0].length:0;
		UIBitmapMask ref = this;
		new Thread(){
			public void run(){
				//Log.trace("UI bitmap mask about to ask for listener");
				Engine2D.getInstance().rootFrame.addMouseListener(ref);
				Engine2D.getInstance().rootFrame.addMouseMotionListener(ref);
				//Log.trace("UI Mask Ready (bitmap)");
				listenerReady = true;
			}
		}.start();
		
		this.parent = parent;
		
		referenceFrame = parent.getWorld().getFrame();
		highlightColor = new Color(255, 255, 255, 50);
		ready = true;
	}
	
	public void setBitMap(boolean[][] newBitMap){
		bitMap = newBitMap;
	}
	public void renderMask(boolean b){
		draw = b;
	}
	
	public void highlight(boolean b){
		highlight = b;
	}
	
	@Override
	public boolean isPointInside(Point toTest){
		Point p = toTest;
		p.setX(toTest.getX()-topLeft.getX());
		p.setY(-(toTest.getY()-topLeft.getY()));
		try{
			return bitMap[(int)p.getX()][(int)p.getY()];
		}catch(Exception e){
			return false; //if there is an exception from the point not existing in the mask, return false
		}
	}
	
	@Override
	public void setParent(Parent p){
		parent = p;
		parentSet = true;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw){
		if(this.height>0&&((mouseInside&&highlight)||draw||UserConfiguration.getBool("drawMasks", false))){
			Point gfxPoint = CoordinateSpace.convertPointToSystem(topLeft, parent.getRelativeSpace(), parent.getDrawingSpace());
			if(mouseInside&&!(draw||UserConfiguration.getBool("drawMasks", false))){
				graphics.setColor(highlightColor);
			}else{
				if(!mouseInside){
					graphics.setColor(new Color(0, 255, 0, 50));
				}else{
					graphics.setColor(new Color(255, 0, 0, 50));
				}
			}
			for(int x=0;x<bitMap.length;x++){
				for(int y=0;y<bitMap[0].length;y++){
					if(bitMap[x][y]){
						graphics.drawRect(x+(int)gfxPoint.getX(), y+(int)gfxPoint.getY(), 0, 0); //draw a rectangle at the x and y spot with 0 width and height, essentially a pixel
					}
				}
			}
		}
	}
	
	@Override
	public boolean isReady(){
		return ready&&listenerReady;
	}

	@Override
	public boolean isMouseInside() {
		return mouseInside;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//only respond to events once this has been added to a container (parent set), and if the active world is the one we should respond to
		if(parentSet&&Engine2D.getInstance().camera.getWorld().equals(parent.getWorld())){
			Point mousePoint = CoordinateSpace.convertPointToSystem(new Point(e.getX(),  e.getY(),  0), Engine2D.getInstance().mouseEventSpace, parent.getRelativeSpace());
			if(this.isPointInside(mousePoint)){
				//mose moved inside mask
				if(mouseInside){
					//if mouse was already inside, do nothing
				}else{
					//if mouse moved into mask, set bool and notify
					mouseInside = true;
					if(listeners.size()>0){
						for(UIMaskListener l:listeners){
							l.mouseEnteredMask();
						}
					}
				}
			}else{
				//mouse moved outside mask
				if(mouseInside){
					//if mouse was previously inside the mask, set bool and notify it left
					mouseInside = false;
					if(listeners.size()>0){
						for(UIMaskListener l:listeners){
							l.mouseLeftMask();
						}
					}
				}else{
					//if mouse was already outside, do nothing
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//only respond to events once this has been added to a container (parent set), and if the active world is the one we should respond to
		if(parentSet&&Engine2D.getInstance().camera.getWorld().equals(parent.getWorld())){
			//Mouse clicked with the game active
			if(mouseInside){
				//if the mouse was inside the mask when it clicked, notify
				if(listeners.size()>0){
					for(UIMaskListener l:listeners){
						//note: if another mask is on top of this one, both will be notified when
						//the mouse clicks any overlapping region. The program should have logic to
						//figure out if it needs to ignore maskClicked() calls.
						l.maskClicked();
					}
				}
			}
		}
	}
	
	@Override
	public int getTopLeftZ(){
		return (int)topLeft.getZ();
	}
}
