package main.java.input;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Keyboard implements KeyListener {

    private boolean[] keys = new boolean[120];
    public boolean up, down, left, right, space, enter, A, D, S, W;

    public void update() {
        up = keys[KeyEvent.VK_UP];
        down = keys[KeyEvent.VK_DOWN];
        left = keys[KeyEvent.VK_LEFT];
        right = keys[KeyEvent.VK_RIGHT];
        space = keys[KeyEvent.VK_SPACE];
        enter = keys[KeyEvent.VK_ENTER];
        A = keys[KeyEvent.VK_A];
        D = keys[KeyEvent.VK_D];
        S = keys[KeyEvent.VK_S];
        W = keys[KeyEvent.VK_W];
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        audioClip.play();

    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;

    }
    public static AudioClip Sound() {
        File file = new File("src/main/resources/audio/Buocchan.wav");
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
        return clip;
    }
    AudioClip audioClip = Sound();

}
