import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {
        // define the dimensions of the game board
        int boardWidth = 360;
        int boardHeight = 640;
        // Create a new JFrame window with the title "Flappy Bird"
        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        // Configure the JFrame settings: set size, center on screen, disable resizing, and set close operation
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Initialize the FlappyBird game panel and add it to the frame
        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        // Adjust the window size based on the preferred size of its components, request focus for key input, and make the frame visible 
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}