package com.julianEngine.config;

import com.julianEngine.utility.Log;
import com.julianEngine.utility.Log.Level;

/**
 * @author Sean
 * Used for any constants that may need to be used thought the program
 */
public final class EngineConstants {
	
	//constant variables **These should eventually be moved to a system that lets the user set them
	public static final int width = 1280;
	public static final int height = 720;
	
	public static final int bufferFrames = 2;
	
	public static Log.Level LOGLEVEL_CONSOLE = Level.ALL;
	public static Log.Level LOGLEVEL_FILE = Level.ALL;
	public static String LOGFILE = "./log.txt";
}
