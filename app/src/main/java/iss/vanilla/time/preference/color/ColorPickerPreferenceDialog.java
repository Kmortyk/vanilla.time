package iss.vanilla.time.preference.color;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.preference.DialogClosedListener;

public class ColorPickerPreferenceDialog extends PreferenceDialogFragmentCompat {

    private SeekBar  redSeekBar;
    private SeekBar  greenSeekBar;
    private SeekBar  blueSeekBar;

    private EditText hexEditText;
    private View     colorView;

    private int red;
    private int green;
    private int blue;

    private Button acceptButton;
    private Button cancelButton;

    private DialogClosedListener listener;

    public static ColorPickerPreferenceDialog newInstance(String key) {
        ColorPickerPreferenceDialog fragment = new ColorPickerPreferenceDialog();

        Bundle args = new Bundle(1);
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View contentView = onCreateDialogView(context);
        onBindDialogView(contentView);
        builder.setView(contentView);

        onPrepareDialogBuilder(builder);

        return builder.create();
    }

    @Override
    protected View onCreateDialogView(Context context) {

        View v = LayoutInflater.from(context).inflate(R.layout.color_picker_preference_dialog, null);
        redSeekBar   = v.findViewById(R.id.red_seek_bar);
        greenSeekBar = v.findViewById(R.id.green_seek_bar);
        blueSeekBar  = v.findViewById(R.id.blue_seek_bar);
        hexEditText  = v.findViewById(R.id.hex_edit_text);
        colorView    = v.findViewById(R.id.color_view);
        acceptButton = v.findViewById(R.id.accept_button);
        cancelButton = v.findViewById(R.id.cancel_button);

        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {

            public void onStartTrackingTouch(SeekBar seekBar) { /* none */ }
            public void onStopTrackingTouch(SeekBar seekBar) { /* none */ }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (seekBar.getId()) {
                    case R.id.red_seek_bar: red = progress;
                        break;
                    case R.id.green_seek_bar: green = progress;
                        break;
                    case R.id.blue_seek_bar: blue = progress;
                        break;
                }
                updateView();
            }

        };

        redSeekBar.setOnSeekBarChangeListener(seekBarListener);
        greenSeekBar.setOnSeekBarChangeListener(seekBarListener);
        blueSeekBar.setOnSeekBarChangeListener(seekBarListener);

        setSeekBarColor(redSeekBar, TimerOptions.DEFAULT_TEXT_COLOR);
        setSeekBarColor(greenSeekBar, TimerOptions.DEFAULT_TEXT_COLOR);
        setSeekBarColor(blueSeekBar, TimerOptions.DEFAULT_TEXT_COLOR);

        hexEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                setColor(view.getText().toString());
                updateView();
                return true;
            }
            return false;
        });

        acceptButton.setOnClickListener(_v -> {
            onDialogClosed(true);
            dismiss();
        });

        cancelButton.setOnClickListener(_v -> {
            onDialogClosed(false);
            dismiss();
        });

        updateView();
        return v;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) { listener.onDialogClosed(positiveResult); }

    public void setOnDialogClosedListener(DialogClosedListener listener) { this.listener = listener; }

    public String hexColor() { return String.format("%06X", (0xFFFFFF & color())); }

    public int color() { return Color.rgb(red, green, blue); }

    public void setColor(String colorString) {
        try {
            setColor(Color.parseColor(colorString));
        } catch (IllegalArgumentException e) {
            Log.e("ColorPicker", "Unknown color '" + colorString + "'");
        }
    }

    public void setColor(int color) {
        red   = Color.red(color);
        green = Color.green(color);
        blue  = Color.blue(color);
    }

    private void updateView() {
        colorView.setBackgroundColor(color());
        redSeekBar.setProgress(red);
        greenSeekBar.setProgress(green);
        blueSeekBar.setProgress(blue);
        hexEditText.setText(hexColor());
    }

    private void setSeekBarColor(SeekBar seekBar, int color) {
        seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

}
