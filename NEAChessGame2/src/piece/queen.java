package piece;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;

public class queen extends piece{

	public queen(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.QUEEN;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-queen");
		}else {
			image = getImage("/piece/b-queen");
		}
	} 
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol,targetRow) == true && isSameSquare(targetCol,targetRow) == false) {
			if(Math.abs(targetCol-preCol) - Math.abs(targetRow - preRow) == 0) {
				if(isValidSquare(targetCol,targetRow) && pieceOnDiagonal(targetCol,targetRow) == false) {
					return true;
				}
			 }
		}
		if(isWithinBoard(targetCol,targetRow)) {
			if(targetCol == preCol || targetRow == preRow) {
				if(isSameSquare(targetCol, targetRow) == false) {
					if(isValidSquare(targetCol,targetRow) && pieceOnLine(targetCol,targetRow) == false) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
