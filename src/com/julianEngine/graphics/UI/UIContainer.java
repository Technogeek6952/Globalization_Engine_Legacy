package com.julianEngine.graphics.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.julianEngine.Engine2D;
import com.julianEngine.config.UserConfiguration;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.core.CoordinateSpace.AxisType;
import com.julianEngine.core.CoordinateSpace.SystemType;
import com.julianEngine.graphics.Frame;
import com.julianEngine.utility.Log;

public class UIContainer implements Shape, Parent{
	
	Frame m_frame;
	Point m_topLeft;
	int m_width;
	int m_height;
	Parent parent;
	BufferedImage buffer = null;
	ArrayList<Shape> shapes = new ArrayList<Shape>();
	Color background = new Color(0, 0, 0, 0);
	ArrayList<UIContainerListener> listeners = new ArrayList<UIContainerListener>();
	UIMask mask = null;
	Color borderColor = new Color(0, 0, 0, 0);
	ScrollType scrollAction = ScrollType.NONE;
	double zoom = 1f;
	static int ids = 0;//debug
	int id;
	Vector shift;
	//CoordinateSpace relSpace;
	
	public UIContainer(Point tl, int width, int height, Parent parent){
		m_frame = new Frame(width, height);
		m_frame.setBackground(new Color(0, 0, 0, 0)); //transparent background
		m_topLeft = tl;
		m_width = width;
		m_height = height;
		ids++;
		id = ids;
		shift = new Vector(0, 0, 0);
		this.parent = parent;
		
		//relSpace = new CoordinateSpace(SystemType.CARTESIAN, AxisType.XAXIS_RIGHT_POS, AxisType.YAXIS_UP_POS); //just to have a system until it is changed by setting the parent
	}

	public void setShift(Vector newShift){
		shift = newShift;
	}
	
	public Vector getShift(){
		return shift;
	}
	
	public void setBorderColor(Color c){
		borderColor = c;
	}
	
	public void addListener(UIContainerListener l){
		listeners.add(l);
	}
	
	public void setBackground(Color c){
		//m_frame.setBackground(c);
		background = c;
	}
	
	public void addShape(Shape s){
		shapes.add(s);
		s.setParent(this);
	}
	
	public void removeShape(Shape s){
		shapes.remove(s);
		s.setParent(null);
	}
	
	public void clear(){
		shapes.clear();
	}
	
	public void addShapes(Shape... shapes){
		for(Shape shape:shapes){
			addShape(shape);
		}
	}
	
	public Frame getFrame(){
		return m_frame;
	}
	
	@Override
	public void setParent(Parent p) {
		parent = p;
		if (parent != null){ //if the parent exists, move our coordinate system to that
			//relSpace = new CoordinateSpace(p.getRelativeSpace(), false, false, this.getTopLeftX(), this.getTopLeftY(), this.zoom);
		}
	}

