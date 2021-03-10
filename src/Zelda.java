import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;

public class Zelda {

    //variables for pi
    private static double pi = Math.PI;
    private static double twoPi = 2 * pi;
    private static double quarterPi = 0.25 * pi;
    private static double halfPi = 0.5 * pi;
    private static double threequartersPi = 0.75 * pi;
    private static double fivequartersPi = 1.25 * pi;
    private static double threehalvesPi = 1.5 * pi;
    private static double sevenquartersPi = 1.75 * pi;

    //Determines if the game is over
    private static boolean endgame;

    //main game frame
    private static JFrame appFrame;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    //JFRAME Dimensions
    private static int WINWIDTH;
    private static int WINHEIGHT;

    //temporary static images for the player for testing purposes
    private static BufferedImage player;
    private static BufferedImage player2;
    private static BufferedImage currentPlayer;

    //BufferedImage array to hold Links animations
    private static BufferedImage[] link = new BufferedImage[16];

    //links starting coordinates for changing the maps
    //NOTE: they are in array format, so 1 = 2 and 2 = 3 because it is 0 indexed
    private static int col = 1;
    private static int row = 2;

    //determines whether or not link is in the overworld or dungeon
    private static boolean isOverworld;

    //BufferedImage array to hold castle scenese
    private static BufferedImage[] castleScenes = new BufferedImage[5];

    //BufferedImage array to hold dungeon scenes
    private static BufferedImage[] dungeonScenes = new BufferedImage[5];

    //BufferedImage arrays for holding enemy animations
    private static BufferedImage[] purpleArmos = new BufferedImage[2];

    //Hashmap to store the images associated with values of Link's life
    private static BufferedImage[] heartImages = new BufferedImage[6];

    //2d arrays for storing the map tiles
    // FIXME I would like the datatype to be for a custom map object that stores data about enemies and other necessary information
    private static BufferedImage[][] overworldTiles = new BufferedImage[3][3];
    private static BufferedImage[][] dungeonTiles = new BufferedImage[3][3];

    //this is a variable to determine the current map tile that is being drawn by the drawBackground method
    //  -> it should change based on collisions that link makes with the map
    private static BufferedImage currentBackground;

    //Integer to store link's health:
    //   1 = half heart, 2 = 1 heart... 6 = 3 hearts
    private static int linksHealth;

    //additional movement variables for link
    private static double lastPressed;

    //arrow keys for moving Link
    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static Boolean aPressed;
    private static Boolean xPressed;

    private static Boolean up = false;
    private static Boolean down = false;

    //ImageObject for Link
    private static ImageObject p1;

    //ImageObjects lists for enemies
    private static ImageObject armos;
    private static ArrayList<ImageObject> purpleArmosList;

    //ImageObject variables for controlling Link
    private static double p1width;
    private static double p1height;
    private static double p1originalX;
    private static double p1originalY;
    private static double p1velocity;

    //offsets for positioning link in the game
    private static int XOFFSET;
    private static int YOFFSET;

    //Map images
    private static BufferedImage tempBG;

    //audio controllers
    private static Clip clip;
    private static Clip clip2;

    /**
     * Main method for calling setup, creating the app frame,
     * initializing the JPanel, creating start and quit buttons,
     * binding movement and attack keys and making the app frame visible
     */
    public static void main(String[] args) {

        //initialize setup variables / images
        setup();

        //setting up the game frame
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(WINWIDTH + 1, WINHEIGHT + 1);
        JPanel myPanel = new JPanel();

        //start button
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new StartGame());
        myPanel.add(startButton);

