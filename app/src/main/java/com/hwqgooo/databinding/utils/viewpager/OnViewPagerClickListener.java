package com.hwqgooo.databinding.utils.viewpager;

import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by weiqiang on 2016/7/26.
 */
public abstract class OnViewPagerClickListener implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    public OnViewPagerClickListener(final ViewPager viewPager) {
        mGestureDetector = new GestureDetector(viewPager.getContext(), new GestureDetector
                .SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                try {
                    View childView = viewPager.getChildAt(viewPager.getCurrentItem());
                    onItemClick(childView, viewPager.getCurrentItem());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                try {
                    View childView = viewPager.getChildAt(viewPager.getCurrentItem());
                    onItemLongClick(childView, viewPager.getCurrentItem());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    public abstract void onItemClick(View childView, int position);

    public void onItemLongClick(View childView, int position) {
    }
}
