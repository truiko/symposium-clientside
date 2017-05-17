package imageUpload;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
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
				JLabel lbl = new JLabel();
				try{
					lbl.setIcon(new ImageIcon(img));
				}catch(Exception e){
					//e.printStackTrace();
					System.out.println("This is not an image.");
				}
				
				frame.getContentPane().add(lbl, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
			
		});
	}

}
