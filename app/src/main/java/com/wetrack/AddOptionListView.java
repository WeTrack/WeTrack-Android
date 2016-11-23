package com.wetrack;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ListView;

public class AddOptionListView extends ListView {

    public AddOptionListView(Context context) {
        super(context);
        init();
    }

    public AddOptionListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AddOptionListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {}

    public void close() {
        int width = getWidth();
        Animation am = new ScaleAnimation(1f, 0f, 1f, 0f, width * 1f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void open() {
        setVisibility(View.VISIBLE);
        int width = getWidth();
        Animation am = new ScaleAnimation(0f, 1f, 0f, 1f, width * 1f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }
}
