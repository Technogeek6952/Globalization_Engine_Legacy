package com.julianEngine.utility;

import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.CoordinateSpace.AxisType;
import com.julianEngine.core.CoordinateSpace.SystemType;
import com.julianEngine.core.Point;

public class Tests {
	public static void runTests(){
		testCoordinateSpace();
	}
	
	private static void testCoordinateSpace(){
		CoordinateSpace root = new CoordinateSpace(SystemType.CARTESIAN, AxisType.XAXIS_RIGHT_POS, AxisType.YAXIS_DOWN_POS);
		CoordinateSpace l1 = new CoordinateSpace(root, false, true, 0, 720, 1); //bottom left of 1080p frame
		CoordinateSpace l2 = new CoordinateSpace(l1, false, false, 100, 100, 1);
		CoordinateSpace l2b = new CoordinateSpace(l1, false, false, 200, 200, 1);
		CoordinateSpace l3 = new CoordinateSpace(l2, false, true, -50, -50, 1);
		CoordinateSpace l4 = new CoordinateSpace(l3, false, false, 0, 0, 2);
		Point p1 = new Point(100, 100, 0); //represents a point in root space
		Point p2 = new Point(230, 150, 0); //represents a point in l4 space
		Point p3 = new Point(25, -285, 0); //represents p1 in l4 space
		try {
			//convert p1 to the l4 space, and check each step along the way
			Point t1 = CoordinateSpace.convertPointToSystem(p1, root, l1); //point p1 in l1 space, should be at (100, 620)
			assert t1.getX()==100;
			assert t1.getY()==620;
			Point t2 = CoordinateSpace.convertPointToSystem(p1, root, l2); //point p1 in l2 space, should be at (0, 520)
			assert t2.getX()==0;
			assert t2.getY()==520;
			Point t3 = CoordinateSpace.convertPointToSystem(p1, root, l3); //point p1 in l3 space, should be at (50, -570)
			assert t3.getX()==50;
			assert t3.getY()==-570;
			Point t4 = CoordinateSpace.convertPointToSystem(p1, root, l4); //point p1 in l4 space, should be at (25, -285)
			assert t4.getX()==25;
			assert t4.getY()==-285;
			
			//convert p3 to root space, to check the other direction of the equation
			Point t5 = CoordinateSpace.convertPointToSystem(p3, l4, root); //point p3 in root space should be at (100, 100)
			assert t5.getX()==100;
			assert t5.getY()==100;
			
			//convert p2 to l2b space to test both systems at the same time
			Point t6 = CoordinateSpace.convertPointToSystem(p2, l4, l2b); //point p2 in l2b space should be at (310, -450)
			assert t6.getX()==310;
			assert t6.getY()==-450;
		} catch (Exception e) {
			assert false;
		}
	}
}
