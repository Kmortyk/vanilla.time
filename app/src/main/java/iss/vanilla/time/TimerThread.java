package iss.vanilla.time;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import iss.vanilla.time.page.TimerPage;

public class TimerThread extends Thread {

    private boolean isRun = false;
    private long prevTime = curTimeMillis();
    private final MainActivity mActivity;
    private TimerPage timerPage;
    private int optionsHash = 0;

    public final TimerOptions options;

    public TimerThread(MainActivity activity, TimerOptions options) {
        this.options = options;
        mActivity = activity;
    }

    public void setCurrentPage(TimerPage timerPage) {
        if(options == null) throw new RuntimeException("No timer options!");
        this.timerPage = timerPage;
        this.timerPage.setOptions(options);
        updatePage(timerPage, 0);
    }

    @Override
    public void run() {
        super.run();
        isRun = true;
        while(isRun) {
            updatePage(timerPage);
            sleep(10);
        }
    }

    public void updatePage(TimerPage page) {
        long curTime = curTimeMillis();
        long delta = curTime - prevTime;

        updatePage(page, delta);

        prevTime = curTimeMillis();
    }

    public void updatePage(TimerPage page, long delta) {

        final String timeStr;

        if(page == null) {
            Log.e("TimerThread", "timerPage is null.");
            sleep(10);
            return;
        }

        switch(page.getType()) {

            case TimerOptions.TYPE_CUR_TIME:
                timeStr = format(Calendar.getInstance().getTimeInMillis(), options);
                break;

            case TimerOptions.TYPE_COUNT_UP:
                if(options.resetCountUp) {
                    options.resetCountUp = false;
                    page.resetCountUp();
                }
                if(page.isRun())
                    page.addCountUpMillis(delta);

                timeStr = formatUTC(page.getCountUpMillis(), options);
                break;

            default:
                timeStr = "hh:mm:ss";

        }

        mActivity.runOnUiThread(() -> {
            page.setText(timeStr);

            if(optionsHash != options.hashCode()) {
                mActivity.applyTimerOptions();
                page.applyOptions();
                optionsHash = options.hashCode();
            }

        });

    }

    public void stopTimer() { isRun = false; }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String formatUTC(long millis, TimerOptions options) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(options.getFormat(), Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date(millis));
    }

    private String format(long millis, TimerOptions options) {
        return DateFormat.format(options.getFormat(), new Date(millis)).toString();
    }

    private long curTimeMillis() { return Calendar.getInstance().getTimeInMillis(); }

}
