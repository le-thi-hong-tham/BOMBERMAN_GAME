package main.java.gui.menu;

import javax.swing.JMenuBar;

import main.java.gui.Frame;

public class Menu extends JMenuBar {

    public Menu(Frame frame) {
        add( new Game(frame) );
        add( new Options(frame) );
        add( new Help(frame) );
        add(new ChoosePerson(frame));
    }

}
