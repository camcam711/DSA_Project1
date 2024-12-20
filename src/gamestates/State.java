package gamestates;

import java.awt.event.MouseEvent;

import UI.Button_in_menu;
import main.Game;

public class State {
    protected Game game;

    public State(Game game) {
        this.game = game;
    }

    public boolean isIn(MouseEvent e, Button_in_menu mb) {
        return mb.getBounds().contains(e.getX(), e.getY());
    }

    public Game getGame() {
        return game;
    }

}
