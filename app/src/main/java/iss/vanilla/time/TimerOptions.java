package iss.vanilla.time;

import android.graphics.Color;

import java.util.Objects;

@SuppressWarnings({"SpellCheckingInspection", "CanBeFinal"})
public class TimerOptions {

    public final static int DEFAULT_BACKGROUND_COLOR = Color.parseColor("#B1ADAC");
    public final static int DEFAULT_TEXT_COLOR       = Color.parseColor("#090909");
    public final static int SHADOW_COLOR             = Color.parseColor("#40000000");
    public final static int DEFAULT_TIMER_TYPE       = 0;

    public final static int LABEL_MAX_CHARACTERS = 20;

    public final static byte TYPE_COUNT_UP = 0;
    public final static byte TYPE_CUR_TIME = 1;
    // public final static byte TYPE_TIMER = 2;

    private final static String typeNames[] = { "Current time", "Count up", "Timer" };

    public int globalTimerType = TYPE_CUR_TIME;

    public int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    public int textColor       = DEFAULT_TEXT_COLOR;
    public double textPxSize;

    private String timeFormat;
    public boolean resetCountUp = false;
    public boolean HH = true;
    public boolean mm = true;
    public boolean ss = true;

    int getLettersCount() {
        int count = 0;
        if(HH) count += 2;
        if(mm) count += 2;
        if(ss) count += 2;
        // :
        if(HH && mm) count++;
        if(mm && ss) count++;

        return count;
    }

    public void setBackgroundColor(int color) { this.backgroundColor = color; }

    public void setTextColor(int color) { this.textColor = color; }

    public void setHoursEnabled(boolean enabled) {
        HH = enabled;
        updateFormat();
    }

    public void setMinutesEnabled(boolean enabled) {
        mm = enabled;
        updateFormat();
    }

    public void setSecondsEnabled(boolean enabled) {
        ss = enabled;
        updateFormat();
    }

    public String getFormat() { return timeFormat; }

    private void updateFormat() {
        timeFormat = "";
        if(HH) {
            timeFormat += "HH";
            if(mm || ss) timeFormat += ":";
        }
        if(mm) {
            timeFormat += "mm";
            if(ss) timeFormat += ":";
        }
        if(ss) timeFormat += "ss";
    }

    public void setGlobalTimerType(String typeStr) { setGlobalTimerType(getTypeCode(typeStr)); }

    public void setGlobalTimerType(int type) { this.globalTimerType = type; }

    public int getGlobalTimerType() { return globalTimerType; }

    public static int getTypeCode(String typeName) { return indexOf(typeNames, typeName); }

    public static String getTypeName(int code) {
        if(code < 0 || code >= typeNames.length)
            return "";
        return typeNames[code];
    }

    private static <T> int indexOf(T[] array, T key)  {
        for (int i = 0; i < array.length; i++) {
            if (array[i] != null && array[i].equals(key) ||
                key == null && array[i] == null) return i;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerOptions that = (TimerOptions) o;
        return globalTimerType == that.globalTimerType &&
                backgroundColor == that.backgroundColor &&
                textColor == that.textColor &&
                Double.compare(that.textPxSize, textPxSize) == 0 &&
                resetCountUp == that.resetCountUp &&
                HH == that.HH && mm == that.mm && ss == that.ss &&
                Objects.equals(timeFormat, that.timeFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(globalTimerType, backgroundColor, textColor, textPxSize, timeFormat, resetCountUp, HH, mm, ss);
    }

}
