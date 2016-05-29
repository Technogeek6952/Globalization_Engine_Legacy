package com.julianEngine;

import com.julianEngine.utility.Log;
import com.julianEngine.utility.Log.Level;

public class Settings {
	//Logging
	public static Log.Level LOGLEVEL_CONSOLE = Level.ALL;
	public static Log.Level LOGLEVEL_FILE = Level.ALL;
	public static String LOGFILE = "./log.txt";
	
	//Main window settings
	public static int width = 1080;
	public static int height = 720;
	
	//Graphics settings
	public static int bufferFrames = 2;
}
