package clientSide;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Login extends JFrame{

	private JTextArea window;
	private JTextField username;
	private JTextField password;
	private JButton login;
	
	public Login() {
		super("Login Information");
		username = new JTextField();
		password = new JTextField();
		window = new JTextArea();
		login = new JButton();
		login.setText("Login");
		add(username, BorderLayout.NORTH);
		add(new JScrollPane(window), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	
	public static void main(String[] args){
		new Login();
	}

}
