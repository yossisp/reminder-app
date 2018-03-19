/*
the class only contains the main method. Explanation about the app logic is inside ReminderGUI class
 */

import javax.swing.*;

public class ReminderMain {
    public static void main(String[] args) {

        ReminderGUI rmg = new ReminderGUI(); //init the GUI
        rmg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rmg.setSize(Utils.FRAME_WIDTH, Utils.FRAME_HEIGHT);
        rmg.setVisible(true);
    }
}
