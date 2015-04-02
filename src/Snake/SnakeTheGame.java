package Snake;

import javax.swing.JFrame;
import java.awt.EventQueue;

/**
 * Snake game clone
 * designed by Dmitry Chernykh, Kiev, Ukraine 2015
 * any one can use this code or annotation to it for studying purposes only
 * */
public class SnakeTheGame extends JFrame {

    /**
     * initializes the game
     * shows tha game frame in center of screen*/
    public SnakeTheGame() {

        add(new Board());
        
        setResizable(false);
        pack();
        
        setTitle("Snake v0.4");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                JFrame ex = new SnakeTheGame();
                ex.setVisible(true);                
            }
        });
    }
}