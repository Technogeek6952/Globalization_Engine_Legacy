package com.julianEngine.data;

import com.julianEngine.Engine2D;
import com.julianEngine.core.World;
import com.julianEngine.graphics.Frame;

/**
 * *** Interface for a "Julian Data File" master plugin file ***
 * There can only be one master file loaded per JE instance
 * The master file determines the settings for the engine, and
 * has complete control over the engine. The master file does
 * not have any dependencies.
 */
public interface JDFMaster extends JDFCommon, PreInitializer{
	
	/**
	 * Create a loading screen to display while the game is loading. Shouldn't include
	 * any intensive functions (so the screen can appear soon after being launched) -
	 * namely any UI elements that may take long while registering listeners.
	 * @return World - return the loadScreen passed as an argument, with any modifications
	 * made to personalize it. The bottom half should be reserved for the engine to display
	 * data regarding the status of loading plugins.
	 */
	World createLoadingScreen(World loadScreen, Frame frame);
	
	/**
	 * Create the main screen that will be displayed after the game is done loading
	 */
	World createMainScreen(World mainScreen, Frame frame, String string);
	
	/**
	 * runGame is called after the "main" world provided has loaded. Allows
	 * the master file to control the engine. runGame returning will exit the game.
	 * The master file should send out messages to plugins so that they can respond
	 * to the events.
	 * @param engine
	 */
	void runGame(Engine2D engine);
}
