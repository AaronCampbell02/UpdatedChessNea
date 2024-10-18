package main;
import javax.swing.*;

public class PuzzlesGame extends JFrame {

    
    public PuzzlesGame() {
        ImageIcon cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
        setIconImage(cornerIcon.getImage());
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        PuzzlePanel puzzlePanel = new PuzzlePanel(this);
        add(puzzlePanel);
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);

        puzzlePanel.launchGame();
    }
}


