package com.julianEngine.graphics.UI;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.Engine2D;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.graphics.CustomFont;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.UI.UIMask.UIMaskListener;
import com.julianEngine.graphics.shapes.Line;
import com.julianEngine.graphics.shapes.Text;
import com.julianEngine.utility.Log;

public class UIButton implements UIElement, UIMaskListener, Parent{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	private Point topLeft;
	private Color highlightColor;
	private Color color;
	private boolean anchored = false;
	private int width;
	private int height;
	private ArrayList<UIButtonListener> listeners = new ArrayList<UIButtonListener>();
	private UIPolygonMask buttonMask;
	private boolean ready = false;
	private String toolTip = "";
	private boolean displayToolTip = false; //should the tooltip be shown if moused over?
	private boolean renderToolTip = false; //has the button been moused over for long enough to render the tooltip
	private int toolTipTimeout = 1000; //ms to wait before displaying tooltip
	private Parent parent;
	private boolean useCustomFont;
	//private CustomFont customFont;
	private Text UIText;
	private int textHeight;
	private int textWidth;
	private boolean renderBox = false;
	private boolean enabled = true;
	private CoordinateSpace relSpace;
	HashMap<Line, Point> bounds = new HashMap<Line, Point>();
	
	/*--------Code--------------------------*/
	public UIButton(Point topLeft, String text, Color color, Frame frame, Parent parent){
		this(topLeft, text,color, 0, 0, frame, parent);
		ready=false;
		this.width = UIText.getWidth();
		this.height = UIText.getHeight();
		
		//Point center = new Point(topLeft.getX()+(width/2),topLeft.getY()-(height/2), 0);
		//Log.trace("UIButton center point: ("+center.getX()+", "+center.getY()+")");
		//Point topRight = new Point(topLeft.getX()+width, topLeft.getY(), 0);
		//Log.trace("UIButton top-right point: ("+topRight.getX()+", "+topRight.getY()+")");
		//Point bottomLeft = new Point(topLeft.getX(), topLeft.getY()-height, 0);
		//Log.trace("UIButton bottomLeft point: ("+bottomLeft.getX()+", "+bottomLeft.getY()+")");
		//Point bottomRight = new Point(topLeft.getX()+width, topLeft.getY()-height, 0);
		//Log.trace("UIButton bottomRight point: ("+bottomRight.getX()+", "+bottomRight.getY()+")");
		//top
		
		Point topLeftBound = new Point(0, height, 0);
		Point topRightBound = new Point(width, height, 0);
		Point bottomLeftBound = new Point(0, 0, 0);
		Point bottomRightBound = new Point(width, 0, 0);
		Point center = new Point(width/2, height/2, 0);
		bounds.put(new Line(topLeftBound, topRightBound), center);
		//left
		bounds.put(new Line(topLeftBound, bottomLeftBound), center);
		//right
		bounds.put(new Line(topRightBound, bottomRightBound), center);
		//bottom
		bounds.put(new Line(bottomLeftBound, bottomRightBound), center);
		buttonMask.setBounds(bounds);
		buttonMask.setParent(this);
		
		ready=true;
	}
	
	//Full constructor. Put most constructor code in this one
	public UIButton(Point topLeft, String text, Color color, int width, int height, Frame frame, Parent parent){
		this.parent = parent;
		this.topLeft = topLeft;
		this.color = color;
		this.highlightColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50);
		this.width = width;
		this.height = height;
		UIText = new Text(new Point(0, height, 0), text, frame);
		UIText.setColor(color);
		//HashMap<Line, Point> bounds = new HashMap<Line, Point>();
		//Point center = new Point(topLeft.getX()+(width/2),topLeft.getY()-(height/2), 0);
		//Point topRight = new Point(topLeft.getX()+width, topLeft.getY(), 0);
		//Point bottomLeft = new Point(topLeft.getX(), topLeft.getY()-height, 0);
		//Point bottomRight = new Point(topLeft.getX()+width, topLeft.getY()-height, 0);
		
		Point topLeftBound = new Point(0, height, 0);
		Point topRightBound = new Point(width, height, 0);
		Point bottomLeftBound = new Point(0, 0, 0);
		Point bottomRightBound = new Point(width, 0, 0);
		Point center = new Point(width/2, height/2, 0);
		
		//top
		bounds.put(new Line(topLeftBound, topRightBound), center);
		//left
		bounds.put(new Line(topLeftBound, bottomLeftBound), center);
		//right
		bounds.put(new Line(topRightBound, bottomRightBound), center);
		//bottom
		bounds.put(new Line(bottomLeftBound, bottomRightBound), center);
		buttonMask = new UIPolygonMask(bounds, frame, this);
		
