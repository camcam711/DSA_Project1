package main;

import javax.swing.JPanel;

import input.KeyboardInputs;// import a class from different package
import input.MouseInputs;
// import java.awt.Graphics;
// import java.awt.event.KeyListener;

import static main.Game.GAME_HEIGHT;
import static main.Game.GAME_WIDTH;

import java.awt.*; // this is used for importing Graphics class ( java.awt.Graphics)

public class GamePanel extends JPanel {

    private MouseInputs mouseInputs;
    private Game game;

    // inside the constructor is where we add our inputs to the game
    public GamePanel(Game game) {
        this.game = game;

        // the line below create an object of MouseInputs class, so that the object can
        // be passed through 2 methods relating to the MouseInputs class which are
        // addMouseListener and addMouseMotionListener
        // --

        MouseInputs mouseInputs = new MouseInputs(this);
        // create KeyboardInputs object to pass in addKeyListener() which is used for
        // tracking the keyboard
        KeyboardInputs keyboardInputs = new KeyboardInputs(this);
        // place the KeyboardInputs object inside the addKeyListener method
        // --
        // Since the object implement the KeyListener, so object can use methods
        // included in KeyListener interface for the addKeylistener method
        // --
        addKeyListener(keyboardInputs);// (GamePanel gamePanel)
        // for clicking, pressing, releasing the mouse
        // --
        // We have to init the variable (considered as object) at first, else we adding
        // 2 different mouse inputs (new MouseInputs())
        // ---
        // whenever the event of the mouse object fits the addMouseListener method, the
        // methods that fit the addMouseListener() will be automatically called by the
        // Java runtime system
        addMouseListener(mouseInputs);
        // for moving(the cursor on the screen), dragging(click and move the mouse)
        addMouseMotionListener(mouseInputs);
        setPanelSize();

    }

    // set the size of the panel
    private void setPanelSize() {
        Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
        setMinimumSize(size);
        setPreferredSize(size);
        setMaximumSize(size);
    }

    public void updateGame() {

    }

    // the paintComponent method is in JPanel, we never call it, it is called when
    // the game is started

    // JPanel itself can not draw, it needs a Graphics object to do it
    // JFrame + JPanel = GameWindow
    // Graphics allow us to draw to the Panel
    // basically, when we need to draw something, we need paintComponent method from
    // JPanel class with
    // object "g" from Graphics class
    // ----------- Graphics g in this case is a object parameter
    // object parameter exists when the parameter type is a class.
    // --
    // The paintComponent method is a part of the JPanel class which gets
    // automatically called by the Swing framework under certain situations, such as
    // when the window is resized or made visible
    public void paintComponent(Graphics g) {
        // this line below is calling JComponent's paintComponent, JComponent is the
        // superclass of JPanel
        // It looks like this, " public class JPanel extends JComponent implements
        // Accessible "
        super.paintComponent(g);

        // the line below is used to draw image on the screen, (0,0) stands for the
        // position of image
        // g.drawImage(image, (int) 0, (int) 0, null);
        // the line below is used to draw image on the screen, (xDelta, yDelta) stands
        // for the position of image, and (100,100) stands for the size of the image
        // g.drawImage(image, (int) xDelta, (int) yDelta, 500, 500, null);
        // g.drawImage(animation[playerAction][animationIndex], (int) xDelta, (int)
        // yDelta, 129, 80, null);
        game.render(g);
    }

    public Game getGame() {
        return game;
    }

}