	public void setScrollType(ScrollType type){
		scrollAction = type;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		Point gfxPoint = CoordinateSpace.convertPointToSystem(m_topLeft, parent.getRelativeSpace(), parent.getDrawingSpace());
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		((Graphics2D)graphics).setColor(background);
		graphics.fillRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		((Graphics2D)graphics).setColor(borderColor);
		graphics.drawRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		
		boolean useBuffer = UserConfiguration.getBool("useContainerBuffers", false); //TODO: make a use buffer setting
		
		if(!useBuffer){
			AffineTransform at = ((Graphics2D)graphics).getTransform();
			((Graphics2D)graphics).translate(gfxPoint.getX(), gfxPoint.getY());
			((Graphics2D)graphics).clipRect(0, 0, m_width, m_height);
			((Graphics2D)graphics).translate(0, -((m_height*zoom)-m_height));
			((Graphics2D)graphics).translate(this.shift.getX(), this.shift.getY());
			((Graphics2D)graphics).scale(zoom, zoom);
			
			
			m_frame.setShapes(shapes);
			m_frame.setBackground(new Color(0, 0, 0, 0));
			m_frame.drawFrame(((Graphics2D)graphics), forceDraw);
			
			if(UserConfiguration.getBool("containerShowMousePoint", false)){
				Point p = new Point(Engine2D.getMouseLocation().getX(), Engine2D.getMouseLocation().getY(), 0);
				p = CoordinateSpace.convertPointToSystem(p, Engine2D.frameRootSystem, this.getRelativeSpace());
				graphics.setFont(new Font("Ariel", Font.PLAIN, 20));
				graphics.drawString("("+p.getX()+", "+p.getY()+")", 0, 20);
			}
			
			((Graphics2D)graphics).setTransform(at);
			((Graphics2D)graphics).setClip(null);
		}else{
			if(buffer==null||forceDraw){
				//set up frame
				m_frame.setShapes(shapes);
				//draw buffer
				buffer = new BufferedImage(m_width, m_height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D bufferGFX = buffer.createGraphics();
				m_frame.drawFrame(bufferGFX, true);
			}
			graphics.drawImage(buffer, xPos, yPos, xPos+m_width, yPos+m_height, 
					0, 0, buffer.getWidth(), buffer.getHeight(), null);
		}
		if(mask!=null&&mask.isMouseInside()){
			Color oldColor = graphics.getColor();
			graphics.setColor(Color.pink);
			graphics.fillRect(xPos, yPos, m_width, m_height);
			graphics.setColor(oldColor);
		}
	}

	@Override
	public int getTopLeftX() {
		return (int)m_topLeft.getX();
	}

	@Override
	public int getTopLeftY() {
		return (int)m_topLeft.getY();
	}

	@Override
	public int getTopLeftZ() {
		return (int)m_topLeft.getZ();
	}

	@Override
	public Point getTopLeft() {
		return m_topLeft;
	}

	@Override
	public void move(Vector path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAnchored(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void centerX(Frame frame) {
		m_topLeft.setX((frame.getWidth()-m_width)/2);
	}

	@Override
	public void centerY(Frame frame) {
		m_topLeft.setY((frame.getHeight()+m_height)/2);
	}

	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public Frame getContainingFrame() {
		return m_frame;
	}

	@Override
	public double getZoom(){
		return zoom;
	}
	
	public void setZoom(double d){
		zoom = d;
		//relSpace.setScale(d);
	}
	
	public void zoomOnPoint(Point focus, double scale){
		try {
			focus = CoordinateSpace.convertPointToSystem(focus, Engine2D.getInstance().mouseEventSpace, this.getRelativeSpace());
			//CoordinateSpace oldSpace = this.getRelativeSpace();
			focus = new Point (m_width/2, m_height/2, 0);
			Point startPoint = CoordinateSpace.convertPointToSystem(focus, this.getRelativeSpace(), Engine2D.frameRootSystem);
			//Point framePoint = CoordinateSpace.convertPointToSystem(focus, this.getRelativeSpace(), Engine2D.getInstance().mouseEventSpace);
			this.zoom += scale;
			Point endPoint = CoordinateSpace.convertPointToSystem(focus, this.getRelativeSpace(), Engine2D.frameRootSystem);
			Vector shift = startPoint.vectorTo(endPoint);
			Log.info("<"+shift.getX()+", "+shift.getY()+">");
			this.shift.addVectorToThis(shift);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public World getWorld(){
		return parent.getWorld();
	}
	
	//given a point in the relative space of the containing world - is that point inside the space of this container?
	public boolean isPointInside(Point p){
		p = CoordinateSpace.convertPointToSystem(p, this.getWorld().getRelativeSpace(), this.getRelativeSpace()); //convert to relative coordinates
		//if either the x or y is negative, the coordinate is out of the frame, the expression below will evaluate to true - it is then NOTed to be false
		//if either the x or y is greater than the width or height respectively, the expression below will evaluate to true - and return false
		return !((p.getX()<0||p.getY()<0)||(p.getX()>m_width||p.getY()>m_height));
	}
	
	public int getWidth(){
		return m_width;
	}
	
	public int getHeight(){
		return m_height;
	}
	
	public interface UIContainerListener{
		public void containerClicked();
	}
	
	public enum ScrollType{
		SCROLL_X,
		SCROLL_Y,
		ZOOM,
		NONE;
	}

	@Override
	public void preLoad() {
		
	}

	@Override
	public CoordinateSpace getRelativeSpace() {
		//since our parent's space might change at any time, don't store it in a variable, just calculate it for every request
		//there may be a way to make this more efficient, but it's fine for now.
		return new CoordinateSpace(parent.getRelativeSpace(), false, false, this.m_topLeft.getX()+this.shift.getX(), (this.m_topLeft.getY()-m_height)+this.shift.getY(), this.zoom);
	}
	
	@Override
	public CoordinateSpace getDrawingSpace(){
		return new CoordinateSpace(this.getRelativeSpace(), false, true, 0, m_height, this.zoom);
	}
}
