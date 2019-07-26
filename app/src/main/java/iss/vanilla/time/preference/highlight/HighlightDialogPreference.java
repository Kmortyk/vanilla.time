package iss.vanilla.time.preference.highlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import iss.vanilla.time.R;

@SuppressLint("RestrictedApi")
public class HighlightDialogPreference extends DialogPreference {

    private TextView titleTextView;
    private TextView titleHighlight;
    private View preferenceView;

    public HighlightDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) { this(context, attrs, defStyleAttr, 0); }

    public HighlightDialogPreference(Context context, AttributeSet attrs) { this(context, attrs, TypedArrayUtils.getAttr(context, android.support.v7.preference.R.attr.dialogPreferenceStyle, 16842897)); }

    public HighlightDialogPreference(Context context) { this(context, null); }

    public HighlightDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.highlight_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        preferenceView = holder.itemView;
        titleTextView = preferenceView.findViewById(android.R.id.title);
        titleHighlight = preferenceView.findViewById(R.id.title_highlight);
        setTitle(getTitle());
    }

    @Override
    public void setTitle(CharSequence text) {
        titleTextView.setText(text);
        titleHighlight.setText(text);
    }

    protected void setHighlightColor(int color) {
        titleHighlight.setTextColor(color);
    }

}
