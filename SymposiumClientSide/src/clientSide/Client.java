package clientSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

import com.vdurmont.emoji.EmojiParser;


public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private JButton micButton;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Message message;
	private String serverIP;
	private Socket connection;
	private MicThread st;
	private ArrayList<AudioChannel> channels = new ArrayList<AudioChannel>();
	private BufferedWriter writer;
	
	public Client(String host) {
		super("Symposium Client");
		serverIP = host;
		userText = new JTextField();
		micButton = new JButton("Voice");
		micButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startMic();	
			}
			
		});
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage((new Message(event.getActionCommand())));
//					File check = new File("" + connection.getInetAddress().getHostName() + "+" + serverIP+ ".txt");
//					if(check.isFile()){
//						try {
//							writer = new BufferedWriter(new FileWriter("C:/Users/Student 8/git/symposium-clientside/SymposiumClientSide/"+connection.getInetAddress()
//									.getHostName() + "+" + serverIP+ ".txt", true));
//							System.out.println("hello");
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						
//					}else{
//						try{
//							File texting = new File("" + connection.getInetAddress().getHostName() + "+" + serverIP+ ".txt");
//							writer = new BufferedWriter(new FileWriter(texting, true));
//							System.out.println("its me");
//						}catch(IOException e){
//							e.printStackTrace();
//						}
//					}
//					try {
//						writer.write(event.getActionCommand() + "\r\n");
//						System.out.println(event.getActionCommand());
//						System.out.println("heyyyy");
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					try {
//						writer.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		add(micButton, BorderLayout.SOUTH);
		setSize(500,500);
		setVisible(true);
	}
	
	private void playbackSound(Message sound) {
		try {
			if(connection.getInputStream().available() > 0){
				AudioChannel sendTo = null;
				for(AudioChannel channel: channels){
					if(channel.getChId() == sound.getChId()){
						sendTo = channel;	
					}
				}
				if(sendTo != null){
					sendTo.addToQueue(sound);
				}else{
					AudioChannel channel = new AudioChannel(sound.getChId());
					channel.addToQueue(sound);
					channel.start();
					channels.add(channel);
				}
			}else{
				ArrayList<AudioChannel> killMe=new ArrayList<AudioChannel>();
                for(AudioChannel c:channels) if(c.canKill()) killMe.add(c);
                for(AudioChannel c:killMe){c.closeAndKill(); channels.remove(c);}
                Utils.sleep(1); //avoid busy wait
			}
					
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
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
			try{
				message = (new Message(input.readObject()));
				if(message.getData() instanceof String){
					message.setData(convertToEmoji((String) message.getData()));
					showMessage("\n" + message.getData());
				}else{ 
					if(message.getData() instanceof byte[]){
						playbackSound(message);
					}
				}
			}catch(Exception e){
				showMessage("\n Can't understand what that user sent!");
				e.printStackTrace();
				System.exit(1);
			}
		}while(!message.getData().equals("SERVER - END"));
	}
	
	private void startMic() {
        try {
        	System.out.println("on");
            Utils.sleep(100); //wait for the GUI microphone test to release the microphone
            st = new MicThread(output);  //creates a MicThread that sends microphone data to the server
            st.start(); //starts the MicThread
        } catch (LineUnavailableException e) { //error acquiring microphone. causes: no microphone or microphone busy
            showMessage("mic unavailable " + e);
        }
		
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
			if(message.getData() instanceof String){
				output.writeObject("CLIENT - " + message.getData());
				System.out.println("sent");
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
	
	//checks through the input to see if there are any characters that correspond with an emoji and changes it if found
	private static String convertToEmoji(String message){
		// this method only used for the type-able Emojis
		String newString =message;
		String[] emojis = {":smiley:", ":wink:", ":slightly_frowning:",
						":upside_down, flipped_face:", ":expressionless:", ":heart:"};
		String[] emojiSymbols = {":)", ";)", ":(", "(:", ":|", "<3"};
		if(EmojiParser.parseToUnicode(message)!=message){
			newString = EmojiParser.parseToUnicode(message);
		}
		for(int i = 1; i < message.length(); i++){
			for(int j = 0; j < emojis.length; j++){
				if(message.substring(i-1, i+1).equals(emojiSymbols[j])){
					newString = message.replace(message.substring(i-1,i+1), EmojiParser.parseToUnicode(emojis[j]));
				}
			}
		}
		return newString;
	}
}
