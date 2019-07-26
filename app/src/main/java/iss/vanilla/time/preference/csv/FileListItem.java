package iss.vanilla.time.preference.csv;

import java.io.File;

public class FileListItem {

    private int iconResourceId;
    public String path;

    public long date;

    public FileListItem(File path) { this(path, 0); }

    public FileListItem(String path, Integer iconResourceId) {
        this.path = path;
        this.iconResourceId = iconResourceId;
    }

    public FileListItem(File path, Integer iconResourceId) {
        this.path = path.getName();
        this.date = path.lastModified();
        this.iconResourceId = iconResourceId;
    }

    public boolean exists() { return date != 0; }

    public boolean hasIcon() { return iconResourceId != 0; }

    public int getIconResourceId() { return iconResourceId; }

    public void setIconResourceId(int id) { iconResourceId = id; }

    public String getPath() { return path; }

    @Override
    public String toString() { return getPath(); }

}
