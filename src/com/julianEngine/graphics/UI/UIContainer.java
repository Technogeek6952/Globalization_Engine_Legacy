package com.julianEngine.graphics.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.julianEngine.Engine2D;
import com.julianEngine.config.UserConfiguration;
import com.julianEngine.core.Parent;
import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.core.Parent.HookListener;
import com.julianEngine.graphics.Frame;

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
	
	public UIContainer(Point t1, int width, int height, World world){
		this(t1, width, height);
	}
	
	public UIContainer(Point tl, int width, int height){
		m_frame = new Frame(width, height);
		m_frame.setBackground(new Color(0, 0, 0, 0)); //transparent background
		m_topLeft = tl;
		m_width = width;
		m_height = height;
		ids++;
		id = ids;
		shift = new Vector(0, 0, 0);
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
	}

	public void setScrollType(ScrollType type){
		scrollAction = type;
	}
	
	@Override
	public void draw(Graphics graphics, Vector shift, boolean forceDraw) {
		Point gfxPoint = parent.getGFXPoint(m_topLeft);
		int xPos = (int) gfxPoint.getX();
		int yPos = (int) gfxPoint.getY();
		((Graphics2D)graphics).setColor(background);
		graphics.fillRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		((Graphics2D)graphics).setColor(borderColor);
		graphics.drawRoundRect(xPos, yPos, m_width, m_height, (m_width<m_height)?m_width/8:m_height/8, (m_width<m_height)?m_width/8:m_height/8);
		
		boolean useBuffer = false; //TODO: make a use buffer setting
		
		if(!useBuffer){
			AffineTransform at = ((Graphics2D)graphics).getTransform();
			((Graphics2D)graphics).translate(gfxPoint.getX(), gfxPoint.getY());
			((Graphics2D)graphics).clipRect(0, 0, m_width, m_height);
			((Graphics2D)graphics).translate(this.shift.getX(), -this.shift.getY());
			((Graphics2D)graphics).scale(zoom, zoom);
			m_frame.setShapes(shapes);
			m_frame.setBackground(new Color(0, 0, 0, 0));
			m_frame.drawFrame(((Graphics2D)graphics), forceDraw);
			
			if(UserConfiguration.getBool("containerShowMousePoint", false)){
				Point p = new Point(Engine2D.getMouseLocation().getX(), Engine2D.getMouseLocation().getY(), 0);
				p = this.getRelativePointForRealPoint(p);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Point getTopLeft() {
		// TODO Auto-generated method stub
		return null;
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
	public Point getGFXPoint(Point p) {
		Point newPoint = new Point();
		newPoint.setX(p.getX());
		newPoint.setY(m_height-p.getY());
		return newPoint;
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
	}
	
	@Override
	public Point getRealPointForRelativePoint(Point p) {
		Point origin = getOrigin();
		return new Point(((p.getX()+shift.getX()+origin.getX())*zoom), ((p.getY()+shift.getY()+origin.getY())*zoom), (p.getY())+origin.getZ());
	}
	
	@Override
	public Point getRelativePointForRealPoint(Point p){
		Point origin = getOrigin();
		return new Point(((p.getX()-origin.getX()-shift.getX())/zoom), ((p.getY()-origin.getY()-shift.getY())/zoom), p.getZ()-origin.getZ());
	}
	
	//HOOKS
	Map<String, List<HookListener>> hookListeners = new HashMap<String, List<HookListener>>(); // maps hookID to listeners
	
	@Override
	public void triggerHook(String hookID, byte[] data){
		for (HookListener l:hookListeners.get(hookID)){
			l.hookTriggered(hookID, data);
		}
		//also send the trigger to the parent
		parent.triggerHook(hookID, data);
	}
	
	@Override
	public void addHookListener(String hookID, HookListener listener){
		if (!hookListeners.containsKey(hookID)){
			hookListeners.put(hookID, new ArrayList<HookListener>());
		}
		hookListeners.get(hookID).add(listener);
	}
	
	/**
	 * calculates the point in the frame a given world-relative point equates to. Not effected by zoom/shift, so that certain math can be done on this point
	 * @return
	 */
	public Point getRasterPointForFramePoint(Point p){
		Point origin = getUnalteredOrigin();
		return new Point(((p.getX()-origin.getX())), ((p.getY()-origin.getY())), p.getZ()-origin.getZ());
	}
	
	@Override
	public Point getOrigin(){
		Point origin = new Point(m_topLeft.getX(), m_topLeft.getY()-(m_height*zoom), m_topLeft.getZ());
		origin = parent.getRealPointForRelativePoint(origin);
		//origin.setX(origin.getX()*parent.getZoom());
		//origin.setY(origin.getY()*parent.getZoom());
		return origin;
	}
	
	public Point getUnalteredOrigin(){
		Point origin = new Point(m_topLeft.getX(), m_topLeft.getY()-(m_height), m_topLeft.getZ());
		origin = parent.getRealPointForRelativePoint(origin);
		return origin;
	}
	
	@Override
	public World getWorld(){
		assert parent!=null;
		return parent.getWorld();
	}
	
	//given a point in the relative space of the containing world - is that point inside the space of this container?
	public boolean isPointInside(Point p){
		p = this.getRasterPointForFramePoint(p); //convert to relative coordinates
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
		// TODO Auto-generated method stub
		
	}
}
