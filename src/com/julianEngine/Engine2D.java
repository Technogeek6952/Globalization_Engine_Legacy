package com.julianEngine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.julianEngine.config.EngineConstants;
import com.julianEngine.config.UserConfiguration;
import com.julianEngine.core.CoordinateSpace;
import com.julianEngine.core.CoordinateSpace.AxisType;
import com.julianEngine.core.CoordinateSpace.SystemType;
import com.julianEngine.core.Point;
import com.julianEngine.core.Vector;
import com.julianEngine.core.World;
import com.julianEngine.core.World.IDAlreadyInUseException;
import com.julianEngine.core.World.WorldWaitListener;
import com.julianEngine.data.DataManager;
import com.julianEngine.data.JDFMaster;
import com.julianEngine.data.JDFPlugin;
import com.julianEngine.data.PreInitializer;
import com.julianEngine.graphics.Camera;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.UI.UIContainer;
import com.julianEngine.graphics.external_windows.ErrorReporter;
import com.julianEngine.graphics.gui.DebugToolsWindow;
import com.julianEngine.graphics.gui.LauncherWindow;
import com.julianEngine.graphics.shapes.ProgressBar;
import com.julianEngine.graphics.shapes.Text;
import com.julianEngine.utility.Log;
import com.julianEngine.utility.Tests;

/**
 * Julian Engine v1.2 - coded in Java with default libraries.
 */
public class Engine2D extends JFrame implements WindowListener, KeyListener {
	
	/*--------Public Static Variables-------*/
	public static String versionID = "1.1"; //Engine version
	public static JDFMaster masterFile; //Variable holder for the master plugin file (see JDFMaster)
	public static List<JDFPlugin> pluginFiles; //ArrayList holder for each plugin file (see JDFPlugin)
	public static boolean debugMode = true; //Should the engine be run in debug mode (set to false for release)
	public static InputStream stdin;
	public static OutputStream stdout;
	public static OutputStream stderr;
	
	
	/*--------Private Static Variables------*/
	private static final long serialVersionUID = -7981520978541595849L; //Serial Version UID for serializing the engine for save files and the like
	private static boolean engineStarted = false; //set to true when the first instance of Engine2D is created. Prevents plugins from creating a second instance
	private static Engine2D instance; //static reference to the engine - only one instance can be created, and this points to it
	private static boolean showLoadBar = false; //should the detailed loading bar be shown?
	private static PreInitializer initializer; //Variable holder for the initializer currently being proposed (see above and main())
	
	
	/*--------Public Instance Variables-----*/
	public World rootWorld; //Variable holder for the main world (populated by master file - usually a title screen of some sort)
	public Camera camera; //Variable holder for the camera rendering the window
	public Frame rootFrame = new Frame(1080, 720); //Variable holder for the frame that the camera renders to, and is displayed in the window
	public static Object engineLock = new Object(); //this should be locked on when modifying the engine, or when the code must use the engine (in the render loop for example)
	public static CoordinateSpace frameRootSystem = new CoordinateSpace(SystemType.CARTESIAN, AxisType.XAXIS_RIGHT_POS, AxisType.YAXIS_DOWN_POS);
	public CoordinateSpace mouseEventSpace;
	
	/*--------Private Instance Variables----*/
	private boolean paused = false; //is the game paused?
	private int fpsLock = -1; //default fps lock - used in constructor to set up render loop
	private ArrayList<EngineLoadListener> loadListeners = new ArrayList<EngineLoadListener>(); //list of parties who are interested in the loading state of the game
	private boolean consoleActive = false; //is the console active (THIS SHOULD BE TAKEN UP BY THE MASTER FILE, AND EVENTUALLY REMOVED)
	//settings for render loop (fps)
	private int renders = 0; //holder for how many times the frame has been drawn since last check
	private long lastUpdateNano = System.nanoTime(); //system time at the last check
	private ScheduledExecutorService renderLoopExecutor = Executors.newSingleThreadScheduledExecutor(); //holder for the executor service that runs the render loop at regular intervals
	private List<String> plugins;
	//Anonymous class for the render loop stored in a runnable object
	private Runnable renderLoop = () -> {
		if(!paused){
			try{
				synchronized(engineLock){
					camera.renderPerspective(rootFrame);
					renders++;
					
					if((System.nanoTime()-lastUpdateNano)>250000000){
						long timePassed = System.nanoTime()-lastUpdateNano;
						lastUpdateNano = System.nanoTime();
						double fps = ((float)renders/(double)((double)timePassed/1000000000f));
						camera.setFPS((float) fps);
						renders = 0;
					}
				}
			}catch(Exception e){
				Log.error("Error in render loop: ");
				e.printStackTrace();
			}
		}
		
	};
	
	
	

