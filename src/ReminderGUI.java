/*
@author - Yosef Spektor

The class creates the GUI for the app as well as provides the necessary handlers for events. It implements ActionListener
interface.

1) The GUI starts with a dialog box which asks the user if they want to load reminders from an existing file.
2) If the user decides to load reminders from an existing file we let the user choose the file via JFileChooser.
Then the app loads.
3) If the user wants to create a new file which will store the reminders we show another dialog box where the user
can type the name of the file. If no name is provided then the default file name is chosen.
4) The reminders app opens with the GUI that lets save/get reminders as well as choose dates.
5) When the app is closed all reminders are automatically saved to the file specified by the user (or default).
6) Reminder app files must have .rem extension.
7) If the user wants to exit the app we first ask if they are sure. If not the app continues working else the app exits.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

@SuppressWarnings("serial")
public class ReminderGUI extends JFrame implements ActionListener {
    //instance fields

    private JComboBox<Integer> daysComboBox;
    private JComboBox<String> monthsComboBox;
    private JComboBox<Integer> yearsComboBox;
    private JButton getReminderButton;
    private JButton saveButton;
    private ReminderManager reminderManager; //contains all reminders
    private JTextArea reminderTextArea;
    private String filePath; //the file path that output will be saved to

    /*
    Constructor
     */
    public ReminderGUI() {
        super(Utils.APP_NAME); //init the frame
        super.setLayout(new BorderLayout()); //we'll use this layout

        //instance fields initialization
        this.reminderManager = new ReminderManager();
        this.monthsComboBox = new JComboBox<>(Utils.MONTHS);
        this.daysComboBox = new JComboBox<>(initDaysArray(Utils.DAYS_31)); //default value
        this.yearsComboBox = new JComboBox<>(Utils.YEARS);
        this.reminderTextArea = new JTextArea(Utils.TEXT_AREA_DEFAULT_TEXT);
        this.getReminderButton = new JButton(Utils.GET_REMINDER_BUTTON);
        this.saveButton = new JButton(Utils.SAVE_BUTTON);
        this.doWeStartFromOldFile();

        JPanel northPanel = new JPanel(); //contains comboboxes
        JPanel southPanel = new JPanel(); //contains save/getReminder buttons

        northPanel.add(monthsComboBox, BorderLayout.NORTH);
        northPanel.add(daysComboBox, BorderLayout.NORTH);
        northPanel.add(yearsComboBox, BorderLayout.NORTH);

        northPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        southPanel.add(getReminderButton, BorderLayout.SOUTH);
        southPanel.add(saveButton, BorderLayout.SOUTH);

        super.add(southPanel, BorderLayout.SOUTH);
        super.add(reminderTextArea, BorderLayout.CENTER);
        super.add(northPanel, BorderLayout.NORTH);

        //----LISTENERS BINDING
        this.monthsComboBox.addActionListener(this);
        this.saveButton.addActionListener(this);
        this.getReminderButton.addActionListener(this);

        /*
        when the app is closed we automatically save the reminders to the file. for this we need to handle the
        close event. Instead of implementing the whole WindowListener interface I decided to use the adapter class
        because we only need the closing event handler
         */
        super.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = Utils.showConfirmDialog(ReminderGUI.this, Utils.CONFIRM_EXIT_MESSAGE, Utils.MESSAGE_STR);
                if (result == JOptionPane.YES_OPTION) { //the user wants to exit
                    ReminderGUI.this.reminderManager.saveToFile(ReminderGUI.this.filePath);
                    System.exit(Utils.NORMAL_EXIT);
                } else {
                    ReminderGUI.super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }

    /*
    creates an array of daysAmount which represents the amount of days in a month
     */
    private Integer[] initDaysArray(int daysAmount) {
        Integer[] array = new Integer[daysAmount];
        int i;
        for (i = 0; i < array.length; i++) {
            array[i] = i + 1;
        }
        return array;
    }

    /*
    we need to implement ActionListener interface. there're only two buttons and we need to adjust days based on the
    month chosen so we need to check for three different events
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.monthsComboBox) {
            String monthName = (String) this.monthsComboBox.getSelectedItem();
            this.monthHandler(monthName);
        } else if (e.getSource() == this.saveButton) {
            this.saveButtonHandler();
        } else if (e.getSource() == this.getReminderButton) {
            this.getReminderButtonHandler();
        }
    }

    /*
    returns id of a month by traversing the Utils.MONTHS array and comparing the currently selected
    month in this.monthsComboBox
     */
    private int monthNameToId() {
        int i;
        String month = (String) this.monthsComboBox.getSelectedItem();
        for (i = 0; i < Utils.MONTHS.length; i++) {
            if (month.equals(Utils.MONTHS[i])) {
                return i + 1;
            }
        }
        return Utils.LOOKUP_ERROR;
    }

    /*
    checks if the month has 30 days
     */
    private boolean is30daysMonth(String month) {
        int i;
        for (i = 0; i < Utils.MONTHS_OF_30_DAYS.length; i++) {
            if (month.equals(Utils.MONTHS_OF_30_DAYS[i])) {
                return true;
            }
        }
        return false;
    }

    /*
    the method adjusts the number of days according to the month selected. each time a month is chosen
    the method checks that we display the appropriate amount of days according to the month.
    First we check if it's February, then is it's a 30-day month and else it can only be a 31-day month.
     */
    private void monthHandler(String month) {
        DefaultComboBoxModel<Integer> model;

        if (month.equals("February")) {
            model = new DefaultComboBoxModel<>(initDaysArray(Utils.DAYS_28));
        } else if (is30daysMonth(month)) {
            model = new DefaultComboBoxModel<>(initDaysArray(Utils.DAYS_30));
        } else {
            model = new DefaultComboBoxModel<>(initDaysArray(Utils.DAYS_31));
        }
        this.daysComboBox.setModel(model);
    }

    /*
    returns the currently selected date in the GUI
     */
    private Date getDateFromGUI() {
        Date d;
        int day, month, year;
        day = (int) this.daysComboBox.getSelectedItem();

        /*
        theoretically we should never get a LOOKUP_ERROR because the months are set and not removed/added
        therefore one of them should return a match. But just in case we check for the error
         */
        if (this.monthNameToId() == Utils.LOOKUP_ERROR) {
            System.err.println(Utils.MONTH_NOT_FOUND_MESSAGE);
            Utils.showMessageDialog(this, Utils.MONTH_NOT_FOUND_MESSAGE, Utils.FATAL_ERROR);
            System.exit(Utils.BAD_EXIT);
        }
        month = this.monthNameToId();
        year = (int) this.yearsComboBox.getSelectedItem();
        return new Date(day, this.monthNameToId(), year);
    }

    /*
    the method handles saveButton events. After each click we add the reminder to the reminderTable hash table
     */
    private void saveButtonHandler() {
        this.reminderManager.putReminder(this.getDateFromGUI(), this.reminderTextArea.getText());
    }

    /*
    the method handles getReminderButton events. When the button is clicked we get the date currently selected
    in the GUI and then get the reminder associated with the date (if such exists). Finally, we set the JTextArea
    to the reminder text (null in case there was no reminder)
     */
    private void getReminderButtonHandler() {
        String reminder = this.reminderManager.getReminder(this.getDateFromGUI());
        this.reminderTextArea.setText(reminder);
    }

    /*
    we get the name for the file to which the reminders will be saved. If the user doesn't provide any name
    then we use the default name
     */
    private String getNameForOutputFile() {
        String fileName = JOptionPane.showInputDialog(null, Utils.OUTPUT_FILE_NAME_MESSAGE);
        return (fileName == null ? Utils.DEFAULT_OUTPUT_FILE_NAME : fileName + "." + Utils.REMINDER_FILE_EXTENSION);
    }

    /*
    the method returns the current working directory so that we can save output file there
     */
    private String getCurrentWorkingDirectory() {
        Path currentRelativePath = Paths.get(Utils.EMPTY_STR);
        return currentRelativePath.toAbsolutePath().toString();
    }

    /*
    the method provides the initial interaction between the user and the app.
    Essentially we want to ask the user if the want to load reminders from an existing file or
    create a new file which will store the reminders. Because the main app is not yet running we're using frame null.
     */
    private void doWeStartFromOldFile() {
        //first we ask the user if they want to load a file or save reminders to a new file
        int result = Utils.showConfirmDialog(null, Utils.DO_WE_START_FROM_OLD_FILE, Utils.MESSAGE_STR);
        Path path; //the path to the file that will be used to store reminders
        if (result == JOptionPane.NO_OPTION) {
            //by default we'll save new files in the current directory. we need to use File.separator
            //because different OS's have different separators
            this.filePath = this.getCurrentWorkingDirectory() + File.separator + this.getNameForOutputFile();
        } else if (result == JOptionPane.YES_OPTION) {
            //the user chose to load reminders from an existing file
            path = this.chooseRemindersFile();
            if (Files.exists(path)) {
                this.filePath = path.toString();
                this.reminderManager.setReminderTable(this.reminderManager.readRemindersFromFile(this.filePath));
            } else {
                //the file path doesn't exist something may have happened to the file
                Utils.showMessageDialog(null, Utils.FILE_MISSING_ERROR, Utils.FATAL_ERROR);
                System.exit(Utils.BAD_EXIT);
            }
        } else {
            //the user closed the dialog without selecting any option. therefore we exit the app as the user
            //is not interested in running the app
            System.exit(Utils.NORMAL_EXIT);
        }
    }

    /*
    the method lets the user select an existing reminders file.
    the file must have .rem extension.

    the method returns Path variable which contains the location of the chosen file.
    if the user cancels the app exits.
     */
    private Path chooseRemindersFile() {
        FileDialog fileDialog = new FileDialog(this,Utils.FILE_DIALOG_STR, FileDialog.LOAD);
        fileDialog.setDirectory(System.getProperty("user.dir"));
        fileDialog.setVisible(true);
        if(fileDialog.getFile() == null) {
            //the user didn't choose any file
            Utils.showMessageDialog(ReminderGUI.this, Utils.FILE_MUST_BE_CHOSEN_ERROR, Utils.MESSAGE_STR);
            System.exit(Utils.BAD_EXIT);
        }
        return Paths.get(fileDialog.getDirectory(), fileDialog.getFile());
    }
}