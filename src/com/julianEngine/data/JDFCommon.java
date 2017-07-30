package com.julianEngine.data;

public interface JDFCommon {
	/**
	 * Run code to initialize the game - the master is always guaranteed to
	 * load first, and therefore will have its init function run first
	 */
	void init(String options);
	
	/**
	 * PostInit begins right after the last plugin in the load order has returned from
	 * init(). This allows plugins to work with each other, despite being loaded at
	 * different times.
	 */
	void postInit();
	
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
