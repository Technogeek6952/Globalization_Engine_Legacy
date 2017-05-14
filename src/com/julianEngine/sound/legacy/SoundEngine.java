package com.julianEngine.sound.legacy;

import java.util.HashMap;

public class SoundEngine {
	
	private HashMap<String, Sound> music = new HashMap<String, Sound>();
	//private HashMap<String, Sound> uiSounds = new HashMap<String, Sound>();
	
	public SoundEngine(){
	}
	
	public void addMusic(String name, Sound sound){
		music.put(name, sound);
		sound.play();
	}
}
