package com.julianEngine.sound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.julianEngine.data.DataManager;
import com.julianEngine.utility.Log;

public class SoundEngine {
	private static SoundEngine engine;
	boolean started = false;
	private FloatControl musicGainControl;
	private List<String> music = new ArrayList<String>();
	private Queue<String> queuedMusic = new LinkedList<String>();
	private AudioInputStream musicStream;
	private boolean skipTrack = false;
	private float musicVolume = 0;
	private SourceDataLine musicLine;
	
	public SoundEngine() throws Exception{
		if(!started){
			initMusicLine();
			engine = this;
		}else{
			throw new Exception("Sound Engine Already Started");
		}
	}
	
	public static SoundEngine getInstance(){
		if (engine!=null){
			return engine;
		}else{
			try {
				return new SoundEngine();
			} catch (Exception e) {
				Log.fatal("Fatal error in creating a new sound engine: "); //this should never happen, but if it does we need to know
				e.printStackTrace();
				System.exit(5); //TODO: instead of hard-coding exit numbers, there should be a static enum somewhere for error codes
				return null; //unreachable code, but causes compiler to be happy about always being able to return a value
			}
		}
	}
	
	public InputStream getNextSong(){
		String songResourceString;
		if(queuedMusic.peek()!=null){
			songResourceString = queuedMusic.poll();
		}else{
			songResourceString = music.get(new Random().nextInt(music.size()));
		}
		return DataManager.getStreamForResource(songResourceString);
	}
	
	public void addSong(String song, boolean queue, boolean stopCurrentSong){
		music.add(song);
		if (queue){
			queuedMusic.add(song);
		}

		if(stopCurrentSong){
			musicVolume = getMusicVolume();
			skipTrack = true;
		}
	}
	
	public void removeSong(String song){
		music.remove(song);
		queuedMusic.remove(song);
	}
	
	public void setMusicVolume(float gain){
		musicGainControl.setValue(gain);
	}
	
	public float getMusicVolume(){
		return musicGainControl.getValue();
	}
	
	public FloatControl getMusicGainContol(){
		if (musicGainControl==null){
			initMusicLine();
		}
		return musicGainControl;
	}
	
	private void initMusicLine(){
		try {
			musicStream = AudioSystem.getAudioInputStream(getNextSong());
			
			AudioFormat format = musicStream.getFormat();
			if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
		      format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format
		          .getSampleRate(), format.getSampleSizeInBits() * 2, format
		          .getChannels(), format.getFrameSize() * 2, format.getFrameRate(),
		          true); // big endian
		      musicStream = AudioSystem.getAudioInputStream(format, musicStream);
		    }
			
			SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, musicStream.getFormat(), (int)musicStream.getFrameLength()*format.getFrameSize());
			SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
			//buffer should be about 50ms (1/20th sec), for almost instant changes
			int bufferSize = format.getFrameSize() * (int)(format.getFrameRate() / 20);
			line.open(musicStream.getFormat(), bufferSize);
			line.start();
			musicGainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
			
			musicLine = line;
		}catch (Exception e){
			Log.error("Error initializing music line");
		}
	}
	
	public void startMusic(){
		try {
			if(musicGainControl==null){
				initMusicLine();
			}
			
			SourceDataLine line = musicLine;
			
			float deltaVolume = 3.5f;
			
			new Thread(){
				public void run(){
					do{
						try{
							musicStream = AudioSystem.getAudioInputStream(getNextSong());
							int numRead = 0;
						    byte[] buf = new byte[line.getBufferSize()];
						    while ((numRead = musicStream.read(buf, 0, buf.length)) >= 0) {
						    	if(skipTrack){
						    		if(musicGainControl.getValue()>musicGainControl.getMinimum()+deltaVolume){
						    			musicGainControl.setValue(musicGainControl.getValue()-deltaVolume);
						    		}else{
						    			Log.trace("next track");
						    			musicGainControl.setValue(musicVolume);
						    			musicStream = AudioSystem.getAudioInputStream(getNextSong());
						    			numRead = 0;
						    			buf = new byte[line.getBufferSize()];
						    			skipTrack = false;
						    		}
						    	}
						      int offset = 0;
						      while (offset < numRead) {
						        offset += line.write(buf, offset, numRead - offset);
						      }
						    }
						    Log.trace("music done");
						}catch (Exception e){
							Log.error("Error in sound thread");
							e.printStackTrace();
						}
					}while (true);
				}
			}.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
