package main;

import java.awt.AlphaComposite;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
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

public class aiPanel extends JPanel implements Runnable{
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
	List<List<Integer>> possibleMoves = new ArrayList<>();
	List<List<Integer>> orriginalPos = new ArrayList<>();
	
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
	private aiGame parentFrame;
	boolean failed = false;
	boolean whiteFirst;
	boolean userGo = true;
	boolean offerDraw = false;
	
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
    
	int userRating;
	int dif,newCol,newRow;
	int boardEval,eval,orriginalEval;
	String filePath = "src//images//lichess.csv";
	public piece hittingP,animateP;
	piece bestPiece;
	int bestCol;
	int bestRow;
	int bestEval;
	public int userEnteredDepth;
	public int savedCol;
	public int savedRow;
	piece savedP;
	int currentEval;
	List<Boolean> movedList = new ArrayList<>();
	List<Boolean> twoSteppedList = new ArrayList<>();
	Boolean forfeit = false;
	JSlider difficultySlider;
	boolean win = false;
	
	final int[][] wPawnPosition = {
        {  0,  0,  0,  0,  0,  0,  0,  0 },
        { 90, 90, 90, 90, 90, 90, 90, 90 },
        { 30, 30, 40, 60, 60, 40, 30, 30 },
        { 10, 10, 20, 40, 40, 20, 10, 10 },
        {  10, 10, 15, 20, 20, 15,  10, 10},
        {  0,  0,  0, 10, 10,  0,  0,  0 },
        {  5, -5, -10,  0,  0, -10, -5,  5 },
        {  0,  0,  0,  0,  0,  0,  0,  0 }
	};

	final int[][] wKnightPosition = {
        { -50, -40, -30, -30, -30, -30, -40, -50 },
        { -40, -20,  0,   5,   5,   0, -20, -40 },
        { -30,   5, 10,  15,  15,  10,   5, -30 },
        { -30,   5, 15,  20,  20,  15,   5, -30 },
        { -30,   5, 15,  20,  20,  15,   5, -30 },
        { -30,   5, 10,  15,  15,  10,   5, -30 },
        { -40, -20,  0,   0,   0,   0, -20, -40 },
        { -50, -40, -30, -30, -30, -30, -40, -50 }
    };

	final int[][] wBishopPosition = {
        { -20, -10, -10, -10, -10, -10, -10, -20 },
        { -10,   0,   0,   0,   0,   0,   0, -10 },
        { -10,   0,   5,  10,  10,   5,   0, -10 },
        { -10,   5,   5,  10,  10,   5,   5, -10 },
        { -10,   0,  10,  15,  15,  10,   0, -10 },
        { -10,  10,  10,  10,  10,  10,  10, -10 },
        { -10,   5,   0,   0,   0,   0,   5, -10 },
        { -20, -10, -10, -10, -10, -10, -10, -20 }
    };

	final int[][] wRookPosition = {
        {  0,  0,  0,  0,  0,  0,  0,  0 },
        {  5, 20, 20, 20, 20, 20, 20,  5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        {  0,  0,  0,  5,  5,  0,  0,  0 }
    };

	final int[][] wQueenPosition = {
        { -20, -10, -10,  -5,  -5, -10, -10, -20 },
        { -10,   0,   0,   0,   0,   0,   0, -10 },
        { -10,   0,   5,   5,   5,   5,   0, -10 },
        {  -5,   0,   5,   5,   5,   5,   0,  -5 },
        {   0,   0,   5,   5,   5,   5,   0,  -5 },
        { -10,   5,   5,   5,   5,   5,   0, -10 },
        { -10,   0,   5,   0,   0,   0,   0, -10 },
        { -20, -10, -10,  -5,  -5, -10, -10, -20 }
    };

	final int[][] wKingPosition = {
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -20, -30, -30, -40, -40, -30, -30, -20 },
	    { -10, -20, -20, -20, -20, -20, -20, -10 },
	    {  20,  20,   0,   0,   0,   0,  20,  20 },
	    {  20,  20,  30,   0,   0,  10,  30,  20 }
	};
	final int[][] bPawnPosition = {
        {  0,  0,  0,  0,  0,  0,  0,  0 },
        {  5, -5, -10,  0,  0, -10, -5,  5 },
        {  0,  0,  0, 10, 10,  0,  0,  0 },
        {  10,  10, 15, 20, 20, 15,  10,  10 },
        { 10, 10, 20, 40, 40, 20, 10, 10 },
        { 30, 30, 40, 60, 60, 40, 30, 30 },
        { 90, 90, 90, 90, 90, 90, 90, 90 },
        {  0,  0,  0,  0,  0,  0,  0,  0 }
    };

