package piece;
import main.GamePanel;
import main.PuzzlePanel;
import main.Type;

public class bishop extends piece{

	public bishop(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.BISHOP;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w-bishop");
		}else {
			image = getImage("/piece/b-bishop");
		}
	} 
	
	public boolean canMove(int targetCol, int targetRow) {
		
		if (isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow) == false) {
			if(Math.abs(targetCol-preCol) - Math.abs(targetRow - preRow) == 0) {
				if(isValidSquare(targetCol,targetRow) && pieceOnDiagonal(targetCol,targetRow) == false) {
					return true;
				}
			 }
		}
		return false;
	}

}
