package iss.vanilla.time.preference.csv;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.util.Stack;

import iss.vanilla.time.R;
import iss.vanilla.time.preference.CustomPreferenceDialogFragmentCompat;
import iss.vanilla.time.preference.DialogClosedListener;

public class ExportCSVPreferenceDialog extends CustomPreferenceDialogFragmentCompat {

    private final static String LOG_TAG = "ExportCSVDialog";
    private final static int ID_DIRECTORY_ICON = R.drawable.ic_directory;
    private final static int ID_FILE_ICON = R.drawable.ic_file_01;
    private final static int ID_UP_DIRECTORY_ICON = R.drawable.ic_arrow_01;

    private FileListRecyclerViewAdapter adapter;
    private Button acceptButton;
    private Button cancelButton;
    private EditText fileName;

    private File path = new File(Environment.getExternalStorageDirectory() + "");
    private Stack<String> pathStack = new Stack<>();

    public static ExportCSVPreferenceDialog newInstance(String key) {
        ExportCSVPreferenceDialog fragment = new ExportCSVPreferenceDialog();
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
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if(isTopLevel()) dismiss();
                else toUpLevelDirectory();
            }
            return false;
        });

        onPrepareDialogBuilder(builder);

        return builder.create();
    }

    @Override
    protected View onCreateDialogView(Context context) {

        View v = LayoutInflater.from(context).inflate(R.layout.export_csv_preference_dialog, null);
        acceptButton = v.findViewById(R.id.accept_button);
        cancelButton = v.findViewById(R.id.cancel_button);
        fileName = v.findViewById(R.id.file_name_edit_text);

        acceptButton.setOnClickListener(_v -> {
            onDialogClosed(true);
            dismiss();
        });

        cancelButton.setOnClickListener(_v -> {
            onDialogClosed(false);
            dismiss();
        });

        RecyclerView fileListRecyclerView = v.findViewById(R.id.file_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
        adapter = new FileListRecyclerViewAdapter(getContext());
        adapter.setFileList(loadFileList());

        fileListRecyclerView.setLayoutManager(layoutManager);
        fileListRecyclerView.setAdapter(adapter);
        fileListRecyclerView.addItemDecoration(itemDecoration);
        fileListRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fileListRecyclerView,
                   new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        String chosenFile = adapter.getItem(position).getPath();
                        File sel = new File(path + "/" + chosenFile);

                        if (sel.isDirectory()) {
                            pathStack.push(chosenFile);
                            path = new File(sel + "");
                            updateAdapterFileList();
                        } else if (chosenFile.equals("...") && !sel.exists()) {
                            toUpLevelDirectory();
                        } else {
                            // check extension
                            int i = path.getName().lastIndexOf('.');
                            String ext = path.getName().substring(i+1);
                            if (i >= 0 && ext.equals("csv")) {
                                fileName.setText(path.getName());
                            }
                        }

                        Log.d("ExportCSVDialog", chosenFile);

                    }

                })
        );

        return v;
    }

    private FileListItem[] loadFileList() {

        if(!path.exists() || path.list() == null) {
            Log.e(LOG_TAG, "Path '" + path +"' does not exist");
            return null;
        }

        String[] fileNames = path.list();
        FileListItem[] fileList = new FileListItem[fileNames.length];

        for (int i = 0; i < fileNames.length; i++) {
            File sel = new File(path, fileNames[i]);
            if(sel.isDirectory())
                fileList[i] = new FileListItem(sel, ID_DIRECTORY_ICON);
            else
                fileList[i] = new FileListItem(sel, ID_FILE_ICON);
        }

        if (!isTopLevel()) {
            FileListItem temp[] = new FileListItem[fileList.length + 1];
            // return to previous directory item
            temp[0] = new FileListItem("...", ID_UP_DIRECTORY_ICON);
            System.arraycopy(fileList, 0, temp, 1, fileList.length);
            fileList = temp;
        }

        return fileList;

    }

    public void toUpLevelDirectory() {
        if(pathStack.empty()) return;
        String s = pathStack.pop();
        // path modified to exclude present directory
        path = new File(path.toString().substring(0, path.toString().lastIndexOf(s)));
        updateAdapterFileList();
    }

    private boolean isTopLevel() { return pathStack.empty(); }

    private void updateAdapterFileList() {
        adapter.setFileList(loadFileList());
        adapter.notifyDataSetChanged();
    }

    public String getPath() { return path.getPath(); }

    public String getFileName() { return fileName.getText().toString(); }

    public void updateFileList() {
        adapter.setFileList(loadFileList());
        adapter.notifyDataSetChanged();
    }

}
