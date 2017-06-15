package clientSide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Login extends JFrame {
	JButton login;
	JPanel loginpanel;
	JTextField txuser;
	JTextField pass;
	JButton newUser;
	JLabel username;
	JLabel password;


	public Login(){
		super("Login Screen");

		login = new JButton("Login");
		loginpanel = new JPanel();
		txuser = new JTextField(15);
		pass = new JPasswordField(15);
		newUser = new JButton("New User?");
		username = new JLabel("User - ");
		password = new JLabel("Pass - ");

		setSize(300,200);
		setLocation(500,280);
		loginpanel.setLayout (null); 


		txuser.setBounds(70,30,150,20);
		pass.setBounds(70,65,150,20);
		login.setBounds(110,100,80,20);
		newUser.setBounds(110,135,80,20);
		username.setBounds(20,28,80,20);
		password.setBounds(20,63,80,20);

		loginpanel.add(login);
		loginpanel.add(txuser);
		loginpanel.add(pass);
		loginpanel.add(newUser);
		loginpanel.add(username);
		loginpanel.add(password);

		getContentPane().add(loginpanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		Writer writer = null;
		File check = new File("userPass.txt");
		if(check.exists()){

			//Checks if the file exists. will not add anything if the file does exist.
		}else{
			try{
				File texting = new File("userPass.txt");
				writer = new BufferedWriter(new FileWriter(texting));
			}catch(IOException e){
				e.printStackTrace();
			}
		}




		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					File file = new File("userPass.txt");
					Scanner scan = new Scanner(file);
					String line = null;
					FileWriter filewrite = new FileWriter(file, true);

					String usertxt = " ";
					String passtxt = " ";
          			String puname = txuser.getText();
          			String ppaswd = pass.getText();


          			if(puname.equals("") && ppaswd.equals("")){
          				JOptionPane.showMessageDialog(null,"Please insert Username and Password");
          			}
          			boolean c = false;
          			while (scan.hasNext()) {
          				usertxt = scan.nextLine();
          				passtxt = scan.nextLine();
						if(puname.equals(usertxt) && ppaswd.equals(passtxt)) {
						    c = true;    
							Client client;
				  				try{
						          	client = new Client("127.0.0.1");
						          	client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						            Thread go = new Thread(new Runnable() {
											@Override
											public void run() {
												client.startRunning();
											}
										});
						          	go.start();
						          					
						          					
						          }catch(Exception ex){
						          		ex.printStackTrace();
						          }
						          		setVisible(false);
						          		break;
						  }
          			}
          			if (c == false) {
          				JOptionPane.showMessageDialog(null,"Wrong Username / Password");
          				txuser.setText("");
          				pass.setText("");
          				txuser.requestFocus();
          			}



          			} catch (IOException d) {
					d.printStackTrace();
				}

			}
		});

		newUser.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				NewUser user = new NewUser();
				dispose();
			}
		});
	} 
}