	/*--------Code--------------------------*/
	/**Entry point for application. Creates engine, loads plugins, and then hands operation off
	 * to master plugin file. if first arg is equal to "--testengine" no plugins will be loaded,
	 * and the engine will be tested instead.
	 * @throws NoMasterDataFileFoundException 
	 * @throws MultipleMasterFilesFoundException 
	**/
	public static void main(String[] args){
		Tests.runTests();
		System.out.println("Starting Engine in: "+System.getProperty("user.dir"));
		//first set up anything needed to run
		preLoadEngine();
		
		//Rename the thread. This has two purposes: first to help find errors during debugging, and also to print in the log
		Thread.currentThread().setName("ENGINE-main");
		try{
			Engine2D engine = Engine2D.getInstance();
			
			//Load plugins
			Log.trace("Loading plugins...");
			loadEnginePlugins();
			
			//get initializer
			initializer = getInitializer();
			
			//initialize
			Thread.currentThread().setName("ENGINE-preInitializer"); //change the name of the thread - for debug and log
			initializer.preInit(); //use the agreed upon initializer to initialize the game
			Thread.currentThread().setName("ENGINE-main"); //set the thread name back to ENGINE-main
			
			//LOADING SCREEN CODE
			LoadingScreen loadingScreen = makeLoadingScreen();
			
			engine.camera.moveToWorld(loadingScreen.getID()); //put the camera in the loading screen, to render it
			World.waitForWorldToBeReady(World.getWorldForID(loadingScreen.getID())); //wait for the loading screen to be ready, so we're not showing nothing when the game opens
			engine.setVisible(true); //once the loading screen is ready, show the window
			
			Thread customLoadThread; //thread to run custom loading code in - we put this in a new thread so we can limit the time spent on it, and keep load times low
			//Set the custom loading thread to 10% - since the code is loaded. We don't set the loading bar here because
			//it tracks the individual progress of each task
			customLoadThread = new Thread(){ //reset the load thread to the following code
				public void run(){
					for(EngineLoadListener l:engine.loadListeners){
						l.setLoadingPercentage((float) .1); //for each party interested in engine loading percentage, set the percenatge to 10%
					}
				}
			};
			customLoadThread.start();
			customLoadThread.join(1000); //start thread, and wait a maximum of 1 second for it to finish (so custom loaders don't hold us up)
			
			
			//INITIALIZE PLUGINS
			initializePlugins(loadingScreen, args, customLoadThread);
			
			
			//POST-INIT PLUGINS
			postInitPlugins(loadingScreen, customLoadThread);
			
			
			//MAKE TITLE SCREEN
			makeTitleScreenAndMove(args, loadingScreen, customLoadThread);
			
			//hand-off execution to master file
			Thread.currentThread().setName(masterFile.getPluginID()+"-main");
			masterFile.runGame(engine);
			
			Log.info("Master file has returned control - exiting game");
			System.exit(0);
		}catch(Exception e){
			Log.fatal("Fatal Error in main(): ");
			e.printStackTrace();
			ErrorReporter.displayError(e);
			System.exit(-1);
		}
	}
	
	/**
	 * Sets up any requirements for starting the engine
	 */
	private static void preLoadEngine(){
		//hold the default system streams in variables so the System.{stream} pointers can point other streams, but the cout and cin will still be available
		stdin = System.in;
		stdout = System.out;
		stderr = System.err;
	}
	
	/**
	 * Loads all plugins, and writes them to the static variables
	 * @throws NoMasterDataFileFoundException 
	 * @throws MultipleMasterFilesFoundException 
	 * @throws NoMasterClassFoundException 
	 * @throws MultipleMasterClassesException 
	 */
	private static void loadEnginePlugins() throws MultipleMasterClassesException, NoMasterClassFoundException, MultipleMasterFilesFoundException, NoMasterDataFileFoundException{
		Engine2D engine = Engine2D.getInstance();
		
		
		if (UserConfiguration.getBool("useLauncher", false)){
			//if we are using the launcher, load the plugins from that
			PluginsInformation info = loadPlugins(engine.plugins);
			
			masterFile = info.masterFile; //load master file
			pluginFiles = info.plugins; //load plugin files
		}else{
			//if we're not using the launcher, load the plugins the old way (these methods load all plugins that can be found)
			masterFile = loadMasterFile(); //load master file
			pluginFiles = loadPluginFiles(); //load plugin files
		}
		
		//Log if either masterFile or pluginFiles is null (null masterFile is an error)
		if(masterFile==null){
			//we should get an exception if there were any errors loading the master file (i.e. no master file, or multiple), so this should never run,
			//and if we see this error in the log before a crash, we know something is very wrong
			Log.fatal("Unknown error while loading master file (null pointer)");
			System.exit(-1); //shutdown the program with an error code
		}
		
		if(pluginFiles==null){ //if we don't have any plugin files loaded, log and proceed
			Log.info("No plugin files found - proceeding without");
		}
	}

