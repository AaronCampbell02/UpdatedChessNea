package piece;

import main.GamePanel;
import main.PuzzlePanel;
import main.Type;

public class knight extends piece{

	public knight(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.KNIGHT;
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-knight");
		}else {
			image = getImage("/piece/b-knight");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol,targetRow)) {
			if(Math.abs(targetCol-preCol) == 1 && Math.abs(targetRow - preRow) == 2) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}else if(Math.abs(targetCol-preCol) == 2 && Math.abs(targetRow - preRow) == 1) {
				if(isValidSquare(targetCol,targetRow)) {
					return true;
				}
			}
		}
		return false;
	}

}
