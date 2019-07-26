package iss.vanilla.time.preference.csv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.DialogPreference;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

import iss.vanilla.time.R;
import iss.vanilla.time.preference.highlight.HighlightDialogPreference;

@SuppressLint("RestrictedApi")
public class ExportCSVPreference extends HighlightDialogPreference {

    public ExportCSVPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public ExportCSVPreference(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr, 0); }

    public ExportCSVPreference(Context context, AttributeSet attrs) { super(context, attrs, TypedArrayUtils.getAttr(context, android.support.v7.preference.R.attr.dialogPreferenceStyle, 16842897)); }

    public ExportCSVPreference(Context context) { super(context, null); }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        setHighlightColor(getContext().getResources().getColor(R.color.export_csv));
    }

}
