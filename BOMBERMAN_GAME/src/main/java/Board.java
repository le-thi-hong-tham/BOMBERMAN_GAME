package main.java;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.entities.Entity;
import main.java.entities.Message;
import main.java.entities.bomb.Bomb;
import main.java.entities.bomb.Explosion;
import main.java.entities.mob.Mob;
import main.java.entities.mob.Player;
import main.java.entities.tile.powerup.Powerup;
import main.java.exceptions.LoadLevelException;
import main.java.graphics.IRender;
import main.java.graphics.Screen;
import main.java.input.Keyboard;
import main.java.level.Coordinates;
import main.java.level.FileLevel;
import main.java.level.Level;

public class Board implements IRender {

    public int _width, _height;
    protected Level _level;
    protected Game _game;
    protected Keyboard _input;
    protected Screen _screen;
    private String person= "one";

    public Entity[] _entities;
    public List<Mob> _mobs = new ArrayList<Mob>();
    protected List<Bomb> _bombs = new ArrayList<Bomb>();
    private List<Message> _messages = new ArrayList<Message>();

    private int _screenToShow = -1;

    private int _time = Game.TIME;
    private int _points = Game.POINTS;
    private int _lives = Game.LIVES;


    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Board(Game game, Keyboard input, Screen screen) {
        _game = game;
        _input = input;
        _screen = screen;

        changeLevel(1);
    }


    @Override
    public void update() {
        if( _game.isPaused() ) return;

        updateEntities();
        updateMobs();
        updateBombs();
        updateMessages();
        detectEndGame();

        for (int i = 0; i < _mobs.size(); i++) {
            Mob a = _mobs.get(i);
            if(((Entity)a).isRemoved()) _mobs.remove(i);
        }
    }


