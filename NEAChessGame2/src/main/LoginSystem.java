package main;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;


public class LoginSystem extends JFrame implements ActionListener{
	JFrame frame = new JFrame();
	JButton loginButton = new JButton("Login");
	JButton signUpButton = new JButton("Sign up");
	JTextField userNameField = new JTextField();
	JPasswordField userPassField = new JPasswordField(); 
	JLabel userNameLabel = new JLabel("Username");
	JLabel userPassLabel = new JLabel("Password");
	JLabel message = new JLabel();
	String url = "jdbc:mysql://localhost:3306/chessloginsystem";
	String uName = "root";
	String uPass = "pass";
	
	ImageIcon chessBackground;
	ImageIcon cornerIcon;
	
	JLabel image1 = new JLabel();
	JLabel image2 = new JLabel();
	
	Color background = new Color(121, 165, 83);
	
	JLabel chessLabel = new JLabel("CHESS");
	public static String name;
	public static String pass;
	public static int userID;
	
	LoginSystem(){
			
		chessBackground = new ImageIcon(getClass().getResource("/images/chessbackround.png"));
		cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
		
		userNameLabel.setBounds(335,135,300,30);
		userPassLabel.setBounds(335,210,300,30);
		userNameLabel.setFont(new Font("Georgia", Font.BOLD, 20));
		userPassLabel.setFont(new Font("Georgia", Font.BOLD, 20));
		message.setBounds(390,270,220,50);
		
		chessLabel.setBounds(400,70,150,50);
		chessLabel.setForeground(Color.BLACK);
		chessLabel.setFont(new Font("Georgia", Font.BOLD, 40));
		
		image1.setIcon(chessBackground);
		image1.setBounds(0,0,320,480);
		
		userNameField.setBounds(335,165,270,30);
		userPassField.setBounds(335,240,270,30);
		
		loginButton.setBounds(360,325,90,40);
		loginButton.addActionListener(this);
		loginButton.setFocusable(false);
		loginButton.setBackground(Color.BLACK);
		loginButton.setForeground(Color.WHITE);
		loginButton.setBorderPainted(false);
		signUpButton.setBounds(490,325,90,40);
		signUpButton.addActionListener(this);
		signUpButton.setFocusable(false);
		signUpButton.setBackground(Color.BLACK);
		signUpButton.setForeground(Color.WHITE);
		signUpButton.setBorderPainted(false);
		
		add(userNameLabel);
		add(userPassLabel);
		add(chessLabel);
				
		add(userNameField);
		add(userPassField);
		
		add(loginButton);
		add(signUpButton);		
		
		add(message);
		
		add(image1);
		
		setIconImage(cornerIcon.getImage());
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		setLayout(null);
		setLocationRelativeTo(null);
		setVisible(true);
		getContentPane().setBackground(background);
		
	}
	

	public void userFound() {
		message.setText("");
		userID = getID();
		optionsMenu options = new optionsMenu();
		dispose();
		
	}
	public void notFound() {
		message.setForeground(Color.RED);
		message.setFont(new Font("Arial", Font.BOLD, 14));
		message.setText("INCORRECT DETAILS");	
	}
	public int getID() {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String query = "SELECT userID FROM userdetails WHERE UserName = ? AND UserPassword = ?";
			Connection connect = DriverManager.getConnection(url,uName,uPass);
			PreparedStatement preparedStatement = connect.prepareStatement(query);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, pass);
			ResultSet results = preparedStatement.executeQuery();
			
            if (results.next()) {
                int firstValue = results.getInt("userID");
                return firstValue;
            }
        }
		
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
	}

	@Override
	public void actionPerformed(ActionEvent g) {
		// TODO Auto-generated method stub
		if(g.getSource()==signUpButton) {
			signUpAccount signUpAccount = new signUpAccount();
		}
		if(g.getSource()==loginButton) {
			String userName = userNameField.getText();
			String userPass = String.valueOf(userPassField.getPassword());
			name = userName;
			pass = userPass;
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				String query = "SELECT * FROM userdetails WHERE UserName = ? AND UserPassword = ?";
				Connection connect = DriverManager.getConnection(url,uName,uPass);
				PreparedStatement preparedStatement = connect.prepareStatement(query);
				preparedStatement.setString(1, userName);
				preparedStatement.setString(2, userPass);
				ResultSet results = preparedStatement.executeQuery();
							
				if (results.next()) {
	                	userFound();
	                } else {
	                    notFound();
	            }
	        }
			
			catch(SQLException e) {
				e.printStackTrace();
			}
			
		
		}
	}
	
}