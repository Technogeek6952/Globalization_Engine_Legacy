package com.julianEngine.graphics.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.CustomFont;
import com.julianEngine.graphics.Frame;

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
		
		Graphics g = frame.getGraphics();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		this.metrics = metrics;
		textHeight = metrics.getHeight();
		textWidth = metrics.stringWidth(text);
		g.dispose();
		ready = true;
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
	}
	
	public void setCustomFont(CustomFont c){
		customFont = c;
	}
	
	public void setText(String newText){
		text = newText;
		textWidth = metrics.stringWidth(newText);
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
			Point gfxPoint = parent.getGFXPoint(topLeft.addVector(shift));
			int xPos = (int) gfxPoint.getX();
			int yPos = (int) gfxPoint.getY() + this.textHeight;
			graphics.drawString(text, xPos, yPos);
		}else{
			customFont.renderString(topLeft.addVector(shift), (Graphics2D) graphics, text, parent, wrapWidth, wrap);
		}
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
		int yPos = (frame.getWidth()-this.textHeight)/2;
		topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		parent = p;
	}
}
