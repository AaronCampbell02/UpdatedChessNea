package main;
import javax.swing.*;

public class TwoPlayerChessGame extends JFrame {

    
    public TwoPlayerChessGame() {
        ImageIcon cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
        setIconImage(cornerIcon.getImage());
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        
        GamePanel gamePanel = new GamePanel(this);
        add(gamePanel);
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.launchGame();
    }
}
