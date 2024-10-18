package main;

import java.awt.AlphaComposite;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import piece.bishop;
import piece.king;
import piece.knight;
import piece.pawn;
import piece.piece;
import piece.queen;
import piece.rook;
import piece.x;

public class PuzzlePanel extends JPanel implements Runnable{
	public static final int WIDTH = 1920;
	public static final int HEIGHT = 1080;
	public static boolean active;
	public boolean closeGame = false;
	Color background = new Color(21, 37, 35);
	final int FPS = 60;
	Thread gameThread;
	
	ChessBoard board = new ChessBoard();
	Mouse mouse = new Mouse();
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	int otherColor = BLACK;
	int moves = 0;
	
	public static ArrayList<piece> pieces = new ArrayList<>();
	public static ArrayList<piece> simPieces = new ArrayList<>();
	ArrayList<piece> promoPieces = new ArrayList<>();
	
	piece activeP;
	public static piece castlingP;
	piece promotionP,checkingP;
	
	boolean canMove;
	boolean validSquare;
	boolean promotion;
	boolean gameOver = false;
	boolean stalemate = false;
	boolean draw = false;
	boolean running = true;
	boolean delay = false;
	private PuzzlesGame parentFrame;
	boolean failed = false;
	boolean whiteFirst;
	boolean userGo = true;
	
	String url = "jdbc:mysql://localhost:3306/chessloginsystem";
	String uName = "root";
	String uPass = "pass";
	String nameElo = "";
	String AccountName = null;
	String AccountElo = null;
	
	BufferedImage WhiteLogo;
    private JTextPane logTextPane;
    private StyledDocument doc;
    String collumDraw;
    String rowDraw;
    String typeOfPiece;
    String colNum;
    String rowNum;
    String resultString;
	int holdRow = 0;
	JSlider difficultySlider;
    
	String puzzleId;
	String fen;
	String puzzleMovesList;
	String rating;
	int puzzleRating;
	int userRating;
	int dif,newCol,newRow;
	String filePath = "src//images//lichess.csv";
	ArrayList<String> puzzleIds = new ArrayList<>();
	ArrayList<Integer> ratings = new ArrayList<>();
	public String[] puzzleMoves;
	public piece hittingP,animateP;
	int promoCol;
	int promoRow;
	int puzzles = 0;
	int successfulPuzzles = 0;
	
