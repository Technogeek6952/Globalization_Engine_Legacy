package com.julianEngine;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ServiceLoader;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.julianEngine.core.Point;
import com.julianEngine.core.Shape;
import com.julianEngine.core.World;
import com.julianEngine.core.World.IDAlreadyInUseException;
import com.julianEngine.data.JDFMaster;
import com.julianEngine.data.JDFPlugin;
import com.julianEngine.graphics.Camera;
import com.julianEngine.graphics.Frame;
import com.julianEngine.graphics.UI.UIButton;
import com.julianEngine.graphics.shapes.Line;
import com.julianEngine.graphics.shapes.Rectangle;
import com.julianEngine.graphics.shapes.Sprite;
import com.julianEngine.graphics.shapes.Text;
import com.julianEngine.utility.Log;

/**
 * Julian Engine v1.2 - coded in Java with default libraries. Successor to v1.0
 * which, although worked somewhat and taught valuable lessons, was ultimately
 * a massive failure. May it rest in peace.
 */
public class Engine2D extends JFrame implements WindowListener, KeyListener {
	/*--------Public Static Variables-------*/
	public static String versionID = "v1.2_a03";
	public static JDFMaster masterFile;
	public static ArrayList<JDFPlugin> pluginFiles;
	/*--------Private Static Variables------*/
	private static final long serialVersionUID = -7981520978541595849L;
	static boolean engineStarted = false; //set to true when the first instance of Engine2D is created. Prevents plugins from creating a second instance
	
	/*--------Public Instance Variables-----*/
	public World mainWorld;
	public Camera mainCamera;
	public Text fpsText;
	
	/*--------Private Instance Variables----*/
	private BufferStrategy bufferStrategy;
	private Frame mainView = new Frame();
	private boolean paused = false;
	