	/**
	 * Gets an initializer for the engine to use to boot up.
	 * This implementation uses an agreements system to find a suitable initializer, but that is not guaranteed functionality
	 */
	private static PreInitializer getInitializer(){
		//!!!!!!!!!!!PLUGIN INITIALIZER AGREEMENT CODE!!!!!!!!!!!!!!!//
		//WARNING: IF NOT USED PROPERY, THIS CAN TURN INTO AN INFINITE LOOP (IF NO PLUGINS CAN AGREE ON AN INITIALIZER)!!!!!!!!!!
		//Inform plugins that we intend to initialize with the master plugin
		PreInitializer proposedInitializer = masterFile; //propose to use the master plugin file for the initializer
		
		/*
		Log.trace("Proposing Pre-Initializer");
		while(!initializerAgreement){ //loop until an initializer is decided on
			initializerAgreement = true; //if this isn't set to false by the end, we can assume an initializer was agreed on
			
			//boradcast our intention to use whatever initializer is being proposed
			JDFMessageManager.broadcastMessage(String.format("proposed-initializer:%s", proposedInitializer.getName()), new JDFMessageSender(){
				//anonymous class to represent the engine as a message sender
				@Override
				public String getName() {
					return "Engine2D loader";
				}

				@Override
				public void replyReceived(String originalMessage, byte[] reply, JDFMessageReceiver receiver) {
					//If we get a reply to our message, we need to deal with it - usually a reply means that a plugin doesn't agree on the initializer
					Log.trace(receiver.getName()+" disagrees with chosen initializer"); //tell the user what's happening
					initializerAgreement = false; //we don't agree anymore, make sure to update the boolean
					String msgReply = new String(reply); //turn the reply into a string to check the first bit
					ByteArrayInputStream replyStream = new ByteArrayInputStream(reply); //if the first bit is good, we need to have a stream for the rest of the data
					if(msgReply.startsWith("alternate-initializer:")){ //if the plugin is in fact proposing a new initializer, we need to get that initializer
						//new initializer proposed
						for(int i=0;i<"alternate-initializer:".length();i++){
							replyStream.read(); //dump bytes corresponding to the string, we only need the bits for the object
						}
						
						//the rest of the data should be a serialized PreInitializer
						try {
							ObjectInputStream initializerStream = new ObjectInputStream(replyStream); //create an object stream from the byte stream
							proposedInitializer = (PreInitializer) initializerStream.readObject(); //read the serialized object
						} catch (Exception e) {
							//If we get an exception, the byte stream was likely corrupted, or not formed properly by the plugin
							Log.error("Could not read pre-initializer object proposed by"+receiver.getName());
							e.printStackTrace();
						}
					}
				}
			});
		}
		*/
		return proposedInitializer;
	}
	
	/**
	 * Creates a loading screen for the engine to show while loading plugins
	 * @return
	 * @throws IDAlreadyInUseException
	 */
	private static LoadingScreen makeLoadingScreen() throws IDAlreadyInUseException{
		//create a loading screen with world id -1
		LoadingScreen loadingScreen = new LoadingScreen(-1);
		Engine2D engine = Engine2D.getInstance();
		
		Thread.currentThread().setName(masterFile.getPluginID()+"-loadScreen"); //change the thread name to signify we're getting the load screen
		loadingScreen = (LoadingScreen) masterFile.createLoadingScreen((World)loadingScreen, engine.rootFrame); //ask the master file for the load screen
		Thread.currentThread().setName("ENGINE-main"); //change thread name back
		
		int loadContainer_width = engine.rootFrame.getWidth();
		int loadContainer_height = (int)((float)engine.rootFrame.getHeight()*(.25f)*(.3f));
		UIContainer loadingContainer = new UIContainer(new Point(0, (int)(((.125f)*(float)engine.rootFrame.getHeight())+(float)loadContainer_height/2f), 1), loadContainer_width, loadContainer_height*2, loadingScreen);
		loadingContainer.centerX(loadingScreen.getContainingFrame());
		if(showLoadBar||debugMode){ //if we should show the loading bar, or if we are in debug name, add it to the loading screen
			/*
			loadingScreen.addShape(loadingBar);
			loadingScreen.addShape(loadingText);
			*/
			loadingScreen.addShape(loadingContainer);
		}
		
		int loadBar_width = (int)(.5*((float)loadContainer_width));
		int loadBar_height = (int)((float)loadContainer_height*(.75f));
		
		loadingScreen.loadingBar = new ProgressBar(new Point(0, loadContainer_height*2, 5), loadBar_width, loadBar_height); //create a progress bar for detailed loading progress
		loadingScreen.loadingText = new Text(new Point(20, (int)(loadContainer_height*(.75f)), 5), " ", Color.WHITE, new Font("Ariel", Font.PLAIN, 12), engine.rootFrame); //text to display loading progress on
		//loadingText.setCustomFont(new CustomFont(12, 0));
		loadingScreen.loadingText.fitCustomFontToContainer(new UIContainer(new Point(), loadContainer_width, (int)(loadContainer_height*(.4f)), loadingScreen));
		loadingScreen.loadingText.useCustomFont(true);
		//set up stylized loading bar and loading text
		loadingScreen.loadingBar.setBarColor(Color.WHITE);
		loadingScreen.loadingBar.setBorderColor(Color.WHITE);
		loadingScreen.loadingBar.centerX(loadingContainer.getFrame());
		loadingScreen.loadingBar.centerY(loadingContainer.getFrame());
		loadingScreen.loadingText.centerX(loadingContainer.getFrame());
		loadingScreen.loadingText.centerY(loadingContainer.getFrame());
		
		loadingContainer.addShapes(loadingScreen.loadingBar, loadingScreen.loadingText);
		//loadingContainer.setBorderColor(Color.magenta);
		
		return loadingScreen;
	}
	/**
	 * A custom implementation of a World (Screen) for a loading screen with a progress bar and some loading text
	 * @author Bowers
	 *
	 */
	private static class LoadingScreen extends World{
		public ProgressBar loadingBar;
		public Text loadingText;
		public LoadingScreen(int id) throws IDAlreadyInUseException{
			super(id);
		}
	}
	
