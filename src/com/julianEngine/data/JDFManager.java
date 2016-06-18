package com.julianEngine.data;

import java.util.HashMap;

public class JDFManager {
	private static HashMap<String, JDFCommon> plugins = new HashMap<String, JDFCommon>();
	public static void registerJDF(JDFCommon plugin){
		plugins.put(plugin.getPluginID(), plugin);
	}
	
	public static void sendMessage(String to, Object data, String from){
		plugins.get(to).messageReceived(data, from);
	}
	
	public static void broadcastMessage(Object data, String from){
		for(JDFCommon plugin:plugins.values()){
			plugin.messageReceived(data, from);
		}
	}
}
