package iss.vanilla.time.preference;

import android.support.v7.preference.PreferenceDialogFragmentCompat;

public class CustomPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private DialogClosedListener listener;

    @Override
    public void onDialogClosed(boolean positiveResult) { listener.onDialogClosed(positiveResult); }

    public void setOnDialogClosedListener(DialogClosedListener listener) { this.listener = listener; }

}
