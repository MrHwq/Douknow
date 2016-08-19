package com.hwqgooo.databinding.command.tablayout;

import android.databinding.BindingAdapter;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

/**
 * Created by weiqiang on 2016/7/6.
 */
public class ViewBindingAdapter {
    @BindingAdapter(value = {"viewpager"},
            requireAll = false)
    public static void setViewPager(final TabLayout tabLayout, final ViewPager viewPager) {
        if (viewPager == null) {
            return;
        }
        tabLayout.setupWithViewPager(viewPager);
    }
}
