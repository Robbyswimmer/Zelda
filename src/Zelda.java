import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    //Determines if the game is over
    private static boolean endgame;

    //main game frame
    private static JFrame appFrame;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    //JFRAME Dimensions
    private static int WINWIDTH;
    private static int WINHEIGHT;

    //static image for the player
    private static BufferedImage player;

    //additional movement variables for link
    private static double lastPressed;

    //arrow keys for moving Link
    private static Boolean upPressed;
    private static Boolean downPressed;
    private static Boolean leftPressed;
    private static Boolean rightPressed;
    private static Boolean aPressed;
    private static Boolean xPressed;

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
        WINWIDTH = 338;
        WINHEIGHT = 271;

        //setting endgame to false so game starts correctly
        endgame = false;

        //initial offset for link
        XOFFSET = 0;
        YOFFSET = 40;

        //values for Link's character and the starting position of Link's ImageObject
        p1width = 20;
        p1height = 20;
        p1originalX = (double) XOFFSET + ((double) WINWIDTH / 2.0) - (p1width / 2.0);
        p1originalY = (double) YOFFSET + ((double) WINHEIGHT / 2.0) - (p1height / 2.0);

        //attempt to import castle images
        try {
            tempBG = ImageIO.read(new File("Images/castle/castle1.png"));
            player = ImageIO.read(new File("Images/Link/walking-down-1.png"));
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

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                System.out.println("Sleeping exception in the main method!");
            }

            endgame = false;

            //threads for managing different game actions
            Thread t1 = new Thread(new Animate());

            //start and manage the threads individually
            t1.start();
        }
    }

    /**
     * Animation class handles the initiation of drawing different dynamic objects
     * on screen throughout the game
     */
    private static class Animate implements Runnable {
        public void run() {
            drawBackground();
            drawPlayer();
            try {
                Thread.sleep(32);
            } catch (InterruptedException ie) {
                System.out.println("Exception caught in Animate!");
            }
        }
    }

    /**
     * Draws the player graphics
     */
    private static void drawPlayer() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(player, XOFFSET, YOFFSET, null);
    }

    /**
     * draws the background graphics
     */
    private static void drawBackground() {
        Graphics g = appFrame.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(tempBG, XOFFSET, YOFFSET, null);
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
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
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