	/*--------Code--------------------------*/
	/**Entry point for application. Creates engine, loads plugins, and then hands opperation off
	 * to master plugin file. if first arg is equal to "--testengine" no plugins will be loaded,
	 * and the engine will be tested instead.
	 * @throws NoMasterDataFileFoundException 
	 * @throws MultipleMasterFilesFoundException 
	**/
	public static void main(String[] args){
		try{
			try {
				//Create the engine
				Engine2D engine = new Engine2D("JulianEngine "+versionID);
				
				if(args.length > 0 && args[0].equals("--testengine")){
					//Test Engine
					Log.trace("Testing engine...");
					
					testEngine(engine);
				}else{
					//Load plugins
					Log.trace("Loading plugins...");
					
					masterFile = loadMasterFile(); //load master file
					pluginFiles = loadPluginFiles(); //load plugin files
					
					//Log if either masterFile or pluginFiles is null (null masterFile is an error)
					if(masterFile==null){
						Log.fatal("Unknown error while loading master file (null pointer)");
						System.exit(-1);
					}
					
					if(pluginFiles==null){
						Log.info("No plugin files found - proceeding without");
					}
					
					//run init
					masterFile.init(); //run master init first
					if(pluginFiles!=null){
						for(JDFPlugin plugin:pluginFiles){
							plugin.init(); //init each plugin file in order
						}
					}
					
					//run postInit
					masterFile.postInit();
					if(pluginFiles!=null){
						for(JDFPlugin plugin:pluginFiles){
							plugin.postInit(); //run postInit in order
						}
					}
					
					//hand-off execution to master file
					masterFile.initGame(engine);
					
					Log.info("Master file has returned control - exiting game");
					System.exit(0);
				}
			} catch (EngineAlreadyInstancedException e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			Log.fatal("Fatal Error in main(): ");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	//Constructor
	public Engine2D(String title) throws EngineAlreadyInstancedException{
		if(!engineStarted){
			Log.info("Engine Starting - Hello World! - version: " + versionID);
			
			//Create and set up main window
			this.setIgnoreRepaint(true); //Since we are using active rendering for the graphics - ignore system calls to repaint
			this.setTitle(title); //Title the main window
			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //Closing must be handled by WindowListener
			this.setSize(Settings.width, Settings.height); //Sets the size of the window
			this.setResizable(false); //Prevents the user from manually resizing the window, and messing up all our hard work
			this.setVisible(true); //the JFrame needs to be visible to set up the BufferStrategy - will be set to invisible later until ready
			this.createBufferStrategy(2); //Set up a buffer strategy for the window - allows for better performance while rendering
			bufferStrategy = this.getBufferStrategy(); //Set the public variable so the buffer strategy can be accessed by other classes
			this.getContentPane().add(mainView); //Add the main Frame object to the window, so we can actually draw stuff on it
			this.addWindowListener(this); //Tell the JFrame to send any window events to the methods below
			this.addKeyListener(this); //Tell the JFrame to send any keyboard events to the methods below
			Log.trace("Main window set up");
			
			//Set up 'mainView' frame
			mainView.setIgnoreRepaint(true); //Stop the internal frame from getting system updates as well
			mainView.resizeFrame(Settings.width, Settings.height); //Resize frame to the size of the window
			//mainView.setTargetFPS(60); // TODO make setFPS more accurate - 60fps target results in average 62-64fps
			mainView.unlockFPS();
			Log.trace("Main viewport set up");
			
			//Set up world
			try {
				mainWorld = new World(0);
			} catch (IDAlreadyInUseException e) {
				e.printStackTrace();
			}
			Log.trace("Main world set up");
			
			//Set up camera
			mainCamera = new Camera();
			mainCamera.moveToWorld(mainWorld.getID());
			Log.trace("Main camera set up");
			
			//Set up render loop
			Engine2D ref = this;
			new Thread("Render Loop"){
				public void run(){
					try{
						int renders = 0;
						long lastCheck = System.currentTimeMillis();
						while(true){
							if(!paused){
								synchronized(mainView){
									mainCamera.renderPerspective(mainView, bufferStrategy);
									if((System.currentTimeMillis()-lastCheck)>500){
										float fps = (float) (renders/.5);
										if(ref.isVisible()){
											fpsText.setText("FPS: "+fps);
										}
										lastCheck = System.currentTimeMillis();
										renders=0;
									}
									renders++;
								}
							}
						}
					}catch(Exception e){
						Log.error("Uncaught exception in the render loop - this should probably be fixed...");
						e.printStackTrace();
					}
				}
			}.start();
			Log.trace("Render loop set up");
		}else{
			throw new EngineAlreadyInstancedException();
		}
		this.setVisible(false);
	}
	
	public int getMainWorldID(){
		return mainWorld.getID();
	}
	
	public void setMainWorld(int newWorldID){
		mainWorld = World.getWorldForID(newWorldID);
	}
	
	//returns an instance of the master file
	private static JDFMaster loadMasterFile() throws NoMasterDataFileFoundException, MultipleMasterFilesFoundException, MultipleMasterClassesException, NoMasterClassFoundException{
		File dataDir = new File("./Data"); //points to /Data directory
		
		//Get an array of files in the data directory that end in .jdm (Julian Data Master)
		File[] dataFiles = dataDir.listFiles(new FileFilter(){
			public boolean accept(File file){
				return file.getPath().toLowerCase().endsWith(".jdm");
			}
		});
		
		//Throw an exception if there are more than 1 .jdm files, or if there are 0 ,jdm files
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
		return null;
	}
	
	private static void testEngine(Engine2D engine){
		try {
			World loadingScreen = new World(-1);
			
			engine.mainView.setBackground(Color.GRAY);
			Sprite background = new Sprite(new Point(0, 720, 0), 1080, 720, "./assets/images/flags/American Flag.png");
			background.setAlpha((float) .45);
			loadingScreen.addShape(background);
			
			Text nameText = new Text(new Point(100, 600, 1), "GLOBALIZATION", Color.BLACK, new Font("Ariel", Font.BOLD, 100), engine.mainView);
			loadingScreen.addShape(nameText);
			
			Text loadingText = new Text(new Point(200, 400, -1), "LOADING...", Color.BLACK, new Font("Ariel", Font.PLAIN, 50), engine.mainView);
			loadingScreen.addShape(loadingText);
		} catch (IDAlreadyInUseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		engine.mainCamera.moveToWorld(-1);
		waitForWorldToBeReady(World.getWorldForID(-1));
		engine.setVisible(true);
		
		Rectangle test = new Rectangle(new Point(10,110,0), 200, 100, Color.BLACK);
		engine.mainWorld.addShape((Shape)test);
		
		Point imageTL = new Point(200, 200, 1);
		Sprite testSprite = new Sprite(imageTL, 200, 50, "./assets/images/gifs/Untitled.gif");
		testSprite.setGifFPS(10);
		engine.mainWorld.addShape(testSprite);
		
		Point textTL = new Point(500, 500, 0);
		engine.fpsText = new Text(textTL, "TESTING", Color.BLACK, new Font("Ariel", Font.PLAIN, 20), engine.mainView);
		engine.mainWorld.addShape(engine.fpsText);
		
		Point buttonTL = new Point(500, 300, 0);
		UIButton buttonTest = new UIButton(buttonTL, "Button!", Color.BLACK, engine.mainView);
		engine.mainWorld.addShape(buttonTest);
		
		Log.info("Waiting on objects to report ready status...");
		waitForWorldToBeReady(engine.mainWorld);
		Log.info("Game Ready!");
		engine.mainCamera.moveToWorld(engine.mainWorld.getID());
		engine.mainView.setBackground(Color.LIGHT_GRAY);
	}
	
	public static void waitForWorldToBeReady(World world){
		boolean ready = false;
		while(!ready&&world.getShapes().size()!=0){
			ready = true;
			for(Shape s:world.getShapes()){
				if(!s.isReady()){
					Log.warn("Still waiting for ready status from: "+s);
					ready = false;
					break;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//KeyListener methods
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()){
		case 192:
			Log.trace("Console key pressed");
			break;
		default:
			Log.trace("Unmapped key pressed");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	//WindowListener methods
	@Override
	public void windowOpened(WindowEvent e) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		Log.info("Engine shutting down (closed by \"X\" on window)");
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		Log.info("Error - Window should not be able to close in this way, probably crashed.");
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
	 * Thrown when an instance of the engine already exists. Used to prevent plugins from creating
	 * multiple engines.
	 */
	public static class EngineAlreadyInstancedException extends Exception{
		private static final long serialVersionUID = 4262077084686078934L;
	}
	
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
}
