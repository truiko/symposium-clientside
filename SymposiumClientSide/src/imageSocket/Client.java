package imageSocket;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Client {

	public static void main(String[] args) throws Exception{
		try(Socket socket = new Socket("localhost", 25000)){
		      BufferedImage image = ImageIO.read(socket.getInputStream());
		      JLabel label = new JLabel(new ImageIcon(image));
		      JFrame f = new JFrame("vnc");
		      f.getContentPane().add(label);
		      f.pack();
		      f.setVisible(true);
		    }

	}

}
