package com.julianEngine.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.julianEngine.config.EngineConstants;;

public class Log {
	//TODO: add colors to output text (http://stackoverflow.com/questions/1448858/how-to-color-system-out-println-output)
	private static long startTime = 0;
	static FileOutputStream fos;
	static{
		try {
			fos = new FileOutputStream(new File(System.getProperty("user.dir"), EngineConstants.Defaults.LOGFILE));
		} catch (FileNotFoundException e) {
			System.out.println("[LOGGING-ERROR] Logfile could not be opened");
			e.printStackTrace();
		}
		
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
		String output = String.format("[%02d:%02d:%02d][%s][%s] ", hours, minutes, seconds, Thread.currentThread().getName(), logLevel.flavoredName()) + object;
		
		switch ((logLevel.isLowerOrEqualTo(EngineConstants.Defaults.LOGLEVEL_CONSOLE)?0:1)+(logLevel.isLowerOrEqualTo(EngineConstants.Defaults.LOGLEVEL_FILE)?0:2)){
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
		try {
			fos.write(out.getBytes());
			fos.write(System.getProperty("line.separator").getBytes());
			fos.flush();
		} catch (Exception e) {
			System.out.println("[LOGGING-ERROR] could not write to log file");
			e.printStackTrace();
		}
	}
	
	public enum Level {
		OFF(Integer.MAX_VALUE, "ALL", "ALL"),
		FATAL(6, "FATAL", "FATAL"),
		ERROR(5, "ERROR", "ERROR"),
		WARN(4, "WARNING", "WARNING"),
		INFO(3, "INFO", "INFO"),
		DEBUG(2, "DEBUG", "DEBUG"),
		TRACE(1, "TRACE", "TRACE"),
		ALL(Integer.MIN_VALUE, "OFF", "OFF");
		
		private final int level;
		private final String string;
		private final String flavoredString;
		
		private Level(int i, String name, String flavoredName){
			level = i;
			string = name;
			flavoredString = flavoredName; //colored text, if I can get that working
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
		
		public String flavoredName(){
			return flavoredString;
		}
		
		public static String nameFor(Level level){
			return level.string;
		}
	}
	
}
