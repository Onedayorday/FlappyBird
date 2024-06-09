import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

// The FlappyBird class extends JPanel to create a custom game panel and implements ActionListener and KeyListener
// to handle game updates and user input events, respectively.
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //images for the game
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //bird class to represent the player
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;
        
        Bird(Image img) {
            this.img = img;
        }
    }

    //pipe class to represent obstacles
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;  //scaled by 1/6
    int pipeHeight = 512;
    int pipeSpeed = 2; // Speed at which pipes move up and down
    boolean pipesMoving = false; 
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;
        boolean moveUp = random.nextBoolean(); // Randomly decided whether the pipe should move up or down
        boolean isMoving = false; //pipes are initially stationary
        int topPipeY; // Y - coordinate of the top pipe 
        int bottomPipeY; // Y - coordinate of the bottom pipe 
        int gap = 200; // Gap between top and bottom pipes 

        Pipe(Image img) {
            this.img = img;
            if (score >= 20) { //checks if 20 pipes were passed through
                this.isMoving = random.nextBoolean(); // Randomly decides if the nextpipe will be moving or stationary (after 20 pipes)
            }
        }



        public void pipeMovement() {
            x += velocityX; // move both pipes horizontally with the same velocity

            if (isMoving) { //checks if 20 pipes were passed through 
                if (moveUp) {
                    y -= pipeSpeed; // if the pipe is moving up, its y coordinate is decreased by pipeSpeed
                    if (y < -pipeHeight) { //ensures that the pipe doesn't move too far up
                        moveUp = false;
                    }
                } else {
                    y += pipeSpeed; // if the pipe is moving down, its y coordinate is increased by pipeSpeed
                    if (y > boardHeight) { // ensures that the pipe doesn't move too far down
                        moveUp = true;
                    }
                }
            }        
        } 
    }

    //game logic variables
    Bird bird;
    int velocityX = -4; //move pipes to the left speed (simulates bird moving right)
    int velocityY = 0; //move bird up/down speed.
    int gravity = 1;


    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    // Initialize the game panel, load images, and start timers
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        //load images for the game
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Initialize the bird and pipes
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes at intervals
        placePipeTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // Code to be executed
              placePipes();
            }
        });
        placePipeTimer.start();
        
		// Main game loop to update game state and repaint 
		gameLoop = new Timer(1000/60, this); //how long it takes to start timer, milliseconds gone between frames 
        gameLoop.start();
	}
    
    // Place pipes at random heights with an opening space
    void placePipes() {
        //(0-1) * pipeHeight/2.
        // 0 -> -128 (pipeHeight/4)
        // 1 -> -128 - 256 (pipeHeight/4 - pipeHeight/2) = -3/4 pipeHeight
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
    
        //Top Pipe
        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
    
        // Bottom Pipe
        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

    // Draw the background, bird, pipes, and score
	public void draw(Graphics g) {
        //background
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);

        //bird
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);

        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
        
	}

    // Update bird and pipe positions, check for collisions, and update score
    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); //apply gravity to current bird.y, limit the bird.y to top of the canvas

        //pipes
        for (Pipe pipe : pipes) {
            pipe.pipeMovement(); // Move both pipes together
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5; // Because there are 2 pipes so 0.5 for each equals 1
                pipe.passed = true;
            }
            // Check for collision
            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // Checks for collisions between bird and pipes
    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    // Handle game loop actions: move, repaint, and check for game over
    @Override
    public void actionPerformed(ActionEvent e) { //called every x milliseconds by gameLoop timer
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }  

    // Handle key press events to control bird
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // System.out.println("JUMP!");
            if (e.isShiftDown()) {
                velocityY = -18; //if shift and space are pressed similtaneously than the flappy bird does a super jump
            } else {
                velocityY = -9;// regular jump if only space is pressed
            }
        
       
            if (gameOver) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
                pipesMoving = false;
            } 
        }
    }
    
    // For FlappyBird these methods are not needed; These methods are unused in this game
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}