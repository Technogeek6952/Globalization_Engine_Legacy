package com.julianEngine.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.julianEngine.utility.Log;

/**
 * Class to store global variables, and load vars from a file
 * @author Sean
 *
 */
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
					parseString(s);
				}
			} catch (Exception e) {
				Log.trace("Error loading config file: "+path);
				e.printStackTrace();
			}
		}else{
			Log.trace("Error loading config file: "+path);
		}
	}
	
	public static void parseString(String data){
		if(!data.startsWith("#")&&!data.equals("")&&data!=null){ //ignore '#' for comments
			String name;
			String value;
			switch(data.charAt(1)){ //look at second char (between [ and ] at start of line)
			case 'b':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				boolean boolValue = false;
				try{ 
					boolValue = Boolean.parseBoolean(value);
					Log.trace("config file int: "+name);
					Log.trace("Value: "+boolValue);
					booleans.put(name, boolValue);
				}catch(NumberFormatException e){
					Log.error("Boolean corrupted - no value assigned");
				}
				break;
			case 's':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				strings.put(name, value);
				break;
			case 'i':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				int intValue = 0;
				try{ 
					intValue = Integer.parseInt(value);
					Log.trace("config file int: "+name);
					Log.trace("Value: "+intValue);
					integers.put(name, intValue);
				}catch(NumberFormatException e){
					Log.error("Int corrupted - no value assigned");
				}
				break;
			case 'd':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				double doubleValue = 0;
				try{ 
					doubleValue = Double.parseDouble(value);
					Log.trace("config file int: "+name);
					Log.trace("Value: "+doubleValue);
					doubles.put(name, doubleValue);
				}catch(NumberFormatException e){
					Log.error("Double corrupted - no value assigned");
				}
				break;
			case 'f':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				float floatValue = 0;
				try{ 
					floatValue = Float.parseFloat(value);
					Log.trace("config file int: "+name);
					Log.trace("Value: "+floatValue);
					floats.put(name, floatValue);
				}catch(NumberFormatException e){
					Log.error("Float corrupted - no value assigned");
				}
				break;
			case 'c':
				name = (String) data.subSequence(3, data.indexOf('=', 3));
				name = (name.endsWith(" "))?name.substring(0, name.length()-1):name;
				value = (String) data.substring(data.indexOf('=', 3)+1, data.length());
				value = (value.startsWith(" "))?value.substring(1, value.length()):value;
				characters.put(name, value.charAt(0));
				break;
			default:
				Log.trace("unrecognized type: "+data.charAt(1));
				break;
			}
		}
	}
	
	public static void writeFile(String path){
		//TODO: implement file saving for config
		File file = new File(path);
		try {
			FileOutputStream writer = new FileOutputStream(file);
			writer.close();
		} catch (Exception e) {
			Log.error("Error while writing configuration file");
			e.printStackTrace();
		}
	}
	
	public static int getNumberOfVariables(){
		return booleans.size()+strings.size()+integers.size()+doubles.size()+floats.size()+characters.size();
	}
	
	public static String[][] getRawData(){
		String[][] data = new String[getNumberOfVariables()][3];
		int row = 0;
		
		//booleans
		for (String name:booleans.keySet()){
			data[row][0] = name;
			data[row][1] = "Boolean";
			data[row][2] = booleans.get(name).toString();
			row++;
		}
		//strings
		for (String name:strings.keySet()){
			data[row][0] = name;
			data[row][1] = "String";
			data[row][2] = strings.get(name);
			row++;
		}
		//ints
		for (String name:integers.keySet()){
			data[row][0] = name;
			data[row][1] = "Integer";
			data[row][2] = integers.get(name).toString();
			row++;
		}
		//doubles
		for (String name:doubles.keySet()){
			data[row][0] = name;
			data[row][1] = "Double";
			data[row][2] = doubles.get(name).toString();
			row++;
		}
		//floats
		for (String name:floats.keySet()){
			data[row][0] = name;
			data[row][1] = "Float";
			data[row][2] = floats.get(name).toString();
			row++;
		}
		//chars
		for (String name:characters.keySet()){
			data[row][0] = name;
			data[row][1] = "Character";
			data[row][2] = characters.get(name).toString();
			row++;
		}
		
		List<String[]> list = Arrays.asList(data);
		list.sort(new Comparator<String[]>(){
			@Override
			public int compare(String[] row1, String[] row2) {
				
				for (int i=0;i<((row1[0].length()<row2[0].length())?row1[0].length():row2[0].length());i++){
					int diff = row1[0].toUpperCase().charAt(i)-row2[0].toUpperCase().charAt(i);
					if (diff>0){
						return 1;
					}else if (diff<0){
						return -1;
					}
				}
				return (row1[0].length()<row2[0].length())?-1:((row2[0].length()<row1[0].length())?1:0);
			}
		});
		
		return (String[][])list.toArray();
	}
	
	public static Object getDataForName(String name){
		if (booleans.containsKey(name)){
			return booleans.get(name);
		}else if (strings.containsKey(name)){
			return strings.get(name);
		}else if (integers.containsKey(name)){
			return integers.get(name);
		}else if (doubles.containsKey(name)){
			return doubles.get(name);
		}else if (floats.containsKey(name)){
			return floats.get(name);
		}else if (characters.containsKey(name)){
			return characters.get(name);
		}else{
			return null;
		}
	}
	
	public static boolean getBool(String name, boolean defaultValue){
		if(booleans.containsKey(name))
			return booleans.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addBool(String name, boolean value){
		booleans.put(name, value);
	}
	
	public static void removeBool(String name){
		booleans.remove(name);
	}
	
	public static String getString(String name, String defaultValue){
		if(strings.containsKey(name))
			return strings.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addString(String name, String value){
		strings.put(name, value);
	}
	
	public static void removeString(String name){
		strings.remove(name);
	}
	
	public static int getInt(String name, int defaultValue){
		if(integers.containsKey(name))
			return integers.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addInt(String name, int value){
		integers.put(name, value);
	}
	
	public static void removeInt(String name){
		integers.remove(name);
	}
	
	public static double getDouble(String name, double defaultValue){
		if(doubles.containsKey(name))
			return doubles.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addDouble(String name, Double value){
		doubles.put(name, value);
	}
	
	public static void removeDouble(String name){
		doubles.remove(name);
	}
	
	public static float getFloat(String name, float defaultValue){
		if(floats.containsKey(name))
			return floats.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addFloat(String name, float value){
		floats.put(name, value);
	}
	
	public static void removeFloat(String name){
		floats.remove(name);
	}
	
	public static char getChar(String name, char defaultValue){
		if(characters.containsKey(name))
			return characters.get(name);
		else
			return defaultValue;
	}
	
	//add a new value, or if it already exists: update it
	public static void addChar(String name, char value){
		characters.put(name, value);
	}
	
	public static void removeChar(String name){
		characters.remove(name);
	}
}