        //quit button
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new QuitGame());
        myPanel.add(quitButton);

        //bind keys for Link's movement
        bindKey(myPanel, "UP");
        bindKey(myPanel, "DOWN");
        bindKey(myPanel, "LEFT");
        bindKey(myPanel, "RIGHT");
        bindKey(myPanel, "X");
        bindKey(myPanel, "A");

        //make the game frame visible and add the correct content
        appFrame.getContentPane().add(myPanel, "South");
        appFrame.setVisible(true);
    }

    /**
     * Setup method for initializing important variables that need to be
     * set at the beginning of the game
     */
    public static void setup() {

        //initialize the frame for the game
        appFrame = new JFrame("The Legend of Zelda: Link's Awakening");
        WINWIDTH = 338;

        //original height 271
        WINHEIGHT = 271;

        //setting endgame to false so game starts correctly
        endgame = false;

        //initial offset for link
        XOFFSET = 0;
        YOFFSET = 40;

        //values for Link's character and the starting position of Link's ImageObject
        p1width = 20;
        p1height = 20;
        p1originalX = 150;
        p1originalY = 150;

        //initialize links health
        linksHealth = 5;

        //make sure link starts in the overworld
        isOverworld = true;

//        purpleArmosList.add(new ImageObject(100, 200, 20, 20, 0.0));

        //attempt to import all images for maps, player, and enemies
        try {

            //currentBackground is initialized to the starting background
            tempBG = ImageIO.read(new File("Images/castle/castle0.png"));
            currentBackground = tempBG;

            player = ImageIO.read(new File("Images/Link/walking0.png"));
            player2 = ImageIO.read(new File("Images/Link/walking1.png"));
            currentPlayer = player;

            //imports the images for links normal walking
            for (int i = 0; i < 8; i++) {
                BufferedImage tempImage = ImageIO.read(new File("Images/Link/walking" + i + ".png"));
                link[i] = tempImage;
                System.out.println("Initialized: Images/Link/walking" + i + ".png");
            }

            //imports the images for links sword attacks
            for (int i = 8; i < 12; i++) {
                BufferedImage tempImage = ImageIO.read(new File("Images/Link/sword" + (i - 8) + ".png"));
                link[i] = tempImage;
                System.out.println("Initialized: Images/Link/Sword" + (i - 8) + ".png");
            }

            //imports the images for links shield blocks
            for (int i = 12; i < 16; i++) {
                BufferedImage tempImage = ImageIO.read(new File("Images/Link/shield" + (i - 12) + ".png"));
                link[i] = tempImage;
                System.out.println("Initialized: Images/Link/Shield" + (i - 12) + ".png");
            }

            //imports the images for castleScenes
            for (int i = 0; i < 5; i++) {
                BufferedImage tempBGImages = ImageIO.read(new File("Images/castle/castle" + (i) + ".png"));
                castleScenes[i] = tempBGImages;
                System.out.println("Initialized: Images/castle/castle" + (i) + ".png");
            }

            //imports the images for dungeonScenes
            for (int i = 0; i < 5; i++) {
                BufferedImage tempBGImages = ImageIO.read(new File("Images/dungeon/angler" + (i) + ".png"));
                castleScenes[i] = tempBGImages;
                System.out.println("Initialized: Images/dungeon/angler" + (i) + ".png");
            }

            for (int i = 0; i < 6; i++) {
                BufferedImage tempHeartImage = ImageIO.read(new File("Images/hearts/heart" + (i + 1) + ".png"));
                heartImages[i] = tempHeartImage;
                System.out.println("Initialized: Images/hearts/heart" + (i + 1) + ".png");
            }

            loadOverworldImages();
            loadDungeonImages();

            //imports the images for the purple Armos
            purpleArmos[0] = ImageIO.read(new File("Images/Enemies/PurpleArmos1.png"));
            purpleArmos[1] = ImageIO.read(new File("Images/Enemies/PurpleArmos1.png"));

        } catch (IOException ioe) {
            System.out.println("Did not import an image correctly in the setup method");
        }
    }

    private static void loadOverworldImages() {

        try {
            overworldTiles[2][1] = ImageIO.read(new File("Images/castle/castle0.png"));
            overworldTiles[2][2] = ImageIO.read(new File("Images/castle/castle1.png"));
            overworldTiles[2][0] = ImageIO.read(new File("Images/castle/castle2.png"));
            overworldTiles[1][1] = ImageIO.read(new File("Images/castle/castle3.png"));
            overworldTiles[1][2] = ImageIO.read(new File("Images/castle/castle4.png"));
        } catch (IOException ioe) {
            System.out.println("Exception in loadOverworld method!");
        }

    }

    private static void loadDungeonImages() {

        try {
            dungeonTiles[1][0] = ImageIO.read(new File("Images/dungeon/angler0.png"));
            dungeonTiles[2][0] = ImageIO.read(new File("Images/dungeon/angler2.png"));
            dungeonTiles[2][1] = ImageIO.read(new File("Images/dungeon/angler3.png"));
            dungeonTiles[1][1] = ImageIO.read(new File("Images/dungeon/angler1.png"));
            dungeonTiles[0][1] = ImageIO.read(new File("Images/dungeon/angler4.png"));
        } catch (IOException ioe) {
            System.out.println("Exception in loadOverworld method!");
        }

    }


    /**
     * Class responsible for quitting the game when the quit game button is pressed
     */
    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
            clip.stop();
        }
    }

    /**
     * When the start game button is pressed all of the below variables are set or reset.
     * This class is also responsible for starting game play threads and handling the different
     * dynamic objects present in the game.
     */
    private static class StartGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //variables that need to be initialized at the beginning of the game
            endgame = true;

            //Link's movement variables
            upPressed = false;
            downPressed = false;
            leftPressed = false;
            rightPressed = false;
            aPressed = false;
            xPressed = false;
            lastPressed = 90.0;

            //p1 initial variables for Link's ImageObject
            p1 = new ImageObject(p1originalX, p1originalY, p1width, p1height, 0.0);
            p1velocity = 0.0;
            p1.setInternalAngle(threehalvesPi);
            p1.setMaxFrames(2);
            p1.setlastposx(p1originalX);
            p1.setlastposy(p1originalY);

            armos = new ImageObject(100, 200, 20, 20, 0.0);
            armos.setInternalAngle(threehalvesPi);
            armos.setMaxFrames(2);
            armos.setlastposx(100);
            armos.setlastposy(200);
            //purpleArmosList.add(armos);


            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Sleeping exception in the main method!");
            }

            endgame = false;

            //threads for managing different game actions
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());
            Thread t3 = new Thread(new SoundSystem());
            Thread t4 = new Thread(new EnemyMover());

            //start and manage the threads individually
            t1.start();
            t2.start();
            t3.start();
            t4.start();
        }
    }

    /**
     * Animation class handles the initiation of drawing different dynamic objects
     * on screen throughout the game
     */
    private static class Animate implements Runnable {
        public void run() {
            while (!endgame) {
                drawBackground();
                drawPlayer();
                drawEnemies();
//                System.out.println("Player x pos: " + p1.getX() + ", Player y pos: " + p1.getY());
                try {
                    Thread.sleep(48);
                } catch (InterruptedException ie) {
                    System.out.println("Exception caught in Animate!");
                }
            }
        }
    }

    /**
     * A Runnable sound system for handling all of the general
     * sounds in the game
     */
    private static class SoundSystem implements Runnable {
        public void run() {
            backgroundMusic();
            while (!endgame) {
                //links sounds
                runningSounds();
                swordSounds();
                shieldSounds();
                try {
                    Thread.sleep(48);
                } catch (InterruptedException ie) {
                    System.out.println("Interrupted exception in sound system!");
                }
            }
        }
    }

    /**
     * The main background music for the whole game
     */
    private static void backgroundMusic() {
        String main = "Sounds/main.wav";
        String dungeon = "Sounds/dungeon-music.wav";
        audioHelper(main);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            System.out.println("Interrupted exception in sound system!");
        }
    }

    /**
     * Retrieving the correct sounds for links shield
     */
    private static void shieldSounds() {
        if (aPressed) {
            String linkSword = "Sounds/Shield.wav";
            audioHelper(linkSword);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                System.out.println("Interrupted exception in sound system!");
            }
        }
    }

    /**
     * Retrieving the correct sounds for links sword attacks
     */
    private static void swordSounds() {
        if (xPressed) {
            String linkSword = "Sounds/Sword2.wav";
            audioHelper(linkSword);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                System.out.println("Interrupted exception in sound system!");
            }
        }
    }

    /**
     * Retrieving the correct sounds for links running
     */
    private static void runningSounds() {
        if (downPressed || upPressed || leftPressed || rightPressed) {
            String linkRunning = "Sounds/Link-Run.wav";
            audioHelper(linkRunning);
        }
    }

    /**
     * Helper method to reduce redundancy when retrieving game sounds
     *
     * @param filename the name of the sound file being retrieved
     */
    private static void audioHelper(String filename) {
        try {
            AudioInputStream audioFile = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            clip.open(audioFile);
            clip.start();
            Thread.sleep(120);
        } catch (UnsupportedAudioFileException uafe) {
            System.out.println("Unsupported audio file type!");
        } catch (IOException ioe) {
            System.out.println("IO exception for a sound!");
        } catch (LineUnavailableException lue) {
            System.out.println("Line unavailable exception for a sound!");
        } catch (InterruptedException ie) {
            System.out.println("Interrupted exception for one of the sounds!");
        }
    }

    /**
     * Method for drawing links current health
     */
    private static void drawHearts() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
