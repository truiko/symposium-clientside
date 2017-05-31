package clientSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

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
	private ArrayList<AudioChannel> chs = new ArrayList<AudioChannel>();
	
	public Client(String host) {
		super("Symposium Client");
		serverIP = host;
		userText = new JTextField();
		micButton = new JButton("Voice");
		micButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				startMic();	
				listenForVoice();
			}
			
		});
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand());
					userText.setText("");
				}
			}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		add(micButton, BorderLayout.SOUTH);
		setSize(300,150);
		setVisible(true);
	}
	
	private void listenForVoice() {
		while(true){
			try {
				if(connection.getInputStream().available() > 0){
					Message sound = (Message)(input.readObject());
					AudioChannel sendTo = null;
					for (AudioChannel ch : chs) {
                        if (ch.getChId() == sound.getChId()) {
                            sendTo = ch;
                        }
                    }
                    if (sendTo != null) {
                        sendTo.addToQueue(sound);
                    } else { //new AudioChannel is needed
                        AudioChannel ch = new AudioChannel(sound.getChId());
                        ch.addToQueue(sound);
                        ch.start();
                        chs.add(ch);
                    }
                }else{ //see if some channels need to be killed and kill them
                    ArrayList<AudioChannel> killMe=new ArrayList<AudioChannel>();
                    for(AudioChannel c:chs) if(c.canKill()) killMe.add(c);
                    for(AudioChannel c:killMe){c.closeAndKill(); chs.remove(c);}
                    Utils.sleep(1); //avoid busy wait
                }
					
			}catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
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
				message = (Message) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n Can't understand what that user sent!");
			}
		}while(!message.equals("SERVER - END"));
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
	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT -" + message);
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
}
