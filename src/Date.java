/*
the class represents a calendar date. The class implements Serializable interface because Date is the the key
for the hash table used in ReminderManager. equals() and hashCode() override Object in order for Date to work
properly as key to has table.
 */

import java.io.Serializable;

public class Date implements Serializable {

    //instance fields
    private int day;
    private int month;
    private int year;
    private static final long serialVersionUID = 1L; //required for serialization

    /*
    Constructor
     */
    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(!(o instanceof Date)) {
            return false;
        }
        Date d = (Date) o;
        return this.day == d.day && this.month == d.month && this.year == d.year;
    }

    @Override
    public String toString() {
        String day = Utils.EMPTY_STR + this.day;
        String month = Utils.EMPTY_STR + this.month;

        //if day or month is less than TEN then we add a zero before the number
        //in order to avoid different hashes for date with/without leading zero
        if(this.day < Utils.TEN) {
            day = Utils.ZERO_STR + this.day;
        }
        if(this.month < Utils.TEN) {
            month = Utils.ZERO_STR + this.month;
        }
        return Utils.EMPTY_STR + day + month + this.year;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