//        g2d.drawImage(heartImages[linksHealth], 10, 40, 110, 40, null);

    }

    /**
     * Draws the player graphics and animations according to what buttons are being pressed
     */
    private static void drawPlayer() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        if (upPressed || downPressed || leftPressed || rightPressed || xPressed || aPressed) {
            //animations: 2-3 = up, 0-1 = down, 3-4 = left, 5-6 = right
            if (upPressed) drawCharacterHelper(p1, 2, g2d, link);
            if (downPressed) drawCharacterHelper(p1, 0, g2d, link);
            if (leftPressed) drawCharacterHelper(p1, 4, g2d, link);
            if (rightPressed) drawCharacterHelper(p1, 6, g2d, link);
            if (xPressed) drawPlayerHelperFighting(g2d);
            if (aPressed) drawPlayerHelperShield(g2d);
        } else {
            //down, right, left, up
            lastPressedHelper(g2d, 1, 3, 5, 7);
        }
    }

    /**
     * Test method for drawing enemies
     */
    private static void drawEnemies() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        //create a test enemy
        drawCharacterHelper(armos, 0, g2d, purpleArmos);
    }

    /**
     * Helper for determining the current direction Link is facing in so that
     * the correct sword attack animation can be drawn
     *
     * @param g2d importing graphics for drawing the correct animation
     */
    private static void drawPlayerHelperFighting(Graphics2D g2d) {
        lastPressedHelper(g2d, 8, 9, 10, 11);
    }

    /**
     * Helper for determining the current direction Link is facing in so that
     * the correct shield animation can be drawn
     *
     * @param g2d importing graphics for drawing the correct animation
     */
    private static void drawPlayerHelperShield(Graphics2D g2d) {
        lastPressedHelper(g2d, 12, 13, 14, 15);
    }

    /**
     * Helper to reduce redundancy in the shield and sword animation methods
     * since they utlize the same directional code for drawing animations
     *
     * @param g2d graphics for drawing animations
     * @param n1  down animation
     * @param n2  up animation
     * @param n3  left animation
     * @param n4  right animationn
     */
    private static void lastPressedHelper(Graphics2D g2d, int n1, int n2, int n3, int n4) {
        if (Math.abs(lastPressed - 270.0) < 1.0) drawPlayerHelper2(n1, g2d);
        if (Math.abs(lastPressed - 90.0) < 1.0) drawPlayerHelper2(n2, g2d);
        if (Math.abs(lastPressed - 180.0) < 1.0) drawPlayerHelper2(n3, g2d);
        if (Math.abs(lastPressed - 0.0) < 1.0) drawPlayerHelper2(n4, g2d);
    }

    /**
     * This is just a helper method to reduce redundancy when drawing Link and enemies
     *
     * @param animationNumber the index of the link animation being drawn
     */
    private static void drawCharacterHelper(ImageObject character, int animationNumber, Graphics2D g2d, BufferedImage[] animationSet) {
        if (character.getCurrentFrame() == 0) {
            g2d.drawImage(rotateImageObject(character).filter(animationSet[animationNumber], null), (int) (character.getX() + 0.5),
                    (int) (character.getY() + 0.5), null);
        } else if (character.getCurrentFrame() == 1) {
            g2d.drawImage(rotateImageObject(character).filter(animationSet[animationNumber + 1], null), (int) (character.getX() + 0.5),
                    (int) (character.getY() + 0.5), null);
        }
        character.updateCurrentFrame();
    }

    /**
     * Just to reduce code redundancy when links animations are being drawn
     *
     * @param animationNumber the index of the link animation being drawn
     * @param g2D             the graphics being imported
     */
    private static void drawPlayerHelper2(int animationNumber, Graphics2D g2D) {
        g2D.drawImage(rotateImageObject(p1).filter(link[animationNumber], null), (int) (p1.getX() + 0.5),
                (int) (p1.getY() + 0.5), null);
    }

    /**
     * draws the map background graphics
     */
    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
