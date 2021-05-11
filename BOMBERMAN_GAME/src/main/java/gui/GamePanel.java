package main.java.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import main.java.Game;
import main.java.exceptions.BombermanException;

public class GamePanel extends JPanel {

    private Game _game;

    public GamePanel(Frame frame) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));

        try {
            _game = new Game(frame);

            add(_game);

            _game.setVisible(true);

        } catch (BombermanException e) {
            e.printStackTrace();
            System.exit(0);
        }
        setVisible(true);
        setFocusable(true);

    }

    public void changeSize() {
        setPreferredSize(new Dimension(Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE));
        revalidate();
        repaint();
    }

    public Game getGame() {
        return _game;
    }

}
