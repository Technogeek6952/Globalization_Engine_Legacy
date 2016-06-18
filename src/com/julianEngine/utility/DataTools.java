package com.julianEngine.utility;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class DataTools {
	public static long byteArrayToLong(byte[] arr){
		long value = 0;
		for(int i=0;i<arr.length;i++){
			value = (value << 8) + (arr[i] & 0xff);
		}
		return value;
	}
	
	public static long getCRC32Checksum(byte[] arr){
		Checksum checksum = new CRC32();
		checksum.update(arr, 0, arr.length);
		return checksum.getValue();
	}
	
	//Stack overflow byte-long conversion methods
	//http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
	public static byte[] longToBytes(long x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.putLong(x);
	    
	    //our use expects an array of length 8, so let's ensure that it is that length
	    return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
	    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(bytes);
	    buffer.flip();//need flip 
	    return buffer.getLong();
	}
}
