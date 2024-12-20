package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import UI.Button_in_menu;
import main.Game;
import utilz.LoadSave;

public class Menu extends State implements Statemethods {
    private Button_in_menu[] buttons = new Button_in_menu[3];
    private BufferedImage backgroundImg, backgroundImgPink;
    private int menuX, menuY, menuWidth, menuHeight;

    public Menu(Game game) {
        super(game);
        LoadButtons();
        LoadBackground();
        backgroundImgPink = LoadSave.GetSpritesAtlas(LoadSave.MENU_BACKGROUND_IMG);

    }

    private void LoadBackground() {
        backgroundImg = LoadSave.GetSpritesAtlas(LoadSave.MENU_BACKGROUND);
        menuWidth = (int) (backgroundImg.getWidth() * Game.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Game.SCALE);
        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (45 * Game.SCALE);

    }

    private void LoadButtons() {
        buttons[0] = new Button_in_menu(Game.GAME_WIDTH / 2, (int) (150 * Game.SCALE), 0, Gamestate.PLAYING);
        buttons[1] = new Button_in_menu(Game.GAME_WIDTH / 2, (int) (220 * Game.SCALE), 1, Gamestate.OPTIONS);
        buttons[2] = new Button_in_menu(Game.GAME_WIDTH / 2, (int) (290 * Game.SCALE), 2, Gamestate.QUIT);
    }

    @Override
    public void update() {
        for (Button_in_menu mb : buttons)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgPink, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
        for (Button_in_menu mb : buttons)
            mb.draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            Gamestate.state = Gamestate.PLAYING;
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (Button_in_menu mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (Button_in_menu mb : buttons) {
            if (isIn(e, mb)) {
                if (mb.isMousePressed())
                    mb.applyGamestate();
                break;
            }
        }

        resetButtons();
    }

    private void resetButtons() {
        for (Button_in_menu mb : buttons)
            mb.resetBools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (Button_in_menu mb : buttons)
            mb.setMouseOver(false);

        for (Button_in_menu mb : buttons)
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }
    }

}
