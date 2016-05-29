package com.julianEngine.data;

public interface JDFCommon {
	/**
	 * Run code to initialize the game - the master is always guaranteed to
	 * load first, and therefore will have its init function run first
	 */
	void init();
	
	/**
	 * PostInit begins right after the last plugin in the load order has returned from
	 * init(). This allows plugins to work with each other, despite being loaded at
	 * different times.
	 */
	void postInit();
	
	/**
	 * Function called whenever another plugin or the engine sends a message to
	 * the plugin. These messages can be ignored, but it is recommended one
	 * does something with each message, even if it is only being printed out to
	 * the console. Please note that in order to keep the system working, messages
	 * should be taken care of relatively quickly (i.e no calculating or otherwise
	 * doing complex tasks in response) If more time is needed, it is recommended
	 * that another thread is notified of the message, and that thread deals with it.
	 */
	void messageReceived(String sender);
	
	/**
	 * Get unique ID
	 * @return
	 * String pluginID - the unique ID for the plugin
	 */
	String getPluginID();
	
	/**
	 * Get version
	 * @return
	 * String pluginVersion - version ID for plugin (ex v1.0.2_a03)
	 */
	String getPluginVersion();
}
