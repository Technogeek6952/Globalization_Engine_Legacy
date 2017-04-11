package com.julianEngine.data.networking;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

import com.julianEngine.utility.Log;

public class DirectConnectClient {
	Socket hostConnection;
	ArrayList<MessageListener> listeners = new ArrayList<MessageListener>();
	public DirectConnectClient(){
		//don't initialize the socket in the constructor, so that listeners and other things can be set up before the socket is connected
	}
	
	public void connectToHost(String host, int port){
		try {
			hostConnection = new Socket(host, port);
			
			new Thread(){
				public void run(){
					try {
						while(true){
							byte b = (byte) hostConnection.getInputStream().read();
							Log.trace("first byte: "+b);
							byte[] data = new byte[hostConnection.getInputStream().available()];
							hostConnection.getInputStream().read(data);
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							stream.write(b);
							stream.write(data);
							
							Log.trace("[CLIENT] got data");
							
							new Thread(){
								public void run(){
									for(MessageListener listener:listeners){
										listener.messageReceived(stream.toByteArray());
									}
								}
							}.start();
							
							if(hostConnection.isClosed()){
								Log.info("[CLIENT] Host connection closed, no longer listening");
								break;
							}
						}
					} catch (IOException e) {
						Log.error("Error getting input stream for socket");
						e.printStackTrace();
					}
				}
			}.start();
		} catch (Exception e) {
			Log.error("Error connecting to server at: "+host+" on port: "+port);
			e.printStackTrace();
		}
	}
	
	public void write(byte[] data) throws IOException{
		byte[] toSend = new byte[data.length];
		toSend = data;
		//toSend[toSend.length-1] = (byte)'\n';
		hostConnection.getOutputStream().write(toSend);
		//hostConnection.getOutputStream().write((byte)'\n');
		Log.trace("[CLIENT] sent data to host");
	}
	
	public void close(){
		try {
			hostConnection.close();
		} catch (IOException e) {
			Log.error("Error while closing socket");
			e.printStackTrace();
		}
	}
	
	public void addMessageListener(MessageListener listener){
		listeners.add(listener);
	}
	
	public static interface MessageListener{
		public void messageReceived(byte[] data);
	}
}
