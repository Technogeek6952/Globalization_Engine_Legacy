package com.julianEngine.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.data.DataManager;
import com.julianEngine.utility.Log;

public class CustomFont {
	int height;
	int padding;
	
	HashMap<Character, BufferedImage> characters = new HashMap<Character, BufferedImage>();
	static HashMap<Character, Float> characterWidths = new HashMap<Character, Float>(); //maps a character to a height multiplyer for the width
	HashMap<String, BufferedImage> preLoadedStrings = new HashMap<String, BufferedImage>(); //for optimization - only does intense rendering on a specific string once, and then uses this image
	public CustomFont(int height, int padding){
		this.height = height;
		this.padding = padding;
	}
	
	public void renderLetter(Point topLeft, Graphics2D graphics, char letter){
		
	}
	
	public void renderString(Point topLeft, Graphics2D graphics, String str, Parent parent, int wrapWidth, boolean wrap){
		if(getWidthOfString(str)==0){
			Log.trace("0 width");
		}
		Point point = parent.getGFXPoint(topLeft);
		int xPos = (int) point.getX();
		int yPos = (int) point.getY();
		if(preLoadedStrings.containsKey(str)){
			graphics.drawImage(preLoadedStrings.get(str), xPos, yPos, null);
		}else{
			//xPos = 0;
			//yPos = 0;
			point.setX(0);
			point.setY(0);
			int lineWidth = 0;
			BufferedImage stringImage = null;
			try{
				stringImage = new BufferedImage(getWidthOfString(str), this.height, BufferedImage.TYPE_INT_ARGB);
			}catch(Exception e){
				Log.trace("Error with text: "+str);
				Log.trace("width: "+getWidthOfString(str));
				e.printStackTrace();
			}
			Graphics2D strImgGfx = stringImage.createGraphics();
			for(char c:str.toCharArray()){
				BufferedImage charImage;
				int imgHeight;
				int imgWidth;
				int dstWidth;
				if(c!=' '){
					charImage = getImageForChar(c);
					imgHeight = charImage.getHeight();
					imgWidth = charImage.getWidth();
					//dstWidth = (int)(imgWidth*((double)this.height/(double)imgHeight));
					dstWidth = (int)((float)this.height*((float)imgWidth/(float)imgHeight));
				}else{
					charImage = null;
					imgHeight = 0;
					imgWidth = 0;
					dstWidth = 0;
				}
				switch (c){
				case ' ':
					if(lineWidth>wrapWidth && wrap){
						lineWidth = 0;
						point.setY(point.getY()+this.height);
						point.setX(xPos);
					}else{
						point.setX(point.getX()+this.height/2);
						lineWidth += this.height/2;
					}
					break;
				case '_':
					break;
					/*
				case '.':
					dstWidth = imgWidth*((this.height/4)/imgHeight);
					lineWidth += (wrap)?dstWidth:0;
					point.setY(point.getY()+(this.height-dstWidth));
					graphics.drawImage(charImage, (int) point.getX(), (int) point.getY(), (int) point.getX()+dstWidth, (int) point.getY()+dstWidth, 0, 0, charImage.getWidth(), charImage.getHeight(), null);
					point.setY(yPos);
					break;
					*/
				case '+':
					dstWidth = imgWidth*((this.height/2)/imgHeight);
					lineWidth += dstWidth;
					point.setY(point.getY()+(this.height/4));
					strImgGfx.drawImage(charImage, (int) point.getX(), (int) point.getY(), (int) point.getX()+dstWidth, (int) point.getY()+(this.height/2), 0, 0, charImage.getWidth(), charImage.getHeight(), null);
					point.setY(yPos);
					break;
				case '-':
					dstWidth = imgWidth*((this.height/2)/imgHeight);
					lineWidth += dstWidth;
					point.setY(point.getY()+(this.height/4));
					strImgGfx.drawImage(charImage, (int) point.getX(), (int) point.getY(), (int) point.getX()+dstWidth, (int) point.getY()+(this.height/2), 0, 0, charImage.getWidth(), charImage.getHeight(), null);
					point.setY(yPos);
					break;
				default:
					lineWidth += dstWidth;
					strImgGfx.drawImage(charImage, (int) point.getX(), (int) point.getY(), (int) point.getX()+dstWidth, (int) point.getY()+this.height, 0, 0, charImage.getWidth(), charImage.getHeight(), null);
					point.setX(point.getX()+dstWidth+padding);
				}
			}
			preLoadedStrings.put(str, stringImage);
			graphics.drawImage(stringImage, xPos, yPos, null);
		}
	}
	
	public int getWidthOfString(String str){
		float width = 0;
		if(str.equals("+")){
			int w = 0;
		}
		for(char c:str.toCharArray()){
			if(characterWidths.containsKey(c)){
				width += (float)characterWidths.get(c)*(float)height;
			}else{
				if(c!=' '){
					BufferedImage charImage = getImageForChar(c);
					int imgHeight = charImage.getHeight();
					int imgWidth = charImage.getWidth();
					
					if(c=='+'){
						width += (float)height*(float)((float)imgWidth/(float)(2f*(float)imgHeight));
						characterWidths.put(c, (float)((float)imgWidth/(float)(2f*(float)imgHeight)));
					}else if(c=='-'){
						width += (float)height*(float)((float)imgWidth/(float)(2f*(float)imgHeight));
						characterWidths.put(c, (float)((float)imgWidth/(float)(2f*(float)imgHeight)));
					}else{
						characterWidths.put(c, ((float)imgWidth/(float)imgHeight));
						width += (float)this.height*((float)imgWidth/(float)imgHeight);
					}
				}else{
					characterWidths.put(c, (float) .5);
					width += height/2;
				}
			}
		}
		return (int)(Math.ceil(width));
	}
	
	private BufferedImage getImageForChar(char c){
		BufferedImage img = null;
		try{
			switch(c){
			case '>':
				img = DataManager.getImageForURI("font/rightAngle.png");
				break;
			case '<':
				img = DataManager.getImageForURI("font/leftAngle.png");
				break;
			default:
				if((c>='A'&&c<='Z')){
					img = DataManager.getImageForURI("font/"+c+".png"); //upercase letters
				}else if((c>='a'&&c<='z')){
					img = DataManager.getImageForURI("font/"+c+"_.png"); //lowercase letters
				}else{
					img = DataManager.getImageForURI("font/"+c+".png");
				}
			}
		}catch (Exception e){
			
		}
		return img;
	}
}
