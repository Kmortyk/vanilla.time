package iss.vanilla.time.preference.csv;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import iss.vanilla.time.R;

class ViewHolder extends RecyclerView.ViewHolder {

    TextView fileName;
    TextView fileDate;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        fileName = itemView.findViewById(R.id.file_list_item_text);
        fileDate = itemView.findViewById(R.id.file_list_item_date);
    }

}

public class FileListRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

    private FileListItem[] fileList;
    private Context context;

    public FileListRecyclerViewAdapter(Context context) { this.context = context; }

    public void setFileList(FileListItem[] fileList) { this.fileList = fileList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int i) {

        vh.fileName.setText(fileList[i].getPath());

        if(fileList[i].exists()) {
            vh.fileDate.setVisibility(View.VISIBLE);
            vh.fileDate.setText(getDateString(fileList[i].date));
        } else
            vh.fileDate.setVisibility(View.GONE);

        if(fileList[i].hasIcon()) {
            Drawable icon = context.getResources().getDrawable(fileList[i].getIconResourceId());
            icon.setBounds(0, 0, 32, 32);
            vh.fileName.setCompoundDrawables(icon, null, null, null);
            int dp = (int) (5 * context.getResources().getDisplayMetrics().density + 0.5f);
            vh.fileName.setCompoundDrawablePadding(dp);
        }

    }

    public FileListItem getItem(int pos) { return fileList[pos]; }

    @Override
    public int getItemCount() {
        if(fileList == null) return 0;
        return fileList.length;
    }

    private String getDateString(long millis) {
        return DateFormat.format("mm/dd/yyyy hh:mm:ss", new Date(millis)).toString();
    }

}