	public PuzzlePanel(PuzzlesGame parentFrame) {
		this.parentFrame = parentFrame;
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(background);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		setLayout(null);
		
		try {
			WhiteLogo = ImageIO.read(getClass().getResourceAsStream("/images/WhiteLogo.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		createScroll();
		createSlider();
        
		nameElo = returnUser();
		System.out.println(nameElo);
		String[] parts = nameElo.split(",");
		AccountName = parts[0];
		AccountElo = parts[1];
		AccountName = checkLength(AccountName);
	}
	
	public void setPieces() {
		pieces.clear();
		simPieces.clear();
		puzzleRating = 0;
		userRating = difficultySlider.getValue();
		if(userRating < 400) {
			userRating = 400;
		}
        dif = Math.abs(userRating - puzzleRating);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while (dif > 100) {
            	line = br.readLine();
                if (firstLine) {
                    firstLine = false;
                }else {	                
	                String[] values = line.split(",");
	
	                puzzleId = values[0];
	                fen = values[1];
	                puzzleMovesList = values[2];
	                rating = values[3];
	                puzzleRating = Integer.valueOf(rating);
	                dif = Math.abs(userRating - puzzleRating);
	                if (puzzleIds.contains(puzzleId)) {
	                	dif = 101;
	                }	                
               }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
       puzzleIds.add(puzzleId);
       String[] values = fen.split("/");
       String sent;
       int j = 0;
       for(int i =0; i < values.length; i ++) {
    	   j = 0;
    	   sent = values[i];
    	   int counter = 0;
    	   while(counter < sent.length() && j < 8) {
               switch (sent.charAt(counter)) {
               case 'P':
            	   pieces.add(new pawn(WHITE,j,i,2));
                   break;
               case 'N':
            	   pieces.add(new knight(WHITE,j,i,2));
                   break;
               case 'B':
            	   pieces.add(new bishop(WHITE,j,i,2));
                   break;
               case 'R':
            	   pieces.add(new rook(WHITE,j,i,2));
                   break;
               case 'Q':
            	   pieces.add(new queen(WHITE,j,i,2));
                   break;
               case 'K':
            	   pieces.add(new king(WHITE,j,i,2));
                   break;               
               case 'p':
            	   pieces.add(new pawn(BLACK,j,i,2));
                   break;
               case 'n':
            	   pieces.add(new knight(BLACK,j,i,2));
                   break;
               case 'b':
            	   pieces.add(new bishop(BLACK,j,i,2));
                   break;
               case 'r':
            	   pieces.add(new rook(BLACK,j,i,2));
                   break;
               case 'q':
            	   pieces.add(new queen(BLACK,j,i,2));
                   break;
               case 'k':
            	   pieces.add(new king(BLACK,j,i,2));
                   break;
               default:
            	   j += Character.getNumericValue(sent.charAt(counter)) - 1;
               }
               j += 1;
               counter += 1;
    	   }
       }
       sent = values[7];
       String[] details = sent.split(" ");
       if(details[1].equals("w")) {
    	   currentColor = WHITE;
    	   whiteFirst = true;
       }else {
    	   currentColor = BLACK;
    	   whiteFirst = false;
       }
       copyPiece(pieces,simPieces);
       puzzleMoves =  puzzleMovesList.split(" ");
	}
	private void copyPiece(ArrayList<piece> source, ArrayList<piece> target) {
		
		target.clear();
		for(int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
	
	private void update() {
				
		if(moves % 2 == 0 && gameOver == false) {
			userGo = false;
			String currentMove = puzzleMoves[moves];
			int tempCol = returnColNum(currentMove.charAt(0));
			int tempRow = currentMove.charAt(1) - '0';
			newCol = returnColNum(currentMove.charAt(2));
			newRow = currentMove.charAt(3) - '0';
			
			tempRow = 8 - tempRow;
			newRow = 8 - newRow;
			
			hittingP = findPiece(newCol,newRow);
			activeP = findPiece(tempCol,tempRow);
			
			activeP.col = newCol;
			activeP.row = newRow;
			activeP.updatePosition();
			copyPiece(simPieces,pieces);
			
			if(hittingP!= null) {
				simPieces.remove(hittingP);
				copyPiece(simPieces,pieces);
			}
			if(canPromote()) {
				simPieces.add(new queen(currentColor,newCol,newRow,2));
				simPieces.remove(activeP);
				copyPiece(simPieces,pieces);
				activeP = findPiece(newCol,newRow); 
			}
			if(castlingP != null) {
				checkCastle();
				castlingP.updatePosition();
				resultString = moves + "     " + currentColor + " castled";
			}
			if(isKingInCheck(true) && isCheckmate()) {
				gameOver = true;
			}
			if(isStalemate()) {
				gameOver = true;
			}
			activeP = null;
			copyPiece(simPieces,pieces);
			changeTurn();						
			moves += 1;
		}else {
			userGo = true;
			if(returnToMenu()) {
				float val = averageCalc();
				int finalElo = (int) (puzzles/successfulPuzzles * val);
				System.out.println("final elo is " + finalElo);
				updateDatabase(finalElo);
				predictedElo predictedElo = new predictedElo();
				running = false;
				parentFrame.dispose();
			}
			if(promotion) {
				promoting();            
			}else if(nextPuzzle()) {
				resetGameState();
				setPieces();
			}
			if(gameOver == false) {
				if(mouse.pressed){
					if(activeP == null) {
						
						for(piece p: simPieces) {
							if(p.color == currentColor && p.col == (mouse.x-50)/ChessBoard.SQUARE_SIZE && p.row == (mouse.y - 50)/ChessBoard.SQUARE_SIZE) {
								activeP = p;
							}
						}
					}else {
						simulate();
					}
				}
				if(mouse.pressed == false) {
					if(activeP != null) {
						if (validSquare) {
							copyPiece(simPieces,pieces);
							activeP.updatePosition();
							typeOfPiece = activeP.type.toString();
							colNum = Integer.toString(activeP.col);
							colNum = returnCol(colNum);
							holdRow = activeP.row+1;
							holdRow = 9 - holdRow;
							rowNum = Integer.toString(holdRow);
							resultString = moves + "     " + typeOfPiece + " to " + colNum + rowNum;
							if(castlingP != null) {
								checkCastle();
								castlingP.updatePosition();
								resultString = moves + "     " + currentColor + " castled";
							}
							if(canPromote()) {
								promotion = true;
							}
							addMove(resultString);
							String currentMove = puzzleMoves[moves];
							int tempCol = returnColNum(currentMove.charAt(0));
							int tempRow = currentMove.charAt(1) - '0';
							int newCol = returnColNum(currentMove.charAt(2));
							int newRow = currentMove.charAt(3) - '0';
							
							System.out.println(newCol);
							System.out.println(newRow);
							
							tempRow = 8 - tempRow;
							newRow = 8 - newRow;
							
							
							if(isKingInCheck(true) && isCheckmate()) {
								gameOver = true;
								ratings.add(puzzleRating);
								System.out.println("correct move");
								moves += 1;
							}else if(isPiece(tempCol,tempRow) == false && isPiece(newCol,newRow) && findPiece(newCol,newRow) == activeP) {
								System.out.println("correct move");
								moves += 1;
							}
							else {
								failed = true;
								gameOver = true;
								updateValues(false);
							}
							if(moves >= puzzleMoves.length-1) {
								gameOver = true;
								ratings.add(puzzleRating);
								if(failed == false) {
									System.out.println("correct");
									updateValues(true);
								}
							}
							activeP = null;
							changeTurn();
						}else {
							copyPiece(pieces,simPieces);
							activeP.resetPosition();
							activeP = null;
						}
					}
				}
			}
		}
	}
	private void simulate() {
		
		canMove = false;
		validSquare = false;
		
		copyPiece(pieces,simPieces);
		
		if(castlingP != null) {
			castlingP.col = castlingP.preCol;
			castlingP.x = castlingP.getX(castlingP.col);
			castlingP = null;
		}
		
		promoCol = activeP.preCol;
		promoRow = activeP.preRow;
		
		activeP.x = mouse.x - ChessBoard.HALF_SQUARE_SIZE;
		activeP.y = mouse.y - ChessBoard.HALF_SQUARE_SIZE;
		activeP.col = activeP.getCol(activeP.x);
		activeP.row = activeP.getRow(activeP.y);
		
		if(activeP.canMove(activeP.col, activeP.row)) {
			canMove = true;
			if(activeP.hittingP != null) {
				simPieces.remove(activeP.hittingP);
			}
			if(isIllegal(activeP) == false && opponentCaptureKing() == false) {
				validSquare = true;
			}
		}		
		
	}
    private void resetGameState() {
    	
    	clearTextPane();
        gameOver = false;
        stalemate = false;
        promotion = false;
        failed = false;
        moves = 0;
        currentColor = WHITE;
        otherColor = BLACK;
        setPieces();
        
    }
	private void changeTurn() {
		if (currentColor == WHITE) {
			currentColor = BLACK;
			for(piece p : GamePanel.simPieces) {
				if(p.color == BLACK) {
					p.twoStepped = false;
				}
			}
		}else {
			currentColor = WHITE;
			for(piece p : GamePanel.simPieces) {
				if(p.color == WHITE) {
					p.twoStepped = false;
				}
			}
		}

	}
	private void checkCastle() {
		if(castlingP != null) {
			if(castlingP.col == 0) {
				castlingP.col += 3;
			}else if(castlingP.col == 7) {
				castlingP.col -= 2;
			}
			castlingP.x = castlingP.getX(castlingP.col);
		}
	}
	private boolean canPromote() {
		if(userGo) {
			if(activeP.type == Type.PAWN) {
				if(currentColor == WHITE && activeP.row == 0) {
					promotionP = activeP;
					promoPieces.clear();
					promoPieces.add(new queen(currentColor,activeP.col,activeP.row,2));
					promoPieces.add(new knight(currentColor,activeP.col,activeP.row-1,2));
					promoPieces.add(new rook(currentColor,activeP.col,activeP.row-2,2));
					promoPieces.add(new bishop(currentColor,activeP.col,activeP.row-3,2));
					promoPieces.add(new x(currentColor,activeP.col,activeP.row-4,2));
					return true;
				}else if(currentColor == BLACK && activeP.row == 7) {
					promotionP = activeP;
					promoPieces.clear();
					promoPieces.add(new queen(currentColor,activeP.col,activeP.row,2));
					promoPieces.add(new knight(currentColor,activeP.col,activeP.row+1,2));
					promoPieces.add(new rook(currentColor,activeP.col,activeP.row+2,2));
					promoPieces.add(new bishop(currentColor,activeP.col,activeP.row+3,2));
					promoPieces.add(new x(currentColor,activeP.col,activeP.row+4,2));
					return true;
				}
			}
		}
		else {
			if(activeP.type == Type.PAWN) {
				if(currentColor == WHITE && activeP.row == 0) {
					return true;
				}else if(currentColor == BLACK && activeP.row == 7) {
					return true;
				}
			}
		}
		
		return false;
	}
	private boolean returnToMenu() {
		if(mouse.pressed) {
			if(mouse.x >= 1100 && mouse.x <= 1450 && mouse.y >= 850 && mouse.y <= 950) {
				return true;
			}
		}

		return false;
	}
	private void promoting() {
		
		if(mouse.pressed) {
			for(piece p: promoPieces) {
				if(p.col == (mouse.x-50)/ChessBoard.SQUARE_SIZE && p.row == (mouse.y - 50)/ChessBoard.SQUARE_SIZE) {
					switch(p.type) {
					case QUEEN: simPieces.add(new queen(currentColor,promotionP.col,promotionP.row,2)); break;
					case KNIGHT: simPieces.add(new knight(currentColor,promotionP.col,promotionP.row,2)); break;
					case ROOK: simPieces.add(new rook(currentColor,promotionP.col,promotionP.row,2)); break;
					case BISHOP: simPieces.add(new bishop(currentColor,promotionP.col,promotionP.row,2)); break;
					case X:promotion = false;break;
					default:break;
					}
					if(promotion != false) {
						simPieces.remove(promotionP);
						copyPiece(simPieces, pieces);
						promotion = false;
						if(isKingInCheck(true) && isCheckmate()) {
							gameOver = true;
							ratings.add(puzzleRating);
						}
						
						changeTurn();
					}
					else {
						simPieces.add(new pawn(currentColor,promoCol,promoRow,2));
						if(currentColor == WHITE) {
							otherColor = BLACK;
						}else {
							otherColor = WHITE;
						}
						
						if(piece.returnP != null) {
							switch(piece.typeOfPiece) {
							case "queen": simPieces.add(new queen(otherColor,promotionP.col,promotionP.row,2));break;
							case "rook": simPieces.add(new rook(otherColor,promotionP.col,promotionP.row,2));break;
							case "knight": simPieces.add(new knight(otherColor,promotionP.col,promotionP.row,2));break;
							case "bishop": simPieces.add(new bishop(otherColor,promotionP.col,promotionP.row,2));break;
							case "king": simPieces.add(new king(otherColor,promotionP.col,promotionP.row,2));break;
							default:break;
							}
							piece.returnP = null;
						}
						simPieces.remove(promotionP);
						copyPiece(simPieces,pieces);
						promotionP = null;
						activeP = null;
					}
					
				}
			}
		}
		
	}
	private boolean isIllegal(piece king) {
		if(king.type == Type.KING) {
			for(piece p: simPieces) {
				if(p != king && p.color != king.color && p.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
		return false;
	}
	private boolean isKingInCheck(boolean team) {
		
		piece king = getKing(team); 
		for(piece p: simPieces) {
			if(p.canMove(king.col, king.row)) {
				checkingP = p;
				return true;
			}
		}
		checkingP = null;
		return false;
	}
	private piece getKing(boolean opponent){
		piece king = null;
		for(piece p: simPieces) {
			if(opponent) {
				if(p.type == Type.KING && p.color != currentColor) {
					king = p;
				}
			}
			else {
				if(p.type == Type.KING && p.color == currentColor) {
					king = p;
				}
			}
		}
		
		
		return king;
	}
	private boolean opponentCaptureKing() {
		
		piece King = getKing(false);
		
		for(piece p: simPieces) {
			if(p.color != King.color && p.canMove(King.col, King.row)) {
				return true;
			}
		}
		
		return false;
	}
	private boolean isCheckmate() {
		
		piece king = getKing(true);
		
		if (kingCanMove(king)) {
			return false;
		}else {
			int colDiff = Math.abs(checkingP.col - king.col);
			int rowDiff = Math.abs(checkingP.row - king.row);
			if(colDiff == 0) {
				if(checkingP.row < king.row) {
					for(int row = checkingP.row; row <= king.row; row ++) {
						for(piece p: simPieces) {
							if(p != king && p.color != currentColor && p.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					for(int row = checkingP.row; row >= king.row; row --) {
						for(piece p: simPieces) {
							if(p != king && p.color != currentColor && p.canMove(checkingP.col, row)) {
								return false;
							}
						}
					}
				}
			}else if(rowDiff == 0) {
				if(checkingP.col < king.col) {
					for(int col = checkingP.col; col <= king.col; col ++) {
						for(piece p: simPieces) {
							if(p != king && p.color != currentColor && p.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
				if(checkingP.col > king.col) {
					for(int col = checkingP.col; col >= king.col; col --) {
						for(piece p: simPieces) {
							if(p != king && p.color != currentColor && p.canMove(col, checkingP.row)) {
								return false;
							}
						}
					}
				}
			}else if(colDiff == rowDiff) {
				if(checkingP.row < king.row) {
					if(checkingP.col < king.col) {
						for(int col = checkingP.col, row = checkingP.row ; col <= king.col;col++, row++) {
							for(piece p: simPieces) {
								if(p != king && p.color != currentColor && p.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						for(int col = checkingP.col, row = checkingP.row ; col >= king.col;col--, row++) {
							for(piece p: simPieces) {
								if(p != king && p.color != currentColor && p.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
				if(checkingP.row > king.row) {
					if(checkingP.col < king.col) {
						for(int col = checkingP.col, row = checkingP.row ; col <= king.col;col++, row--) {
							for(piece p: simPieces) {
								if(p != king && p.color != currentColor && p.canMove(col, row)) {
									return false;
								}
							}
						}
					}
					if(checkingP.col > king.col) {
						for(int col = checkingP.col, row = checkingP.row ; col >= king.col;col--, row--) {
							for(piece p: simPieces) {
								if(p != king && p.color != currentColor && p.canMove(col, row)) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	private boolean isStalemate() {
		
		piece king = getKing(true);
		if(kingCanMove(king) == false && isKingInCheck(true) == false) {
			for(piece p: simPieces) {
				if(p.color != currentColor && p != king) {
					for(int col = 0; col < 9; col++) {
						for(int row = 0; row < 9; row++) {
							if(p.canMove(col, row)) {
								return false;
							}
						}
					} 
				}
			}
		}else {
			return false;
		}

		
		stalemate = true;
		return true;
	}
	private boolean kingCanMove(piece king) {
		
		if(isValidMove(king,-1,-1)) {return true;}
		if(isValidMove(king,0,-1)) {return true;}
		if(isValidMove(king,1,-1)) {return true;}
		if(isValidMove(king,-1,1)) {return true;}
		if(isValidMove(king,-1,0)) {return true;}
		if(isValidMove(king,1,0)) {return true;}
		if(isValidMove(king,1,1)) {return true;}
		if(isValidMove(king,0,1)) {return true;}
		
		return false;
	}
	private boolean isValidMove(piece king, int colPlus, int rowPlus) {
		
		boolean isValidMove = false;
		king.col += colPlus;
		king.row += rowPlus;
		if(king.canMove(king.col, king.row)) {
			if(king.hittingP != null) {
				simPieces.remove(king.hittingP);
			}
			if(isIllegal(king) == false) {
			isValidMove = true;
			}
		}
		king.resetPosition();
		copyPiece(pieces, simPieces);
		return isValidMove;
	}
	private String returnUser() {
		String userName = LoginSystem.name;
		System.out.println(userName);
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
			
			int total = 0;
			int counter = 0;
			String userElo = "";
			while(results.next()) {
				total += results.getInt("userElo");
				counter += 1;
			}
			if(counter != 0) {
				userElo = Integer.toString(total/counter);
			}else {
				userElo = "0";
			}
			String value = userName + "," + userElo;
			return value;
        }
		
		catch(SQLException e) {
			e.printStackTrace();
		}
		return "";
		
		
	}
	private String checkLength(String name) {
		
		int length = name.length();
		if(length > 8) {
			name = name.substring(0, 8) + "...";
		}
		return name;
	}
	private void createSlider() {
        difficultySlider = new JSlider(JSlider.VERTICAL, 0, 3200, 0);
        difficultySlider.setBackground(Color.BLACK);
        difficultySlider.setForeground(Color.WHITE);
        difficultySlider.setMajorTickSpacing(800);
        difficultySlider.setMinorTickSpacing(200); 
        difficultySlider.setBounds(1620, 270, 100, 400);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        
        difficultySlider.setLabelTable(difficultySlider.createStandardLabels(800));
        add(difficultySlider);
	}
	private void createScroll() {
		
		logTextPane = new JTextPane();
		logTextPane.setBackground(Color.BLACK);
		logTextPane.setForeground(Color.WHITE);
		logTextPane.setEditable(false);
		logTextPane.setBorder(BorderFactory.createEmptyBorder());
		
        
		doc = logTextPane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(logTextPane);
        scrollPane.setBounds(1150, 270, 325, 400);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        add(scrollPane);
		
        appendColoredText("\n", Color.WHITE,20);
        appendColoredText("     Puzzle started\n", Color.RED,20);
	}
	private String returnCol(String colNum) {
        String letter;

        switch (colNum) {
            case "0":letter = "A";break;
            case "1":letter = "B";break;
            case "2":letter = "C";break;
            case "3":letter = "D";break;
            case "4":letter = "E";break;
            case "5": letter = "F";break;
            case "6":letter = "G";break;
            case "7":letter = "H";break;
            default:letter = ""; break;
        }

        return letter;
	}
	private int returnColNum(char c) {
		int colVal = 0;
		switch (c) { 
	    case 'a': colVal = 0; break;
	    case 'b': colVal = 1; break;
	    case 'c': colVal = 2; break;
	    case 'd': colVal = 3; break;
	    case 'e': colVal = 4; break;
	    case 'f': colVal = 5; break;
	    case 'g': colVal = 6; break;
	    case 'h': colVal = 7; break;
	    default:break;
		}
		return colVal;
	}
	private void addMove(String result) {
		if(currentColor == WHITE) {
			appendColoredText(result +"\n", Color.WHITE,25);
		}else {
			appendColoredText(result +"\n", Color.GRAY,25);
		}		
	}

    private void appendColoredText(String text, Color color,int fontSize) {
        Style style = logTextPane.addStyle("Style", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setFontFamily(style, "Palatino Linotype");
        StyleConstants.setBold(style, true);
        StyleConstants.setFontSize(style, fontSize);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            logTextPane.setCaretPosition(doc.getLength());
        });
    }
    private boolean nextPuzzle() {

    if(mouse.pressed) {
        if(mouse.x >= 1500 && mouse.x <= 1850 && mouse.y >= 850 && mouse.y <= 950) {
        	return true;
        }
    }

    return false;
    }
    private boolean isPiece(int targetCol, int targetRow) {
    	
		for(piece p: PuzzlePanel.simPieces) {
			if(p.col == targetCol && p.row == targetRow) {
				return true;
			}
		}
		return false;
    	

    }
    private piece findPiece(int targetCol, int targetRow) {

		for(piece p: PuzzlePanel.pieces) {
			if(p.col == targetCol && p.row == targetRow) {
				return p;
			}
		}

		return null;

    }
    private void updateDatabase(int finalElo) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			
			String sql = "INSERT INTO puzzleattempts (userID, puzzles, successfullPuzzles, userElo) VALUES (?, ?, ?, ?)";

			Connection connect = DriverManager.getConnection(url, uName, uPass);
			PreparedStatement preparedStatement = connect.prepareStatement(sql);

			preparedStatement.setInt(1, LoginSystem.userID);
			preparedStatement.setInt(2, puzzles);
			preparedStatement.setInt(3, successfulPuzzles);
			preparedStatement.setInt(4, finalElo);

			preparedStatement.executeUpdate();
        }
		
		catch(SQLException e) {
			e.printStackTrace();
		}

    }
    public void updateValues(boolean win) {
    	puzzles += 1;
    	if(win) {
    		successfulPuzzles += 1;
    	}
   	
    }
    public int averageCalc() {
    	int total = 0;
    	for(int i = 0; i < ratings.size(); i ++) {
    		total += ratings.get(i);
    	}
    	if(ratings.size() > 0) {
    		return(total/ratings.size());
    	}else {
    		return 0;
    	}
    }
    private void clearTextPane() {
        if (doc != null) {
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace(); 
            }
        }
        appendColoredText("\n", Color.WHITE,20);
        appendColoredText("     Puzzle started\n", Color.RED,20);
    }
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		board.draw(g2);
		try {
			for (piece p: simPieces) {
				if(p.color != currentColor) {
					p.draw(g2);
				}
			}
			for (piece p1: simPieces) {
				if(p1.color == currentColor && p1 != activeP && p1 != animateP) {
					p1.draw(g2);
				}
			}
			for (piece p2: simPieces) {
				if(p2 == activeP && p2 != animateP) {
					p2.draw(g2);
				}
			}
		} catch (Exception e) {
		    System.out.println("updating array while in use");
		}
		if(activeP != null) {
			if(canMove) {
				if(isIllegal(activeP) || opponentCaptureKing()) {
					g2.setColor(Color.red);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(50+(activeP.col*ChessBoard.SQUARE_SIZE), 50 +(activeP.row*ChessBoard.SQUARE_SIZE),ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
				
					activeP.draw(g2);
				}else {
					g2.setColor(Color.white);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(50+(activeP.col*ChessBoard.SQUARE_SIZE), 50 +(activeP.row*ChessBoard.SQUARE_SIZE),ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
				
					activeP.draw(g2);
				}

			}
		}
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Georgia", Font.BOLD, 60));
		g2.setColor(Color.white);
		if(gameOver == false) {
			if(currentColor == WHITE && promotion == true) {
				g2.drawString("PROMOTION", 1250, 250);
			}else if(currentColor == WHITE) {
				g2.drawString("WHITE'S TURN", 1250, 250);
			}else if(promotion){
				g2.setColor(Color.gray);
				g2.drawString("PROMOTION", 1300, 250);
			}else {
				g2.setColor(Color.gray);
				g2.drawString("BLACK'S TURN", 1250, 250);
			}
			if(checkingP != null) {
				g2.setColor(Color.red);
				g2.drawString("THE KING IS IN CHECK", 1100, 750);
			}
		}
		if(promotion) {
			g2.setColor(Color.white);
			if(currentColor == WHITE) {
				g2.fillRect(promotionP.getX(promotionP.col), promotionP.getY(promotionP.row), ChessBoard.SQUARE_SIZE, 5*ChessBoard.SQUARE_SIZE);
			}else {
				g2.fillRect(promotionP.getX(promotionP.col), promotionP.getY(promotionP.row+1), ChessBoard.SQUARE_SIZE, -5*ChessBoard.SQUARE_SIZE);
			}
		
			for(piece p: promoPieces) {
				g2.drawImage(p.image,p.getX(p.col),p.getY(p.row),
					ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE,null);
			}
			for(piece p1:promoPieces) {
				if(p1.col == (mouse.x-50)/ChessBoard.SQUARE_SIZE && p1.row == (mouse.y - 50)/ChessBoard.SQUARE_SIZE) {
					g2.setColor(Color.gray);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
					g2.fillRect(50+(p1.col*ChessBoard.SQUARE_SIZE), 50 +(p1.row*ChessBoard.SQUARE_SIZE),ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			
					p1.draw(g2);
				}
			}
		
		}

		if(gameOver && failed) {
			g2.setFont(new Font("Georgia", Font.BOLD, 60));
			g2.setColor(Color.RED);
			g2.drawString("FAILED", 1340, 800);
		}
		else if(gameOver) {
			g2.setFont(new Font("Georgia", Font.BOLD, 60));
			g2.setColor(Color.GREEN);
			g2.drawString("YOU WON", 1340, 800);
		}


		g2.setColor(Color.WHITE);
		g2.fillRect(1100, 850, 350, 100);
		g2.fillRect(1500, 850, 350, 100);
		g2.setFont(new Font("Georgia", Font.BOLD, 60));
		g2.setColor(Color.BLACK);
		g2.drawString("QUIT", 1185, 920);
		g2.drawString("NEXT", 1580, 920);
		
		if(mouse.x >= 1100 && mouse.x <= 1450 && mouse.y >= 850 && mouse.y <= 950) {
			g2.setColor(Color.gray);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
			g2.fillRect(1100, 850, 350, 100);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
		if(mouse.x >= 1500 && mouse.x <= 1850 && mouse.y >= 850 && mouse.y <= 950) {
			g2.setColor(Color.gray);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
			g2.fillRect(1500, 850, 350, 100);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
		}
		//draw account details and name in corner
		
		g2.setColor(Color.BLACK);
		g2.fillRect(1300, 20, 600, 150);
		g2.setColor(Color.WHITE);
		g2.drawString(AccountName, 1400,100);
		g2.setFont(new Font("Georgia", Font.BOLD, 20));
		g2.drawString("elo:  " + AccountElo, 1400,130);
		g2.drawImage(WhiteLogo,1770,40,100,100, null);
		
		//Draw row and Col nums
		
		for(int i = 0; i < 9; i++) {
			collumDraw = Integer.toString(i);
			collumDraw = returnCol(collumDraw);
			g2.drawString( collumDraw,(i*ChessBoard.SQUARE_SIZE) +100,1040);
		}
		for(int i = 1; i < 9; i++) {
			rowDraw = Integer.toString(i);
			g2.drawString( rowDraw,20,1070 - (i*ChessBoard.SQUARE_SIZE));
		}
		g2.setFont(new Font("Georgia", Font.BOLD, 30));
		g2.drawString("Difficulty", 1740,350);
		g2.setFont(new Font("Georgia", Font.BOLD, 30));
		g2.drawString(Integer.toString(difficultySlider.getValue()), 1780,460);
		
	}
	public void launchGame() {
		resetGameState();
		gameThread = new Thread(this);
		gameThread.start();
	}
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(running) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime)/drawInterval;
			lastTime = currentTime;
			if(delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}


}
