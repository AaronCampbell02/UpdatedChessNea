package main;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class optionsMenu extends JFrame implements ActionListener {

	JFrame frame = new JFrame();
	
	JButton option1 = new JButton("TWO PLAYER");
	JButton option2 = new JButton("PLAY AI");
	JButton option3 = new JButton("PUZZLES");
	
	JLabel image1 = new JLabel();
	JLabel image2 = new JLabel();
	JLabel image3 = new JLabel();
	JLabel image4 = new JLabel();
	JLabel image5 = new JLabel();
	JLabel image6 = new JLabel();
	JLabel leftBackground = new JLabel();
	
	JLabel chessLabel = new JLabel("CHESS"); 
	
	ImageIcon cornerIcon;
	ImageIcon background;
	ImageIcon png1;
	ImageIcon png2;
	ImageIcon png3;
	ImageIcon png4;
	ImageIcon png5;
	ImageIcon png6;
	
	Color backgroundColor = new Color(121, 165, 83);
	
	optionsMenu(){
		
		cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
		png1 = new ImageIcon(getClass().getResource("/images/image1.png"));
		png2 = new ImageIcon(getClass().getResource("/images/image2.png"));
		png3 = new ImageIcon(getClass().getResource("/images/image3.png"));
		png4 = new ImageIcon(getClass().getResource("/images/image4.png"));
		png5 = new ImageIcon(getClass().getResource("/images/image5.png"));
		png6 = new ImageIcon(getClass().getResource("/images/image6.png"));
		background = new ImageIcon(getClass().getResource("/images/leftbackground.png"));
		
		leftBackground.setIcon(background);
		leftBackground.setBounds(0,0,320,480);
		
		chessLabel.setBounds(235,40,150,50);
		chessLabel.setForeground(Color.WHITE);
		chessLabel.setFont(new Font("Georgia", Font.BOLD, 40));
		
		option1.setBounds(170,120,270,60);
		option1.addActionListener(this);
		option1.setFocusable(false);
		option1.setBackground(Color.WHITE);
		option1.setForeground(Color.BLACK);
		option1.setBorderPainted(false);
		
		option2.setBounds(170,200,270,60);
		option2.addActionListener(this);
		option2.setFocusable(false);
		option2.setBackground(Color.WHITE);
		option2.setForeground(Color.BLACK);
		option2.setBorderPainted(false);
		
		option3.setBounds(170,280,270,60);
		option3.addActionListener(this);
		option3.setFocusable(false);
		option3.setBackground(Color.WHITE);
		option3.setForeground(Color.BLACK);
		option3.setBorderPainted(false);
		
		image1.setIcon(png1);
		image2.setIcon(png2);
		image3.setIcon(png3);
		image4.setIcon(png4);
		image5.setIcon(png5);
		image6.setIcon(png6);
		
		image1.setBounds(440,120,64,64);
		image2.setBounds(440,200,64,64);
		image3.setBounds(440,280,64,64);
		image4.setBounds(100,120,64,64);
		image5.setBounds(100,200,64,64);
		image6.setBounds(100,280,64,64);
		
		add(image1);
		add(image2);
		add(image3);
		add(image4);
		add(image5);
		add(image6);
		
		add(option1);
		add(option2);
		add(option3);
		
		add(chessLabel);
		
		add(leftBackground);
		
		setIconImage(cornerIcon.getImage());
		setTitle("Chess");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640,480);
		setLayout(null);
		setLocationRelativeTo(null);
		setVisible(true);
		getContentPane().setBackground(backgroundColor);
	}
	
	@Override
	public void actionPerformed(ActionEvent g) {
		// TODO Auto-generated method stub
		if(g.getSource()==option1) {
			dispose();
			TwoPlayerChessGame TwoPlayerChessGame = new TwoPlayerChessGame();
		}
		if(g.getSource()==option2) {
			dispose();
			aiGame aiGame = new aiGame();
		}
		if(g.getSource()==option3) {
			dispose();
			PuzzlesGame PuzzlesGame = new PuzzlesGame();
		}
		
		
	}

}
