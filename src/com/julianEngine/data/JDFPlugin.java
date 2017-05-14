package com.julianEngine.data;

/**
 * *** Interface for a "Julian Data File" plugin file ***
 * A normal plugin must be dependent on one master file, and
 * additionally may be dependent on any number of plugin
 * files (which are required to be loaded before the plugin)
 */
public interface JDFPlugin extends JDFCommon{
	/**
	 * Called by the engine before any plugins initialize, so that
	 * Dependencies can be checked.
	 * @return
	 * String[] - an array of strings with the pluginID of any
	 * dependencies. Every plugin must be dependent on the master
	 * file.
	 */
	String[] getDependencies();
}
