package clientSide;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private JTextPane pane;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	//private String message = "";
	private String serverIP;
	private Socket connection;
	private JButton attachment;
	
	private Message message;
	
	public Client(String host) {
		super("Symposium Client");
		serverIP = host;
		userText = new JTextField();
		pane = new JTextPane();
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					//sendMessage(event.getActionCommand());
					
					sendMessage((new Message(event.getActionCommand())));
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
		
		pane.setPreferredSize(new Dimension(50, 50));
		add(new JScrollPane(pane), BorderLayout.WEST);
		pane.setVisible(true);
		//pane.insertIcon(new ImageIcon("src/resources/redcolor.jpg"));
		//pane.insertIcon(new ImageIcon("src/resources/Desert.jpg"));
		//((JTextPane) chatWindow).insertIcon(new ImageIcon("src/resources/redcolor.jpg"));
		attachment = new JButton("Attachment");
		attachment.setSize(1,1);
		add(attachment, BorderLayout.EAST);
		attachment.setVisible(true);
		
		attachment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					sendImage();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			//receiveImage();
			whileChatting();
		}catch(EOFException eofException){
			showMessage("\n Client terminated connection");
		}catch(IOException ioException){
			ioException.printStackTrace();
		}finally{
			closeAll();
		}
	}
	
	//connect to server
	public void connectToServer() throws IOException{
		showMessage("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//set up streams to send and receive messages
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now good to go! \n");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			//if(message.getData() instanceof String){
				try{
					//message = (String) input.readObject();
					message = (new Message(input.readObject()));
					showMessage("\n" + message.getData());
				}catch(ClassNotFoundException classNotFoundException){
					showMessage("\n I don't know that object type");
				}
			//}
//			else{
//				if(message.getData() instanceof BufferedImage){
//					try{
//						message = (new Message(input.readObject()));	
//					}
//				}
//			}
		}while(!message.getData().equals("SERVER - END"));
	}
	
	//close the streams and sockets
	private void closeAll(){
		showMessage("\n Closing all down... ");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	//send messages to the server
	private void sendMessage(Message message){
		try{
//			output.writeObject("CLIENT - " + message);
//			output.flush();
//			showMessage("\nCLIENT -" + message);
			
			if(message.getData() instanceof String){
				output.writeObject("CLIENT - " + message.getData());
				showMessage("\nCLIENT -" + message.getData());
			}else{
				output.writeObject(message.getData());
			}
			output.flush();
		}catch(IOException ioException){
			chatWindow.append("\n Something went wrong when sending message!");
		}
	}
	
	// change/update chatWindow
	private void showMessage(final String m){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(m);
				}
			}
		);
	}
	
	//gives user permission to type into the text box
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);	
				}
			}
		);		
	}
	
	private void sendImage() throws IOException{
		//try (ServerSocket serv = new ServerSocket(25000)) {
		   //  System.out.println("waiting...");
		    //  try (Socket socket = serv.accept()) {
		
		BufferedImage img = null;
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(fc);
		String filePath = null;
		if(returnVal == JFileChooser.APPROVE_OPTION){
			System.out.println("chosen");
			filePath = fc.getSelectedFile().getAbsolutePath();
			System.out.println("got filepath");
		}else{
			System.out.println("User clicked CANCEL");
			//System.exit(1);
		}
		try{
			img = ImageIO.read(new File(filePath));
			System.out.println("made img variable");
		}catch(Exception e){
			e.printStackTrace();
		}
        ImageIO.write(img, "jpg", output);
        output.writeObject(null);
        //output.flush();
        System.out.println("sent");
        
		     // }//
		//}
        
        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //ImageIO.write(img, "jpg", baos);
        //byte[] bytes = baos.toByteArray();
        //output.writeObject(null);
        
//        try {
//
//            byte[] imageInByte;
//            BufferedImage originalImage1 = ImageIO.read(new File(filePath));
//
//            // convert BufferedImage to byte array
//            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
//            ImageIO.write(originalImage1, "jpg", baos1);
//            baos1.flush();
//            byte[] ba1 = baos1.toByteArray();
//            imageInByte = new byte[ba1.length];
//            //System.out.println(new String(imageInByte));
//            System.arraycopy(ba1, 0, imageInByte, 0, ba1.length);
//            //System.out.println(new String(imageInByte));
//            //System.out.println(new String(imageInByte));
//            baos1.close();
//
//            // convert byte array back to BufferedImage
//            InputStream in = new ByteArrayInputStream(imageInByte);
//
//            int w = Math.max(originalImage1.getWidth(), w);
//            //int h = Math.max(originalImage1.getHeight(), originalImage2.getHeight());
//            int h = originalImage1.getHeight();
//            BufferedImage bImageFromConvert = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//            //BufferedImage bImageFromConvert = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR );
//
//            //BufferedImage bImageFromConvert = ImageIO.read(in);
//
//            Graphics g = bImageFromConvert.getGraphics();
//            g.drawImage(originalImage1, 0, 0, null);
//
//            ImageIO.write(bImageFromConvert, "jpg", new File("result.jpg"));
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
	}
	
	private void receiveImage() throws IOException{
		boolean running = true;
//		System.out.println("initiating receival of image");
//		BufferedImage image = ImageIO.read(input);
//	      System.out.println("got image");
//	      JLabel label = new JLabel(new ImageIcon(image));
//	      JFrame f = new JFrame("Image sent from server");
//	      f.getContentPane().add(label);
//	      f.pack();
//	      f.setVisible(true);
//	      System.out.println("image is displayed");
		
	      
	      do{
				System.out.println("initiating receival of image");
				BufferedImage image = ImageIO.read(input);
				  System.out.println("got image");
				  JLabel label = new JLabel(new ImageIcon(image));
				  JFrame f = new JFrame("Image sent from server");
				  f.getContentPane().add(label);
				  f.pack();
				  f.setVisible(true);
				  System.out.println("image is displayed");
				  running = false;
			}while(running);
	}
}
