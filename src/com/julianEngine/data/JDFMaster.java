package com.julianEngine.data;

import com.julianEngine.Engine2D;

/**
 * *** Interface for a "Julian Data File" master plugin file ***
 * There can only be one master file loaded per JE instance
 * The master file determines the settings for the engine, and
 * has complete control over the engine. The master file does
 * not have any dependencies.
 */
public interface JDFMaster extends JDFCommon{
	/**
	 * initGame is called after the last plugin returns from postInit. Allows
	 * the master file to set up the engine. initGame returning will exit the game.
	 * The master file should send out messages to plugins so that they can respond
	 * to the events.
	 * @param engine
	 */
	void initGame(Engine2D engine);
}
