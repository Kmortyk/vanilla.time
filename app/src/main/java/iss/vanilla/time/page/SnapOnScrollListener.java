package iss.vanilla.time.page;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

public class SnapOnScrollListener extends RecyclerView.OnScrollListener {

    private int snapPosition = RecyclerView.NO_POSITION;
    private OnSnapPositionChangeListener onSnapPositionChangeListener;
    private View.OnScrollChangeListener onScrollChangeListener;
    private SnapHelper snapHelper;

    public SnapOnScrollListener(SnapHelper snapHelper, OnSnapPositionChangeListener onSnapPositionChangeListener) {
        this.snapHelper = snapHelper;
        this.onSnapPositionChangeListener = onSnapPositionChangeListener;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        notifySnapPositionChange(recyclerView);
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            notifySnapPositionChange(recyclerView);
        }
    }

    private void notifySnapPositionChange(RecyclerView recyclerView) {
        View snapView = getSnapView(recyclerView);
        int snapPosition = getSnapPosition(recyclerView.getLayoutManager(), snapView);

        if (this.snapPosition != snapPosition) {
            onSnapPositionChangeListener.onSnapPositionChange(snapView, snapPosition);
            this.snapPosition = snapPosition;
        }
    }

    private int getSnapPosition(RecyclerView.LayoutManager lm, View snapView) {
        if(snapView == null) return RecyclerView.NO_POSITION;
        return lm.getPosition(snapView);
    }

    public View getSnapView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        return snapHelper.findSnapView(lm);
    }

}