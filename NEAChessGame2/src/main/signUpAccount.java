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


public class signUpAccount extends JFrame implements ActionListener{
	
	JFrame frame = new JFrame();
	JButton signUp = new JButton("Sign up");
	JTextField userNameField = new JTextField();
	JPasswordField userPassField = new JPasswordField();
	JLabel userNameLabel = new JLabel("Username");
	JLabel userPassLabel = new JLabel("Password");
	JLabel chessLabel = new JLabel("CHESS");
	
	JButton signUpButton = new JButton("Sign Up");
	
	Color background = new Color(21, 37, 35);
	ImageIcon cornerIcon;
	
	String url = "jdbc:mysql://localhost:3306/chessloginsystem";
	String uName = "root";
	String uPass = "pass";
	
	signUpAccount(){
		
		cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
		
		userNameLabel.setBounds(25,100,75,30);
		userPassLabel.setBounds(25,190,75,30);
		userNameLabel.setForeground(Color.white);
		userPassLabel.setForeground(Color.white);
		
		chessLabel.setBounds(80,40,150,50);
		chessLabel.setForeground(Color.WHITE);
		chessLabel.setFont(new Font("Georgia", Font.BOLD, 40));
		
		userNameField.setBounds(25,130,250,50);
		userPassField.setBounds(25,220,250,50);
		
		signUpButton.setBounds(110,320,90,40);
		signUpButton.addActionListener(this);
		signUpButton.setFocusable(false);
		signUpButton.setBackground(Color.WHITE);
		signUpButton.setForeground(Color.BLACK);
		signUpButton.setBorderPainted(false);
		
		add(chessLabel);
		
		add(userNameField);
		add(userPassField);
		
		add(userNameLabel);
		add(userPassLabel);
		
		add(signUpButton);
		
		setIconImage(cornerIcon.getImage());
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(320,480);
		setLayout(null);
		setLocationRelativeTo(null);
		setVisible(true);
		getContentPane().setBackground(background);
	}

	@Override
	public void actionPerformed(ActionEvent g) {
		// TODO Auto-generated method stub
		if(g.getSource()==signUpButton) {
			String userName = userNameField.getText();
			String userPass = String.valueOf(userPassField.getPassword());
			
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				
				String query = "INSERT INTO userdetails (UserName, UserPassword, UserGames, UserWins, UserElo) VALUES (?, ?, ?, ?,?)";
				Connection connect = DriverManager.getConnection(url,uName,uPass);
				PreparedStatement preparedStatement = connect.prepareStatement(query);
				
	            preparedStatement.setString(1, userName);
	            preparedStatement.setString(2, userPass);
	            preparedStatement.setInt(3, 0);
	            preparedStatement.setInt(4, 0);
	            preparedStatement.setInt(5, 0);
	            
	            preparedStatement.executeUpdate();
				//ResultSet results = preparedStatement.executeQuery();		
				
	        }
			
			catch(SQLException e) {
				e.printStackTrace();
			}
			dispose();
		}
	}
	

}
