package clientSide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

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
	private long lastPacketLength = SoundPacket.defaultDataLength;
	private byte[] toPlay;
	
	
	public AudioChannel(long chId){
		this.chId = chId;
	}
	
	public long getChId(){
		return this.chId;
	}
	
    public void addToQueue(Message m) { //adds a message to the play queue
        query.add(m);
    }
    
    public boolean canKill() { //returns true if it's been a long time since last received packet
        if (System.nanoTime() - lastPacketTime > 5000000000L) {
            return true; //5 seconds with no data
        } else {
            return false;
        }
    }

    public void closeAndKill() {
        if (speaker != null) {
            speaker.close();
        }
        stop = true;
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
	                    Message message = query.remove(0);;
	                    if (message.getData() instanceof SoundPacket) { //it's a sound packet, send it to sound card
	                        SoundPacket m = (SoundPacket) (message.getData());
	                        //decompress data
	                        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(m.getData()));
	                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                        for (;;) {
                                int b = gis.read();
                                if (b == -1) {
                                    break;
                                } else {
                                    baos.write((byte) b);
                                }
                            }    
	                        //play decompressed data
	                        toPlay=baos.toByteArray();
                            speaker.write(toPlay, 0, toPlay.length);
                            lastPacketLength = m.getData().length;
                            
	                    }else { //not a sound packet, trash
	                    	continue; //invalid message
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
