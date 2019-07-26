package iss.vanilla.time.preference.delete;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import iss.vanilla.time.R;
import iss.vanilla.time.preference.CustomPreferenceDialogFragmentCompat;

public class DeleteDataPreferenceDialog extends CustomPreferenceDialogFragmentCompat {

    public static DeleteDataPreferenceDialog newInstance(String key) {
        DeleteDataPreferenceDialog fragment = new DeleteDataPreferenceDialog();
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

        View v = LayoutInflater.from(context).inflate(R.layout.delete_data_preference_dialog, null);
        Button acceptButton = v.findViewById(R.id.accept_button);
        Button cancelButton = v.findViewById(R.id.cancel_button);

        acceptButton.setOnClickListener(_v -> {
            onDialogClosed(true);
            dismiss();
        });

        cancelButton.setOnClickListener(_v -> {
            onDialogClosed(false);
            dismiss();
        });

        return v;
    }


}
