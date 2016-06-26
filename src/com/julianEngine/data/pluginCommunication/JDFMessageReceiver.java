package com.julianEngine.data.pluginCommunication;

public interface JDFMessageReceiver {
	public String getName();
	public byte[] messageReceived(String msg, JDFMessageSender sender);
}
