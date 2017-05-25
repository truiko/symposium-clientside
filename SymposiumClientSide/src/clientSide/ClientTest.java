package clientSide;

import javax.swing.JFrame;

public class ClientTest {

	public static void main(String[] args) {
		Client client;
		client = new Client("127.0.0.1");//64.71.171.62
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.startRunning();

	}

}
