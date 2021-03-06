import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.time.*;
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
    private static BufferedImage[] link = new BufferedImage[12];

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
        appFrame.setSize(WINWIDTH + 1, WINHEIGHT + 85);
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
        WINWIDTH = 500;
        WINHEIGHT = 500;

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

        //attempt to import castle images
        try {
            tempBG = ImageIO.read(new File("Images/castle/castle1.png"));
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

        } catch (IOException ioe) {
            System.out.println("Did not import an image correctly in the setup method");
        }

    }

    /**
     * Class responsible for quitting the game when the quit game button is pressed
     */
    private static class QuitGame implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            endgame = true;
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

            //FIXME implement the set life variables for p1

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Sleeping exception in the main method!");
            }

            endgame = false;

            //threads for managing different game actions
            Thread t1 = new Thread(new Animate());
            Thread t2 = new Thread(new PlayerMover());

            //start and manage the threads individually
            t1.start();
            t2.start();
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
                try {
                    Thread.sleep(48);
                } catch (InterruptedException ie) {
                    System.out.println("Exception caught in Animate!");
                }
            }
        }
    }

    /**
     * Draws the player graphics and animations according to what buttons are being pressed
     */
    private static void drawPlayer() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;

        if (upPressed || downPressed || leftPressed || rightPressed || xPressed) {
            //animations: 2-3 = up, 0-1 = down, 3-4 = left, 5-6 = right
            if (upPressed) drawPlayerHelper(2, g2d);
            if (downPressed) drawPlayerHelper(0, g2d);
            if (leftPressed) drawPlayerHelper(4, g2d);
            if (rightPressed) drawPlayerHelper(6, g2d);
            if (xPressed) drawPlayerHelperFighting(g2d);
        } else {
            //down, right, left, up
            if (Math.abs(lastPressed - 270.0) < 1.0) drawPlayerHelper2(1, g2d);
            if (Math.abs(lastPressed - 0.0) < 1.0) drawPlayerHelper2(7, g2d);
            if (Math.abs(lastPressed - 180.0) < 1.0) drawPlayerHelper2(5, g2d);
            if (Math.abs(lastPressed - 90.0) < 1.0) drawPlayerHelper2(3, g2d);
        }
    }

    /**
     * Helper for determining the current direction Link is facing in so that
     * the correct sword attack animation can be drawn
     * @param g2d importing graphics for drawing the correct animation
     */
    private static void drawPlayerHelperFighting(Graphics2D g2d) {
        if (Math.abs(lastPressed - 270.0) < 1.0) drawPlayerHelper2(8, g2d);
        if (Math.abs(lastPressed - 90.0) < 1.0) drawPlayerHelper2(9, g2d);
        if (Math.abs(lastPressed - 180.0) < 1.0) drawPlayerHelper2(10, g2d);
        if (Math.abs(lastPressed - 0.0) < 1.0) drawPlayerHelper2(11, g2d);
    }

    /**
     * This is just a helper method to reduce redundancy in the drawPlayer method
     * @param animationNumber the index of the link animation being drawn
     */
    private static void drawPlayerHelper(int animationNumber, Graphics2D g2D) {

        if (p1.getCurrentFrame() == 0) {
            g2D.drawImage(rotateImageObject(p1).filter(link[animationNumber], null), (int) (p1.getX() + 0.5),
                    (int) (p1.getY() + 0.5), null);
        } else if (p1.getCurrentFrame() == 1) {
            g2D.drawImage(rotateImageObject(p1).filter(link[animationNumber + 1], null), (int) (p1.getX() + 0.5),
                    (int) (p1.getY() + 0.5), null);
        }
        p1.updateCurrentFrame();
    }

    /**
     * Just to reduce code redundancy when links animations are being drawn
     * @param animationNumber the index of the link animation being drawn
     * @param g2D the graphics being imported
     */
    private static void drawPlayerHelper2(int animationNumber, Graphics2D g2D) {
        g2D.drawImage(rotateImageObject(p1).filter(link[animationNumber], null), (int) (p1.getX() + 0.5),
                (int) (p1.getY() + 0.5), null);
    }

    /**
     * draws the background graphics
     */
    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
//        g2d.setBackground(Color.white);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 500, 550);
//        g2d.drawImage(tempBG, XOFFSET, YOFFSET, null);
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

        public void run() {
            while(!endgame) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ie) {
                    System.out.println("Caught exception in PlayerMover!");
                }

                //handles lines of movement for link, including strafing
                if (upPressed || downPressed || leftPressed || rightPressed) {

                    //for debugging if input is being received or not
                    //System.out.println("Press detected!");

                    //set the velocity of the player equal to the constant movement velocity variable
                    p1velocity = velocityStep;
                    //System.out.println("Velocity of the player: " + p1velocity);

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
     * @param myPanel the main app panel
     * @param input the key that is being bound
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
