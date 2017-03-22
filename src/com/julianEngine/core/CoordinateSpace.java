package com.julianEngine.core;

import com.julianEngine.utility.Log;

/**
 * Describes a coordinate system, and gives methods to convert to other spaces
 * @author Bowers
 *
 */
public class CoordinateSpace {
	double originx; //x value of origin as a point in this space's parent (0 if this space is a root)
	double originy; //y value of origin as a point in this space's parent (0 if this space is a root)
	double scale; //scale of this space relative to it's parent (1 if this space is a root)
	boolean isRoot; //is this the parent space. Points cannot be converted between two systems with different roots
	CoordinateSpace parent; //the parent space to this system, null if this is the the root space
	AxisType domain; //describes the type of axis used for the domain (x value in traditional graphs)
	AxisType range; //describes the type of axis used for the range (y value in traditional graphs)
	SystemType type;
	
	public CoordinateSpace(SystemType type, AxisType domainAxis, AxisType rangeAxis){
		//This constructor describes a parent coordinate system
		originx = 0;
		originy = 0;
		scale = 1;
		isRoot = true;
		parent = null;
		
		//Currently there is no protection to make sure the provided axis make sense, so there is potential for bugs...
		this.type = type;
		domain = domainAxis;
		range = rangeAxis;
	}
	
	public CoordinateSpace(CoordinateSpace parent, boolean reverseDomain, boolean reverseRange, double xpos, double ypos, double scale){
		//This constructor describes a child of another system
		originx = xpos;
		originy = ypos;
		if (reverseDomain){
			domain = parent.getDomainAxis().getReverseAxis();
		}else{
			domain = parent.getDomainAxis();
		}
		if (reverseRange){
			range = parent.getRangeAxis().getReverseAxis();
		}else{
			range = parent.getRangeAxis();
		}
		this.parent = parent;
		this.scale = scale;
		isRoot = false;
		type = parent.getType();
	}
	
	public CoordinateSpace getRootSpace(){
		if (this.isRoot){
			return this;
		}else{
			return parent.getRootSpace();
		}
	}
	
	/**
	 * returns the parent if it exists, otherwise returns null
	 * @return
	 */
	public CoordinateSpace getParent(){
		return parent;
	}
	
	public void setScale(double newScale){
		scale = newScale;
	}
	
	/**
	 * checks if two coordinate systems are equal
	 * To be equal the systems must have:
	 * the same root space (or the other requirements if they are root spaces)
	 * the same origin location
	 * the same scale
	 * the same axis types
	 * the same system type
	 * @param space
	 * @return
	 */
	public boolean equals(CoordinateSpace space){
		if (this.originx==space.originx && this.originy==space.originy && this.scale==space.scale && this.domain==space.domain && this.range==space.range && this.type==space.type){
			//if they have everything else in common, check the root
			if (this.isRoot && space.isRoot){
				return true; //if they are both roots, they are equal
			}else if((!this.isRoot && !space.isRoot) && this.getRootSpace().equals(space.getRootSpace())){
				return true; //otherwise they must have equal root spaces, and not be root spaces themselves
			}else{
				return false;
			}
		}else{
			//if anything is different, they are not equal
			return false;
		}
	}
	
	public static Point convertPointToSystem(Point toConvert, CoordinateSpace oldSystem, CoordinateSpace newSystem){
		if (!(oldSystem.getRootSpace().equals(newSystem.getRootSpace()))){
			Log.error("Cannot convert points between two different root spaces, returning (0, 0, 0)");
			return new Point();
		}
		//first convert the point into the root space, and then convert it out to the new system
		return convertRootPointToSystemPoint(convertSystemPointToRootPoint(toConvert, oldSystem), newSystem);
	}
	
