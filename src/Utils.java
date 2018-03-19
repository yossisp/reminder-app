/*
the class contains constants and dialog utility methods.
 */

import javax.swing.*;

public class Utils {
    //CONSTANTS
    public final static int BAD_EXIT = 1; //means that a fatal error occurred in the app
    public final static int NORMAL_EXIT = 0; //means that a fatal error occurred in the app
    public final static int TEN = 10; //needed for Date
    public final static String ZERO_STR = "0"; //needed for Date
    public final static String APP_NAME = "Reminders App"; //the name of the app
    public final static int FRAME_WIDTH = 500;
    public final static int FRAME_HEIGHT = 300;
    public final static String GET_REMINDER_BUTTON = "Get reminder";
    public final static String SAVE_BUTTON = "Save reminder";
    public final static String TEXT_AREA_DEFAULT_TEXT = "Enter reminder here";
    public final static String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    public final static String[] MONTHS_OF_30_DAYS = {"April", "June", "September", "November" };
    public final static Integer[] YEARS = {2017, 2018, 2019, 2020};
    public final static int DAYS_28 = 28;
    public final static int DAYS_30 = 30;
    public final static int DAYS_31 = 31;
    public final static String EMPTY_STR = "";
    public final static int LOOKUP_ERROR = -1; //used in methods which can't find a value in a range
    public final static String FILE_DIALOG_STR = "Please choose a .rem file";

    //---MESSAGES for showMessageDialog()
    public final static String FATAL_ERROR = "Critical Error";
    public final static String MONTH_NOT_FOUND_MESSAGE = "Couldn't find the month\nExiting the program\n";
    public final static String OUTPUT_FILE_NAME_MESSAGE = "Please enter the name for the output file:\n" +
            "(if file name will not be provided then the file will be named \"reminders.rem\")";
    public final static String DO_WE_START_FROM_OLD_FILE = "Would you like to load reminders from an existing file?\n" +
            "(The file must be a .rem file)";
    public final static String MESSAGE_STR = "Message";
    public final static String DEFAULT_OUTPUT_FILE_NAME = "reminders.rem";
    public final static String REMINDER_FILE_EXTENSION = "rem";
    public final static String FILE_MUST_BE_CHOSEN_ERROR = "You must select a file for the program to save the reminders" +
            " to. \nProgram is exiting, please try again";
    public final static String FILE_MISSING_ERROR = "The file is missing\nExiting the app";
    public final static String CONFIRM_EXIT_MESSAGE = "Are you sure that you want to exit the app?";
    public final static String FILE_READ_ERROR_STR = "Error opening file. Exiting program";
    public final static String FILE_SAVE_ERROR_STR = "Error saving file. Exiting program";
    public final static String INVALID_OBJECT_ERROR = "Invalid object type. Exiting the app";
    public final static String INVALID_INPUT_ERROR = "Invalid input. Exiting the app";

    //wrapper method
    public static void showMessageDialog(JFrame frame, String message, String title) {
        String[] options = {"OK"};
        JOptionPane.showOptionDialog(frame, message, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,null, options, options[0]);
    }

    //wrapper method
    public static int showConfirmDialog(JFrame frame, String message, String title) {
        return JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.YES_NO_OPTION);
    }
}
