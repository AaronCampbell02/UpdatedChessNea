package piece;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;
import main.aiPanel;

public class pawn extends piece{
	
	public int savedRow;
	public int savedCol;
	public piece returnHittingPiece;
	
	
	public pawn(int color, int col, int row, int classNum) {
		super(color, col, row,classNum);
		
		type = Type.PAWN;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-pawn");
		}else {
			image = getImage("/piece/b-pawn");
		}

	}
public boolean canMove(int targetCol, int targetRow) {
	
	if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
		int moveValue = 1;
		if (color == GamePanel.WHITE) {
			moveValue = -1;
		}
		hittingP = getHittingP(targetCol,targetRow);
		piece tempP = getHittingP(targetCol,targetRow);
		if(targetRow == 7 || targetRow == 0) {
			if(tempP != null) {
				if(tempP.type != Type.KING) {
					returnHittingPiece = getHittingP(targetCol,targetRow);
					savePromotion(preCol,preRow);
				}
			}
			if(returnHittingPiece != null) {
				returnPiece(returnHittingPiece);
			}
		}
		
			
		if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null) {
			return true;
		}
		if(targetCol == preCol && targetRow == preRow + moveValue*2 &&
				hittingP == null && moved == false && pieceOnLine(targetCol,targetRow)== false) {
			return true;
		}
		if(Math.abs(targetCol-preCol) == 1 && targetRow == preRow + moveValue &&
				hittingP != null && hittingP.color != color) {
			return true;
		}
		if(classNum == 1) {
			if(Math.abs(targetCol-preCol) == 1 && targetRow == preRow + moveValue) {
				for(piece p : GamePanel.simPieces) {
					if(p.col == targetCol && p.row == preRow && p.twoStepped == true) {
						hittingP = p;
						return true;
					}
				}
			}
		}else if(classNum == 2) {
			if(Math.abs(targetCol-preCol) == 1 && targetRow == preRow + moveValue) {
				for(piece p : PuzzlePanel.simPieces) {
					if(p.col == targetCol && p.row == preRow && p.twoStepped == true) {
						hittingP = p;
						return true;
					}
				}
			}
		}else if(classNum == 3) {
			if(Math.abs(targetCol-preCol) == 1 && targetRow == preRow + moveValue) {
				for(piece p : aiPanel.simPieces) {
					if(p.col == targetCol && p.row == preRow && p.twoStepped == true) {
						hittingP = p;
						return true;
					}
				}
			}
		}
	}
	
	return false;	
	}

}
