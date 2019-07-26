package iss.vanilla.time.page;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iss.vanilla.time.R;
import iss.vanilla.time.TimerOptions;
import iss.vanilla.time.data.TimerPageDao;
import iss.vanilla.time.data.TimerPageEntity;

public class TimerRecyclerViewAdapter extends RecyclerView.Adapter<TimerPage> {

    private TimerPageDao dao;

    public TimerRecyclerViewAdapter(TimerPageDao dao) { this.dao = dao; }

    @NonNull
    @Override
    public TimerPage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_page, parent, false);
        return new TimerPage(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerPage page, int position) {
        TimerPageEntity entity = dao.get(position);

        if(entity == null)
            entity = new TimerPageEntity(position);

        if(!entity.hasPosition())
            entity.setPosition(position);

        page.setLabel(entity.label);
        page.setType(TimerOptions.getTypeCode(entity.timerType));
        page.setCountUpMillis(entity.countUpMillis);
    }

    @Override // max timers count
    public int getItemCount() { return Integer.MAX_VALUE; }

    public void addPage(TimerPageEntity entity) {
        dao.insertOrUpdate(entity);
        notifyDataSetChanged();
    }

}
