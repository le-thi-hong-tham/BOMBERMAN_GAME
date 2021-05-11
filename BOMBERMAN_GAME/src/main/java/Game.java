package main.java;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


import main.java.exceptions.BombermanException;
import main.java.graphics.Screen;
import main.java.gui.Frame;
import main.java.input.Keyboard;

public class Game extends Canvas {




    public static final int TILES_SIZE = 16,
            WIDTH = TILES_SIZE * (int)(62 / 2),
            HEIGHT = 13 * TILES_SIZE;

    public static int SCALE = 3;

    public static final String TITLE = "Bomberman " ;


    private static final int BOMBRATE = 1;
    private static final int BOMBRADIUS = 1;
    private static final double PLAYERSPEED = 1.0;
    private static final int WALLPASS = 0;
    private static final int PLAYERLIVE = 0;

    public static final int TIME = 200;
    public static final int POINTS = 0;
    public static final int LIVES = 3;

    protected static int SCREENDELAY = 3;



    protected static int bombRate = BOMBRATE;
    protected static int bombRadius = BOMBRADIUS;
    protected static double playerSpeed = PLAYERSPEED;
    protected static int wallpass = WALLPASS;
    protected int _screenDelay = SCREENDELAY;
    protected static int playerlive = PLAYERLIVE;

    private Keyboard _input;
    private boolean _running = false;
    private boolean _paused = true;

    private Board _board;
    private Screen screen;
    private Frame _frame;


    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    public Game(Frame frame) throws BombermanException {
        _frame = frame;
        _frame.setTitle(TITLE);

        screen = new Screen(WIDTH, HEIGHT);
        _input = new Keyboard();

        _board = new Board(this, _input, screen);
        addKeyListener(_input);
    }


    private void renderGame() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        screen.clear();

        _board.render(screen);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen._pixels[i];
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        _board.renderMessages(g);

        g.dispose();
        bs.show();
    }

    private void renderScreen() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null) {
            createBufferStrategy(3);
            return;
        }

        screen.clear();

        Graphics g = bs.getDrawGraphics();

        _board.drawScreen(g);

        g.dispose();
        bs.show();
    }

    private void update() {
        _input.update();
        _board.update();
    }

    public void start() {
        _running = true;

        long  lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double ns = 1000000000.0 / 60.0;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        requestFocus();
        while(_running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                update();
                updates++;
                delta--;
            }

            if(_paused) {
                if(_screenDelay <= 0) {
                    _board.setShow(-1);
                    _paused = false;
                }

                renderScreen();
            } else {
                renderGame();
            }


            frames++;
            if(System.currentTimeMillis() - timer > 1000) {
                _frame.setTime(_board.subtractTime());
                _frame.setPoints(_board.getPoints());
                _frame.setLives(_board.getLives());
                timer += 1000;
                _frame.setTitle(TITLE + " | " + updates + " rate, " + frames + " fps");
                updates = 0;
                frames = 0;

                if(_board.getShow() == 2)
                    --_screenDelay;
            }
        }
    }


    public static double getPlayerSpeed() {
        return playerSpeed;
    }

    public static int getBombRate() {
        return bombRate;
    }


    public static int getBombRadius() {
        return bombRadius;
    }

    public static void addPlayerSpeed(double i) {
        playerSpeed += i;
    }

    public static void addBombRadius(int i) {
        bombRadius += i;
    }

    public static void addBombRate(int i) {
        bombRate += i;
    }
    public static void addWallpass(int i) {
        setWallpass(i);
    }
    public static void addPlayLive(int i){
        setPlayerlive(i);
    }
    public int getScreenDelay() {
        return _screenDelay;
    }

    public static int getWallpass() {
        return wallpass;
    }

    public static void setWallpass(int wallpass) {
        Game.wallpass = wallpass;
    }

    public static int getPlayerlive() {
        return playerlive;
    }

    public static void setPlayerlive(int playerlive) {
        Game.playerlive = playerlive;
    }

    public void decreaseScreenDelay() {
        _screenDelay--;
    }

    public void resetScreenDelay() {
        _screenDelay = SCREENDELAY;
    }

    public Keyboard getInput() {
        return _input;
    }

    public Board getBoard() {
        return _board;
    }

    public void run() {
        _running = true;
        _paused = false;
    }

    public void stop() {
        _running = false;
    }

    public boolean isRunning() {
        return _running;
    }

    public boolean isPaused() {
        return _paused;
    }

    public void pause() {
        _paused = true;
    }

}
