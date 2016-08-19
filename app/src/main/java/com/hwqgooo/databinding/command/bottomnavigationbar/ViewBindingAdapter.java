package com.hwqgooo.databinding.command.bottomnavigationbar;

import android.databinding.BindingAdapter;
import android.support.design.widget.AppBarLayout;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;

/**
 * Created by weiqiang on 2016/7/5.
 */
public class ViewBindingAdapter {
    public static final String TAG = "BottomNavigationBar";


    @BindingAdapter("addOnOffsetChangedListener")
    public static void initToolbar(BottomNavigationBar navigationBar, AppBarLayout
            .OnOffsetChangedListener listener) {
        Log.d(TAG, "addOnOffsetChangedListener: ");
    }
}
