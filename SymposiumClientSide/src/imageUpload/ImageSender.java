package imageUpload;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

public class ImageSender {

	public ImageSender(String filePath) {
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				JFrame frame = new JFrame(filePath);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				BufferedImage img = null;
				try{
					img = ImageIO.read(new File(filePath));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		});
	}

}
