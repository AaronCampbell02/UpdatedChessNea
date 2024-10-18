package piece;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;

public class rook extends piece{

	public rook(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.ROOK;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-rook");
		}else {
			image = getImage("/piece/b-rook");
		}
	} 
	public boolean canMove(int targetCol, int targetRow) {
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
