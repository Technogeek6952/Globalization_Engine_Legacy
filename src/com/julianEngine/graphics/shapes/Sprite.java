package com.julianEngine.graphics.shapes;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.data.DataManager;
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
	private int frameIndex = 0;
	private long lastSwitch;
	private int timePerFrame = 25; //number of ms to hold each frame of a gif
	private int pauseOnFrame = -1;
	private boolean ready = false;
	private float alpha = 1;
	private boolean pause = false;
	private Parent parent;
	
	/*--------Code--------------------------*/
	
	public Sprite(Point topLeft, double scale, String texture){
		this.topLeft = topLeft;
		
		try{
			if(DataManager.doesResourceExist(texture)){
				if(texture.toLowerCase().endsWith(".png")){
					BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource(texture));
					this.image = spriteImage;
					this.imgWidth = spriteImage.getWidth();
					this.imgHeight = spriteImage.getHeight();
					this.dstWidth = (int) (imgWidth*scale);
					this.dstHeight = (int) (imgHeight*scale);
				}else if(texture.toLowerCase().endsWith(".gif")){
					frames = getFramesFromStream(DataManager.getStreamForResource(texture));
					this.imgWidth = frames.get(0).getWidth();
					this.imgHeight = frames.get(0).getHeight();
					this.dstWidth = (int) (imgWidth*scale);
					this.dstHeight = (int) (imgHeight*scale);
				}else if(texture.equals("")){
					BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource("images/misc/Error.png"));
					this.image = spriteImage;
					this.imgWidth = spriteImage.getWidth();
					this.imgHeight = spriteImage.getHeight();
					this.dstWidth = (int) (imgWidth*scale);
					this.dstHeight = (int) (imgHeight*scale);
				}else{
					Log.error("Unrecognized image format for resource: "+texture);
				}
			}else {
				BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource("images/misc/Error.png"));
				this.image = spriteImage;
				this.imgWidth = spriteImage.getWidth();
				this.imgHeight = spriteImage.getHeight();
				this.dstWidth = (int) (imgWidth*scale);
				this.dstHeight = (int) (imgHeight*scale);
			}
		}catch(Exception e){
			Log.error("Error while loading resource: "+texture);
		}
		ready = true;
	}
	
	public Sprite(Point topLeft, int width, int height, String texture){
		this.topLeft = topLeft;
		this.dstWidth = width;
		this.dstHeight = height;
		
		try{
			if(DataManager.doesResourceExist(texture)){
				if(texture.toLowerCase().endsWith(".png")){
					BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource(texture));
					this.image = spriteImage;
					this.imgWidth = spriteImage.getWidth();
					this.imgHeight = spriteImage.getHeight();
				}else if(texture.toLowerCase().endsWith(".gif")){
					frames = getFramesFromStream(DataManager.getStreamForResource(texture));
					this.imgWidth = frames.get(0).getWidth();
					this.imgHeight = frames.get(0).getHeight();
				}else if(texture.equals("")){
					BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource("images/misc/Error.png"));
					this.image = spriteImage;
					this.imgWidth = spriteImage.getWidth();
					this.imgHeight = spriteImage.getHeight();
				}else{
					Log.error("Unrecognized image format for resource: "+texture);
				}
			}else {
				BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource("images/misc/Error.png"));
				this.image = spriteImage;
				this.imgWidth = spriteImage.getWidth();
				this.imgHeight = spriteImage.getHeight();
			}
		}catch(Exception e){
			
		}
		ready = true;
	}
	
	public Sprite(Point topLeft, int width, int height, BufferedImage spriteImage){
		this.topLeft = topLeft;
		this.dstWidth = width;
		this.dstHeight = height;
		this.image = spriteImage;
		this.imgWidth = spriteImage.getWidth();
		this.imgHeight = spriteImage.getHeight();
		ready = true;
	}
	
	public void setImage(String texture){
		try{
			if(texture.toLowerCase().endsWith(".png")){
				BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource(texture));
				this.image = spriteImage;
				this.imgWidth = spriteImage.getWidth();
				this.imgHeight = spriteImage.getHeight();
			}else if(texture.toLowerCase().endsWith(".gif")){
				frames = getFramesFromStream(DataManager.getStreamForResource(texture));
				this.imgWidth = frames.get(0).getWidth();
				this.imgHeight = frames.get(0).getHeight();
			}else if(texture.equals("")){
				BufferedImage spriteImage = ImageIO.read(DataManager.getStreamForResource("images/misc/Error.png"));
				this.image = spriteImage;
				this.imgWidth = spriteImage.getWidth();
				this.imgHeight = spriteImage.getHeight();
			}else{
				Log.error("Unrecognized image format for resource: "+texture);
			}
		}catch(Exception e){
			
		}
	}
	
	public void setImage(BufferedImage image){
		this.image = image;
		this.imgWidth = image.getWidth();
		this.imgHeight = image.getHeight();
	}
	
	public void setHeight(int newHeight, boolean scale){
		if(scale){
			double aspectRatio = (double)this.imgWidth/(double)this.imgHeight;
			this.dstHeight = newHeight;
			this.dstWidth = (int)Math.round(newHeight*aspectRatio);
		}else{
			this.dstHeight = newHeight;
		}
	}
	
	public void setWidth(int newWidth, boolean scale){
		if(scale){
			double aspectRatio = (double)this.imgWidth/(double)this.imgHeight;
			this.dstWidth = newWidth;
			this.dstHeight = (int)Math.round(newWidth/aspectRatio);
		}else{
			this.dstWidth = newWidth;
		}
	}
	
	public int getBaseWidth(){
		return this.imgWidth;
	}
	
	public int getBaseHeight(){
		return this.imgHeight;
	}
	
	public int getDestWidth(){
		return this.dstWidth;
	}
	
	public int getDestHeight(){
		return this.dstHeight;
	}
	
	public void blur(){
		Kernel avgKernel = new Kernel(3, 3, new float[] {1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f});
		Kernel gausKernel = new Kernel(3, 3, new float[] {1f/16f, 1f/8f, 1f/16f, 1f/8f, 1f/4f, 1f/8f, 1f/16f, 1f/8f, 1f/16f});
		BufferedImageOp op = new ConvolveOp(avgKernel);
		BufferedImageOp gausOp = new ConvolveOp(gausKernel);
		image = op.filter((BufferedImage) image, null);
		image = op.filter((BufferedImage) image, null);
		image = op.filter((BufferedImage) image, null);
		image = gausOp.filter((BufferedImage) image, null);
		image = gausOp.filter((BufferedImage) image, null);
		image = gausOp.filter((BufferedImage) image, null);
	}
	
	public void blur(int times){
		blur(times, 3);
	}
	
	public void blur(int times, int type){
		Kernel avgKernel = new Kernel(3, 3, new float[] {1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f, 1f/9f});
		Kernel gausKernel = new Kernel(3, 3, new float[] {1f/16f, 1f/8f, 1f/16f, 1f/8f, 1f/4f, 1f/8f, 1f/16f, 1f/8f, 1f/16f});
		BufferedImageOp op = new ConvolveOp(avgKernel);
		BufferedImageOp gausOp = new ConvolveOp(gausKernel);
		for(int i=0;i<times;i++){
			if(type==1){
				image = op.filter((BufferedImage) image, null);
			}else if(type==2){
				image = gausOp.filter((BufferedImage) image, null);
			}else if(type==3){
				image = op.filter((BufferedImage) image, null);
				image = gausOp.filter((BufferedImage) image, null);
			}
		}
	}
	
	public void setGifFPS(int fps){
		timePerFrame = 1000/fps;
	}
	
	public void pauseOnFrame(int frame){
		pauseOnFrame = frame;
	}
	
	public void resumeGIF(){
		frameIndex = pauseOnFrame+1;
		pause = false;
	}
	
	public boolean isPaused(){
		return pause;
	}
	
	public int getNumberOfFrames(){
		return frames.size();
	}
	
	public void setAlpha(float newAlpha){
		alpha = newAlpha;
	}
	
	public int getAlphaAtPoint(int x, int y){
		//convert points
		//x = x*(this.imgWidth/this.dstWidth);
		//y = y*(this.imgHeight/this.dstHeight);
		try{
			if(image!=null){
				int color = ((BufferedImage) image).getRGB((int)Math.floor(x*((double)this.imgWidth/(double)this.dstWidth)), (int)Math.floor(y*((double)this.imgHeight/(double)this.dstHeight)));
				return ((color>>24) & 0xff);
			}else if(frames!=null){
				int color = frames.get(frameIndex).getRGB((int)Math.floor(x*((double)this.imgWidth/(double)this.dstWidth)), (int)Math.floor(y*((double)this.imgHeight/(double)this.dstHeight)));
				return ((color>>24) & 0xff);
			}
		}catch(Exception e){
			//might happen if coords are out of bounds, can just ignore
		}
		return 0;
	}
	
	/* !!!!!!!!!!!!!!!!!!!MAY STILL BE USEFUL, COMMENTED OUT BECAUSE WASN'T USED AND GENERATED WARNINGS 
	private ArrayList<BufferedImage> getFramesFromFile(File gif) throws IOException{
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		ImageReader reader = new GIFImageReader(new GIFImageReaderSpi());
		reader.setInput(ImageIO.createImageInputStream(gif));
		for(int i=0; i<reader.getNumImages(true);i++){
			frames.add(reader.read(i));
		}
		return frames;
	}
	*/
	
	private ArrayList<BufferedImage> getFramesFromStream(InputStream stream) throws IOException{
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		ImageReader reader = new GIFImageReader(new GIFImageReaderSpi());
		reader.setInput(ImageIO.createImageInputStream(stream));
		for(int i=0; i<reader.getNumImages(true);i++){
			frames.add(reader.read(i));
		}
		return frames;
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public void draw(Graphics graphics, Vector shift, boolean forceDraw){
		if(anchored){
			
		}
		//int xPos = Math.round((float)topLeft.getX() + ((anchored)?0:(float)shift.getX()));
		//int yPos = Math.round((float)(windowHeight - topLeft.getY())+ ((anchored)?0:(float)shift.getY()));
		
		Point gfxPoint = parent.getGFXPoint(topLeft);
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		
		Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		Composite opaqueComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
		((Graphics2D) graphics).setComposite(alphaComp);
		if(image!=null){
			graphics.drawImage(image, xPos, yPos, xPos+dstWidth, yPos+dstHeight, 
					0, 0, imgWidth, imgHeight, null);
		}else if(frames!=null){
			graphics.drawImage(frames.get(frameIndex), xPos, yPos, xPos+dstWidth, 
					yPos+dstHeight, 0, 0, imgWidth, imgHeight, null);
			if(System.currentTimeMillis()-lastSwitch>timePerFrame&&!pause){
				if(frameIndex==pauseOnFrame){
					pause = true;
				}else{
					frameIndex++;
				}
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
	
	@Override
	public void centerX(Frame frame) {
		int xPos = (frame.getWidth()-this.dstWidth)/2;
		topLeft = new Point(xPos, topLeft.getY(), topLeft.getZ());
	}

	@Override
	public void centerY(Frame frame) {
		int yPos = (frame.getHeight()+this.dstHeight)/2;
		topLeft = new Point(topLeft.getX(), yPos, topLeft.getZ());
	}

	@Override
	public void setParent(Parent p) {
		this.parent = p;
	}
}
