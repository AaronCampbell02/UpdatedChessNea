package main;
import java.awt.*;

public class ChessBoard {
	final int MAX_COL = 8;
	final int MAX_ROW = 8;
	public static final int SQUARE_SIZE = 120;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;
	
	public void draw(Graphics2D g2) {
		
		int a = 0;
		
		for(int row = 0; row < MAX_ROW; row++) {
			if(a==1) {
				a = 0;
			}else {
				a = 1;
			}
			for (int col = 0; col < MAX_COL; col++) {
				if(a == 0) {
					g2.setColor(new Color(0,0,0));
					a = 1;
				}else {
					g2.setColor(new Color(121,165,83));
					a = 0;
				}
				
				g2.fillRect(50 + col*SQUARE_SIZE, 50 + row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
		}
	}
	
}
