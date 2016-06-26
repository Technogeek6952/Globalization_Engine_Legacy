package com.julianEngine.data.pluginCommunication;

public interface JDFMessageSender {
	public String getName();
	public void replyReceived(String originalMessage, byte[] reply, JDFMessageReceiver receiver);
}
