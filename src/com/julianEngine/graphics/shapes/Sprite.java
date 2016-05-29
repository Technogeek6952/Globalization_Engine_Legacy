package com.julianEngine.graphics.shapes;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.Frame;
import com.julianEngine.utility.Log;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;

public class Sprite implements Shape{
	/*--------Public Static Variables-------*/
	
	/*--------Private Static Variables------*/
	
	/*--------Public Instance Variables-----*/
	
	/*--------Private Instance Variables----*/
	private Point topLeft;
	private Image image;
	private ArrayList<BufferedImage> frames = null;
	private int imgWidth;
	private int imgHeight;
	private int dstWidth;
	private int dstHeight;
	private boolean anchored;
	//variables for gif playback
	private int frameIndex;
	private long lastSwitch;
	private int timePerFrame = 25; //number of ms to hold each frame of a gif
	private boolean ready = false;
	private float alpha = 1;
	/*--------Code--------------------------*/
	
	public Sprite(Point topLeft, double scale, String texture){
		this.topLeft = topLeft;
		
		try {
			BufferedImage spriteImage = ImageIO.read(new File(texture));
			this.image = spriteImage;
			this.imgWidth = spriteImage.getWidth();
			this.imgHeight = spriteImage.getHeight();
			this.dstWidth = (int) (imgWidth*scale);
			this.dstHeight = (int) (imgHeight*scale);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ready = true;
	}
	
	public Sprite(Point topLeft, int width, int height, String texture){
		this.topLeft = topLeft;
		this.dstWidth = width;
		this.dstHeight = height;
		
		File imageFile = new File(texture);
		try {
			if(imageFile.getPath().toLowerCase().endsWith(".png")){
				BufferedImage spriteImage = ImageIO.read(new File(texture));
				this.image = spriteImage;
				this.imgWidth = spriteImage.getWidth();
				this.imgHeight = spriteImage.getHeight();
			}else if(imageFile.getPath().toLowerCase().endsWith(".gif")){
				frames = getFramesFromFile(imageFile);
				this.imgWidth = frames.get(0).getWidth();
				this.imgHeight = frames.get(0).getHeight();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//check if texture can be located in loaded files
		//if not, check disk
		//else assign error texture
		ready = true;
	}
	
	public void setGifFPS(int fps){
		timePerFrame = 1000/fps;
	}
	
	public void setAlpha(float newAlpha){
		alpha = newAlpha;
	}
	
	private ArrayList<BufferedImage> getFramesFromFile(File gif) throws IOException{
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		ImageReader reader = new GIFImageReader(new GIFImageReaderSpi());
		reader.setInput(ImageIO.createImageInputStream(gif));
		for(int i=0; i<reader.getNumImages(true);i++){
			frames.add(reader.read(i));
		}
		return frames;
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public void draw(Graphics graphics, int windowHeight, Vector shift, Frame frame){
		int xPos = Math.round((float)topLeft.getX() + ((anchored)?0:(float)shift.getX()));
		int yPos = Math.round((float)(windowHeight - topLeft.getY())+ ((anchored)?0:(float)shift.getY()));
		
		Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		Composite opaqueComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
		((Graphics2D) graphics).setComposite(alphaComp);
		if(image!=null){
			graphics.drawImage(image, xPos, yPos, xPos+dstWidth, yPos+dstHeight, 
					0, 0, imgWidth, imgHeight, null);
		}else if(frames!=null){
			graphics.drawImage(frames.get(frameIndex), xPos, yPos, xPos+dstWidth, 
					yPos+dstHeight, 0, 0, imgWidth, imgHeight, null);
			if(System.currentTimeMillis()-lastSwitch>timePerFrame){
				frameIndex++;
				if(frameIndex>=frames.size()){
					frameIndex=0;
				}
				lastSwitch = System.currentTimeMillis();
			}
		}
		((Graphics2D) graphics).setComposite(opaqueComp);
	}
	
	public void setAnchored(boolean b){
		anchored = b;
	}
	
	public int getTopLeftX(){
		return (int) topLeft.getX();
	}
	
	public int getTopLeftY(){
		return (int) topLeft.getY();
	}
	
	public int getTopLeftZ(){
		return (int) topLeft.getZ();
	}
	
	public void move(Vector path){
		topLeft = topLeft.addVector(path);
	}
	
	public Point getTopLeft() {
		return topLeft;
	}
}