	/**
	 * Initializes plugins
	 * @param loadingScreen
	 * @param args
	 * @param customLoadThread
	 * @throws InterruptedException
	 */
	private static void initializePlugins(LoadingScreen loadingScreen, String[] args, Thread customLoadThread) throws InterruptedException{
		Engine2D engine = Engine2D.getInstance();
		loadingScreen.loadingBar.setPercentFilled((float) 0); //since we're starting a new task, set the loading bar to 0 and set the text to our current task
		loadingScreen.loadingText.setText("Initializing Plugins...");
		loadingScreen.loadingText.centerX(engine.rootFrame); //re-center text since it changed
		
		Thread.currentThread().setName(masterFile.getPluginID()+"-init"); //change the thread name to show we're in init for the master file
		masterFile.init((args.length>0)?args[0]:""); //run master init first with the first argument if it exists
		if(pluginFiles!=null){ //if we have any plugins
			for(JDFPlugin plugin:pluginFiles){ //for each loaded plugin:
				Thread.currentThread().setName(plugin.getPluginID()+"-init"); //set the thread name to signal we're initing a plugin
				plugin.init((args.length>0)?args[0]:""); //init each plugin with the first argument if it exists
			}
		}
		Thread.currentThread().setName("ENGINE-main"); //when we're done go back to our thread name
		
		loadingScreen.loadingBar.setPercentFilled((float) 1); //we're done, so set the bar to 100%
		
		//set custom loading percentage to 30%
		customLoadThread = new Thread(){
			public void run(){
				for(EngineLoadListener l:engine.loadListeners){
					l.setLoadingPercentage((float) .3);
				}
			}
		};
		customLoadThread.start();
		customLoadThread.join(1000); //again wait a max of 1 second
	}
	
	/**
	 * Post-Initializes plugins
	 * @param loadingScreen
	 * @param customLoadThread
	 * @throws InterruptedException
	 */
	private static void postInitPlugins(LoadingScreen loadingScreen, Thread customLoadThread) throws InterruptedException{
		Engine2D engine = Engine2D.getInstance();
		loadingScreen.loadingBar.setPercentFilled((float) .5); //set loading bar
		//loadingText.setText("Post-Initializing Plugins...");
		loadingScreen.loadingText.centerX(engine.rootFrame);
		
		Thread.currentThread().setName(masterFile.getPluginID()+"-postInit");
		masterFile.postInit(); //run postInit for master first
		if(pluginFiles!=null){
			for(JDFPlugin plugin:pluginFiles){
				Thread.currentThread().setName(plugin.getPluginID()+"-postInit");
				plugin.postInit(); //run postInit in order
			}
		}
		Thread.currentThread().setName("ENGINE-main");
		
		loadingScreen.loadingBar.setPercentFilled((float) 1);
		
		customLoadThread = new Thread(){
			public void run(){
				for(EngineLoadListener l:engine.loadListeners){
					l.setLoadingPercentage((float) .6);
				}
			}
		};
		customLoadThread.start();
		customLoadThread.join(1000);
		
		engine.camera.forceRender();
		
		loadingScreen.loadingBar.setPercentFilled((float) 0);
		loadingScreen.loadingText.setText("Creating Title Screen...");
		loadingScreen.loadingText.centerX(engine.rootFrame);
		
		engine.camera.forceRender();
	}
	
