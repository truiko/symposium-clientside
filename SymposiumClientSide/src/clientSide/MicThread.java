package clientSide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * reads data from microphone and sends it to the server
 *
 * @author dosse
 */
public class MicThread extends Thread {

    public static double amplification = 1.0;
    private ObjectOutputStream toServer;
    private TargetDataLine mic;
    private AudioFormat format;


    public MicThread(ObjectOutputStream toServer) throws LineUnavailableException {
        this.toServer = toServer;
        format = new AudioFormat(11025f, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
        	System.out.println("Line not supported");
        }
        
        mic = (TargetDataLine) AudioSystem.getLine(info);
        mic.open(format);
        mic.start();   // start capturing
    }

    @Override
    public void run() {
        for (;;) {
            if (mic.available() >= 1200) { //we got enough data to send
                byte[] buff = new byte[1200];
                while (mic.available() >= 1200) { //flush old data from mic to reduce lag, and read most recent data
                    mic.read(buff, 0, buff.length); //read from microphone
                }
                try {
                    //create and send packet
                    Message m = null;
                    //compress the sound packet with GZIP
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    GZIPOutputStream go = new GZIPOutputStream(baos);
                    go.write(buff);
                    go.flush();
                    go.close();
                    baos.flush();
                    baos.close();
                    m = new Message(-1, -1, new SoundPacket(baos.toByteArray()));  //create message for server, will generate chId and timestamp from this computer's IP and this socket's port 
                    toServer.writeObject(m); //send message
                } catch (IOException ex) { //connection error
                	ex.printStackTrace();
                }
            } else {
                Utils.sleep(10); //sleep to avoid busy wait
            }
        }
    }
}
