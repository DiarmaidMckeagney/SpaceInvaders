package week5;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*; //imports
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Iterator;

public class InvadersApplication extends JFrame implements Runnable, KeyListener {

    // member data
    private static String workingDirectory;
    private static boolean isInitialised = false;
    private static final Dimension WindowSize = new Dimension(800,600);
    private BufferStrategy strategy;
    private Graphics offscreenGraphics;
    private static final int NUMALIENS = 30;
    private Alien[] AliensArray = new Alien[NUMALIENS];
    private Spaceship PlayerShip;
    private boolean inGame;
    private ArrayList<PlayerBullet> bulletList = new ArrayList<PlayerBullet>();
    private final ImageIcon iconBullet= new ImageIcon(workingDirectory + "/src/week5/bullet.png");
    private final Image bulletImage = iconBullet.getImage();
    private int currentScore = 0;
    private int bestScore = 0;
    private int roundNum = 0;
    private long lastTime = 0;

    // constructor
    public InvadersApplication() {
        //Display the window, centred on the screen
        Dimension screensize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int x = screensize.width/2 - WindowSize.width/2;
        int y = screensize.height/2 - WindowSize.height/2;
        setBounds(x, y, WindowSize.width, WindowSize.height);
        setVisible(true);
        this.setTitle("Space Invaders!");

        // load image from disk
        ImageIcon icon = new ImageIcon(workingDirectory + "/src/week5/alien_ship_1.png");
        Image alienImage = icon.getImage();
        ImageIcon icon2 = new ImageIcon(workingDirectory + "/src/week5/alien_ship_2.png");
        Image alienImage2 = icon2.getImage();

        // create and initialise some aliens, passing them each the image we have loaded
        for (int i=0; i<NUMALIENS; i++) {
            AliensArray[i] = new Alien(alienImage, alienImage2);
            double xx = (i % 5) * 80 + 70;
            double yy = (i / 5) * 40 + 50; // integer division!
            AliensArray[i].setPosition(xx, yy);
        }
        Alien.setFleetXSpeed(2);

        // create and initialise the player's spaceship
        icon = new ImageIcon(workingDirectory + "/src/week5/player_ship.png");
        Image shipImage = icon.getImage();
        PlayerShip = new Spaceship(shipImage);
        PlayerShip.setPosition(300,530);

        // tell all sprites the window width
        Sprite2D.setWinWidth(WindowSize.width);

        // create and start our animation thread
        Thread t = new Thread(this);
        t.start();

        // send keyboard events arriving into this JFrame back to its own event handlers
        addKeyListener(this);

        // initialise double-buffering
        createBufferStrategy(2);
        strategy = getBufferStrategy();
        offscreenGraphics = strategy.getDrawGraphics();
        inGame = false;
        isInitialised = true;
    }

