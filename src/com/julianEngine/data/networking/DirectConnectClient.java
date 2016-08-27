package com.julianEngine.data.networking;

import java.io.BufferedReader;
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
						BufferedReader in = new BufferedReader(new InputStreamReader(hostConnection.getInputStream()));
						
						while(true){
							byte[] data = in.readLine().getBytes();
							
							Log.trace("[client] got data");
							
							for(MessageListener listener:listeners){
								listener.messageReceived(data);
							}
							
							if(hostConnection.isClosed()){
								break;
							}
						}
						in.close();
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
		hostConnection.getOutputStream().write(data);
		hostConnection.getOutputStream().write((byte)'\n');
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
