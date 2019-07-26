package iss.vanilla.time;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import iss.vanilla.time.data.AppDatabase;
import iss.vanilla.time.data.TimerPageEntity;
import iss.vanilla.time.page.TimerRecyclerViewAdapter;
import iss.vanilla.time.page.TimerPage;
import iss.vanilla.time.preference.PreferencesFragment;
import iss.vanilla.time.preference.csv.CSVWriter;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends FragmentActivity {

    private final static int REQUEST_PERMISSION_EXTERNAL_STORAGE = 1;

    private TimerOptions timerOptions;
    private TimerThread timerThread;
    private TimerRecyclerViewAdapter adapter;
    private RecyclerView timerRecyclerView;
    private LinearLayoutManager layoutManager;
    private AppDatabase db;
    private PreferencesFragment preferencesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fullscreen, no action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar ab = getActionBar();
        if(ab != null) { ab.hide(); }

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        db =  Room
                .databaseBuilder(getApplicationContext(), AppDatabase.class, "timer_db")
                .allowMainThreadQueries()
                .build();

        preferencesFragment = (PreferencesFragment) fragmentManager.findFragmentById(R.id.settings_fragment);
        timerOptions = new TimerOptions();
        timerThread = new TimerThread(this, timerOptions);

        if(preferencesFragment != null) {
            preferencesFragment.setPageActivity(this);
        } else
            Log.e("MainActivity", "Cannot find fragment 'R.id.settings_fragment'");

        PagerSnapHelper snapHelper = new PagerSnapHelper();

        adapter = new TimerRecyclerViewAdapter(db.timerPageDao());
        timerRecyclerView = findViewById(R.id.timer_recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        timerRecyclerView.setAdapter(adapter);
        timerRecyclerView.setLayoutManager(layoutManager);
        snapHelper.attachToRecyclerView(timerRecyclerView);

        timerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean firstPage = true;
            private int previousPosition;
            private TimerPage prevPage;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int position = layoutManager.findFirstVisibleItemPosition();

                // first page only once
                if(firstPage) {
                    previousPosition = position;
                    firstPage = false;
                } else {
                    // check if we at same page
                    if(position == previousPosition)
                        return;
                    // stop count up at previous page and save
                    if(prevPage != null) {
                        prevPage.setRun(false);
                        savePage(previousPosition, prevPage);
                    }
                }

                TimerPage page = getPage(position);
                if(page == null) { page = addNewPage(position); }
                // update preferences
                if(preferencesFragment != null) { preferencesFragment.updateWithPage(page); }
                // run timer
                timerThread.setCurrentPage(page);
                // hold prev page
                previousPosition = position;
                prevPage = page;

            }

        });

        timerRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                getCurrentPage().setRun(false);
                return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }

        });

        // start application
        timerThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        int position = layoutManager.findFirstVisibleItemPosition();
        savePage(position, getPage(position));
        // save position
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("page_position", position);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyTimerOptions();
        // restore position
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        layoutManager.scrollToPosition(prefs.getInt("page_position", 0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        timerThread.stopTimer();

        boolean tryAgain = true;
        while(tryAgain) {
            try {
                timerThread.join();
                tryAgain = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        timerThread = null;
        System.gc();
    }

    public void applyTimerOptions() {
        timerRecyclerView.setBackgroundColor(timerOptions.backgroundColor);
        scaleText();
    }

    public void exportCSV(String path, String fileName) {

        if(path == null || path.isEmpty() || fileName == null || fileName.isEmpty())
            return;

        File file = new File(path, fileName);

        try{
            if(!file.exists() && !file.createNewFile())
                    throw new IOException("Cannot create file.");
            CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
            // write column names
            csvWriter.writeNext(TimerPageEntity.getColumnNames());
            // write data
            for(TimerPageEntity entity : db.timerPageDao().getAll()) {
                csvWriter.writeNext(entity.getStringValues());
            }
            csvWriter.close();
            Toast.makeText(MainActivity.this, "File successfully saved.", Toast.LENGTH_SHORT).show();

        }catch(IOException exception) {
            exception.printStackTrace();
            Toast.makeText(MainActivity.this, "Error while saving file.", Toast.LENGTH_SHORT).show();
        }

    }

    public void deleteData() {
        layoutManager.scrollToPosition(0);
        db.timerPageDao().deleteAll();
        addNewPage(0);
    }

    private void scaleText() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        timerOptions.textPxSize = size.x / (double) timerOptions.getLettersCount();
    }

    public TimerPage addNewPage(int pos) {
        adapter.addPage(new TimerPageEntity(pos));
        return getPage(pos);
    }

    public TimerOptions getTimerOptions() { return timerOptions; }

    public void savePage(int position, TimerPage page) { db.timerPageDao().insertOrUpdate(new TimerPageEntity(position, page)); }

    public TimerPage getPage(int position) { return (TimerPage) timerRecyclerView.findViewHolderForAdapterPosition(position); }

    public TimerPage getCurrentPage() {
        int position = layoutManager.findFirstVisibleItemPosition();
        return (TimerPage) timerRecyclerView.findViewHolderForAdapterPosition(position);
    }

    public void requestStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
               ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
            else ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                                                             Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_EXTERNAL_STORAGE) { preferencesFragment.updateExportCSVDialog(); }
    }

}