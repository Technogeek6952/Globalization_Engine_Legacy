package com.julianEngine.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.julianEngine.utility.Log;

public class UserConfiguration {
	
	//TODO: make a robust config system
	//TODO: save to file ability
	//TODO: load from file at start
	
	private static HashMap<String, Boolean> booleans = new HashMap<String, Boolean>();
	private static HashMap<String, String> strings = new HashMap<String, String>();
	private static HashMap<String, Integer> integers = new HashMap<String, Integer>();
	private static HashMap<String, Double> doubles = new HashMap<String, Double>();
	private static HashMap<String, Float> floats = new HashMap<String, Float>();
	private static HashMap<String, Character> characters = new HashMap<String, Character>();
	
	public static void loadFile(String path){
		File cfgFile = new File(path);
		if(cfgFile.exists()){
			try {
				FileInputStream fileStream = new FileInputStream(cfgFile);
				ArrayList<String> lines = new ArrayList<String>();
				String line = "";
				while(fileStream.available()>0){
					char nextChar = (char) fileStream.read();
					if(nextChar!='\0'&&nextChar!='\n'&&nextChar!='\r')
						line += nextChar;
					else{
						lines.add(line);
						line = "";
					}
				}
				lines.add(line);
				fileStream.close();
				
				//parse lines
				for(String s:lines){
					if(!s.startsWith("#")&&!s.equals("")&&s!=null){ //ignore '#' for comments
						switch(s.charAt(1)){ //look at second char (between [ and ] at start of line)
						case 'b':
							break;
						case 's':
							break;
						case 'i':
							String name = (String) s.subSequence(3, s.indexOf('=', 3));
							name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
							String value = (String) s.substring(s.indexOf('=', 3)+1, s.length());
							value = (value.startsWith(" "))?value.substring(1, value.length()):value;
							int actualValue = 0;
							try{ 
								actualValue = Integer.parseInt(value);
								Log.trace("config file int: "+name);
								Log.trace("Value: "+actualValue);
								integers.put(name, actualValue);
							}catch(NumberFormatException e){
								Log.error("Int corrupted - no value assigned");
							}
							break;
						case 'd':
							break;
						case 'f':
							break;
						case 'c':
							break;
						default:
							Log.trace("unrecognized type: "+s.charAt(1));
							break;
						}
					}
				}
			} catch (Exception e) {
				Log.trace("Error loading config file: "+path);
				e.printStackTrace();
			}
		}else{
			Log.trace("Error loading config file: "+path);
		}
	}
	
	public static void writeFile(String path){
		File file = new File(path);
		try {
			FileOutputStream writer = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean getBool(String name, boolean defaultValue){
		if(booleans.containsKey(name))
			return booleans.get(name);
		else
			return defaultValue;
	}
	
	public static String getString(String name, String defaultValue){
		if(strings.containsKey(name))
			return strings.get(name);
		else
			return defaultValue;
	}
	
	public static int getInt(String name, int defaultValue){
		if(integers.containsKey(name))
			return integers.get(name);
		else
			return defaultValue;
	}
	
	public static double getDouble(String name, double defaultValue){
		if(doubles.containsKey(name))
			return doubles.get(name);
		else
			return defaultValue;
	}
	
	public static float getFloat(String name, float defaultValue){
		if(floats.containsKey(name))
			return floats.get(name);
		else
			return defaultValue;
	}
	
	public static char getChar(String name, char defaultValue){
		if(characters.containsKey(name))
			return characters.get(name);
		else
			return defaultValue;
	}
}
