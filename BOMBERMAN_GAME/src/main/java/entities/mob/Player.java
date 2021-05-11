package main.java.entities.mob;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.Board;
import main.java.Game;
import main.java.entities.Entity;
import main.java.entities.Message;
import main.java.entities.bomb.Bomb;
import main.java.entities.bomb.DirectionalExplosion;
import main.java.entities.mob.enemy.Enemy;
import main.java.entities.tile.powerup.Powerup;
import main.java.graphics.Screen;
import main.java.graphics.Sprite;
import main.java.input.Keyboard;
import main.java.level.Coordinates;

public class Player extends Mob {

    private List<Bomb> _bombs;
    protected Keyboard _input;
    protected String _name;

    protected int _timeBetweenPutBombs = 0;

    public static List<Powerup> _powerups = new ArrayList<Powerup>();


    public Player(int x, int y, Board board,String name) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
        _name = name;
    }


    @Override
    public void update() {
        clearBombs();
        if(_alive == false) {
            afterKill();
            return;
        }

        if(_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0; else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if(_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }



    private void detectPlaceBomb() {
        if(_name.equals("player2")) {
            if (_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {
                int xt = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
                int yt = Coordinates.pixelToTile((_y + _sprite.getSize() / 2) - _sprite.getSize());

                placeBomb(xt, yt);
                Game.addBombRate(-1);

                _timeBetweenPutBombs = 30;
            }
        }
        else if(_name.equals("player1")) {
            if (_input.enter && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {
                int xt = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
                int yt = Coordinates.pixelToTile((_y + _sprite.getSize() / 2) - _sprite.getSize());

                placeBomb(xt, yt);
                Game.addBombRate(-1);

                _timeBetweenPutBombs = 30;
            }
        }
    }

    protected void placeBomb(int x, int y) {
        Bomb b = new Bomb(x, y, _board);
        _board.addBomb(b);
    }

    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while(bs.hasNext()) {
            b = bs.next();
            if(b.isRemoved())  {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }


    @Override
    public void kill() {
        if(!_alive) return;

        _alive = false;

        _board.addLives(-1);

        Message msg = new Message("-1 LIVE", getXMessage(), getYMessage(), 2, Color.white, 14);
        _board.addMessage(msg);
    }

    @Override
    protected void afterKill() {
        if(_timeAfter > 0) --_timeAfter;
        else {
            if(_bombs.size() == 0) {

                if(_board.getLives() == 0)
                    _board.endGame();
                else
                    _board.restartLevel();
            }
        }
    }


    @Override
    protected void calculateMove() {
        int xa = 0, ya = 0;
        if(_name.equals("player1")) {
            if (_input.up) ya--;
            if (_input.down) ya++;
            if (_input.left) xa--;
            if (_input.right) xa++;
        }
        else if(_name.equals("player2")) {
            if (_input.W) ya--;
            if (_input.S) ya++;
            if (_input.A) xa--;
            if (_input.D) xa++;
        }
        if(xa != 0 || ya != 0)  {
            move(xa * Game.getPlayerSpeed(), ya * Game.getPlayerSpeed());
            _moving = true;
        } else {
            _moving = false;
        }

    }

    @Override
    public boolean canMove(double x, double y) {
        for (int c = 0; c < 4; c++) {
            double xt = ((_x + x) + c % 2 * 11) / Game.TILES_SIZE;
            double yt = ((_y + y) + c / 2 * 12 - 13) / Game.TILES_SIZE;

            Entity a = _board.getEntity(xt, yt, this);

            if(!a.collide(this))
                return false;
        }

        return true;
    }

    @Override
    public void move(double xa, double ya) {
        if(xa > 0) _direction = 1;
        if(xa < 0) _direction = 3;
        if(ya > 0) _direction = 2;
        if(ya < 0) _direction = 0;

        if(canMove(0, ya)) {
            _y += ya;
        }

        if(canMove(xa, 0)) {
            _x += xa;
        }
    }

    @Override
    public boolean collide(Entity e) {
        if (e instanceof DirectionalExplosion && Game.getPlayerlive() != 0) {
            Game.setPlayerlive(0);
            return false;
        } else if (e instanceof DirectionalExplosion && Game.getPlayerlive() == 0) {
            kill();
            return false;
        }

        if (e instanceof Enemy && Game.getPlayerlive() != 0) {
            System.out.println(Game.getPlayerlive());
            Game.setPlayerlive(Game.getPlayerlive() - 1);
            return true;
        } else if (e instanceof Enemy && Game.getPlayerlive() == 0) {
            System.out.print(Game.getPlayerlive());
            kill();
            return true;
        }
        return true;
    }


    public void addPowerup(Powerup p) {
        if(p.isRemoved()) return;

        _powerups.add(p);

        p.setValues();
    }

    public void clearUsedPowerups() {
        Powerup p;
        for (int i = 0; i < _powerups.size(); i++) {
            p = _powerups.get(i);
            if(p.isActive() == false)
                _powerups.remove(i);
        }
    }

    public void removePowerups() {
        for (int i = 0; i < _powerups.size(); i++) {
            _powerups.remove(i);
        }
    }

    private void chooseSprite() {
        switch(_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if(_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if(_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if(_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if(_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if(_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}
