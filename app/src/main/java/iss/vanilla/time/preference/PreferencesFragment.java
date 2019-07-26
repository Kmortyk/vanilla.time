package iss.vanilla.time.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import iss.vanilla.time.MainActivity;
import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.page.TimerPage;
import iss.vanilla.time.preference.delete.DeleteDataPreference;
import iss.vanilla.time.preference.delete.DeleteDataPreferenceDialog;
import iss.vanilla.time.preference.color.ColorPickerPreference;
import iss.vanilla.time.preference.color.ColorPickerPreferenceDialog;
import iss.vanilla.time.preference.csv.ExportCSVPreference;
import iss.vanilla.time.preference.csv.ExportCSVPreferenceDialog;
import iss.vanilla.time.preference.label.EditTextPreference;

public class PreferencesFragment extends PreferenceFragmentCompat {

    public final static int COLOR_TYPE_BACKGROUND = 1;
    public final static int COLOR_TYPE_TEXT = 2;

    private TimerOptions timerOptions;
    private EditTextPreference labelPreference;
    private DropDownPreference typePreference;
    private ColorPickerPreference backgroundColorPreference;
    private ColorPickerPreference textColorPreference;
    private FragmentManager fragmentManager;
    private Preference exportCsvPreference;
    private SwitchPreferenceCompat hoursPreference;
    private SwitchPreferenceCompat minutesPreference;
    private SwitchPreferenceCompat secondsPreference;
    private MainActivity mActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            preferenceScreen.getPreference(i).setIconSpaceReserved(false);
        }

        fragmentManager = getFragmentManager();
        if(fragmentManager == null) {
            throw new RuntimeException("FragmentManager is null.");
        }

        hoursPreference   = (SwitchPreferenceCompat) preferenceScreen.findPreference("hours");
        minutesPreference = (SwitchPreferenceCompat) preferenceScreen.findPreference("minutes");
        secondsPreference = (SwitchPreferenceCompat) preferenceScreen.findPreference("seconds");

        hoursPreference.setOnPreferenceChangeListener  ((pref, v) -> { timerOptions.setHoursEnabled  ((Boolean)v); return true; });
        minutesPreference.setOnPreferenceChangeListener((pref, v) -> { timerOptions.setMinutesEnabled((Boolean)v); return true; });
        secondsPreference.setOnPreferenceChangeListener((pref, v) -> { timerOptions.setSecondsEnabled((Boolean)v); return true; });

        typePreference = (DropDownPreference) preferenceScreen.findPreference("timer_type");
        typePreference.setOnPreferenceChangeListener((preference, value) -> {
            timerOptions.setGlobalTimerType(TimerOptions.getTypeCode((String) value));
            timerOptions.resetCountUp = true;
            TimerPage page = mActivity.getCurrentPage();
            if(page != null) page.setType(timerOptions.globalTimerType);
            return true;
        });

        labelPreference = (EditTextPreference) preferenceScreen.findPreference("label");
        labelPreference.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }
            public void onTextChanged(CharSequence s, int start, int before, int count)    { }

            @Override
            public void afterTextChanged(Editable s) {
                TimerPage page = mActivity.getCurrentPage();
                if(page != null) page.setLabel(s.toString());
                // Log.d("PreferencesFragment", "Label value is: " + s);
            }

        });

        backgroundColorPreference = (ColorPickerPreference) preferenceScreen.findPreference("background_color");
        backgroundColorPreference.setColor(timerOptions.backgroundColor);
        backgroundColorPreference.setType(COLOR_TYPE_BACKGROUND);

        textColorPreference = (ColorPickerPreference) preferenceScreen.findPreference("text_color");
        textColorPreference.setColor(timerOptions.textColor);
        textColorPreference.setType(COLOR_TYPE_TEXT);

        exportCsvPreference = preferenceScreen.findPreference("export_csv");

        backgroundColorPreference.setOnResetColor(positiveResult -> {
            timerOptions.setBackgroundColor(TimerOptions.DEFAULT_BACKGROUND_COLOR);
            backgroundColorPreference.setColor(TimerOptions.DEFAULT_BACKGROUND_COLOR);
        });

        textColorPreference.setOnResetColor(positiveResult -> {
            timerOptions.setTextColor(TimerOptions.DEFAULT_TEXT_COLOR);
            textColorPreference.setColor(TimerOptions.DEFAULT_TEXT_COLOR);
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        saveCustomValues();
    }

    public void setPageActivity(MainActivity activity) {
        this.timerOptions = activity.getTimerOptions();
        this.mActivity = activity;
        restoreSavedOptions();
    }

    public void updateWithPage(TimerPage page) {
        typePreference.setValueIndex(page.getType());
        labelPreference.setText(page.getLabel());
    }

    private void restoreSavedOptions() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        if(prefs == null) {
            Log.e("PreferencesFragment", "PreferenceDataStore is null");
            return;
        }

        timerOptions.setGlobalTimerType(prefs.getString("timer_type", "Current time"));
        timerOptions.setHoursEnabled(prefs.getBoolean("hours", true));
        timerOptions.setMinutesEnabled(prefs.getBoolean("minutes", true));
        timerOptions.setSecondsEnabled(prefs.getBoolean("seconds", true));
        timerOptions.setBackgroundColor(prefs.getInt("background_color", TimerOptions.DEFAULT_BACKGROUND_COLOR));
        timerOptions.setTextColor(prefs.getInt("text_color", TimerOptions.DEFAULT_TEXT_COLOR));
    }

    private void saveCustomValues() {
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        if(prefs == null) {
            Log.e("PreferencesFragment", "PreferenceDataStore is null");
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("background_color", timerOptions.backgroundColor);
        editor.putInt("text_color", timerOptions.textColor);
        editor.putBoolean("hours", hoursPreference.isChecked());
        editor.putBoolean("minutes", minutesPreference.isChecked());
        editor.putBoolean("seconds", secondsPreference.isChecked());
        editor.commit();
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if(preference instanceof ColorPickerPreference) {
            displayColorPickerPreferenceDialog(preference);
        } else if(preference instanceof ExportCSVPreference) {
            mActivity.requestStoragePermission(); // permission needed
            displayExportCSVPreferenceDialog(preference);
        } else if(preference instanceof DeleteDataPreference) {
            displayDeleteDataPreferenceDialog(preference);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }

    public void updateExportCSVDialog() {
        Fragment dialog = fragmentManager.findFragmentByTag("ExportCSVDialog");
        if(dialog instanceof ExportCSVPreferenceDialog) {
            ExportCSVPreferenceDialog csvDialog = (ExportCSVPreferenceDialog) dialog;
            csvDialog.updateFileList();
        }
    }

    private void displayColorPickerPreferenceDialog(Preference preference) {
        ColorPickerPreferenceDialog dialog = ColorPickerPreferenceDialog.newInstance(preference.getKey());
        ColorPickerPreference colorPickerPreference = (ColorPickerPreference) preference;

        dialog.setTargetFragment(this, 0);
        dialog.setColor(colorPickerPreference.getColor());
        dialog.setOnDialogClosedListener(positiveResult -> {
            if(positiveResult) {
                if(colorPickerPreference.getType() == COLOR_TYPE_BACKGROUND)
                    timerOptions.setBackgroundColor(dialog.color());
                else if(colorPickerPreference.getType() == COLOR_TYPE_TEXT)
                    timerOptions.setTextColor(dialog.color());
                colorPickerPreference.setColor(dialog.color());
            }
        });
        dialog.show(fragmentManager, "ColorPickerDialog");
    }

    private void displayExportCSVPreferenceDialog(Preference preference) {
        ExportCSVPreferenceDialog dialog = ExportCSVPreferenceDialog.newInstance(preference.getKey());

        dialog.setTargetFragment(this, 0);
        dialog.setOnDialogClosedListener(positiveResult -> {
            if(positiveResult)
                mActivity.exportCSV(dialog.getPath(), dialog.getFileName() + ".csv");
        });
        dialog.show(fragmentManager, "ExportCSVDialog");
    }

    private void displayDeleteDataPreferenceDialog(Preference preference) {
        DeleteDataPreferenceDialog dialog = DeleteDataPreferenceDialog.newInstance(preference.getKey());

        dialog.setTargetFragment(this, 0);
        dialog.setOnDialogClosedListener(positiveResult -> {
            if(positiveResult)
                mActivity.deleteData();
        });
        dialog.show(fragmentManager, "DeleteDataPreference");
    }

}
