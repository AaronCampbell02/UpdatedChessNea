package piece;

import java.awt.Graphics2D;

import java.awt.image.*;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;
import main.aiPanel;
import main.ChessBoard;


public class piece {
	
	
	public Type type;
	public BufferedImage image;
	public int x,y, color, classNum;
	public int col,row,preCol,preRow,savedRow,savedCol;
	public piece hittingP;
	public boolean moved,twoStepped;
	public static piece returnP;
	public static String typeOfPiece;
	
	public piece(int color,int col, int row, int classNum) {
		this.color = color;
		this.col = col;
		this.row = row;
		this.classNum = classNum;
		x = getX(col);
		y = getY(row);
		preCol = col;
		preRow = row;
	}
	public BufferedImage getImage(String imagePath) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
		}catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	public int getX(int col) {
		return (50 + (col * ChessBoard.SQUARE_SIZE));
	}
	public int getY(int row) {
		return (50 + (row * ChessBoard.SQUARE_SIZE));
	}
	public int getCol(int x) {
		return(-50 + x + ChessBoard.HALF_SQUARE_SIZE)/ChessBoard.SQUARE_SIZE;
	}
	public int getRow(int y) {
		return(-50 + y + ChessBoard.HALF_SQUARE_SIZE)/ChessBoard.SQUARE_SIZE;
	}
	public void updatePosition() {
		
		if(type == Type.PAWN) {
			if(Math.abs(row - preRow) == 2) {
				twoStepped = true;
			}
		}
		
		x = getX(col);
		y = getY(row);
		preCol = getCol(x);
		preRow = getRow(y);
		moved = true;
	}
	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = getX(col);
		y = getY(row);
	}
	public void savePromotion(int pastCol, int pastRow) {
		savedCol = pastCol;
		savedRow = pastRow;
	}
	public void returnPiece(piece p) {
		returnP = p;
		switch(p.type) {
		case QUEEN: typeOfPiece = "queen" ; break;
		case KNIGHT: typeOfPiece = "knight" ; break;
		case ROOK: typeOfPiece = "rook" ; break;
		case BISHOP: typeOfPiece = "bishop" ; break;
		case KING: typeOfPiece = "king" ;break;
		default: break;
		}
	}
	public boolean canMove(int targetCol, int targetRow) {
		return false;
	}
	public boolean isWithinBoard(int targetCol, int targetRow) {
		if(targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
			return true;
		}
		return false;
	}
	public piece getHittingP (int targetCol, int targetRow) {
		if(classNum == 1) {
			for(piece p: GamePanel.simPieces) {
				if(p.col == targetCol && p.row == targetRow && p != this) {
					return p;
				}
			}
			return null;
		}
		else if(classNum == 2) {
			for(piece p: PuzzlePanel.simPieces) {
				if(p.col == targetCol && p.row == targetRow && p != this) {
					return p;
				}
			}
			return null;
		}
		else if(classNum == 3) {
			for(piece p: aiPanel.simPieces) {
				if(p.col == targetCol && p.row == targetRow && p != this) {
					return p;
				}
			}
			return null;
		}
		return null;
	}
	public boolean isValidSquare(int targetCol, int targetRow) {
		hittingP = getHittingP(targetCol,targetRow);
		if (hittingP == null) {
			return true;
		}else if (hittingP.color != this.color){
			return true;
		}else {
			hittingP = null;
		}
		return false;
	}
	public boolean isSameSquare(int targetCol, int targetRow) {
		if (targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	public boolean pieceOnLine(int targetCol, int targetRow) {
		if(classNum == 1) {
			for(int c = preCol-1;c > targetCol; c--) {
				for(piece piece: GamePanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int c = preCol+1;c < targetCol; c++) {
				for(piece piece: GamePanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow-1;r > targetRow; r--) {
				for(piece piece: GamePanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow+1;r < targetRow; r++) {
				for(piece piece: GamePanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			return false;
		}
		else if(classNum == 2) {
			for(int c = preCol-1;c > targetCol; c--) {
				for(piece piece: PuzzlePanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int c = preCol+1;c < targetCol; c++) {
				for(piece piece: PuzzlePanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow-1;r > targetRow; r--) {
				for(piece piece: PuzzlePanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow+1;r < targetRow; r++) {
				for(piece piece: PuzzlePanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			return false;
		}
		else if(classNum == 3) {
			for(int c = preCol-1;c > targetCol; c--) {
				for(piece piece: aiPanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int c = preCol+1;c < targetCol; c++) {
				for(piece piece: aiPanel.simPieces) {
					if(piece.col == c && piece.row == targetRow) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow-1;r > targetRow; r--) {
				for(piece piece: aiPanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			for(int r = preRow+1;r < targetRow; r++) {
				for(piece piece: aiPanel.simPieces) {
					if(piece.row == r && piece.col == targetCol) {
						hittingP = piece;
						return true;
					}
				}
			}
			
			return false;
		}
		return false;
	}
	public boolean pieceOnDiagonal(int targetCol, int targetRow) {
		if(classNum == 1) {
			if(targetRow < preRow) {
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: GamePanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: GamePanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				
			}else if(targetRow > preRow) {
				
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: GamePanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}
	
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: GamePanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}	
			}
			return false;
		}
		else if(classNum == 2) {
			if(targetRow < preRow) {
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: PuzzlePanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: PuzzlePanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				
			}else if(targetRow > preRow) {
				
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: PuzzlePanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}
	
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: PuzzlePanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}	
			}
			return false;
		}
		if(classNum == 3) {
			if(targetRow < preRow) {
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: aiPanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: aiPanel.simPieces) {
						if(p.col == c && p.row == preRow-dif) {
							hittingP = p;
							return true;
						}
					}
				}
				
			}else if(targetRow > preRow) {
				
				for(int c = preCol-1; c > targetCol;c--) {
					int dif = Math.abs(c - preCol);
					for(piece p: aiPanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}
	
				for(int c = preCol+1; c < targetCol;c++) {
					int dif = Math.abs(c - preCol);
					for(piece p: aiPanel.simPieces) {
						if(p.col == c && p.row == preRow+dif) {
							hittingP = p;
							return true;
						}
					}
				}	
			}
			return false;
		}
		return false;
	}
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image,x,y,ChessBoard.SQUARE_SIZE,ChessBoard.SQUARE_SIZE,null);
	}
}
