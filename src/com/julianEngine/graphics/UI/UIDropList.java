package com.julianEngine.graphics.UI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import com.julianEngine.Engine2D;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.graphics.UI.UIButton.UIButtonListener;
import com.julianEngine.graphics.shapes.Sprite;
import com.julianEngine.utility.Log;

public class UIDropList extends UIContainer{

	private List<ListSelection> objects = new ArrayList<ListSelection>();
	private class ListSelection{
		public UIContainer container;
		public UIButton button;
	}
	
	private boolean renderList = false;
	private ListSelection selectedObject;
	private Sprite dropIcon;
	private UIButton selectionButton;
	private boolean renderDown;
	
	public UIDropList(Point tl, int width, int height, Parent parent, boolean openDown) {
		super(tl, width, height, parent);
		dropIcon = new Sprite(new Point(width-height, height, 100), height, height, "arrow");
		this.addShape(dropIcon);
		
		selectionButton = new UIButton(new Point(0, height, 101), " ", Color.white, width, height, this.getWorld().getFrame(), this);
		selectionButton.addUIButtonListener(new UIButtonListener(){
			@Override
			public void buttonClicked() {
				Log.info("List Clicked");
				setDropDown(!renderList);
			}
			@Override
			public void buttonMousedOver() {
			}
			@Override
			public void buttonLostMouse() {
			}
		});
		this.addShape(selectionButton);
		renderDown = openDown;
	}
	
	public int getObjectWidth(){
		return this.m_width-this.m_height;
	}
	
	public int getObjectHeight(){
		return this.m_height;
	}
	
	public void addObject(UIContainer obj, boolean select){
		ListSelection newselection = new ListSelection();
		newselection.container = obj;
		newselection.button = new UIButton(new Point(0, 0, 101), " ", Color.white, m_width, m_height, this.getWorld().getFrame(), this);
		newselection.button.addUIButtonListener(new UIButtonListener(){
			@Override
			public void buttonClicked() {
				changeSelected(newselection);
				setDropDown(false);
			}
			@Override
			public void buttonMousedOver() {
			}
			@Override
			public void buttonLostMouse() {
			}
		});
		objects.add(newselection);
		obj.getTopLeft().setY(0);
		if (select||selectedObject==null){
			changeSelected(newselection);
		}
		newselection.container.setParent(this);
		newselection.button.setParent(this);
	}
	
	private void changeSelected(ListSelection obj){
		if (selectedObject!=null){
			selectedObject.container.getTopLeft().setY(0);
			obj.button.setEnabled(true);
			this.removeShape(selectedObject.container);
		}
		selectedObject = obj;
		obj.container.getTopLeft().setY(this.m_height);
		obj.button.setEnabled(false);
		this.addShape(obj.container);
	}
	
	public void setDropDown(boolean drop){
		if (drop){
			int i=1;
			for (ListSelection obj:objects){
				if (obj.equals(selectedObject)){
				}else{
					if (renderDown){
						obj.container.getTopLeft().setY(m_height-(m_height*i));
						obj.button.getTopLeft().setY(m_height-(m_height*i));
					}else{
						obj.container.getTopLeft().setY(m_height+(m_height*i));
						obj.button.getTopLeft().setY(m_height+(m_height*i));
					}
					obj.container.setParent(this);
					obj.button.setParent(this);
					obj.button.setEnabled(true);
					i++;
				}
			}
			renderList = true;
		}else{
			renderList = false;
		}
	}
	
	public void setDropDirection(boolean drawDown){
		renderDown = drawDown;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw){
		if (!renderList){
			super.draw(graphics, shift, forceDraw);
		}else{
			Point screenPoint = CoordinateSpace.convertPointToSystem(new Point(), Engine2D.frameRootSystem, this.getDrawingSpace());
			((Graphics2D)graphics).setClip((int)screenPoint.getX(), (int)screenPoint.getY(), Engine2D.getInstance().rootFrame.getWidth(), Engine2D.getInstance().rootFrame.getHeight());
			
			Point gfxPoint = CoordinateSpace.convertPointToSystem(m_topLeft, parent.getRelativeSpace(), parent.getDrawingSpace());
			Point renderTL = CoordinateSpace.convertPointToSystem(new Point(m_topLeft.getX(), m_topLeft.getY()+((renderDown)?0:m_height*(objects.size()-1)), 0), parent.getRelativeSpace(), parent.getDrawingSpace());
			((Graphics2D)graphics).setColor(background);
			graphics.fillRect((int)renderTL.getX(), (int)renderTL.getY(), m_width, m_height*objects.size());//, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
			((Graphics2D)graphics).setColor(borderColor);
			graphics.drawRect((int)renderTL.getX(), (int)renderTL.getY(), m_width, m_height*objects.size());//, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
			
			AffineTransform at = ((Graphics2D)graphics).getTransform();
			((Graphics2D)graphics).translate(gfxPoint.getX(), gfxPoint.getY());
			((Graphics2D)graphics).translate(0, -((m_height*zoom)-m_height));
			((Graphics2D)graphics).translate(this.shift.getX(), this.shift.getY());
			((Graphics2D)graphics).scale(zoom, zoom);
			
			for (ListSelection obj:objects){
				if (obj.equals(selectedObject)){
				}else{
					obj.button.draw(graphics, shift, forceDraw);
					obj.container.draw(graphics, shift, forceDraw);
				}
			}
			
			selectedObject.container.draw(graphics, shift, forceDraw);
			selectionButton.draw(graphics, shift, forceDraw);
			
			((Graphics2D)graphics).setTransform(at);
		}
	}

}