		buttonMask.addUIMaskListener(this);
		
		UIText.setParent(this);
		buttonMask.setParent(this);
		relSpace = new CoordinateSpace(parent.getRelativeSpace(), false, false, topLeft.getX(), topLeft.getY()-height, 1);
		
		ready=true;
	}
	
	public String getText(){
		return this.UIText.getText();
	}
	
	public void setText(String text){
		UIText.setText(text);
		this.useCustomFont(true);
	}
	
	public UIPolygonMask getMask(){
		return buttonMask;
	}
	
	public void setEnabled(boolean b){
		enabled = b;
	}
	
	@Override
	public double getZoom(){
		return 1;
	}
	
	public void useCustomFont(boolean b){
		useCustomFont = b;
		if(b){
			for(int i=0;i<this.height;i++){
				CustomFont c = new CustomFont(i, 0);
				if(c.getWidthOfString(UIText.getText())<=this.width){
					UIText.setCustomFont(c);
					textHeight = i;
					textWidth = c.getWidthOfString(UIText.getText());
				}
			}
		}
		UIText.useCustomFont(b);
	}
	
	public void showToolTip(boolean b){
		displayToolTip = b;
	}
	
	public void setToolTip(String toolTip){
		this.toolTip = toolTip;
	}
	
	public void setToolTipTimeout(int timeout){
		this.toolTipTimeout = timeout;
	}
	
	public void renderBox(boolean b){
		this.renderBox = b;
	}
	
	public void setColor(Color newColor){
		color = newColor;
	}
	
	public void addUIButtonListener(UIButtonListener newListener){
		listeners.add(newListener);
	}
	
	//@Deprecated
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		Composite originalComposite = ((Graphics2D)graphics).getComposite();
		
		if(!enabled){
			Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			((Graphics2D) graphics).setComposite(alphaComp);
		}else{
			Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
			((Graphics2D) graphics).setComposite(alphaComp);
		}
		
		Point oldTL = new Point(topLeft.getX(), topLeft.getY(), topLeft.getZ());
		topLeft.addVectorToThis(shift);
		Point gfxPoint = CoordinateSpace.convertPointToSystem(topLeft, parent.getRelativeSpace(), parent.getDrawingSpace());
		if(!useCustomFont){
			AffineTransform originalTransform = ((Graphics2D)graphics).getTransform();
			((Graphics2D)graphics).translate(gfxPoint.getX(), gfxPoint.getY());
			UIText.draw(graphics, shift.addVector(new Vector(0, this.height, 0)), forceDraw);
			((Graphics2D)graphics).setTransform(originalTransform);
		}else{
			int xShift = (int) (((this.width-textWidth)/2) + shift.getX());
			int yShift = (int) (((this.height-textHeight)/2) + shift.getY());
			//UIText.move(UIText.getTopLeft().vectorTo(new Point(topLeft.getX()+xShift, topLeft.getY()-yShift, 0)));
			//UIText.move(UIText.getTopLeft().vectorTo(new Point(xShift, yShift, 0)));
			AffineTransform originalTransform = ((Graphics2D)graphics).getTransform();
			((Graphics2D)graphics).translate(gfxPoint.getX(), gfxPoint.getY());
			UIText.draw(graphics, new Vector(shift.getX()+xShift, -yShift, 0), forceDraw);
			((Graphics2D)graphics).setTransform(originalTransform);
		}
		//int xPos = Math.round((float)topLeft.getX() + ((anchored )?0:(float)shift.getX()));
		//int yPos = Math.round((float)(height - topLeft.getY()) + ((anchored)?0:(float)shift.getY())+this.height);
		//int xPos = (int) frame.convertPointJGFXtoJEGFX(topLeft).getX();
		//int yPos = (int) frame.convertPointJGFXtoJEGFX(topLeft).getY();
		
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		
		graphics.setColor(color);
		if(renderBox){
			graphics.drawRect(xPos, yPos, this.width, this.height);
		}
		
		if(buttonMask.isMouseInside() && enabled){
			graphics.setColor(highlightColor);
			graphics.fillRect(xPos, yPos, width, this.height);
		}
		
		if(renderToolTip){
			graphics.setColor(Color.BLACK);
			graphics.drawString(toolTip, xPos, yPos);
		}
		topLeft = oldTL;
		
		buttonMask.draw(graphics, shift);
		
		((Graphics2D)graphics).setComposite(originalComposite); //reset the composite at the end to avoid accidental side effects.
	}
	
	public boolean isReady(){
		return ready&&buttonMask.isReady()&&UIText.isReady();
	}
	
	public int getTopLeftX() {
		return (int) topLeft.getX();
	}
	
	public int getTopLeftY() {
		return (int) topLeft.getY();
	}
	
	public int getTopLeftZ() {
		return (int) topLeft.getZ();
	}
	
	public Point getTopLeft() {
		return topLeft;
	}
	
	public void move(Vector path) {
		topLeft = topLeft.addVector(path);
	}
	
	public void setAnchored(boolean b) {
		anchored = b;
	}
	
	public interface UIButtonListener{
		public void buttonClicked();
		public void buttonMousedOver();
		public void buttonLostMouse();
	}
	
	@Override
	public void maskClicked() {
		if(enabled){
			for(UIButtonListener l:listeners){
				l.buttonClicked();
			}
		}
	}

	@Override
	public void mouseEnteredMask() {
		for(UIButtonListener l:listeners){
			l.buttonMousedOver();
		}
		if(displayToolTip){
			new Thread(){
				public void run(){
					try {
						Thread.sleep(toolTipTimeout);
					} catch (InterruptedException e) {
						Log.trace("Error caught in UIButton.mouseEnteredMask(");
						e.printStackTrace();
					}
					if(buttonMask.isMouseInside()){
						//if mouse is still inside after timeout, show tooltip
						renderToolTip = true;
					}
				}
			}.start();
		}
	}

	@Override
	public void mouseLeftMask() {
		for(UIButtonListener l:listeners){
			l.buttonLostMouse();
		}
		renderToolTip = false;
	}

	@Override
	public void centerX(Frame frame) {
		int xPos = (frame.getWidth()-this.width)/2;
		topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
	}

	@Override
	public void centerY(Frame frame) {
		int yPos = (frame.getWidth()-this.height)/2;
		topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		this.parent = p;
	}

	@Override
	public void addShape(Shape s) {
		//Don't do anything - We shouldn't be adding new shapes to this container, as it is just a container for two shapes:
		//the text and the rectangle
	}
	
	public void removeShape(Shape s){
		//Don't do anything - We shouldn't be adding new shapes to this container, as it is just a container for two shapes:
		//the text and the rectangle
	}

	/*
	@Override
	public Point getGFXPoint(Point p) {
		Point btnOrigin = new Point(topLeft.getX(), topLeft.getY()-this.height, 0);
		//return new Point(0, this.height, 0);
		Frame contFrame = getContainingFrame();
		if(contFrame!=null){
			return contFrame.convertPointJEGFXtoJGFX(new Point(p.getX()+btnOrigin.getX(), p.getY()+btnOrigin.getY(), p.getZ()+btnOrigin.getZ()));
		}else{
			return new Point(0, 0, 0);
		}
	}
	*/
	
	@Override
	public Frame getContainingFrame() {
		// We don't live directly on a frame, so return the frame that our parent is on
		return parent.getContainingFrame();
	}

	/*
	@Override
	public Point getRealPointForRelativePoint(Point p) {
		return p;
	}
	
	@Override
	public Point getOrigin(){
		Point origin = new Point(topLeft.getX(), topLeft.getY()-(height), topLeft.getZ());
		origin = parent.getRealPointForRelativePoint(origin);
		//origin.setX(origin.getX()*parent.getZoom());
		//origin.setY(origin.getY()*parent.getZoom());
		return origin;
	}
	
	@Override
	public Point getRelativePointForRealPoint(Point p){
		Point origin = getOrigin();
		return new Point(((p.getX()-origin.getX())), ((p.getY()-origin.getY())), p.getZ()-origin.getZ());
	}
	*/
	
	@Override
	public World getWorld() {
		return parent.getWorld();
	}
	
	@Override
	public Frame getFrame(){
		return this.getContainingFrame();
	}

	@Override
	public void preLoad() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CoordinateSpace getRelativeSpace() {
		return new CoordinateSpace(parent.getRelativeSpace(), false, false, topLeft.getX(), topLeft.getY()-(this.height), 1);
	}
	
	@Override
	public CoordinateSpace getDrawingSpace(){
		return new CoordinateSpace(this.getRelativeSpace(), false, true, 0, height, 1);
		//Point origin = CoordinateSpace.convertPointToSystem(new Point(), this.getRelativeSpace(), this.getWorld().getRelativeSpace());
		//return new CoordinateSpace(this.getWorld().getRelativeSpace(), false, true, origin.getX(), origin.getY(), 1);
	}
}
