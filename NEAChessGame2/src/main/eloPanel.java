package main;

import java.awt.*;
import java.awt.geom.Path2D;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

public class eloPanel extends JPanel implements Runnable{
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	Color background = new Color(17, 48, 44);
	final int FPS = 60;
	Thread gameThread;
	
	ChessBoard board = new ChessBoard();
	Mouse mouse = new Mouse();
	
	boolean eloVisible = false;
    
	String url = "jdbc:mysql://localhost:3306/chessloginsystem";
	String uName = "root";
	String uPass = "pass";
	String AccountElo = null;
	int puzzleGames,puzzleWins;
	float winPercentage;
	int elo;
	int num = 0, counter = 0;
	int timed = 180;
	private predictedElo parentFrame;
	boolean running = true;
	
	public eloPanel (predictedElo frame) {
		
		this.parentFrame = frame;
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(background);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setLayout(null);
		elo = returnPuzzleResults();
	}


	
	private void update() {
		
		if(revealElo()) {
			eloVisible = true;
		}
		if(returnMain()) {
			optionsMenu OptionsMenu = new optionsMenu();
			running = false;
			parentFrame.dispose();
			
		}
		
	}
	private boolean revealElo() {
		if(mouse.pressed) {
			if(mouse.x >= 100 && mouse.x <= 530 && mouse.y >= 100 && mouse.y <= 320) {
				return true;
			}
		}
		return false;
	}
	private int returnPuzzleResults() {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String query = "SELECT userElo FROM puzzleattempts WHERE userID = ?";
			Connection connect = DriverManager.getConnection(url,uName,uPass);
			PreparedStatement preparedStatement = connect.prepareStatement(query);
			preparedStatement.setInt(1, LoginSystem.userID);
			ResultSet results = preparedStatement.executeQuery();

			int tempElo = 0;
			
			while(results.next()) {
		        tempElo = results.getInt("userElo");
			}
			
			System.out.println(tempElo);
			return tempElo;
        }
		
		catch(SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	private boolean returnMain() {
		if(timed < 1 && mouse.pressed) {
			if(mouse.x >= 20 && mouse.x <= 370 && mouse.y >= 400 && mouse.y <= 450) {
				return true;
			}
		}
		return false;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		if(eloVisible == false) {
			g2.setColor(Color.WHITE);
			g2.fillRect(100, 130, 430, 220);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 60));
			g2.drawString("REVEAL ELO", 120, 250);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(100, 130, 430, 220);
			g2.setStroke(new BasicStroke(1));
			if(mouse.x >= 100 && mouse.x <= 530 && mouse.y >= 100 && mouse.y <= 320) {
				g2.setColor(Color.gray);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
				g2.fillRect(100, 130, 430, 220);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			}
		}
		else {
			if(elo == num) {
				timed -= 1;
				if(counter == 30) {
					counter += 1;
				}else if(counter == 60) {
					counter = 0;
				}else{
					counter += 1;
				}
				if(counter > 29) {
					g2.setColor(Color.GREEN);
				}else {
					g2.setColor(Color.WHITE);
				}
			}else {
				g2.setColor(Color.WHITE);
			}
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 80));			
			if (num != elo) {
				int dif = elo - num;
				if(dif > 1000) {
					num += 20;
				}else if(dif > 500) {
					num += 15;
				}else if(dif> 300){
					num += 10;
				}else if(dif> 100) {
					num += 3;
				}else {
					num += 1;
				}
				g2.drawString(Integer.toString(num), 50, 240);
			}else {
				g2.drawString(Integer.toString(elo), 50, 240);
			}
				
			g2.fillRect(450, 0, 10, 480);
			
			int x1 = 380, y1 = 380 - num/10;
	        int x2 = 380, y2 = 480 - num/10;
	        int x3 = 440, y3 = 430 - num/10;
	        
	        Path2D triangle = new Path2D.Double();
	        triangle.moveTo(x1, y1);
	        triangle.lineTo(x2, y2);
	        triangle.lineTo(x3, y3);
	        triangle.closePath();  

	        g2.fill(triangle);
	        
	        for(int i = 0; i < 4100; i += 500) {
        		g2.setFont(new Font("Palatino Linotype", Font.BOLD, 25));
        		g2.drawString(Integer.toString(i), 500, 440 - i/10);
	        }
	        if(timed < 1) {
				g2.setColor(Color.WHITE);
				g2.fillRect(20, 400, 350, 50);
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("Palatino Linotype", Font.BOLD, 40));
				g2.drawString("MAIN MENU", 65, 440);
				g2.setColor(Color.BLACK);
				g2.setStroke(new BasicStroke(3));
				g2.drawRect(20, 400, 350, 50);
				g2.setStroke(new BasicStroke(1));
				if(mouse.x >= 20 && mouse.x <= 370 && mouse.y >= 400 && mouse.y <= 450) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(20, 400, 350, 50);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
				}
	        }
		}
		
	}
	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	@Override
	public void run() {
	    double drawInterval = 1000000000 / FPS;
	    double delta = 0;
	    long lastTime = System.nanoTime();
	    long currentTime;

	    while (running) { 
	        currentTime = System.nanoTime();
	        delta += (currentTime - lastTime) / drawInterval;
	        lastTime = currentTime;

	        if (delta >= 1) {
	            update();
	            repaint();
	            delta--;
	        }
	    }
	}

}
