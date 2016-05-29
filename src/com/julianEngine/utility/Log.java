package com.julianEngine.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import com.julianEngine.Settings;

public class Log {
	private static long startTime = 0;
	static{
		//gets the system time as soon as Log.java is fist instanced
		//In order for the times appearing on logs to be accurate, the
		//program must log something at the start of execution (Hello world message)
		startTime = System.currentTimeMillis();
	}
	
	public static void log(Level logLevel, Object object){
		//Format string: [UPTIME(hh:mm:ss)][LEVEL] {User Message}
		long rawTime = System.currentTimeMillis() - startTime;
		int seconds = (int) Math.floorDiv(rawTime, 1000)%60;
		int minutes = (int) Math.floorDiv(rawTime, 60000)%60;
		int hours = (int) Math.floorDiv(rawTime, 3600000);
		String output = String.format("[%02d:%02d:%02d][%s] ", hours, minutes, seconds, logLevel.stringName()) + object;
		
		switch ((logLevel.isHigherOrEqualTo(Settings.LOGLEVEL_CONSOLE)?0:1)+(logLevel.isHigherOrEqualTo(Settings.LOGLEVEL_FILE)?0:2)){
			case 0:
				break;
			case 1:
				System.out.println(output);
				break;
			case 2:
				writeToLogFile(output);
				break;
			case 3:
				System.out.println(output);
				writeToLogFile(output);
				break;
		}
	}
	
	public static void debug(Object object) { log(Level.DEBUG, object); }
	
	public static void error(Object object) { log(Level.ERROR, object); }
	
	public static void fatal(Object object) { log(Level.FATAL, object); }
	
	public static void info(Object object) { log(Level.INFO, object); }
	
	public static void trace(Object object) { log(Level.TRACE, object); }
	
	public static void warn(Object object) { log(Level.WARN, object); }
	
	private static void writeToLogFile(String out){
		List<String> lines = Arrays.asList(out);
		try {
			Files.write(Paths.get(Settings.LOGFILE), lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public enum Level {
		ALL(Integer.MAX_VALUE, "ALL"),
		FATAL(6, "FATAL"),
		ERROR(5, "ERROR"),
		WARN(4, "WARNING"),
		INFO(3, "INFO"),
		DEBUG(2, "DEBUG"),
		TRACE(1, "TRACE"),
		OFF(Integer.MIN_VALUE, "OFF");
		
		private final int level;
		private final String string;
		
		private Level(int i, String name){
			level = i;
			string = name;
		}
		
		public boolean isHigherOrEqualTo(Level level){
			return (this.level>=level.level);
		}
		
		public boolean isLowerOrEqualTo(Level level){
			return (this.level<=level.level);
		}
		
		public int intValueOf(Level level){
			return level.level;
		}
		
		public String stringName(){
			return string;
		}
		
		public static String nameFor(Level level){
			return level.string;
		}
	}
	
}
