package com.julianEngine.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.julianEngine.data.DataManager;
import com.julianEngine.utility.Log;

public class Sound{
	
	private Clip clip;
	private FloatControl gainControl;
	private boolean loop = false;
	public Sound(String resource){
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(DataManager.getStreamForResource(resource)));
			
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
			Log.trace("Error opening audio file: "+resource);
			e.printStackTrace();
		}
	}
	
	public void play(){
		clip.start();
	}
	
	public void pause(){
		clip.stop();
	}
	
	public void changeVolume(float deltaDB){
		float newGain = gainControl.getValue()+deltaDB;
		if(gainControl.getMaximum()>=newGain&&gainControl.getMinimum()<=newGain){
			gainControl.setValue(newGain);
		}
		
		if(loop){
			clip.setLoopPoints(0, -1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}else{
			clip.loop(0);
		}
		
	}
	
	public float getGain(){
		return gainControl.getValue();
	}
	
	public FloatControl getGainControl(){
		return gainControl;
	}
	
	public void loop(boolean b){
		if(b){
			loop = b;
			clip.setLoopPoints(0, -1);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}else{
			clip.loop(0);
			loop = b;
		}
	}
	
	public Clip getClip(){
		return clip;
	}
	
	public void setClip(Clip newClip){
		clip = newClip;
	}
}
