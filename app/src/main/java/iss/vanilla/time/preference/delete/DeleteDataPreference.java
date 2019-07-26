package iss.vanilla.time.preference.delete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;

import iss.vanilla.time.R;
import iss.vanilla.time.preference.highlight.HighlightDialogPreference;

@SuppressLint("RestrictedApi")
public class DeleteDataPreference extends HighlightDialogPreference {

    public DeleteDataPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) { super(context, attrs, defStyleAttr, defStyleRes); }

    public DeleteDataPreference(Context context, AttributeSet attrs, int defStyleAttr) { this(context, attrs, defStyleAttr, 0); }

    public DeleteDataPreference(Context context, AttributeSet attrs) { this(context, attrs, TypedArrayUtils.getAttr(context, android.support.v7.preference.R.attr.dialogPreferenceStyle, 16842897)); }

    public DeleteDataPreference(Context context) { this(context, null); }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        setHighlightColor(getContext().getResources().getColor(R.color.clear_all));
    }

}
