package com.julianEngine.data.pluginCommunication;

import java.util.HashMap;
import java.util.function.Consumer;

public class JDFMessageManager {
	
	private static HashMap<String, JDFMessageReceiver> registeredReceivers = new HashMap<String, JDFMessageReceiver>();
	
	public static void registerReceiver(JDFMessageReceiver receiver){
		registeredReceivers.put(receiver.getName(), receiver);
	}
	
	public static JDFMessageReceiver getReceiverForName(String receiverID){
		return registeredReceivers.get(receiverID);
	}
	
	public static void broadcastMessage(String msg, JDFMessageSender sender){
		for(JDFMessageReceiver receiver:registeredReceivers.values()){
			sendMessage(msg, sender, receiver);
		}
	}
	
	public static void sendMessage(String msg, JDFMessageSender sender, String receiverID){
		sendMessage(msg, sender, registeredReceivers.get(receiverID));
	}
	
	public static void sendMessage(String msg, JDFMessageSender sender, JDFMessageReceiver receiver){
		sendMessage(msg, sender, receiver, (byte[] reply) -> {
			sender.replyReceived(msg, reply, receiver); //If the receiver responds
		});
	}
	
	private static void sendMessage(String msg, JDFMessageSender sender, JDFMessageReceiver receiver, Consumer<byte[]> responce){
		byte[] reply = receiver.messageReceived(msg, sender);
		if(reply!=null){
			responce.accept(reply);
		}
	}
}
