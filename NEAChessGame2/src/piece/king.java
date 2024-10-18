package piece;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;
import main.aiPanel;

public class king extends piece{

	public king(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.KING;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-king");
		}else {
			image = getImage("/piece/b-king");
		}
	}
	public boolean canMove(int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol,targetRow)) {
			if(Math.abs(targetCol-preCol)+ Math.abs(targetRow - preRow) == 1) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}else if(Math.abs(targetCol-preCol) == 1 && Math.abs(targetRow - preRow) == 1) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
			if(moved == false && classNum == 1) {
				
				//castle right
				if(targetCol == preCol+2 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : GamePanel.simPieces) {
						if(p.col == preCol+3 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							GamePanel.castlingP = p;
							return true;
						}
					}
					
				}
				//castle left
				if(targetCol == preCol-2 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : GamePanel.simPieces) {
						if(p.col == preCol-4 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							GamePanel.castlingP = p;
							return true;
						}
					}
					
				}
			}
			if(moved == false && classNum == 2) {
				
				//castle right
				if(targetCol == preCol+2 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : PuzzlePanel.simPieces) {
						if(p.col == preCol+3 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							PuzzlePanel.castlingP = p;
							return true;
						}
					}
					
				}
				//castle left
				if(targetCol == preCol-2 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : PuzzlePanel.simPieces) {
						if(p.col == preCol-4 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							PuzzlePanel.castlingP = p;
							return true;
						}
					}
				}
			}
			if(moved == false && classNum == 3) {
				
				//castle right
				if(targetCol == preCol+2 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : aiPanel.simPieces) {
						if(p.col == preCol+3 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							aiPanel.castlingP = p;
							return true;
						}
					}
					
				}
				//castle left
				if(targetCol == preCol-3 && targetRow == preRow && pieceOnLine(targetCol,targetRow) == false) {
					for(piece p : aiPanel.simPieces) {
						if(p.col == preCol-4 && p.row == preRow && p.moved == false && isValidSquare(targetCol,targetRow)) {
							aiPanel.castlingP = p;
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}
