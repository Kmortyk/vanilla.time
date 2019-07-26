package iss.vanilla.time.page.old;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.TimerThread;
import iss.vanilla.time.data.TimerPageDao;
import iss.vanilla.time.data.TimerPageEntity;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAX_PAGES = 100;
    private final TimerThread timerThread;
    private final SparseArray<TimerPageOld> pages;
    private final TimerPageDao timerPageDao;
    private int currentPosition;

    public ScreenSlidePagerAdapter(FragmentManager fm, TimerThread timerThread, TimerPageDao timerPageDao) {
        super(fm);
        this.timerThread = timerThread;
        this.timerPageDao = timerPageDao;
        pages = new SparseArray<>();
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        // timerThread.setCurrentPage(getItem(position));
    }

    @Override
    public Fragment getItem(int position) {
        if(pages.get(position) != null)
            return pages.get(position);

        TimerPageOld timerPageOld = new TimerPageOld();
        // load from db
        TimerPageEntity entity = timerPageDao.get(position);
        if(entity != null) {
            timerPageOld.setLabel(entity.label);
            timerPageOld.setType(TimerOptions.getTypeCode(entity.timerType));
            timerPageOld.setCountUpMillis(entity.countUpMillis);
        }

        timerPageOld.setOptions(timerThread.options);
        pages.put(position, timerPageOld);
        return timerPageOld;
    }

    @Override
    public int getCount() { return MAX_PAGES; }

    public void setCurrentPosition(int pos) { currentPosition = pos; }

    public int getCurrentPosition() { return currentPosition; }

    public TimerPageOld getCurrentPage(int currentPosition) {
        setCurrentPosition(currentPosition);
        return getCurrentPage();
    }

    public TimerPageOld getCurrentPage() {
        Fragment f = getItem(currentPosition);
        return (TimerPageOld) f;
    }

}