    @Override
    public void render(Screen screen) {
        if( _game.isPaused() ) return;

        int szx = screen.getWidth();
        if (_level.getWidth() * Game.TILES_SIZE < szx) {
            szx = (_level.getWidth() - 1) * Game.TILES_SIZE;
        }
        int szy = screen.getHeight();
        if (_level.getHeight() * Game.TILES_SIZE < szy) {
            szy = (_level.getHeight()) * Game.TILES_SIZE;
        }

        int x0 = Screen.xOffset >> 4;
        int x1 = (Screen.xOffset + szx + Game.TILES_SIZE) / Game.TILES_SIZE;
        int y0 = Screen.yOffset >> 4;
        int y1 = (Screen.yOffset + szy) / Game.TILES_SIZE;


        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                if (x + y * _level.getWidth() < _level.getWidth() * _level.getHeight()) {
                    _entities[x + y * _level.getWidth()].
                            render(
                                    screen
                            );
                }
            }
        }

        renderBombs(screen);
        renderMobs(screen);

    }

    public void newGame() {
        resetProperties();
        changeLevel(1);
    }

    private void resetProperties() {
        _points = Game.POINTS;
        _lives = Game.LIVES;
        Player._powerups.clear();

        _game.playerSpeed = 1.0;
        _game.bombRadius = 1;
        _game.bombRate = 1;
        //_game.playerLives = 3;
    }

    public void restartLevel() {

        changeLevel(_level.getLevel());
    }

    public void nextLevel() {

        changeLevel(_level.getLevel() + 1);
    }

    public void changeLevel(int level) {

        _time = Game.TIME;
        _screenToShow = 2;
        _game.resetScreenDelay();
        _game.pause();
        _mobs.clear();
        _bombs.clear();
        _messages.clear();
        try {
            _level = new FileLevel("levels/Level"+getPerson() + level + ".txt", this);
            _entities = new Entity[_level.getHeight() * _level.getWidth()];

            _level.createEntities();
        } catch (LoadLevelException e) {
            endGame();
        }
    }

    public void changeLevelByCode(String str) {
        int i = _level.validCode(str);

        if(i != -1) changeLevel(i + 1);
    }

    public boolean isPowerupUsed(int x, int y, int level) {
        Powerup p;
        for (int i = 0; i < Player._powerups.size(); i++) {
            p = Player._powerups.get(i);
            if(p.getX() == x && p.getY() == y && level == p.getLevel())
                return true;
        }

        return false;
    }


    protected void detectEndGame() {
        if(_time <= 0)
            restartLevel();
    }
    /*
    public void Gamenew(){
        _screenToShow =4;
        _game.resetScreenDelay();
        _game.pause();
    }
    */

    public void endGame() {
        File file = new File("src/main/resources/audio/gameover.wav");
        URL url = null;
        if (file.canRead()) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException malformedURLException) {
                malformedURLException.printStackTrace();
            }
        }
        System.out.println(url);
        AudioClip clip = Applet.newAudioClip(url);
        clip.play();
        _screenToShow = 1;
        _game.resetScreenDelay();
        _game.pause();
    }

    public boolean detectNoEnemies() {
        int total = 0;
        for (int i = 0; i < _mobs.size(); i++) {
            if(_mobs.get(i) instanceof Player == false)
                ++total;
        }

        return total == 0;
    }

    public void gamePause() {
        _game.resetScreenDelay();
        if(_screenToShow <= 0)
            _screenToShow = 3;
        _game.pause();
    }

    public void gameResume() {
        _game.resetScreenDelay();
        _screenToShow = -1;
        _game.run();
    }


    public void drawScreen(Graphics g) {
        switch (_screenToShow) {
            case 1:
                _screen.drawEndGame(g, _points, _level.getActualCode());
                break;
            case 2:
                _screen.drawChangeLevel(g, _level.getLevel());
                break;
            case 3:
                _screen.drawPaused(g);
                break;
            //case 4:
              //  _screen.drawNewGame(g);
        }
    }


    public Entity getEntity(double x, double y, Mob m) {

        Entity res = null;

        res = getExplosionAt((int)x, (int)y);
        if( res != null) return res;

        res = getBombAt(x, y);
        if( res != null) return res;

        res = getMobAtExcluding((int)x, (int)y, m);
        if( res != null) return res;

        res = getEntityAt((int)x, (int)y);

        return res;
    }

    public List<Bomb> getBombs() {
        return _bombs;
    }

    public Bomb getBombAt(double x, double y) {
        Iterator<Bomb> bs = _bombs.iterator();
        Bomb b;
        while(bs.hasNext()) {
            b = bs.next();
            if(b.getX() == (int)x && b.getY() == (int)y)
                return b;
        }

        return null;
    }

    public Mob getMobAt(double x, double y) {
        Iterator<Mob> itr = _mobs.iterator();

        Mob cur;
        while(itr.hasNext()) {
            cur = itr.next();

            if(cur.getXTile() == x && cur.getYTile() == y)
                return cur;
        }

        return null;
    }

    public Player getPlayer() {
        Iterator<Mob> itr = _mobs.iterator();

        Mob cur;
        while(itr.hasNext()) {
            cur = itr.next();

            if(cur instanceof Player)
                return (Player) cur;
        }

        return null;
    }

    public Mob getMobAtExcluding(int x, int y, Mob a) {
        Iterator<Mob> itr = _mobs.iterator();

        Mob cur;
        while(itr.hasNext()) {
            cur = itr.next();
            if(cur == a) {
                continue;
            }

            if(cur.getXTile() == x && cur.getYTile() == y) {
                return cur;
            }

        }

        return null;
    }

    public Explosion getExplosionAt(int x, int y) {
        Iterator<Bomb> bs = _bombs.iterator();
        Bomb b;
        while(bs.hasNext()) {
            b = bs.next();

            Explosion e = b.explosionAt(x, y);
            if(e != null) {
                return e;
            }

        }

        return null;
    }

    public Entity getEntityAt(double x, double y) {
        return _entities[(int)x + (int)y * _level.getWidth()];
    }


    public void addEntitie(int pos, Entity e) {
        _entities[pos] = e;
    }

    public void addMob(Mob e) {
        _mobs.add(e);
    }

    public void addBomb(Bomb e) {
        _bombs.add(e);
    }

    public void addMessage(Message e) {
        _messages.add(e);
    }


    protected void renderEntities(Screen screen) {
        for (int i = 0; i < _entities.length; i++) {
            _entities[i].render(screen);
        }
    }

    protected void renderMobs(Screen screen) {
        Iterator<Mob> itr = _mobs.iterator();

        while(itr.hasNext())
            itr.next().render(screen);
    }

    protected void renderBombs(Screen screen) {
        Iterator<Bomb> itr = _bombs.iterator();

        while(itr.hasNext())
            itr.next().render(screen);
    }

    public void renderMessages(Graphics g) {
        Message m;
        for (int i = 0; i < _messages.size(); i++) {
            m = _messages.get(i);

            g.setFont(new Font("Arial", Font.PLAIN, m.getSize()));
            g.setColor(m.getColor());
            g.drawString(m.getMessage(), (int)m.getX() - Screen.xOffset  * Game.SCALE, (int)m.getY());
        }
    }


    protected void updateEntities() {
        if( _game.isPaused() ) return;
        for (int i = 0; i < _entities.length; i++) {
            _entities[i].update();
        }
    }

    protected void updateMobs() {
        if( _game.isPaused() ) return;
        Iterator<Mob> itr = _mobs.iterator();

        while(itr.hasNext() && !_game.isPaused())
            itr.next().update();
    }

    protected void updateBombs() {
        if( _game.isPaused() ) return;
        Iterator<Bomb> itr = _bombs.iterator();

        while(itr.hasNext())
            itr.next().update();
    }

    protected void updateMessages() {
        if( _game.isPaused() ) return;
        Message m;
        int left = 0;
        for (int i = 0; i < _messages.size(); i++) {
            m = _messages.get(i);
            left = m.getDuration();

            if(left > 0)
                m.setDuration(--left);
            else
                _messages.remove(i);
        }
    }

    public Keyboard getInput() {
        return _input;
    }

    public Level getLevel() {
        return _level;
    }

    public Game getGame() {
        return _game;
    }

    public int getShow() {
        return _screenToShow;
    }

    public void setShow(int i) {
        _screenToShow = i;
    }

    public int getTime() {
        return _time;
    }

    public int getLives() {
        return _lives;
    }

    public void setlives(int _lives) {
        this._lives = _lives;
    }

    public int subtractTime() {
        if(_game.isPaused())
            return this._time;
        else
            return this._time--;
    }

    public int getPoints() {
        return _points;
    }

    public void addPoints(int points) {
        this._points += points;
    }

    public void addLives(int lives) {
        this._lives += lives;
    }



    public int getWidth() {
        return _level.getWidth();
    }

    public int getHeight() {
        return _level.getHeight();
    }

    public Game get_game() {
        return _game;
    }
}
