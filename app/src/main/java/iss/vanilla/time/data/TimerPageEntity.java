package iss.vanilla.time.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.page.TimerPage;

/**
 * Data to be written as a row in the database.
 */
@Entity
public class TimerPageEntity {

    @PrimaryKey
    public int pagePosition = -1;
    public String timerType;
    public String label;
    public long createdDate;
    public long countUpMillis;

    public TimerPageEntity() {
        timerType = TimerOptions.getTypeName(TimerOptions.DEFAULT_TIMER_TYPE);
        createdDate = Calendar.getInstance().getTimeInMillis();
        label = DateFormat.getDateInstance().format(new Date(createdDate));
        countUpMillis = 0;
    }

    public TimerPageEntity(int position) {
        this();
        pagePosition = position;
    }

    public TimerPageEntity(int position, TimerPage page) {
        createdDate = Calendar.getInstance().getTimeInMillis();
        countUpMillis = page.getCountUpMillis();
        timerType = page.getTypeStr();
        label = page.getLabel();
        pagePosition = position;
    }

    public void setPosition(int position) {
        this.pagePosition = position;
    }

    public boolean hasPosition() { return pagePosition != -1; }

    // headers for csv table
    public static String[] getColumnNames() {
        return new String[] { "page_position", "timer_type", "label", "created_date_millis", "count_up_millis" };
    }

    // values for csv table
    public String[] getStringValues() {
        return new String[] { pagePosition + "", timerType + "", label, createdDate + "", countUpMillis + "" };
    }

}
