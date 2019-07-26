package iss.vanilla.time.page;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.data.TimerPageEntity;

public class TimerPage extends RecyclerView.ViewHolder {

    private TimerOptions timerOptions;
    private TextView timerText;
    private TextView timerLabel;
    private ImageView runIcon;
    private View background;

    private int type = TimerOptions.TYPE_CUR_TIME;
    private long countUpMillis = 0;
    private String label = "";
    private boolean isRun = false;

    public TimerPage(@NonNull View itemView) {
        super(itemView);
        timerText = itemView.findViewById(R.id.timer_text);
        timerLabel = itemView.findViewById(R.id.timer_label);
        runIcon = itemView.findViewById(R.id.run_icon);
        runIcon.setOnClickListener(v -> setRun(true));
        background = (View) timerText.getParent();
        setLabel(label);
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
        applyOptions();
        updateRunIconVisibility();
    }

    public void resetCountUp() { countUpMillis = 0; }

    public void applyOptions() {
        if(timerText == null) { return; }
        timerText.setTextColor(timerOptions.textColor);
        timerText.setTextSize((int)timerOptions.textPxSize);
        timerLabel.setTextColor(timerOptions.textColor);
        runIcon.setColorFilter(timerOptions.textColor);
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

    public void setLabel(String label) {
        this.label = label;

        if(label.length() > TimerOptions.LABEL_MAX_CHARACTERS) {
            label = label.substring(0, TimerOptions.LABEL_MAX_CHARACTERS) + "\u2026";
        }

        timerLabel.setText(label);
    }

    private void updateRunIconVisibility() {
        if(runIcon == null) return;
        if(type == TimerOptions.TYPE_CUR_TIME || isRun) {
            runIcon.setVisibility(View.GONE);
            timerLabel.setVisibility(View.GONE);
        } else {
            runIcon.setVisibility(View.VISIBLE);
            timerLabel.setVisibility(View.VISIBLE);
        }
    }

}