	/**
	 * Gets the title screen from the master plugin, sets it up, and then moves the camera to it when it's ready.
	 * @param args
	 * @param loadingScreen
	 * @param customLoadThread
	 * @throws InterruptedException
	 */
	private static void makeTitleScreenAndMove(String[] args, LoadingScreen loadingScreen, Thread customLoadThread) throws InterruptedException{
		Engine2D engine = Engine2D.getInstance();
		Log.trace("about to make title screen");
		Thread.currentThread().setName(masterFile.getPluginID()+"-createMainScreen");
		engine.rootWorld = masterFile.createMainScreen(engine.rootWorld, engine.rootFrame, (args.length>0)?args[0]:"");
		Thread.currentThread().setName("ENGINE-main");
		Log.trace("made title screen");
		
		loadingScreen.loadingBar.setPercentFilled((float) 1);
		
		engine.camera.forceRender();
		
		customLoadThread = new Thread(){
			public void run(){
				for(EngineLoadListener l:engine.loadListeners){
					l.setLoadingPercentage((float) .7);
				}
			}
		};
		customLoadThread.start();
		customLoadThread.join(1000);
		
		engine.camera.forceRender();
		
		loadingScreen.loadingBar.setPercentFilled((float) 0);
		loadingScreen.loadingText.setText("Loading...");
		loadingScreen.loadingText.centerX(engine.rootFrame);
		
		engine.camera.forceRender();
		
		World.waitForWorldToBeReady(World.getWorldForID(engine.rootWorld.getID()), new WorldWaitListener(){
			@Override
			public void worldChecked(int totalObjects, int readyObjects) {
				Log.trace("world checked");
				loadingScreen.loadingBar.setPercentFilled((float)((float)readyObjects/(float)totalObjects));
			}
		});
		//engine.loading = false;
		
		loadingScreen.loadingBar.setPercentFilled(1);
		customLoadThread = new Thread(){
			public void run(){
				for(EngineLoadListener l:engine.loadListeners){
					l.setLoadingPercentage((float) 1);
				}
			}
		};
		customLoadThread.start();
		customLoadThread.join(1000);
		
		customLoadThread = new Thread(){
			public void run(){
				for(EngineLoadListener l:engine.loadListeners){
					l.waitForLoadComplete();
				}
			}
		};
		customLoadThread.start();
		customLoadThread.join(5000);
		
		engine.camera.moveToWorld(engine.rootWorld.getID());
	}
	
	
	
	
	//Constructor
	private Engine2D(String title, Engine2D predecessor) throws Exception{
		if(!engineStarted){
			try {
				this.setWindowIcon("engine/icon.png");//loads the default icon image - this should stay in the data directory instead of being packaged into a jrf file
				//this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(DataManager.getImageForURI("engine/cursor.png"), this.getLocation(), "cursor")); //loads the default cursor, see above
				
			} catch (Exception e) {
				Log.error("Error while loading default icons");
				e.printStackTrace();
			} //loads icon and cursor as first thing
			
			
			if(predecessor==null){ //if there was no predecessor then create everything, else use the objects from the predecessor
				//don't let the user see the default java icon, so set it to null for now
				
				//load engine config
				UserConfiguration.loadFile("./engine.config");
				
				//open launcher if bool set
				if (UserConfiguration.getBool("useLauncher", false)){
					plugins = LauncherWindow.getInstance().launchEngine();
				}
				
				//open debug window if bool set
				if(UserConfiguration.getBool("debug", false)){
					DebugToolsWindow consoleWin = DebugToolsWindow.getInstance();
					System.setOut(consoleWin.getPrintStream());
					System.setErr(consoleWin.getPrintStream());
					System.setIn(consoleWin.getInputStream());
					consoleWin.setVisible(true);
				}
				Log.info("Engine Starting - Hello World! - version: " + versionID);
				
				//initialize world 0 if new engine
				try {
					rootWorld = new World(0);
				} catch (IDAlreadyInUseException e) {
					Log.fatal("New engine being created, but world ID 0 is already being used...");
					e.printStackTrace();
					System.exit(3);
				}
				
				//initialize new camera
				camera = new Camera(rootFrame);
			}else{
				this.rootWorld = predecessor.rootWorld;
				this.camera = predecessor.camera;
			}
			
			//load relevant configuration vars into local memory
			boolean fullscreen = UserConfiguration.getBool("Fullscreen", false);
			Log.info("Fullscreen - "+fullscreen);
			
			//vars from config file
			int width;
			int height;
			fpsLock = UserConfiguration.getInt("FPSCap", 60);
			if(!fullscreen){
				width = UserConfiguration.getInt("Frame-width", EngineConstants.Defaults.width);
				height = UserConfiguration.getInt("Frame-height", EngineConstants.Defaults.height);
			}else{
				GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				width = gd.getDisplayMode().getWidth();
				height = gd.getDisplayMode().getHeight();
				this.setUndecorated(true);
				this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			}
			
			//set custom cursor
			//this.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("./Data/Cursor.png").getImage(), new java.awt.Point(0, 0), "Custom cursor"));
			
			//Create and set up main window
			this.setIgnoreRepaint(true); //Since we are using active rendering for the graphics - ignore system calls to repaint
			this.setTitle(title); //Title the main window
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //Closing must be handled by WindowListener
			this.setSize(width, height);
			this.setResizable(false); //Prevents the user from manually resizing the window, and messing up all our hard work
			this.setVisible(true); //the JFrame needs to be visible to set up the BufferStrategy - will be set to invisible later until ready
			this.createBufferStrategy(2); //Set up a buffer strategy for the window - allows for better performance while rendering
			//this.getContentPane().add(mainView); //Add the main Frame object to the window, so we can actually draw stuff on it
			this.add(rootFrame);
			this.addWindowListener(this); //Tell the JFrame to send any window events to the methods below
			this.addKeyListener(this); //Tell the JFrame to send any keyboard events to the methods below
			Log.trace("Main window set up");
			
			//Set up 'mainView' frame
			rootFrame.setIgnoreRepaint(true); //Stop the internal frame from getting system updates as well
			rootFrame.resizeFrame(width, height); //Resize frame to the size of the window
			rootFrame.setBorder(null);
			this.pack();
			rootFrame.setTargetFPS(fpsLock);
			//mainView.unlockFPS();
			
			Log.trace("Main viewport set up");
			
			//TODO: This is copied from old code, probably can be changed (window border no longer used)
			mouseEventSpace = new CoordinateSpace(Engine2D.frameRootSystem, false, false, 0, 0, 1);
			
			//Set up camera
			//mainCamera.showFPS(true);
			camera.moveToWorld(rootWorld.getID());
			Log.trace("Main camera set up");
			
			camera.showFPS(UserConfiguration.getBool("ShowFPS", false));
			//renderLoopExecutor.scheduleAtFixedRate(renderLoop, 0, 1000000000/60, TimeUnit.NANOSECONDS); //runs loop at ~60Hz
			setFPSTarget(fpsLock);
			
			Log.trace("Render loop set up");
			
			this.setVisible(false);
			instance = this; //set the static variable so that the active engine can always be instanced
			rootFrame.setCursor("engine/cursor.png");
		}else{
			throw new Exception("Engine Already Started"); //this should never be thrown, since the constructor is not public, and only the getInstance will call it
		}
	}
	
