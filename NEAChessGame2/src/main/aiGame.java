package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class aiGame extends JFrame {

    
    public aiGame() {
        ImageIcon cornerIcon = new ImageIcon(getClass().getResource("/images/pawn.png"));
        setIconImage(cornerIcon.getImage());
        setTitle("Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        
        aiPanel aiPanel = new aiPanel(this);
        add(aiPanel);
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);

        aiPanel.launchGame();
    }

}
