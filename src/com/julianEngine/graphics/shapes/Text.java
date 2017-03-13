package com.julianEngine.graphics.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.julianEngine.Engine2D;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.CustomFont;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.UI.UIContainer;

public class Text implements Shape{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	private Point topLeft;
	private String text;
	private Color color;
	private boolean anchored;
	private Font font;
	private boolean useCustomFont = false;
	CustomFont customFont;
	private int textHeight;
	private int textWidth;
	private FontMetrics metrics;
	private boolean ready = false;
	private Parent parent;
	private int wrapWidth;
	private boolean wrap = false;
	private Frame centerFrame = null;
	private boolean centerX = false;
	private boolean centerY = false;
	
	/*--------Code--------------------------*/
	//Constructors
	public Text(Point topLeft, String text, Frame frame){
		this(topLeft, text, Color.BLACK, new Font("Ariel", Font.PLAIN, 20), frame);
		ready = true;
	}
	
	public Text(Point topLeft, String text, Color color, Font font, Frame frame){
		this.topLeft = topLeft;
		this.text = text;
		this.color = color;
		this.font = font;
		
		Graphics g = Engine2D.getInstance().rootFrame.getGraphics();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		this.metrics = metrics;
		textHeight = metrics.getHeight();
		textWidth = metrics.stringWidth(text);
		g.dispose();
		ready = true;
	}
	
	public void fitCustomFontToContainer(UIContainer container){
		int containerHeight = container.getFrame().getHeight();
		int containerWidth = container.getFrame().getWidth();
		for(int i = 1;i<=containerHeight;i++){
			CustomFont font = new CustomFont(i, 0);
			if(font.getWidthOfString(text)<=containerWidth){
				this.setCustomFont(font);
			}
		}
		this.useCustomFont(true);
	}
	
	public void setWidth(int width){ //wraps text if beond width
		wrapWidth = width;
		wrap = true;
	}
	
	public int getWidth(){
		return textWidth;
	}
	
	public int getHeight(){
		return textHeight;
	}
	
	public void setColor(Color newColor){
		color = newColor;
	}
	
	public void setFont(Font newFont){
		font = newFont;
	}
	
	public void useCustomFont(boolean b){
		useCustomFont = b;
		textHeight = customFont.getHeight();
	}
	
	public void setCustomFont(CustomFont c){
		customFont = c;
	}
	
	public void setText(String newText){
		text = newText;
		textWidth = metrics.stringWidth(newText);
		
		/*
		if (centerFrame!=null){
			if (centerX){
				centerX(centerFrame);
			}
			if (centerY){
				centerY(centerFrame);
			}
		}
		*/
	}
	
	public void preRenderText(String text){
		customFont.preRenderString(text, wrapWidth, wrap, parent);
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public String getText(){
		return text;
	}
	
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		if(anchored){
			
		}
		if(!useCustomFont){
			graphics.setColor(color);
			graphics.setFont(font);
			//int xPos = Math.round((float)topLeft.getX() + ((anchored)?0:(float)shift.getX()));
			//int yPos = Math.round((float)(height - topLeft.getY()) + ((anchored)?0:(float)shift.getY()) + (2*textHeight));
			Point gfxPoint = CoordinateSpace.convertPointToSystem(topLeft.addVector(shift), parent.getRelativeSpace(), parent.getDrawingSpace());
			int xPos = (int) gfxPoint.getX();
			int yPos = (int) gfxPoint.getY() + this.textHeight;
			graphics.drawString(text, xPos, yPos);
		}else{
			customFont.renderString(topLeft.addVector(shift), (Graphics2D) graphics, text, parent, wrapWidth, wrap);
		}
	}

	@Override
	public int getTopLeftX() {
		return (int)topLeft.getX();
	}

	@Override
	public int getTopLeftY() {
		return (int)topLeft.getY();
	}

	@Override
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

	@Override
	public void centerX(Frame frame) {
		centerFrame = frame;
		centerX = true;
		if(!useCustomFont){
			int xPos = (frame.getWidth()-this.textWidth)/2;
			topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
		}else{
			int xPos = (frame.getWidth()-this.customFont.getWidthOfString(text))/2;
			topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
		}
	}

	@Override
	public void centerY(Frame frame) {
		centerFrame = frame;
		centerY = true;
		if(!useCustomFont){
			int yPos = (frame.getHeight()+this.textHeight)/2;
			topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
		}else{
			int yPos = (frame.getHeight()+this.customFont.getHeight())/2;
			topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
		}
	}

	@Override
	public void setParent(Parent p) {
		parent = p;
	}
}
