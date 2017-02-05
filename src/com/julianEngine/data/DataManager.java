package com.julianEngine.data;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;

import com.julianEngine.graphics.external_windows.ErrorReporter;
import com.julianEngine.utility.DataTools;
import com.julianEngine.utility.Log;

public class DataManager {
	static HashMap<FileInformation, byte[]> data = new HashMap<FileInformation, byte[]>(); //file name, data
	
	public static void loadDataFile(String filePath) throws Exception{
		File dataFile = new File(System.getProperty("user.dir"), filePath);
		if(dataFile.isFile()){
			Log.trace("Loading resource file: " + dataFile.getPath());
			FileInputStream in = new FileInputStream(dataFile.getPath());
			boolean read = true;
			if(in.available()>0){
				byte[] checkSum = new byte[8]; //get fist 8 bytes for the checksum
				in.read(checkSum); //read the first byte, for a checksum
				
				//convert the 8 bytes into a long
				long readChecksum = DataTools.byteArrayToLong(checkSum);
				
				byte[] fileBytes = new byte[in.available()]; //read the rest of the file
				in.read(fileBytes);
				long checksumValue = DataTools.getCRC32Checksum(fileBytes);
				
				if(!(readChecksum==checksumValue)){
					Log.fatal("ERROR: CHECKSUM DIDN'T MATCH... FILE CORRUPTED... CHECKSUM OF FILE: "+checksumValue+", CHECKSUM EXPECTED (read): "+readChecksum);
					in.close();
					throw new Exception();
				}else{
					Log.trace("Checksum passed");
				}
				
				//Read files into map
				InputStream byteStream = new ByteArrayInputStream(fileBytes);
				byteStream.read(new byte[8]); //read the first 8 bytes, which are the end of the 16-byte jdf header
				while(read){
					//Reads the first byte, which tells us what kind of file is coming up
					byte[] bytesIn = new byte[1];
					byteStream.read(bytesIn);
					DataType dataType = DataType.getTypeForID(bytesIn[0]);
					Log.trace("DATA TYPE: "+dataType.getName());
					
					//parse header
					byte[] headerSizeRaw = new byte[1];
					byteStream.read(headerSizeRaw);
					int headerSize = headerSizeRaw[0];
					Log.trace("Header Size: "+headerSize);
					byte[] dataHeader = new byte[headerSize];
					byteStream.read(dataHeader);
					InputStream dataHeaderStream = new ByteArrayInputStream(dataHeader);
					byte[] fileNameRaw = new byte[headerSize-8]; //the file name will be the size of the header - 8 bytes for the file size
					dataHeaderStream.read(fileNameRaw);
					String fileName = new String(fileNameRaw);
					Log.trace("Reading file: "+fileName);
					byte[] fileSizeRaw = new byte[8];
					dataHeaderStream.read(fileSizeRaw);
					long fileSize = DataTools.byteArrayToLong(fileSizeRaw); //!!THIS NEEDS TO BE CHECKED - IF IT IS LARGER THAN AN INT, FILE NEEDS TO BE BROKEN DOWN!!
					Log.trace("Image read is "+fileSize+" bytes");
					
					//read rest of data for file
					byte[] fileData = new byte[(int) fileSize];
					byteStream.read(fileData);
					
					//Create a new entry in data map
					FileInformation fileInfo = new FileInformation();
					fileInfo.type = dataType;
					fileInfo.uri = fileName;
					data.put(fileInfo, fileData);
					if(!(byteStream.available()>0)){
						Log.trace("finished parsing data file");
						read=false;
					}
				}
				byteStream.close();
			}
			in.close();
		}else{
			Log.trace("Resource file not found: " + dataFile.getPath());
		}
	}
	
	public static BufferedImage getImageForURI(String URI){
		File textureFile = new File(System.getProperty("user.dir"), "./Data/"+URI);
		if(textureFile.exists()){ //if a file exists for the resource, override already loaded files, else look for the resource in loaded files
			try {
				FileInputStream fileStream = new FileInputStream(textureFile);
				byte[] fileBytes = new byte[fileStream.available()];
				fileStream.read(fileBytes);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(fileBytes);
				fileStream.close();
				return ImageIO.read(byteStream);
			} catch (Exception e) {
				Log.fatal("Error loading resource "+textureFile.getPath()+" from disk");
				ErrorReporter.displayError(e);
				e.printStackTrace();
			}
		}else{
			for(FileInformation fileInfo:data.keySet()){
				if(fileInfo.uri.equals(URI)){
					InputStream in = new ByteArrayInputStream(data.get(fileInfo));
					try {
						return ImageIO.read(in);
					} catch (IOException e) {
						Log.trace("Error getting image from byte stream");
						ErrorReporter.displayError(e);
						e.printStackTrace();
					}
				}
			}
		}
		Log.trace("No registered file for: "+URI);
		return null;
	}
	
	public static ByteArrayInputStream getStreamForResource(String resourceName){
		//create a file object for the resource - append ./Data/ to make sure the engine is looking for the resource there
		File file = new File(System.getProperty("user.dir"), "./Data/"+resourceName);
		if(file.exists()){ //if a file exists for the resource, override already loaded files, else look for the resource in loaded files
			try {
				FileInputStream fileStream = new FileInputStream(file);
				byte[] fileBytes = new byte[fileStream.available()];
				fileStream.read(fileBytes);
				ByteArrayInputStream byteStream = new ByteArrayInputStream(fileBytes);
				fileStream.close();
				return byteStream;
			} catch (Exception e) {
				Log.fatal("Error loading resource "+resourceName+" from disk");
				e.printStackTrace();
			}
		}else{
			for(FileInformation fileInfo:data.keySet()){
				if(fileInfo.uri.equals(resourceName)){
					return new ByteArrayInputStream(data.get(fileInfo));
				}
			}
		}
		Log.trace("Resource not found: "+resourceName);
		return null;
	}
	
	public static boolean doesResourceExist(String rs){
		for(FileInformation fileInfo:data.keySet()){
			if(fileInfo.uri.toLowerCase().equals(rs.toLowerCase())){
				return true;
			}
		}
		File textureFile = new File(System.getProperty("user.dir"), "./Data/"+rs);
		if(textureFile.exists()){
			return true;
		}
		return false;
	}
	
	public static class FileInformation{
		public DataType type;
		public String uri;
	}
	
	public static enum DataType{
		//most files have 256 byte headers, broken down to:
		//8 bytes (64 bits) to describe the size of the file in bytes,
		//248 bytes to allow for 248 characters of the file name (+path) (NOTE: this is less than is usually allowed by the OS, but we probably won't reach it)
		TXT("Text File", 256, (byte)0b00000001),
		PNG("Image File (PNG)", 256, (byte)0b00000010),
		WAV("Sound File (WAV)", 256, (byte)0b00000011),
		GIF("Animated Image File (GIF)", 256, (byte)0b00000100),
		ERR("ERROR READING FILE", 0, (byte)0b11111110), //error getting file type
		NULL("No File", 256, (byte)0b11111111); //Usually used to indicate reading the header for more info
		
		String name;
		int headerLength;
		byte id;
		DataType(String name, int length, byte id){
			this.name = name;
			headerLength = length;
			this.id = id;
		}
		
		public String getName(){
			return name;
		}
		
		public int getHeaderLength(){
			return headerLength;
		}
		
		public byte getID(){
			return id;
		}
		
		public static DataType getTypeForID(byte id){
			for(DataType t:DataType.values()){
				if(t.getID()==id){
					return t;
				}
			}
			return DataType.ERR;
		}
	}
}
