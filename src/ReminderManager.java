/*
@author - Yosef Spektor

The class stores reminders into hash table, reads reminders from file and saves reminders to file.
HashTable collection is used where Date objects are keys and strings are values which represent reminder text.

We use serialization to save/read files because it's easier then having to parse strings (reminder text can be several
lines, in addition some kind of format would have to be used).
 */


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/*
unchecked cast warning would've been generated when initializing reminderTable object because
readObject() invoked on serializable object returns Object type which needs to be cast to
Hashtable<Date, String> in order to extract any meaningful info. There's not much that can be done
in such case because we don't want to check for ClassCastException since it's a runtime exception
therefore the warning is suppressed
 */
@SuppressWarnings("unchecked")
public class ReminderManager {

    //instance field which contains keys and values
    private Hashtable<Date, String> reminderTable;

    /*
    Constructor
     */
    public ReminderManager() {
        this.reminderTable = new Hashtable<>();
    }

    //adds a new reminder
    public void putReminder(Date key, String value) {
        this.reminderTable.put(key, value);
    }

    //gets the reminder under key
    public String getReminder(Date key) {
        return this.reminderTable.get(key);
    }

    //setter
    public void setReminderTable(Hashtable<Date, String> reminderTable) {
        this.reminderTable = reminderTable;
    }

    /*
    reads from a serialized file and returns HashTable<Date, String> object.
    try-with-resources is used so we don't need to close the file.
    @param path - represents the absolute path to the file which will be read
     */
    public Hashtable<Date, String> readRemindersFromFile(String path) {
        String errorMsg; //if errors will be needed to displayed
        Hashtable<Date, String> reminderTable = null;
        try(ObjectInputStream input = new ObjectInputStream(Files.newInputStream(Paths.get(path)))) {

            //we know that the object to be read is an instance of Hashtable<Date, String> hence
            //the cast is OK
            reminderTable = (Hashtable<Date, String>) input.readObject();

            //we catch possible exceptions as in the book on page 710-711
        } catch(EOFException e) {
            //not really a problem we just reached end of file
            errorMsg = "No more records";
            System.err.println(errorMsg);
        } catch (IOException e) {
            errorMsg = Utils.FILE_READ_ERROR_STR;
            Utils.showMessageDialog(null, Utils.FILE_READ_ERROR_STR, Utils.FATAL_ERROR);
            System.err.println(errorMsg);
            System.exit(Utils.BAD_EXIT);
        } catch (ClassNotFoundException e) {
            errorMsg = Utils.INVALID_OBJECT_ERROR;
            System.err.println(errorMsg);
            Utils.showMessageDialog(null, Utils.INVALID_OBJECT_ERROR, Utils.FATAL_ERROR);
            System.exit(Utils.BAD_EXIT);
        }
        return reminderTable;
    }

    /*
    saves this.reminderTable to file using serialization.
    try-with-resources is used so we don't need to close the file.

    @param path - absolute path to the file which will be saved
     */
    public void saveToFile(String path) {
        String errorMsg; //if errors will be needed to displayed
        try(ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(Paths.get(path)))) {

            output.writeObject(this.reminderTable);

            //we catch possible exceptions as in the book on page 698
        } catch (IOException e) {
            errorMsg = Utils.FILE_SAVE_ERROR_STR;
            System.err.println(errorMsg);
            Utils.showMessageDialog(null, Utils.FILE_SAVE_ERROR_STR, Utils.FATAL_ERROR);
            System.exit(Utils.BAD_EXIT);
        } catch (NoSuchElementException e) {
            errorMsg = Utils.INVALID_INPUT_ERROR;
            System.err.println(errorMsg);
            Utils.showMessageDialog(null, Utils.INVALID_INPUT_ERROR, Utils.FATAL_ERROR);
            System.exit(Utils.BAD_EXIT);
        }
    }
}
