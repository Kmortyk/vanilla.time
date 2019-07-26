package iss.vanilla.time.preference.label;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import iss.vanilla.time.R;

public class EditTextPreference extends Preference {

    private List<TextWatcher> textWatchers;
    private CharSequence initText = null;
    private EditText editText;

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextPreference(Context context) {
        super(context);
        init();
    }

    private void init() { textWatchers = new ArrayList<>(); }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        editText = holder.itemView.findViewById(R.id.preference_edit_text);

        if(editText == null) {
            Log.e("EditTextPreference", "Preference with id 'R.id.preference_edit_text' is null.");
            return;
        }

        if(initText != null) {
            editText.setText(initText);
            initText = null;
        }

        for(TextWatcher textWatcher: textWatchers)
            editText.addTextChangedListener(textWatcher);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if(editText == null)
            textWatchers.add(textWatcher);
        else
            editText.addTextChangedListener(textWatcher);
    }

    public void setText(CharSequence text) {
        if(editText == null) { initText = text; }
        else { editText.setText(text); }
    }

}
