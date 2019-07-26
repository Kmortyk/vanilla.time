package iss.vanilla.time.preference.color;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.support.v7.preference.R.attr;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.preference.DialogClosedListener;

import static iss.vanilla.time.preference.PreferencesFragment.COLOR_TYPE_BACKGROUND;
import static iss.vanilla.time.preference.PreferencesFragment.COLOR_TYPE_TEXT;

@SuppressLint({"RestrictedApi", "ResourceType"})
public class ColorPickerPreference extends DialogPreference {

    private ImageView colorWidget;
    private int color;
    private int strokeWidth;
    private int type;

    private Spinner resetColorSpinner;
    private DialogClosedListener onResetColor;

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) { this(context, attrs, defStyleAttr, 0); }

    public ColorPickerPreference(Context context, AttributeSet attrs) { this(context, attrs, TypedArrayUtils.getAttr(context, attr.dialogPreferenceStyle, 16842897)); }

    public ColorPickerPreference(Context context) { this(context, null); }

    private void init(Context context) { setWidgetLayoutResource(R.layout.color_picker_preference); }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        // disable icon
        View icon = holder.findViewById(R.id.icon_frame);
        icon.setVisibility(View.GONE);
        // find color circle
        colorWidget = (ImageView) holder.findViewById(R.id.color_picker_widget);
        // 0.5 dp, there's no such method as 'getStrokeWidth' in GradientDrawable, what a shame....
        strokeWidth = (int) (0.25f * getContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        if(strokeWidth < 1) { strokeWidth = 1; }
        // on long click show menu with color reset item
        createResetSpinner(holder, getContext());
        holder.itemView.setOnLongClickListener(v -> {
            resetColorSpinner.performClick();
            resetColorSpinner.setSelection(1, false);
            return true;
        });

        setWidgetColor(color);
    }

    public int getColor() { return color; }

    public void setColor(int color) {
        this.color = color;
        if(colorWidget != null)
            setWidgetColor(color);
    }

    public void setType(int type) { this.type = type; }

    public int getType() { return type; }

    public void setOnResetColor(DialogClosedListener onResetColor) { this.onResetColor = onResetColor; }

    private void setWidgetColor(int color) {
        GradientDrawable drawable = (GradientDrawable) colorWidget.getBackground();
        // set solid color
        drawable.setColor(color);
        drawable.setStroke(strokeWidth, TimerOptions.SHADOW_COLOR);
        // stroke will appear if color equal to background color
        if(colorDiff(color, TimerOptions.DEFAULT_BACKGROUND_COLOR) <= 10) {
            drawable.setStroke(strokeWidth, TimerOptions.SHADOW_COLOR);
        } else {
            drawable.setStroke(strokeWidth, color);
        }
    }

    private double colorDiff(int color1, int color2) {
        double square = Math.pow(Color.red(color1)  - Color.red(color2),   2) +
                        Math.pow(Color.green(color1)- Color.green(color2), 2) +
                        Math.pow(Color.blue(color1) - Color.blue(color2),  2);
        return Math.sqrt(square);
    }

    private void createResetSpinner(PreferenceViewHolder holder, Context context) {

        String[] data = new String[]{context.getString(R.string.reset_color), context.getString(R.string.cancel)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data) {
            @Override // data has two items, truncate one to show one option with chosen another
            public int getCount() { return 1; }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        resetColorSpinner = (Spinner) holder.findViewById(R.id.reset_color_spinner);
        resetColorSpinner.setAdapter(adapter);
        resetColorSpinner.setSelection(1, false);
        resetColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == 0)
                    onResetColor.onDialogClosed(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /* none */ }
        });

    }

}