	final int[][] bKnightPosition  = {
        { -50, -40, -30, -30, -30, -30, -40, -50 },
        { -40, -20,  0,   5,   5,   0, -20, -40 },
        { -30,   5, 10,  15,  15,  10,   5, -30 },
        { -30,   5, 15,  20,  20,  15,   5, -30 },
        { -30,   5, 15,  20,  20,  15,   5, -30 },
        { -30,   5, 10,  15,  15,  10,   5, -30 },
        { -40, -20,  0,   0,   0,   0, -20, -40 },
        { -50, -40, -30, -30, -30, -30, -40, -50 }
    };

	final int[][] bBishopPosition = {
        { -20, -10, -10, -10, -10, -10, -10, -20 },
        { -10,   5,   0,   0,   0,   0,   5, -10 },
        { -10,  10,  10,  10,  10,  10,  10, -10 },
        { -10,   0,  10,  15,  15,  10,   0, -10 },
        { -10,   5,  10,  15,  15,  10,   5, -10 },
        { -10,   0,  10,  10,  10,  10,   0, -10 },
        { -10,   0,   0,   0,   0,   0,   0, -10 },
        { -20, -10, -10, -10, -10, -10, -10, -20 }
    };

	final int[][] bRookPosition = {
        {  0,  0,  0,  20,  5,  20,  0,  0 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        { -5,  0,  0,  0,  0,  0,  0, -5 },
        {  5, 20, 20, 20, 20, 20, 20,  5 },
        {  0,  0,  0,  0,  0,  0,  0,  0 }
    };

	final int[][] bQueenPosition = {
        { -20, -10, -10,  -5,  -5, -10, -10, -20 },
        { -10,   0,   5,   0,   0,   0,   0, -10 },
        { -10,   5,   5,   5,   5,   5,   0, -10 },
        {   0,   0,   5,   5,   5,   5,   0,  -5 },
        {  -5,   0,   5,   5,   5,   5,   0,  -5 },
        { -10,   0,   5,   5,   5,   5,   0, -10 },
        { -10,   0,   0,   0,   0,   0,   0, -10 },
        { -20, -10, -10,  -5,  -5, -10, -10, -20 }
    };

	final int[][] bKingPosition = {
	    {  20,  20,  30,   0,   0,  10,  30,  20 },
	    {  20,  20,   0,   0,   0,   0,  20,  20 },
	    { -10, -20, -20, -20, -20, -20, -20, -10 },
	    { -20, -30, -30, -40, -40, -30, -30, -20 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 },
	    { -30, -40, -40, -50, -50, -40, -40, -30 }
	};
	boolean takingKing = false;
	int promoCol;
	int promoRow;
	Boolean rejectDraw = false;
	int timer = 180;
	Boolean acceptDraw = false;
	
	
	public aiPanel(aiGame parentFrame) {
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
		setPieces();
		createSlider();
        appendColoredText("     Player 1: White\n", Color.WHITE,30);
        appendColoredText("     Player 2: Black\n", Color.GRAY,30);
		
		nameElo = returnUser();
		String[] parts = nameElo.split(",");
		AccountName = parts[0];
		AccountElo = parts[1];
		AccountName = checkLength(AccountName);
		
		
	}
	
	public void setPieces() {
		//white team
		pieces.add(new pawn(WHITE,0,6,3));
		pieces.add(new pawn(WHITE,1,6,3));
		pieces.add(new pawn(WHITE,2,6,3));
		pieces.add(new pawn(WHITE,3,6,3));
		pieces.add(new pawn(WHITE,4,6,3));
		pieces.add(new pawn(WHITE,5,6,3));
		pieces.add(new pawn(WHITE,6,6,3));
		pieces.add(new pawn(WHITE,7,6,3));
		pieces.add(new rook(WHITE,0,7,3));
		pieces.add(new rook(WHITE,7,7,3));
		pieces.add(new knight(WHITE,1,7,3));
		pieces.add(new knight(WHITE,6,7,3));
		pieces.add(new bishop(WHITE,2,7,3));
		pieces.add(new bishop(WHITE,5,7,3));
		pieces.add(new queen(WHITE,3,7,3));
		pieces.add(new king(WHITE,4,7,3));
		//black team
		pieces.add(new pawn(BLACK,0,1,3));
		pieces.add(new pawn(BLACK,1,1,3));
		pieces.add(new pawn(BLACK,2,1,3));
		pieces.add(new pawn(BLACK,3,1,3));
		pieces.add(new pawn(BLACK,4,1,3));
		pieces.add(new pawn(BLACK,5,1,3));
		pieces.add(new pawn(BLACK,6,1,3));
		pieces.add(new pawn(BLACK,7,1,3));
		pieces.add(new rook(BLACK,0,0,3));
		pieces.add(new rook(BLACK,7,0,3));
		pieces.add(new knight(BLACK,1,0,3));
		pieces.add(new knight(BLACK,6,0,3));
		pieces.add(new bishop(BLACK,2,0,3));
		pieces.add(new bishop(BLACK,5,0,3));
		pieces.add(new queen(BLACK,3,0,3));
		pieces.add(new king(BLACK,4,0,3));
		copyPiece(pieces,simPieces);
	}
	
	private void copyPiece(ArrayList<piece> source, ArrayList<piece> target) {
		
		if(source != target) {
			target.clear();
			for(int i = 0; i < source.size(); i++) {
				target.add(source.get(i));
			}
		}
	}
	
	private void update() {
		

		if(moves % 2 == 1 && gameOver == false && promotion == false && offerDraw == false ) {
			
    		orriginalPositions();    		
			userGo = false;
			orriginalEval = calcEval(0);
			bestEval = orriginalEval;
			userEnteredDepth = difficultySlider.getValue();
			userEnteredDepth += 2;
			eval = miniMax(userEnteredDepth,false,simPieces);
			checkingP = null;
	        stalemate = false;
	        promotion = false;
	        gameOver = false;
			piece hittingP = findPiece(bestCol, bestRow);
			currentColor = BLACK;
			activeP = bestPiece;
			bestPiece.canMove(bestCol,bestRow);
			bestPiece.col = bestCol;
			bestPiece.row = bestRow;
			activeP.updatePosition();
			
			System.out.println(bestPiece);
			System.out.println(bestCol);
			System.out.println(bestRow);
			
			copyPiece(simPieces,pieces);
			
			typeOfPiece = activeP.type.toString();
			colNum = Integer.toString(activeP.col+1);
			colNum = returnCol(colNum);
			holdRow = activeP.row+1;
			holdRow = 9 - holdRow;
			rowNum = Integer.toString(holdRow);
			resultString = moves + "     " + typeOfPiece + " to " + colNum + rowNum;
			
			if(hittingP!= null) {
				simPieces.remove(hittingP);
				copyPiece(simPieces,pieces);
			}
			if(canPromote()) {
				simPieces.add(new queen(currentColor,activeP.col,activeP.row,3));
				simPieces.remove(activeP);
				copyPiece(simPieces,pieces);
				activeP = findPiece(newCol,newRow); 
			}
			if(castlingP != null) {
				checkCastle();
				castlingP.updatePosition();
				String colourStr = "White";
				if(currentColor == BLACK) {
					colourStr = "Black";
				}
				resultString = moves + "     " + colourStr + " castled";
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
			addMove(resultString);
			moves += 1;
		}else {
			if(returnToMenu()) {
				optionsMenu OptionsMenu = new optionsMenu();
				addToDatabase();
				running = false;
				parentFrame.dispose();
			}else if(promotion) {
				promoting();
			}else if(forfeitClicked()) {
				forfeit = true;
				gameOver = true;
			}else if(drawClicked()) {
				offerDraw = true;
			}else if (drawGame()) {
				draw = true;
				gameOver = true;
			}else {
				userGo = true;
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
									String colourStr = "White";
									if(currentColor == BLACK) {
										colourStr = "Black";
									}
									resultString = moves + "     " + colourStr + " castled";
								}
								if(isKingInCheck(true) && isCheckmate()) {
									gameOver = true;
									win = true;
								}
								if(isStalemate()) {
									gameOver = true;
								}
								if(canPromote()) {
									promotion = true;
								}else {
									changeTurn();
								}
								activeP = null;
								addMove(resultString);
								moves += 1;
								orriginalPositions();
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
	public int miniMax(int depth, boolean teamMax,ArrayList<piece> currentPieces  ) {
		ArrayList<piece> tempCurrentPieces = new ArrayList<>(currentPieces);
		int depthBeforeCall = depth;
		copyPiece(currentPieces,simPieces);
	    if (depth == 0 || gameOver) {
			checkingP = null;
	        stalemate = false;
	        promotion = false;
	        gameOver = false;
	        if(currentColor == WHITE) {
	        	currentColor = BLACK;
	        }else {
	        	currentColor = WHITE;
	        }
	        return calcEval(depth);
	    }

		ArrayList<piece> piecesCopy = new ArrayList<>(pieces);
		copyPiece(simPieces,pieces);
		getState();
		ArrayList<Boolean> movedListCopy = new ArrayList<>(movedList);
		ArrayList<Boolean> twoSteppedListCopy = new ArrayList<>(twoSteppedList);
		if(teamMax) {			
			calcPossibleMoves(false);
		}else {
			calcPossibleMoves(true);
		}
		resetAIState(movedListCopy,twoSteppedListCopy);
		int tempBestEval = -100000000;
    	if(teamMax == false) {
    		tempBestEval = 100000000;
    	}
		if(takingKing) {
			takingKing = false;
			return -1 * tempBestEval;
		}
		copyPiece(piecesCopy,pieces);

        piece tempBestPiece = null;
        int tempBestCol = 0;
        int tempBestRow = 0;
		
		List<List<Integer>> possibleMovesCopy = List.copyOf(possibleMoves);
    	int lengthOfArr = possibleMovesCopy.size();
    	int j = 0;
    	
    	while(j < lengthOfArr) {
    		depth = depthBeforeCall;
//    		System.out.println(j + " " + depth);
    		copyPiece(tempCurrentPieces,simPieces);
    		orriginalPositions();
    		List<List<Integer>> oGPositions = List.copyOf(orriginalPos);
    		int value = possibleMovesCopy.get(j).get(0);
    		
    		piece p = simPieces.get(value);
    		
    		int colVal = possibleMovesCopy.get(j).get(1);
    		int rowVal = possibleMovesCopy.get(j).get(2);
    		
    		piece hittingThisP = findPiece(colVal, rowVal);
    		p.canMove(colVal, rowVal);
    		p.col = colVal;
    		p.row = rowVal;
    		p.updatePosition();
    		
    	    if(depth == userEnteredDepth) {
    			savedCol = colVal;
    			savedRow = rowVal;
    			savedP = p;
    	    }
            activeP = p;
            if (hittingThisP != null) {
            	if(hittingThisP.type == Type.KING) {
                	depth = depthBeforeCall;
                	copyPiece(piecesCopy,pieces);
                	copyPiece(pieces,simPieces);
                	resetPositions(oGPositions);
                	resetAIState(twoSteppedListCopy, movedListCopy);
                	if(teamMax == false) {
                		return -100000000;
                	}else {
                		return 100000000;
                	}
            	}
            	simPieces.remove(hittingThisP);	
            }
            if (canPromote()) {
            	simPieces.add(new queen(p.color, colVal, rowVal, 3));
            	simPieces.remove(p);
            }
    		copyPiece(simPieces,pieces);
            if (castlingP != null) {
                checkCastle();
                castlingP.updatePosition();
                castlingP = null;
            }
            if (isKingInCheck(false) && isCheckmate()) {
            	if(teamMax) {
            		return((20 - depth) * 1000000);
            	}else {
            		return( -1* (20 - depth) * 1000000);
            	} 
            }
            if (isStalemate()) {
            	if(teamMax) {
	                if (orriginalEval > 500) {
	                	return -9000000;
	                }else if(orriginalEval < -500) {
	                	return 9000000;
	                }else {
	                	return 0;
	                }
            	}else {
	                if (orriginalEval > 500) {
	                	return -9000000;
	                }else if(orriginalEval < -500) {
	                	return 9000000;
	                }else {
	                	return 0;
	                }
            	}
                
            }
            depth -=1;
        	if(teamMax) {
        		currentColor = BLACK;
        		currentEval = miniMax(depth, false,simPieces);
        	}else {
        		currentColor = WHITE;
        		currentEval = miniMax(depth, true, simPieces);
        	}
        	if(teamMax) {
            	if(currentEval >= tempBestEval) {
            		tempBestEval = currentEval;
                    tempBestPiece = savedP;
                    tempBestCol = savedCol;
                    tempBestRow = savedRow;
            	}
        	}else {
            	if(currentEval <= tempBestEval) {
            		tempBestEval = currentEval;
                    tempBestPiece = savedP;
                    tempBestCol = savedCol;
                    tempBestRow = savedRow;
            	}
        	}
        	depth = depthBeforeCall;
        	copyPiece(piecesCopy,pieces);
        	copyPiece(pieces,simPieces);
        	resetPositions(oGPositions);
        	resetAIState(twoSteppedListCopy, movedListCopy);

            j +=1;
    	}
    	
    	
		if(depth == userEnteredDepth) {
			bestEval = tempBestEval;
            bestPiece = tempBestPiece;
            bestCol = tempBestCol;
            bestRow = tempBestRow;
		}		
		
	    return tempBestEval; 
	}
	public void getState() {
		

		movedList.clear();
		twoSteppedList.clear();
		
		for(piece p: simPieces) {
			if(p.moved) {
				movedList.add(true);
			}else {
				movedList.add(false);
			}
			if(p.twoStepped) {
				twoSteppedList.add(true);
			}else {
				twoSteppedList.add(false);
			}
		}
	}
	public void resetAIState( ArrayList<Boolean> twoSteppedListCopy, ArrayList<Boolean>  movedListCopy) {
		
		int i = 0;
		for(piece p: simPieces) {
			if(twoSteppedListCopy.get(i) == true) {
				p.twoStepped = true;
			}else {
				p.twoStepped = false;
			}
			if(movedListCopy.get(i) == true) {
				p.moved = true;
			}else {
				p.moved = false;
			}
			i += 1;
		}
	}
	public void showState() {
		for(piece p: simPieces) {
			System.out.println(p + " " + p.col + " " + p.row);
		}
	}
	public void orriginalPositions() {
		
		orriginalPos.clear();
		
		for(piece p : simPieces) {
	        List<Integer> pPos = new ArrayList<>();
	        pPos.add(p.col);
	        pPos.add(p.row);
	        
	        orriginalPos.add(pPos);
		}
		
	}
	public void resetPositions(List<List<Integer>> oGpositions) {
		int i = 0;
		for(piece p : simPieces) {
			
			p.col = oGpositions.get(i).get(0);
			p.row = oGpositions.get(i).get(1);
			p.updatePosition();
			i += 1;
		}
	}
	public void calcPossibleMoves(boolean team) {
		possibleMoves.clear();
		castlingP = null;
		checkingP = null;
        stalemate = false;
        promotion = false;
        gameOver = false;
		int count = 0;
		for(piece p: pieces) {
			count += 1;
			if(team) {				
				if(p.color == BLACK) {
		            for (int col = 0; col < 9; col++) {
		                for (int row = 0; row < 9; row++) {
	                		p.col = col;
	                		p.row = row;
	                		if(p.canMove(col, row)) {
		            			if(p.hittingP != null) {
			            			if(p.hittingP.type == Type.KING) {
			            				takingKing = true;
			            			}
			            			simPieces.remove(p.hittingP);
		            			}
		            			if(takingKing == false) {
			                		if(isIllegal(p) == false && opponentCaptureKing() == false) {
				                        List<Integer> row1 = new ArrayList<>();
				                        row1.add(count-1);
				                        row1.add(col);
				                        row1.add(row);
				                        possibleMoves.add(row1);
			                		}
		            			}	

	                		}
	                		copyPiece(pieces,simPieces);
	                		p.resetPosition();
	                	}

		            }
				}
						
			}else if(team == false) {
				if(p.color == WHITE) {
		            for (int col = 0; col < 9; col++) {
		                for (int row = 0; row < 9; row++) {
	                		p.col = col;
	                		p.row = row;
	                		if(p.canMove(col, row)) {
		            			if(p.hittingP != null) {
			            			if(p.hittingP.type == Type.KING) {
			            				takingKing = true;
			            			}
			            			simPieces.remove(p.hittingP);
		            			}
		            			if(takingKing == false) {
			                		if(isIllegal(p) == false && opponentCaptureKing() == false) {
				                        List<Integer> row1 = new ArrayList<>();
				                        row1.add(count-1);
				                        row1.add(col);
				                        row1.add(row);
				                        possibleMoves.add(row1);
			                		}
		            			}	

	                		}
	                		copyPiece(pieces,simPieces);
	                		p.resetPosition();
	                	}

		            }
				}

			}
		}
	}

    public int calcEval(int depth) {
    	int evaluated = 0;
    	for(piece p: simPieces) {
    		if(p.color == WHITE) {
    		    switch (p.type) {
    	        case PAWN:
    	        	evaluated += 100 + (wPawnPosition[p.row][p.col]);
    	            break;
    	        case KNIGHT:
    	        	evaluated += 320 + (wKnightPosition[p.row][p.col]);
    	            break;
    	        case BISHOP:
    	        	evaluated += 330 + (wBishopPosition[p.row][p.col]); 
    	            break;
    	        case ROOK:
    	        	evaluated += 500 + (wRookPosition[p.row][p.col]);
    	            break;
    	        case QUEEN:
    	        	evaluated += 900 + (wQueenPosition[p.row][p.col]);
    	            break;
    	        case KING:
    	        	evaluated += 20000 + (wKingPosition[p.row][p.col]);
    	            break;
				default:
					break;
    		    }    	
    		}else {
    		    switch (p.type) {
    	        case PAWN:
    	        	evaluated -= (100 + (bPawnPosition[p.row][p.col]));
    	            break;
    	        case KNIGHT:
    	        	evaluated -= (320 + (bKnightPosition[p.row][p.col]));
    	            break;
    	        case BISHOP:
    	        	evaluated -= (330 + (bBishopPosition[p.row][p.col]));
    	            break;
    	        case ROOK:
    	        	evaluated -= (500 + (bRookPosition[p.row][p.col]));
    	            break;
    	        case QUEEN:
    	        	evaluated -= (900 + (bQueenPosition[p.row][p.col]));
    	            break;
    	        case KING:
    	        	evaluated -= (20000 + (bKingPosition[p.row][p.col]));
    	            break;
				default:
					break;
    		    } 
    		}
    	}
    	
    	return evaluated;
    }
    private void resetGameState() {
    	
        gameOver = false;
        draw = false;
        stalemate = false;
        offerDraw = false;
        promotion = false;
        moves = 0;
        currentColor = WHITE;
        otherColor = BLACK;
        pieces.clear();
        setPieces();
        copyPiece(pieces, simPieces);
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
					promoPieces.add(new queen(currentColor,activeP.col,activeP.row,3));
					promoPieces.add(new knight(currentColor,activeP.col,activeP.row+1,3));
					promoPieces.add(new rook(currentColor,activeP.col,activeP.row+2,3));
					promoPieces.add(new bishop(currentColor,activeP.col,activeP.row+3,3));
					promoPieces.add(new x(currentColor,activeP.col,activeP.row+4,3));
					return true;
				}else if(currentColor == BLACK && activeP.row == 7) {
					promotionP = activeP;
					promoPieces.clear();
					promoPieces.add(new queen(currentColor,activeP.col,activeP.row,3));
					promoPieces.add(new knight(currentColor,activeP.col,activeP.row-1,3));
					promoPieces.add(new rook(currentColor,activeP.col,activeP.row-2,3));
					promoPieces.add(new bishop(currentColor,activeP.col,activeP.row-3,3));
					promoPieces.add(new x(currentColor,activeP.col,activeP.row-4,3));
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
		if(delay) {
			delay = false;
			try {
				gameThread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else if(mouse.pressed && gameOver) {
			if(mouse.x >= 290 && mouse.x <= 770 && mouse.y >= 570 && mouse.y <= 650) {
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
					case X:promotion = false; moves -=1;break;
					default:break;
					}
					if(promotion != false) {
						simPieces.remove(promotionP);
						copyPiece(simPieces, pieces);
						promotion = false;
						if(isKingInCheck(true) && isCheckmate()) {
							gameOver = true;
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
//		System.out.println(currentColor);
//		System.out.println(king + " " + king.color);
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
					}else {
						System.out.println("checkmate by knight");
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
	    difficultySlider = new JSlider(JSlider.VERTICAL, 0, 2, 0); 

	    difficultySlider.setOpaque(false);
	    
	    difficultySlider.setForeground(Color.WHITE);
	    
	    difficultySlider.setMajorTickSpacing(1); 
	    difficultySlider.setPaintTicks(true);
	    difficultySlider.setPaintLabels(true);
	    
	    String[] difficulties = { "", "", "" };
	    
	    Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
	    for (int i = 0; i < difficulties.length; i++) {
	        JLabel label = new JLabel(difficulties[i]);	        
	        labelTable.put(i, label);
	    }
	    
	    difficultySlider.setLabelTable(labelTable);	    
	    difficultySlider.setBounds(1620, 270, 100, 400);
	    
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
        appendColoredText("     New game started\n", Color.RED,20);
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
		if(currentColor != WHITE) {
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
	private boolean drawGame() {
		if(timer != 0) {
			if(offerDraw) {
				timer -= 1;
			}				
		}else {
			timer = 180;
			offerDraw = false;
			int tempEval = calcEval(2);
			if(tempEval >= 100) {
				acceptDraw = true;
				return true;
			}else {
				rejectDraw = true;
			}
			
		}
		return false;
	}
	private boolean drawClicked() {
		if(mouse.pressed && offerDraw == false && forfeit == false && gameOver == false) {
			if(mouse.x >= 1100 && mouse.x <= 1450 && mouse.y >= 850 && mouse.y <= 950) {
				return true;
			}
		}
		return false;
	}
	private boolean forfeitClicked() {
		if(mouse.pressed && offerDraw == false && forfeit == false && gameOver == false) {
			if(mouse.x >= 1500 && mouse.x <= 1850 && mouse.y >= 850 && mouse.y <= 950) {
				return true;
			}
		}
		
		return false;
		
	}
    private piece findPiece(int targetCol, int targetRow) {

		for(piece p: aiPanel.simPieces) {
			if(p.col == targetCol && p.row == targetRow) {
				return p;
			}
		}

		return null;

    }
    private void addToDatabase() {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			if(win) {
				String sql = "UPDATE userDetails SET userGames = userGames + 1, userWins = userWins + 1 WHERE userID = ?";;
	
				Connection connect = DriverManager.getConnection(url, uName, uPass);
				PreparedStatement preparedStatement = connect.prepareStatement(sql);
	
				preparedStatement.setInt(1, LoginSystem.userID);
				preparedStatement.executeUpdate();
			}else {
				String sql = "UPDATE userDetails SET userGames = userGames + 1 WHERE userID = ?";;
				
				Connection connect = DriverManager.getConnection(url, uName, uPass);
				PreparedStatement preparedStatement = connect.prepareStatement(sql);
	
				preparedStatement.setInt(1, LoginSystem.userID);
				preparedStatement.executeUpdate();
			}
        }
		
		catch(SQLException e) {
			e.printStackTrace();
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
        appendColoredText("     New game started\n", Color.RED,20);
        appendColoredText("     Player 1: White\n", Color.WHITE,30);
        appendColoredText("     Player 2: Black\n", Color.GRAY,30);
    }
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		board.draw(g2);
		for (piece p: simPieces) {
			if(p.color != currentColor) {
				p.draw(g2);
			}
		}
		for (piece p1: simPieces) {
			if(p1.color == currentColor && p1 != activeP) {
				p1.draw(g2);
			}
		}
		for (piece p2: simPieces) {
			if(p2 == activeP) {
				p2.draw(g2);
			}
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
		if(gameOver) {
			g2.setColor(Color.WHITE);
			g2.fillRect(290, 410, 480, 240);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 60));
			g2.drawString("GAME OVER", 340, 500);
			g2.setColor(Color.GRAY);
			g2.fillRect(290, 570, 480, 80);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(290, 410, 480, 240);
			g2.drawRect(290, 570, 480, 80);
			g2.setStroke(new BasicStroke(1));
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 40));
			g2.drawString("Return to Menu", 380, 620);
			if(mouse.x >= 290 && mouse.x <= 770 && mouse.y >= 570 && mouse.y <= 650) {
				g2.setColor(Color.white);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.7f));
				g2.fillRect(290, 570, 480, 80);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			}
		}
		if(gameOver == true && stalemate == false) {
			if(draw) {
				g2.setFont(new Font("Georgia", Font.BOLD, 60));
				g2.setColor(Color.GRAY);
				g2.drawString("DRAW", 1360, 800);
			}else {
				checkingP = null;
				String a = "";
				if(forfeit) {
					if(currentColor == WHITE) {
						g2.setColor(Color.BLACK);
						a = "BLACK WON";
					}else {
						g2.setColor(Color.white);
						a = "WHITE WON";
					}
				}else {
					if(currentColor == WHITE) {
						g2.setColor(Color.BLACK);
						a = "BLACK WON";
					}else {
						g2.setColor(Color.white);
						a = "WHITE WON";
					}
				}
				g2.setFont(new Font("Georgia", Font.BOLD, 40));
				g2.drawString(a, 1300, 800);
			}
		}else if(stalemate == true) {
			g2.setFont(new Font("Georgia", Font.BOLD, 40));
			g2.setColor(Color.GRAY);
			g2.drawString("STALEMATE", 1300, 800);
		}

		g2.setColor(Color.WHITE);
		g2.fillRect(1100, 850, 350, 100);
		g2.fillRect(1500, 850, 350, 100);
		g2.setFont(new Font("Georgia", Font.BOLD, 60));
		g2.setColor(Color.BLACK);
		g2.drawString("DRAW", 1165, 920);
		g2.drawString("FORFEIT", 1530, 920);
		
		if(offerDraw) {
			g2.setColor(Color.WHITE);
			g2.fillRect(290, 410, 480, 240);
			g2.setColor(Color.BLACK);
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 55));
			g2.drawString("Offered a Draw", 330, 490);
			g2.drawString("AI is thinking...", 330, 560);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(290, 410, 480, 240);
			g2.setStroke(new BasicStroke(1));
		}
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
		
		g2.setFont(new Font("Palatino Linotype", Font.BOLD, 30));
		g2.drawString("HARD", 1700,290);
		g2.drawString("MEDIUM", 1700,480);
		g2.drawString("EASY", 1700,670);
		
		//Draw row and Col nums
		
		for(int i = 0; i < 9; i++) {
			collumDraw = Integer.toString(i);
			collumDraw = returnCol(collumDraw);
			g2.drawString(collumDraw,((i+1)*ChessBoard.SQUARE_SIZE)-20,1040);
		}
		for(int i = 1; i < 9; i++) {
			rowDraw = Integer.toString(i);
			g2.drawString( rowDraw,20,1070 - (i*ChessBoard.SQUARE_SIZE));
		}
		if(rejectDraw) {
			g2.setColor(Color.WHITE);
			g2.fillRect(290, 410, 480, 240);
			g2.setColor(Color.RED);
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 55));
			g2.drawString("AI REJECTED", 360, 520);
			g2.drawString("DRAW", 440, 580);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(290, 410, 480, 240);
			g2.setStroke(new BasicStroke(1));
			timer -= 1;
			if (timer == 0) {
				timer = 180;
				rejectDraw = false;
			}
		}
		if(acceptDraw) {
			g2.setColor(Color.WHITE);
			g2.fillRect(290, 410, 480, 240);
			g2.setColor(Color.GREEN);
			g2.setFont(new Font("Palatino Linotype", Font.BOLD, 65));
			g2.drawString("AI ACCEPTED", 340, 540);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.drawRect(290, 410, 480, 240);
			g2.setStroke(new BasicStroke(1));
			timer -= 1;
			if (timer == 0) {
				timer = 180;
				acceptDraw = false;
			}
		}
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
