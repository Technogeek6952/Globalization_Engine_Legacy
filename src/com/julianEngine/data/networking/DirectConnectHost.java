package com.julianEngine.data.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.julianEngine.utility.Log;

public class DirectConnectHost {
	
	private ServerSocket server;
	private HashMap<Long, Socket> clients = new HashMap<Long, Socket>();
	private ArrayList<NewClientListener> ncListeners = new ArrayList<NewClientListener>();
	private HashMap<Long, ClientMessageListener> messageListeners = new HashMap<Long, ClientMessageListener>();
	
	public DirectConnectHost(){
		//don't init socket in constructor, so that event hooks etc can be set up first
	}
	
	public void listenForClients(int port){
		try {
			server = new ServerSocket(port);
			new Thread(){
				public void run(){
					while(!server.isClosed()){ //continue to look for connections 
						try {
							Socket client = server.accept();
							Log.info("New client connection from: "+client.getInetAddress().getHostAddress()+" on port: "+port);
							
							long uid = newUID();
							clients.put(uid, client);
							for(NewClientListener listener:ncListeners){
								listener.newClient(uid);
							}
							
							new Thread(){
								public void run(){
									try {
										BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
										while(true){
											byte[] data = in.readLine().getBytes();
											
											for(Long filter:messageListeners.keySet()){
												if(filter==uid){
													messageListeners.get(filter).clientMessageReceived(uid, data);
												}
											}
											
											if(server.isClosed()){
												break;
											}
										}
										in.close();
									} catch (Exception e) {
										Log.error("Error while reading from socket");
										e.printStackTrace();
									}
								}
							}.start();
						} catch (Exception e) {
							Log.error("Error accepting host...");
							e.printStackTrace();
						}
					}
				}
			}.start();
		} catch (Exception e) {
			Log.error("Error initializing DirectConnectHost");
			e.printStackTrace();
		}
	}
	
	private long newUID(){
		Random r = new Random();
		long uid;
		do{
			uid = r.nextLong();
		}while(clients.containsKey(uid));
		
		return uid;
	}
	
	public void close(){
		try {
			server.close();
		} catch (IOException e) {
			Log.warn("Error closing server");
			e.printStackTrace();
		}
	}
	
	public void addNewClientListener(NewClientListener listener){
		ncListeners.add(listener);
	}
	
	public void addClientMessageListener(long uidFilter, ClientMessageListener listener){
		messageListeners.put(uidFilter, listener);
	}
	
	public void removeClientMessageListener(long uidFilter, ClientMessageListener listener){
		messageListeners.remove(uidFilter, listener);
	}
	
	public void write(byte[] data, long uid) throws IOException{
		clients.get(uid).getOutputStream().write(data);
		clients.get(uid).getOutputStream().write((byte)'\n');
		Log.trace("wrote data[host]");
	}
	
	public static interface NewClientListener{
		public void newClient(long uid);
	}
	
	public static interface ClientMessageListener{
		public void clientMessageReceived(long uid, byte[] data);
	}
}
