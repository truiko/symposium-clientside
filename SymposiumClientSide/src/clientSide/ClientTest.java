package clientSide;

import javax.swing.JFrame;

public class ClientTest {
	
	public static void main(String[] args) {
		Client client;
		Login login;
		//login = new Login();
		client = new Client("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startRunning();

	}
	
	public static void runClient(){
		
	}

}
