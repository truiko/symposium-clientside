package clientSide;

import javax.swing.JFrame;

public class ClientTest {
	static Client client;
	static boolean allow;
	public static void main(String[] args) {
//		allow = false;
		Login login;
		login = new Login();
//		while(true){
//			if (allow == true) runClient();
//			allow = false;
//		}

	}
	
	public static void runClient(){
		client = new Client("127.0.0.1");
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startRunning();		
	}

}
