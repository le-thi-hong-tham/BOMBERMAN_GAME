package main.java.level;

import main.java.Board;
import main.java.exceptions.LoadLevelException;

public abstract class Level implements ILevel {

    protected int _width, _height, _level;
    protected String[] _lineTiles;
    protected Board _board;

    protected static String[] codes = {
            "1p1",
            "1p2",
            "1p3",
            "1p4",
            "1p5",
    };

    public Level(String path, Board board) throws LoadLevelException {
        loadLevel(path);
        _board = board;
    }

    @Override
    public abstract void loadLevel(String path) throws LoadLevelException;

    public abstract void createEntities();


    public int validCode(String str) {
        for (int i = 0; i < codes.length; i++) {
            if (codes[i].equals(str)) {
                return i;
            }
        }
        return -1;
    }

    public String getActualCode() {
        return codes[_level -1];
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getLevel() {
        return _level;
    }

}
