package main.java.entities.mob.enemy;


import main.java.Board;
import main.java.Game;
import main.java.entities.mob.enemy.ai.AILow;
import main.java.graphics.Sprite;

public class Balloom extends Enemy {


    public Balloom(int x, int y, Board board) {
        super(x, y, board, Sprite.balloom_dead, Game.getPlayerSpeed() / 2, 100);

        _sprite = Sprite.balloom_left1;

        _ai = new AILow();
        _direction = _ai.calculateDirection();
    }

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                _sprite = Sprite.movingSprite(Sprite.balloom_right1, Sprite.balloom_right2, Sprite.balloom_right3, _animate, 60);
                break;
            case 2:
            case 3:
                _sprite = Sprite.movingSprite(Sprite.balloom_left1, Sprite.balloom_left2, Sprite.balloom_left3, _animate, 60);
                break;
        }
    }
}
