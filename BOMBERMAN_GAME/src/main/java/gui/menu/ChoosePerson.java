package main.java.gui.menu;

import main.java.gui.Frame;
import main.java.gui.InfoDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class ChoosePerson extends JMenu {
    public Frame frame;

    public ChoosePerson(Frame frame) {
        super("Choose Person");
        this.frame = frame;

        JMenuItem onePerson = new JMenuItem("One Person");
        onePerson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        onePerson.addActionListener(new ChoosePerson.MenuActionListener(frame));
        add(onePerson);

        JMenuItem twoPerson = new JMenuItem("Two Person");
        twoPerson.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        twoPerson.addActionListener(new ChoosePerson.MenuActionListener(frame));
        add(twoPerson);

    }
    class MenuActionListener implements ActionListener {
        public Frame _frame;
        public MenuActionListener(Frame frame) {
            _frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getActionCommand().equals("One Person")) {
                _frame.getPerson("one");

            }

            if(e.getActionCommand().equals("Two Person")) {
                _frame.getPerson("two");
                _frame.addBombRateFrame();

            }

        }
    }
}
