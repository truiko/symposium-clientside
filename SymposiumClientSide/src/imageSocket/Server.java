package imageSocket;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import imageUpload.ImageSender;

public class Server {

	public static void main(String[] args) throws Exception{
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(fc);
		RenderedImage filePath = null;
		//changed String to RenderedImage
		if(returnVal == JFileChooser.APPROVE_OPTION){
			filePath = (RenderedImage) fc.getSelectedFile();
			//removed .getAbsolutePath()
		}else{
			System.out.println("User clicked CANCEL");
			System.exit(1);
		}
		//new ImageSender(filePath);
		
		//BufferedImage screencapture = new ImageSender(filePath);
		//BufferedImage screencapture = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	    try (ServerSocket serv = new ServerSocket(25000)) {
	      System.out.println("waiting...");
	      try (Socket socket = serv.accept()) {
	        System.out.println("client connected");
	        //replaced screencapture with filePath
	        ImageIO.write(filePath, "jpg", socket.getOutputStream());
	        System.out.println("sent");
	      }
	    }

	}

}
