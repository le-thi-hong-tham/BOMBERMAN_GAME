package main.java.level;


import main.java.exceptions.LoadLevelException;

public interface ILevel {

    public void loadLevel(String path) throws LoadLevelException;
}