	/**
	 * Returns an instance of Engine2D
	 * @return the current instance of the engine, or a new one if this is the first time calling getInstance()
	 */
	public static Engine2D getInstance(){
		if (instance==null){
			try {
				synchronized (engineLock){
					instance = new Engine2D("JulianEngine "+versionID, null);
				}
			} catch (Exception e) {
				//this should never be called, since we protect for it with an if, but just in case:
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	
	
	
	//Miscellaneous methods
	/**
	 * Changes all windows to use the specified icon (a resource string that should be resolved by DataManager)
	 * @param rs_icon The resource string pointing to the new icon
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public void setWindowIcon(String rs_icon) throws IOException, IllegalArgumentException{
		this.setIconImage(ImageIO.read(DataManager.getStreamForResource(rs_icon)));
		DebugToolsWindow.getInstance().setIconImage(ImageIO.read(DataManager.getStreamForResource(rs_icon)));
		LauncherWindow.getInstance().setIconImage(ImageIO.read(DataManager.getStreamForResource(rs_icon)));
	}

	/**
	 * in theory re-runs the constructor and replaces the Engine instance
	 * This code might not be safe..
	 * @return
	 */
	//FIXME: There is a bit of unused code here from different ways of reloading the engine
	public boolean reloadEngine(){
		synchronized(engineLock){
			this.setVisible(false);
			this.dispose();
			try {
				boolean fullscreen = UserConfiguration.getBool("Fullscreen", false);
				Log.info("Fullscreen - "+fullscreen);
				
				//vars from config file
				int width;
				int height;
				if(!fullscreen){
					width = UserConfiguration.getInt("Frame-width", EngineConstants.Defaults.width);
					height = UserConfiguration.getInt("Frame-height", EngineConstants.Defaults.height);
				}else{
					GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
					width = gd.getDisplayMode().getWidth();
					height = gd.getDisplayMode().getHeight();
					this.setUndecorated(true);
					this.setExtendedState(JFrame.MAXIMIZED_BOTH);
				}
				
				this.setResizable(true);
				this.setSize(width, height);
				this.setResizable(false);
				
				this.setVisible(true); //the JFrame needs to be visible to set up the BufferStrategy - will be set to invisible later until ready
				this.createBufferStrategy(2); //Set up a buffer strategy for the window - allows for better performance while rendering
				this.setVisible(false);
				
				rootFrame.resizeFrame(width, height);
				this.pack();
				
				camera.showFPS(UserConfiguration.getBool("ShowFPS", false));
			} catch (Exception e) {
				Log.fatal("Failure reloading engine");
				e.printStackTrace();
				System.exit(2);
			}
			this.setVisible(true);
			return true;
		}
	}
	
	public void setPaused(boolean b){
		paused = b;
	}
	
	public void setName(String title){
		this.setTitle(title);
	}
	
	//Returns a point (com.julianEngine.core.Point) with the location of the mouse, in the
	//julian engine coordinate space. null if not in window
	@Deprecated
	public static Point getMouseLocation(){
		java.awt.Point mousePoint = instance.rootFrame.getMousePosition(true);
		if(mousePoint!=null){
			//return new Point(mousePoint.getX(), mousePoint.getY(), 0);
			return /*CoordinateSpace.convertPointToSystem(*/new Point(mousePoint.getX(), mousePoint.getY(), 0);//, instance.mouseEventSpace, frameRootSystem);
			//return instance.rootFrame.convertPointJGFXtoJEGFX(new Point(mousePoint.getX(), mousePoint.getY(), 0));
		}
		return null;
	}
	
	public void showLoadBar(boolean b){
		showLoadBar = b;
	}
	
	public int getMainWorldID(){
		return rootWorld.getID();
	}
	
	public void setMainWorld(int newWorldID){
		rootWorld = World.getWorldForID(newWorldID);
	}
	
	public void setFPSLock(int targetFPS){
		rootFrame.setTargetFPS(targetFPS);
	}
	
	private static class PluginsInformation{
		JDFMaster masterFile;
		List<JDFPlugin> plugins = new ArrayList<JDFPlugin>();
	}
	
	private static PluginsInformation loadPlugins(List<String> plugins) throws MultipleMasterClassesException, NoMasterClassFoundException, MultipleMasterFilesFoundException, NoMasterDataFileFoundException{
		PluginsInformation pluginInfo = new PluginsInformation();
		int masterFiles = 0;
		for (String plugin:plugins){
			if (plugin.endsWith(".jdm")){
				masterFiles++;
				if (masterFiles > 1){
					throw new MultipleMasterFilesFoundException();
				}
				File jdmFile = new File(System.getProperty("user.dir"), "./Data/"+plugin);
				
				try {
					URL[] importURL = {jdmFile.toURI().toURL()};
					URLClassLoader classLoader = new URLClassLoader(importURL);
					ServiceLoader<JDFMaster> masterLoader = ServiceLoader.load(JDFMaster.class, classLoader);
					Iterator<JDFMaster> masters = masterLoader.iterator();
					if(masters.hasNext()){
						JDFMaster masterFile = masters.next();
						if(masters.hasNext()){
							Log.error("Multiple master classes loaded from \""+jdmFile.getName()+"\"");
							throw new MultipleMasterClassesException();
						}
						pluginInfo.masterFile = masterFile;
					}else{
						Log.error("No master class found - the .jdm file was likely compiled wrong.");
						throw new NoMasterClassFoundException();
					}
					
				} catch (MalformedURLException e) {
					Log.error("Error loading the master plugin file:");
					e.printStackTrace();
				}
			}else if (plugin.endsWith(".jdp")){
				File jdpFile = new File(System.getProperty("user.dir"), "./Data/"+plugin);
				
				try {
					URL[] importURL = {jdpFile.toURI().toURL()};
					URLClassLoader classLoader = new URLClassLoader(importURL);
					ServiceLoader<JDFPlugin> masterLoader = ServiceLoader.load(JDFPlugin.class, classLoader);
					Iterator<JDFPlugin> pluginsIterator = masterLoader.iterator();
					while(pluginsIterator.hasNext()){
						pluginInfo.plugins.add(pluginsIterator.next());
					}
					
				} catch (MalformedURLException e) {
					Log.error("Error loading the master plugin file:");
					e.printStackTrace();
				}
			}
		}
		
		if (masterFiles == 0){
			throw new NoMasterDataFileFoundException();
		}
		
		return pluginInfo;
	}
	
	//returns an instance of the master file
	private static JDFMaster loadMasterFile() throws NoMasterDataFileFoundException, MultipleMasterFilesFoundException, MultipleMasterClassesException, NoMasterClassFoundException{
		File dataDir = new File(System.getProperty("user.dir"), "./Data"); //points to /Data directory
		
		//Get an array of files in the data directory that end in .jdm (Julian Data Master)
		File[] dataFiles = dataDir.listFiles(new FileFilter(){
			public boolean accept(File file){
				return file.getPath().toLowerCase().endsWith(".jdm");
			}
		});
		
		//Throw an exception if there are more than 1 .jdm files, or if there are 0 .jdm files
		if(dataFiles==null){
			throw new NoMasterDataFileFoundException();
		}else if(dataFiles.length>1){
			throw new MultipleMasterFilesFoundException();
		}
		
		//If the array consists of only 1 .jdm file
		try {
			URL[] importURL = {dataFiles[0].toURI().toURL()};
			URLClassLoader classLoader = new URLClassLoader(importURL);
			ServiceLoader<JDFMaster> masterLoader = ServiceLoader.load(JDFMaster.class, classLoader);
			Iterator<JDFMaster> masters = masterLoader.iterator();
			if(masters.hasNext()){
				JDFMaster masterFile = masters.next();
				if(masters.hasNext()){
					Log.error("Multiple master classes loaded from \""+dataFiles[0].getName()+"\"");
					throw new MultipleMasterClassesException();
				}
				return masterFile;
			}else{
				Log.error("No master class found - the .jdm file was likely compiled wrong.");
				throw new NoMasterClassFoundException();
			}
			
		} catch (MalformedURLException e) {
			Log.error("Error loading the master plugin file:");
			e.printStackTrace();
		}
		return null;
	}
	
	//returns an ArrayList of plugin files - in order based on load order - and checks for dependencies
	private static ArrayList<JDFPlugin> loadPluginFiles(){
		ArrayList<JDFPlugin> loadedPlugins = new ArrayList<JDFPlugin>();
		File dataDir = new File(System.getProperty("user.dir"), "./Data"); //points to /Data directory
		
		//Get an array of files in the data directory that end in .jdp (Julian Data Plugin)
		File[] dataFiles = dataDir.listFiles(new FileFilter(){
			public boolean accept(File file){
				return file.getPath().toLowerCase().endsWith(".jdp");
			}
		});
		
		for(File dataFile:dataFiles){
			try {
				URL[] importURL = {dataFile.toURI().toURL()};
				URLClassLoader classLoader = new URLClassLoader(importURL);
				ServiceLoader<JDFPlugin> masterLoader = ServiceLoader.load(JDFPlugin.class, classLoader);
				Iterator<JDFPlugin> plugins = masterLoader.iterator();
				while(plugins.hasNext()){
					loadedPlugins.add(plugins.next());
				}
				
			} catch (MalformedURLException e) {
				Log.error("Error loading the master plugin file:");
				e.printStackTrace();
			}
		}
		return loadedPlugins;
	}
	
	public BufferedImage pauseGame(){
		BufferedImage frameSnap = new BufferedImage(rootFrame.getWidth(), rootFrame.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D snapGfx = (Graphics2D)frameSnap.getGraphics();
		rootFrame.drawFrame(snapGfx, true);
		return frameSnap;
	}
	
	public int getFrameSideSize(){
		return (this.getSize().width - rootFrame.getWidth())/2;
	}
	
	public int getFrameTitleSize(){
		return (this.getSize().height - rootFrame.getHeight())-getFrameSideSize();
	}
	
	/**
	 * Sets the fps target for the render loop
	 * @param target
	 * fps target. If less than or equal to zero, uncaps fps
	 */
	public void setFPSTarget(int target){
		renderLoopExecutor.shutdown();
		try {
			renderLoopExecutor.awaitTermination(1, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			Log.trace("Interrupted while waiting for render executor service to shutdown");
			e1.printStackTrace();
		}
		renderLoopExecutor = Executors.newSingleThreadScheduledExecutor();
		if(target<=0){
			renderLoopExecutor.scheduleAtFixedRate(renderLoop, 0, 1, TimeUnit.NANOSECONDS);
		}else{
			renderLoopExecutor.scheduleAtFixedRate(renderLoop, 0, 1000000000/target, TimeUnit.NANOSECONDS);
		}
	}
	
	//KeyListener methods
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	boolean f3pressed = false;
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(!consoleActive){
			if(e.getExtendedKeyCode()==KeyEvent.VK_ENTER)
				System.out.println("enter");
			switch (e.getKeyCode()){
			case 192:
				Log.trace("Console key pressed");
				this.setPaused(true);
				consoleActive = true;
				Log.trace("Console active");
				break;
			case KeyEvent.VK_LEFT:
				camera.moveCamera(new Vector(-1, 0, 0));
				camera.update();
				break;
			case KeyEvent.VK_ESCAPE:
				break;
			case KeyEvent.VK_W:
				
				break;
			default:
				Log.trace("Unmapped key pressed");
			}
		}
		
		if(e.getKeyCode()==KeyEvent.VK_F3){
			f3pressed = true;
		}
		
		if(f3pressed){
			switch(e.getKeyChar()){
			case 'f':
				//toggle fps
				camera.showFPS(!camera.isShowingFPS());
				break;
			case 'p':
				//toggle UIContainers showing the mouse position
				UserConfiguration.addBool("containerShowMousePoint", !UserConfiguration.getBool("containerShowMousePoint", false));
				break;
			case 'm':
				//toggle showing masks
				UserConfiguration.addBool("drawMasks", !UserConfiguration.getBool("drawMasks", false));
				break;
			case 'd':
				Log.trace("Opening console...");
				DebugToolsWindow consoleWin = DebugToolsWindow.getInstance();
				System.setOut(consoleWin.getPrintStream());
				System.setErr(consoleWin.getPrintStream());
				System.setIn(consoleWin.getInputStream());
				consoleWin.setVisible(true);
				Log.trace("Console oppened");
				break;
			default:
				Log.trace("Unknown F3 key combo");
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_F3){
			f3pressed = false;
		}
	}
	
	//WindowListener methods
	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Log.info("Engine shutting down");
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		Log.info("Error - Window should not be able to close in this way, possibly crashed.");
	}

	@Override
	public void windowIconified(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}
	
	//Exceptions
	
	/**
	 * Thrown when there are multiple master files (.jdm) in the /data directory
	 */
	public static class MultipleMasterFilesFoundException extends Exception{
		private static final long serialVersionUID = 6452930918923379467L;
	}
	
	/**
	 * Thrown when no master file (.jdm) can be found
	 */
	public static class NoMasterDataFileFoundException extends Exception{
		private static final long serialVersionUID = 7646662446783964911L;
	}
	
	/**
	 * Thrown when multiple master classes are found in a single .jdm file
	 */
	public static class MultipleMasterClassesException extends Exception{
		private static final long serialVersionUID = 5703397513494083620L;
	}
	
	/**
	 * Thrown when no master class file is found in a .jdm file
	 */
	public static class NoMasterClassFoundException extends Exception{
		private static final long serialVersionUID = -2784167273272537183L;
	}
	
	public void addEngineLoadListener(EngineLoadListener l){
		loadListeners.add(l);
	}
	
	public interface EngineLoadListener{
		public void setLoadingPercentage(float percent);
		public void waitForLoadComplete();
	}

}