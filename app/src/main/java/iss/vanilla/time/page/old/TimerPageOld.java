package iss.vanilla.time.page.old;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;

public class TimerPageOld extends Fragment {

    private TimerOptions timerOptions;
    private TextView timerText;
    private ImageView runIcon;
    private View background;

    private int type = TimerOptions.TYPE_CUR_TIME;
    private long countUpMillis = 0;
    private String label = "";
    private boolean isRun = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.timer_page, container, false);
        timerText = rootView.findViewById(R.id.timer_text);
        runIcon = rootView.findViewById(R.id.run_icon);
        runIcon.setOnClickListener(v -> setRun(true));
        background = (View) timerText.getParent();
        applyOptions();
        updateRunIconVisibility();
        return rootView;
    }

    public void setText(String text) {
        if(timerText == null) {
            Log.e("TimerPageOld", "Error while setText: timer text view is null");
            return;
        }
        timerText.setText(text);
    }

    public void setOptions(TimerOptions timerOptions) {
        this.timerOptions = timerOptions;
        this.timerOptions.globalTimerType = type;
    }

    public void resetCountUp() { countUpMillis = 0; }

    public void applyOptions() {
        if(timerText == null) { return; }
        timerText.setTextColor(timerOptions.textColor);
        timerText.setTextSize((int)timerOptions.textPxSize);
        background.setBackgroundColor(timerOptions.backgroundColor);
    }

    /* --- get set ------------------------------------------------------------------------------ */

    public void setRun(boolean run) {
        this.isRun = run;
        updateRunIconVisibility();
    }

    public boolean isRun() { return isRun; }

    public long getCountUpMillis() { return countUpMillis; }

    public void addCountUpMillis(long millis) { countUpMillis += millis; }

    public void setCountUpMillis(long countUpMillis) { this.countUpMillis = countUpMillis; }

    public int getType() { return type; }

    public String getTypeStr() { return TimerOptions.getTypeName(type); }

    public void setType(int type) {
        this.type = type;
        updateRunIconVisibility();
    }

    public String getLabel() { return label; }

    public void setLabel(String label) { this.label = label; }

    private void updateRunIconVisibility() {
        if(runIcon == null) return;
        if(type == TimerOptions.TYPE_CUR_TIME || isRun) {
            runIcon.setVisibility(View.GONE);
        } else {
            runIcon.setVisibility(View.VISIBLE);
        }
    }

}