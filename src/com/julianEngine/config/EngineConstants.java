package com.julianEngine.config;

import com.julianEngine.utility.Log;
import com.julianEngine.utility.Log.Level;

/**
 * @author Sean
 * Used for any constants that may need to be used thought the program
 */
public final class EngineConstants {
	public static String CONFIG_FILE = "./engine.config";
	
	public static final class Defaults{
		public static int width = 1280;
		public static int height = 720;
		
		public static final int bufferFrames = 2;
		
		public static Log.Level LOGLEVEL_CONSOLE = Level.ALL;
		public static Log.Level LOGLEVEL_FILE = Level.ALL;
		public static String LOGFILE = "./log.txt";
	}
}