//        g2d.setBackground(Color.white);
//        g2d.setColor(Color.WHITE);
//        g2d.fillRect(0, 0, 500, 550);
        g2d.drawImage(currentBackground, XOFFSET, YOFFSET, null);

        //this draws the hearts better for some reason than having the drawhearts method be separate
        //please do not delete :(
        g2d.drawImage(heartImages[linksHealth], 10, 40, 70, 20, null);
    }

    /**
     * The class responsible for handling enemy movement
     */
    private static class EnemyMover implements Runnable {
        private double velocityStep;
        Graphics G = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) G;

        public EnemyMover() {
            velocityStep = 0.5;
        }

        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    System.out.println("Caught exception in EnemyMover!");
                }
//                purpleArmosList.get(0).move(velocityStep * Math.cos(purpleArmosList.get(0).getInternalAngle()),
//                        velocityStep * Math.sin(purpleArmosList.get(0).getInternalAngle()));

                //angle = 0.0 moves to the right, anlge = 90.0 moves to the left
                //FIXME make it so that it moves right for a period of time, then left for the same period and have it repeat
                armos.setInternalAngle(0.0);
                armos.move(velocityStep * Math.cos(armos.getInternalAngle()), 0.0);
            }
        }
    }

    /**
     * The class responsible for handling player movement,
     * storing the velocity constant, determining strafing angle
     * and bouncing on certain collisions
     */
    private static class PlayerMover implements Runnable {

        private double velocityStep;
        Graphics G = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) G;

        public PlayerMover() {
            velocityStep = 1;
        }

        // need to check which scene is active, then sent the collisions based
        // off of that scene.


        public void run() {
            while (!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    System.out.println("Caught exception in PlayerMover!");
                }

                if (!inBounds(p1.getX(), p1.getY())) {
                    changeSceneBackground();
                }

//                if(linksHealth < 0){
//                    endgame = true;
//                }

                //TODO need to check with other enemies too!!
                //overlapping link with armos
                if (collision(p1.getX(), p1.getY(), armos.getX(), armos.getY())) {
                    if (!aPressed || !xPressed) { // shield not up, sword not used
                        //link loses half heart
                        //FIXME checks too quickly.. kinda works, but link goes to 0 super fast
                        // and both characters disappear
                        //linksHealth -= 1;
                        //System.out.println(linksHealth);
                    } else if (xPressed) { // sword used
                        //enemy loses half heart

                    }
                }

//                System.out.println("X coordinates: " + p1.getX() + ", Y coordinates: " + p1.getY());

                //handles lines of movement for link, including strafing
                if (upPressed || downPressed || leftPressed || rightPressed) {
                    //set the velocity of the player equal to the constant movement velocity variable
                    p1velocity = velocityStep;

                    if (upPressed) {
                        if (leftPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (rightPressed) {
                            p1.setInternalAngle(5.49779);
                        } else {
                            p1.setInternalAngle(threehalvesPi);
                        }
                    }
                    if (downPressed) {
                        if (leftPressed) {
                            p1.setInternalAngle(2.35619);
                        } else if (rightPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(halfPi);
                        }
                    }
                    if (leftPressed) {
                        if (upPressed) {
                            p1.setInternalAngle(fivequartersPi);
                        } else if (downPressed) {
                            p1.setInternalAngle(threequartersPi);
                        } else {
                            p1.setInternalAngle(pi);
                        }
                    }
                    if (rightPressed) {
                        if (upPressed) {
                            p1.setInternalAngle(5.49779);
                        } else if (downPressed) {
                            p1.setInternalAngle(quarterPi);
                        } else {
                            p1.setInternalAngle(0.0);
                        }
                    }

                } else {
                    p1velocity = 0.0;
                    p1.setInternalAngle(threehalvesPi);
                }

                //checks bounce condition for link
                p1.updateBounce();

                //correct movement code for link
                p1.move(p1velocity * Math.cos(p1.getInternalAngle()), p1velocity * Math.sin(p1.getInternalAngle()));

                //does wrapping for links ImageObject
//                int wrap = p1.screenWrap(XOFFSET, XOFFSET + WINWIDTH, YOFFSET, YOFFSET + WINHEIGHT);

                //FIXME maybe implement 'backgroundState' array with screenwrap functionality

                //FIXME maybe implement the clearEnemies and generateEnemies methods here depending on screenWrap
                //CHECK source code for more info in playerMover
            }
        }

        private static void changeSceneBackground() {
            // perhaps create a collision for each background
            // image so that it triggers a new scene...
            // I do not condone hardcoding values, but i will allow it for this project – Robby

            BufferedImage[][] currentTileSet;

            if (!isOverworld) {
                currentTileSet = dungeonTiles;
            } else {
                currentTileSet = overworldTiles;
            }

            //load dungeon
            if (isOverworld && row == 1 && col == 2 && p1.getX() > 220 && p1.getY() < 80 && p1.getX() < 250) {
                //change boolean to dungeon
                isOverworld = false;
                currentBackground = dungeonTiles[1][0];
                p1.moveto(150, 150);
                col = 0;
                row = 1;
            }

            //left bound
            if (p1.getX() <= -2.0) {
                if (currentTileSet[row][col - 1] != null && col > 0) {
                    p1.moveto(p1.getX() + 328, p1.getY());
                    currentBackground = currentTileSet[row][col - 1];
                    col--;
                }
            }
            //right bound
            if (p1.getX() >= 328.0) {
                if (currentTileSet[row][col + 1] != null && col < 2) {
                    p1.moveto(p1.getX() - 328, p1.getY());
                    currentBackground = currentTileSet[row][col + 1];
                    col++;
                }
            }
            //top bound
            if(p1.getY() <= 33){
                if (currentTileSet[row - 1][col] != null && row > 0) {
                    p1.moveto(p1.getX(), p1.getY() + 245);
                    currentBackground = currentTileSet[row - 1][col];
                    row--;
                }

            }
            //bottom bound
            if(p1.getY() >= 264){
                if (currentTileSet[row + 1][col] != null && row < 2) {
                    p1.moveto(p1.getX(), p1.getY() - 245);
                    currentBackground = currentTileSet[row + 1][col];
                    row++;
                }
            }
        }

        private static boolean inBounds(double playerX, double playerY) {
            return playerX < -2.0 && playerX > 328.0 && playerY < 25 && playerY > 300;
        }

        private static boolean collision(double playerX, double playerY, double enemyX, double enemyY) {
            return (playerX < enemyX) && (enemyX < playerX + player.getWidth()) && (playerY < enemyY) &&
                    (enemyY - player.getHeight() < playerY);
        }
    }

    /**
     * Detects whether keys were pressed or not
     */
    private static class KeyPressed extends AbstractAction {
        public KeyPressed() {
            action = "";
        }

        public KeyPressed(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = true;
                lastPressed = 90.0;
            }
            if (action.equals("DOWN")) {
                downPressed = true;
                lastPressed = 270.0;
            }
            if (action.equals("LEFT")) {
                leftPressed = true;
                lastPressed = 180.0;
            }
            if (action.equals("RIGHT")) {
                rightPressed = true;
                lastPressed = 0.0;
            }
            if (action.equals("A")) {
                aPressed = true;
            }
            if (action.equals("X")) {
                xPressed = true;
            }
        }

        private String action;
    }

    /**
     * Detects whether keys were released or not
     */
    private static class KeyReleased extends AbstractAction {
        public KeyReleased() {
            action = "";
        }

        public KeyReleased(String input) {
            action = input;
        }

        public void actionPerformed(ActionEvent e) {
            if (action.equals("UP")) {
                upPressed = false;
            }
            if (action.equals("DOWN")) {
                downPressed = false;
            }
            if (action.equals("LEFT")) {
                leftPressed = false;
            }
            if (action.equals("RIGHT")) {
                rightPressed = false;
            }
            if (action.equals("A")) {
                aPressed = false;
            }
            if (action.equals("X")) {
                xPressed = false;
            }
        }

        private String action;
    }

    /**
     * Takes the codes for the keyboard keys and binds them to strings so that
     * key presses can be detected within the app panel and used for movement
     *
     * @param myPanel the main app panel
     * @param input   the key that is being bound
     */
    private static void bindKey(JPanel myPanel, String input) {
        System.out.println("Bound " + input + " key.");
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

    /**
     * Helper method for rotating the imageobject according to current orientation
     */
    private static AffineTransformOp rotateImageObject(ImageObject obj) {
        AffineTransform at = AffineTransform.getRotateInstance(-obj.getAngle(), obj.getWidth() / 2.0, obj.getHeight() / 2.0);
        AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        return atop;
    }

    /**
     * This is the main class that is responsible for setting up every enemy object that is in the game
     * It controls the location of the enemies, their health, speed, and the damage they do to link.
     */
    private static class enemyObject {

        public BufferedImage[] animations;
        private int xCoord;
        private int yCoord;
        private int speed;
        private int damage;
        private int health;

        public enemyObject(BufferedImage[] enemyAnimations, int x, int y, int movementSpeed, int damageToLink, int enemyHealth) {
            this.animations = enemyAnimations;
            this.xCoord = x;
            this.yCoord = y;
            this.speed = movementSpeed;
            this.damage = damageToLink;
            this.health = enemyHealth;
        }

        public void doDamage(int damage) {
            health -= damage;
        }

        public void setX(int x) {
            xCoord = x;
        }

        public void setY(int y) {
            yCoord = y;
        }

        public int getX() {
            return xCoord;
        }

        public int getY() {
            return yCoord;
        }
    }

    /**
     * ImageObject is the object that is attached to images so that they can move
     * within the app frame and give the appearance of motion and animation
     */
    private static class ImageObject {

        //initialization variables for ImageObject
        private double x;
        private double y;
        private double lastposx;
        private double lastposy;
        private double xwidth;
        private double yheight;
        private double angle;// in Radians
        private double internalangle;// in Radians
        private Vector<Double> coords;
        private Vector<Double> triangles;
        private double comX;
        private double comY;
        private int maxFrames;
        private int currentFrame;
        private int life;
        private int maxLife;
        private int dropLife;
        private Boolean bounce;

        public ImageObject() {
            maxFrames = 1;
            currentFrame = 0;
            bounce = false;
            life = 1;
            maxLife = 1;
            dropLife = 0;
        }

        public ImageObject(double xinput, double yinput, double xwidthinput, double yheightinput, double angleinput) {
            this();
            x = xinput;
            y = yinput;
            lastposx = x;
            lastposy = y;
            xwidth = xwidthinput;
            yheight = yheightinput;
            angle = angleinput;
            internalangle = 0.0;
            coords = new Vector<Double>();
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getlastposx() {
            return lastposx;
        }

        public double getlastposy() {
            return lastposy;
        }

        public void setlastposx(double input) {
            lastposx = input;
        }

        public void setlastposy(double input) {
            lastposy = input;
        }

        public double getWidth() {
            return xwidth;
        }

        public double getHeight() {
            return yheight;
        }

        public double getAngle() {
            return angle;
        }

        public double getInternalAngle() {
            return internalangle;
        }

        public void setAngle(double angleinput) {
            angle = angleinput;
        }

        public void setInternalAngle(double internalangleinput) {
            internalangle = internalangleinput;
        }

        public Vector<Double> getCoords() {
            return coords;
        }

        public void setCoords(Vector<Double> coordsinput) {
            coords = coordsinput;
            generateTriangles();
            //printTriangles();
        }

        public int getMaxFrames() {
            return maxFrames;
        }

        public void setMaxFrames(int input) {
            maxFrames = input;
        }

        public int getCurrentFrame() {
            return currentFrame;
        }

        public void setCurrentFrame(int input) {
            currentFrame = input;
        }

        public Boolean getBounce() {
            return bounce;
        }

        public void setBounce(Boolean input) {
            bounce = input;
        }

        public int getLife() {
            return life;
        }

        public void setLife(int input) {
            life = input;
        }

        public int getMaxLife() {
            return maxLife;
        }

        public void setMaxLife(int input) {
            maxLife = input;
        }

        public int getDropLife() {
            return dropLife;
        }

        public void setDropLife(int input) {
            dropLife = input;
        }

        public void updateBounce() {
            if (getBounce()) {
                moveto(getlastposx(), getlastposy());
            } else {
                setlastposx(getX());
                setlastposy(getY());
            }
            setBounce(false);
        }

        public void updateCurrentFrame() {
            currentFrame = (currentFrame + 1) % maxFrames;
        }

        public void generateTriangles() {
            triangles = new Vector<Double>();  // format: (0, 1), (2, 3), (4, 5) is the (x, y) coords of a triangle.
            // get center point of all coordinates.
            comX = getComX();
            comY = getComY();
            for (int i = 0; i < coords.size(); i = i + 2) {
                triangles.addElement(coords.elementAt(i));
                triangles.addElement(coords.elementAt(i + 1));
                triangles.addElement(coords.elementAt((i + 2) % coords.size()));
                triangles.addElement(coords.elementAt((i + 3) % coords.size()));
                triangles.addElement(comX);
                triangles.addElement(comY);
            }
        }

        public void printTriangles() {
            for (int i = 0; i < triangles.size(); i = i + 6) {
                System.out.print("p0x: " + triangles.elementAt(i) + ",p0y: " + triangles.elementAt(i + 1));
                System.out.print(" p1x: " + triangles.elementAt(i + 2) + ",p1y:  " + triangles.elementAt(i + 3));
                System.out.println("p2x: " + triangles.elementAt(i + 4) + "  ,p2y: " + triangles.elementAt(i + 5));
            }
        }

        public double getComX() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 0; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public double getComY() {
            double ret = 0;
            if (coords.size() > 0) {
                for (int i = 1; i < coords.size(); i = i + 2) {
                    ret = ret + coords.elementAt(i);
                }
                ret = ret / (coords.size() / 2.0);
            }
            return ret;
        }

        public void move(double xinput, double yinput) {
            x = x + xinput;
            y = y + yinput;
        }

        public void moveto(double xinput, double yinput) {
            x = xinput;
            y = yinput;
        }

        public int screenWrap(double leftEdge, double rightEdge, double topEdge, double bottomEdge) {
            int ret = 0;
            if (x > rightEdge) {
                moveto(leftEdge, getY());
                ret = 1;
            }
            if (x < leftEdge) {
                moveto(rightEdge, getY());
                ret = 2;
            }
            if (y > bottomEdge) {
                moveto(getX(), topEdge);
                ret = 3;
            }
            if (y < topEdge) {
                moveto(getX(), bottomEdge);
                ret = 4;
            }
            return ret;
        }

        public void rotate(double angleinput) {
            angle = angle + angleinput;
            while (angle > twoPi) {
                angle = angle - twoPi;
            }
            while (angle < 0) {
                angle = angle + twoPi;
            }
        }

        public void spin(double internalangleinput) {
            internalangle = internalangle + internalangleinput;
            while (internalangle > twoPi) {
                internalangle = internalangle - twoPi;
            }
            while (internalangle < 0) {
                internalangle = internalangle + twoPi;
            }
        }
    }
}