	public static Point convertSystemPointToRootPoint(Point toConvert, CoordinateSpace system){
		Point point = new Point(toConvert.getX(), toConvert.getY(), toConvert.getZ());
		if (system.isRoot){
			return point; //just return the point without modification if this is already the root
		}
		CoordinateSpace lastSpace = system;
		CoordinateSpace parent = system.getParent();
		do{
			double scaleFactor =  parent.scale / lastSpace.scale;
			boolean flipx = parent.getDomainAxis().getReverseAxis().equals(lastSpace.getDomainAxis());
			boolean flipy = parent.getRangeAxis().getReverseAxis().equals(lastSpace.getRangeAxis());
			//Point newPoint = new Point(((flipx?-1:1)*(toConvert.getX()/scaleFactor))+system.originx, ((flipy?-1:1)*(toConvert.getY()/scaleFactor))+system.originy, toConvert.getZ());
			point.setX(((flipx?-1f:1f)*(point.getX()/scaleFactor))+lastSpace.originx);
			point.setY(((flipy?-1f:1f)*(point.getY()/scaleFactor))+lastSpace.originy);
			lastSpace = parent;
		}while((parent=parent.getParent())!=null);
		
		return point;
	}
	
	public static Point convertRootPointToSystemPoint(Point toConvert, CoordinateSpace system){
		Point point = new Point(toConvert.getX(), toConvert.getY(), toConvert.getZ());
		if (system.isRoot){
			//just return the point without modification if this is already the root
			return point;
		}
		
		point = convertRootPointToSystemPoint(point, system.getParent());
		
		double scaleFactor = system.scale / system.getParent().scale;
		boolean flipx = system.getParent().getDomainAxis().getReverseAxis().equals(system.getDomainAxis());
		boolean flipy = system.getParent().getRangeAxis().getReverseAxis().equals(system.getRangeAxis());
		//Point newPoint = new Point((flipx?-1:1)*(point.getX()-system.originx)/scaleFactor, (flipy?-1:1)*(toConvert.getY()-system.originy)/scaleFactor, toConvert.getZ());
		point.setX((flipx?-1f:1f)*((point.getX()-system.originx)/scaleFactor));
		point.setY((flipy?-1f:1f)*((point.getY()-system.originy)/scaleFactor));
		return point;
		
		/*
		CoordinateSpace lastSpace = system;
		CoordinateSpace parent = system.getParent();
		do{
			double scaleFactor =  parent.scale / lastSpace.scale;
			boolean flipx = parent.getDomainAxis().getReverseAxis().equals(lastSpace.getDomainAxis());
			boolean flipy = parent.getRangeAxis().getReverseAxis().equals(lastSpace.getRangeAxis());
			//Point newPoint = new Point(((flipx?-1:1)*(toConvert.getX()/scaleFactor))+system.originx, ((flipy?-1:1)*(toConvert.getY()/scaleFactor))+system.originy, toConvert.getZ());
			point.setX((flipx?-1:1)*(point.getX()-lastSpace.originx)/scaleFactor);
			point.setY((flipy?-1:1)*(point.getY()-lastSpace.originy)/scaleFactor);
			lastSpace = parent;
		}while((parent=parent.getParent())!=null);
		
		return point;
		*/
		
	}
	
	public AxisType getDomainAxis(){
		return domain;
	}
	
	public AxisType getRangeAxis(){
		return range;
	}
	
	public SystemType getType(){
		return type;
	}
	
	public static enum AxisType{
		XAXIS_RIGHT_POS,
		XAXIS_LEFT_POS,
		YAXIS_UP_POS,
		YAXIS_DOWN_POS,
		THETA_CW_POS,
		THETA_CCW_POS,
		RAXIS_OUT_POS,
		RAXIS_IN_POS;
		
		public AxisType getReverseAxis(){
			switch (this){
			case XAXIS_RIGHT_POS:
				return XAXIS_LEFT_POS;
			case XAXIS_LEFT_POS:
				return XAXIS_RIGHT_POS;
			case YAXIS_UP_POS:
				return YAXIS_DOWN_POS;
			case YAXIS_DOWN_POS:
				return YAXIS_UP_POS;
			case THETA_CW_POS:
				return THETA_CCW_POS;
			case THETA_CCW_POS:
				return THETA_CW_POS;
			case RAXIS_OUT_POS:
				return RAXIS_IN_POS;
			case RAXIS_IN_POS:
				return RAXIS_OUT_POS;
			default:
				return null;
			}
		}
	}
	
	public static enum SystemType{
		CARTESIAN,
		POLAR;
	}
}
