package piece;
import main.Type;

public class x extends piece{

	public x(int color, int col, int row,int classNum) {
		super(color, col, row, classNum);
		
		type = Type.X;
		image = getImage("/piece/red-x");
	}

}
