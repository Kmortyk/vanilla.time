package iss.vanilla.time.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import iss.vanilla.time.R;

@SuppressLint("RestrictedApi")
public class CustomPreferenceCategory extends PreferenceCategory {

    private int[] coversRes = { R.drawable.cover, R.drawable.cover2, R.drawable.cover3 };
    private int coverResIndex = 0;
    private TextView mTitleView;
    private ImageView mCoverView;

    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) { this(context, attrs, defStyleAttr, 0); }

    public CustomPreferenceCategory(Context context, AttributeSet attrs) { this(context, attrs, TypedArrayUtils.getAttr(context, android.support.v7.preference.R.attr.dialogPreferenceStyle, 16842897)); }

    public CustomPreferenceCategory(Context context) { this(context, null); }

    public CustomPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.timer_preference_category);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        mTitleView = holder.itemView.findViewById(R.id.title);
        mCoverView = holder.itemView.findViewById(R.id.cover_image_view);
        mCoverView.setOnLongClickListener(v -> {
            // next cover
            coverResIndex = (coverResIndex + 1) % coversRes.length;
            mCoverView.setBackgroundResource(coversRes[coverResIndex]);
            return true;
        });
    }

    @Override
    public void setTitle(CharSequence title) {
        if(title != null)
            mTitleView.setText(title);
    }

    @Override
    public boolean isEnabled() { return true; }

}
