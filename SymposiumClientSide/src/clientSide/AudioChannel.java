package clientSide;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 * this thread plays the sound coming from one of the users that are connected
 * on the server. each user has its own AudioChannel.
 *
 * @author dosse
 */
public class AudioChannel extends Thread {
	private long chId;
	private SourceDataLine speaker;
	private boolean stop;
	private ArrayList<Message> query;
	private long lastPacketTime = System.nanoTime();
	
	
	public AudioChannel(long chId){
		this.chId = chId;
	}
	
	public long getChId(){
		return this.chId;
	}
	
	public void run(){
		while(!stop){
			try{
				AudioFormat af = SoundPacket.defaultFormat;
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
				speaker = (SourceDataLine) AudioSystem.getLine(info);
				speaker.open(af);
				speaker.start();
				for(;;){
					if(query.isEmpty()){
						Utils.sleep(10);
						continue;
					}else{
						lastPacketTime = System.nanoTime();
						Message m = query.remove(0);
						if(m.getData() instanceof SoundPacket){
							
						}
					}
				}
				
			}catch(Exception e){
				if(speaker != null){
					speaker.close();
				}
				stop = true;
			}	
		}
		

	}
}