    // thread's entry point
    public void run() {
        while ( 1==1 ) {

            // 1: sleep for 1/50 sec
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) { }

            // 2: animate game objects
            //if in the game
            if (inGame) {
                //moves all the aliens and sees if any of them hit the wall
                boolean alienDirectionReversalNeeded = false;
                for (int i=0;i<NUMALIENS; i++) {
                    if (AliensArray[i].move())
                        alienDirectionReversalNeeded=true;
                }//if one of the aliens hits a wall then reverse direction
                if (alienDirectionReversalNeeded) {
                    Alien.reverseDirection();
                    for (int i=0;i<NUMALIENS; i++)
                        AliensArray[i].jumpDownwards();
                }

                PlayerShip.move();//move the player
                // 3: force an application repaint
                this.repaint();
            }
            else {//if not in game then just repaint the menu page
                this.repaint();
            }
        }
    }

    // Three Keyboard Event-Handler functions
    public void keyPressed(KeyEvent e) {
        if (inGame) {//if in game
            if (e.getKeyCode()==KeyEvent.VK_LEFT) {
                PlayerShip.setXSpeed(-4);
            }
            else if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
                PlayerShip.setXSpeed(4);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastTime > 150){
                    shootBullet();
                    lastTime = currentTime;
                }
            }
        }
        else { // if not in game
            for (int i=0; i<NUMALIENS; i++) { // reset the aliens position back to the start
                double xx = (i % 5) * 80 + 70;
                double yy = (i / 5) * 40 + 50; // integer division!
                AliensArray[i].setPosition(xx, yy);
                AliensArray[i].isAlive = true;//set them all back to alive
            }
            Alien.setFleetXSpeed(2);//reset the alien speed
            inGame = true; //transition from menu to game

        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_RIGHT)
            PlayerShip.setXSpeed(0);
    }

    public void keyTyped(KeyEvent e) {}

    public void shootBullet() {
        // add a new bullet to our list
        PlayerBullet b = new PlayerBullet(bulletImage, WindowSize.width);
        b.setPosition(PlayerShip.x+27, PlayerShip.y);
        bulletList.add(b);
    }

    // application's paint method
    public void paint(Graphics g) {
        if (!isInitialised)//for when the paint method is called before the buffer strategy is initialised
            return;

        if (inGame) { // if the game is running
            g = offscreenGraphics;

            // clear the canvas with a big black rectangle
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WindowSize.width, WindowSize.height);

            int numOfDeadAliens = 0;//counts the number of dead aliens
            // redraw all game objects
            for (int i=0;i<NUMALIENS; i++) {
                if (AliensArray[i].isAlive) {
                    AliensArray[i].paint(g);
                }
                else{
                    numOfDeadAliens++;
                }
            }
            if (numOfDeadAliens == 30){//if all aliens are dead
                roundNum++;//increase the round number
                for (int i = 0; i < NUMALIENS; i++) { //reset all the aliens
                    AliensArray[i].isAlive = true;//set them all back to life
                    double xx = (i % 5) * 80 + 70;
                    double yy = (i / 5) * 40 + 50; // integer division!
                    AliensArray[i].setPosition(xx, yy);
                }
                Alien.setFleetXSpeed(roundNum + 2); //increase the alien speed
            }
            for (int i = 0; i < NUMALIENS; i++) {//checks if the player collides with any of the aliens
                boolean playernotDead = true;
                if (AliensArray[i].isAlive) {//only checks the alive aliens
                    playernotDead = PlayerShip.doesNotCollide(AliensArray[i]); // checks for a collision
                }
                if (!playernotDead){//if there is a collision
                    inGame = false;//go back to the menu
                    if(currentScore > bestScore){//updates the best score if the current score is higher
                        bestScore = currentScore;
                    }
                    currentScore = 0;//resets the current score
                    roundNum = 0;//resets the round number
                    for (int j = 0; j < NUMALIENS; j++) {
                        AliensArray[i].isAlive = false;//stops rendering anymore of the aliens
                    }
                    break;
                }
            }
            PlayerShip.paint(g);//paints the playership

            Iterator<PlayerBullet> iterator = bulletList.iterator(); //creates an iterator to go through the bullet list
            while(iterator.hasNext()){//while there is another bullet in the list
                PlayerBullet b = iterator.next();
                boolean isStillHere = b.move();//moves the bullet

                for (int i = 0; i < AliensArray.length; i++) {//checks for a collision with any of the aliens
                    if (AliensArray[i].isAlive) {//only checks alive aliens
                        isStillHere = b.doesNotCollide(AliensArray[i]);
                        if (!isStillHere){//if the bullet collidies with an alien
                            AliensArray[i].isAlive = false;//kill the alien
                            numOfDeadAliens++;//increase the number of dead aliens
                            currentScore += 20; //increase the current score
                            break;
                        }
                    }
                    else{//if the current alien is dead
                        numOfDeadAliens++;//increase the number of dead aliens
                    }
                }
                if (isStillHere) {//if the bullet is still here paint it
                    b.paint(g);
                }
                else {//remove the bullet if not here anymore
                    iterator.remove();
                }
            }
            String scoreString = "Score: " + currentScore + "                          Best Score: " + bestScore; // the scores
            g.setFont(new Font("Cosmic Sans", Font.PLAIN, 25));// setting the font
            g.setColor(Color.WHITE);//set the text colour to white
            g.drawString(scoreString, 100, 60);//draw the string at the top of the screen

            // flip the buffers offscreen<-->onscreen
            strategy.show();
        }
        else {//if in the menu
            g = offscreenGraphics;
            g.setColor(Color.BLACK);//draw a black rectange
            g.fillRect(0, 0, WindowSize.width, WindowSize.height);
            g.setColor(Color.WHITE);//set the text colour to white
            g.setFont(new Font("Comic Sans", Font.PLAIN, 45));
            g.drawString("Game Over", 300, 250);
            g.setFont(new Font("myFOnt", Font.PLAIN, 35));
            g.drawString("Press any button to start the game", 100, 350);
            strategy.show();//show the buffer
        }
    }

    // application entry point
    public static void main(String[] args) {
        workingDirectory = System.getProperty("user.dir");
        InvadersApplication w = new InvadersApplication();
    }

}
