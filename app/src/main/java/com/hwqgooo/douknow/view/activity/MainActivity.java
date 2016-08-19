package com.hwqgooo.douknow.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityMainBinding;
import com.hwqgooo.douknow.view.fragment.BaseFragment;
import com.hwqgooo.douknow.view.fragment.DailyListFragment;
import com.hwqgooo.douknow.view.fragment.HotNewsFragment;
import com.hwqgooo.douknow.view.fragment.SectionsFragment;
import com.hwqgooo.douknow.view.fragment.ThemesDailyFragment;
import com.hwqgooo.douknow.viewmodel.MainVM;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        BottomNavigationBar.OnTabSelectedListener {
    final static String TAG = "MainActivity";
    ActivityMainBinding binding;
    MainVM mainVM;
    List<Fragment> fragments = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainVM = MainVM.getInstance(getApplicationContext());
        binding.setMainvm(mainVM);
        Log.d(TAG, "onCreate: " + savedInstanceState);
        initAppbar();
        intNavigationBar(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
            //这是设置成夜间模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        }
    }

    void initAppbar() {
        // 初始化ToolBar
        setSupportActionBar(binding.toolbar);
//        ActionBar mActionBar = getSupportActionBar();
//        if (mActionBar != null) {
//            mActionBar.setDisplayHomeAsUpEnabled(true);
//        }

//        binding.toolbar.setNavigationIcon(R.drawable.back);
//        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle
            persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.d(TAG, "onRestoreInstanceState: " + savedInstanceState + "..." + persistentState);
    }

    void intNavigationBar(Bundle savedInstanceState) {
        Log.d(TAG, "intNavigationBar: " + fragments.size());
        Class[] fragClass = {DailyListFragment.class, ThemesDailyFragment.class,
                SectionsFragment.class, HotNewsFragment.class};
        FragmentManager fm = getSupportFragmentManager();
        try {
            for (int i = 0; i < fragClass.length; ++i) {
                if (fm.findFragmentByTag(Integer.toString(i)) != null) {
                    fragments.add(fm.findFragmentByTag(Integer.toString(i)));
                    Log.d(TAG, "intNavigationBar: in tag");
                } else {
                    fragments.add((BaseFragment) fragClass[i].newInstance());
                }
            }
        } catch (Exception e) {

        }
//        fragments.add(new DailyListFragment());
//        fragments.add(new ThemesDailyFragment());
//        fragments.add(new SectionsFragment());
//        fragments.add(new HotNewsFragment());
        binding.bottomNavigationBar.setTabSelectedListener(this);
//        BadgeItem badgeItemHide = new BadgeItem()
//                .setBorderWidth(4)
//                .setBackgroundColorResource(R.color.colorPrimary)
//                .setText("5")
//                .setHideOnSelect(true);
//        BadgeItem badgeItemShow = new BadgeItem()
//                .setBorderWidth(4)
//                .setBackgroundColorResource(R.color.colorAccent)
//                .setText("5")
//                .setHideOnSelect(true);

        binding.bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_profile_answer, "日报")
                        .setActiveColorResource(android.R.color.holo_blue_dark)
                        .setInActiveColorResource(android.R.color.holo_blue_bright))
                .addItem(new BottomNavigationItem(R.drawable.ic_profile_article, "主题")
                        .setActiveColorResource(android.R.color.holo_green_dark)
                        .setInActiveColorResource(android.R.color.holo_green_light))
                .addItem(new BottomNavigationItem(R.drawable.ic_profile_column, "专栏")
                        .setActiveColorResource(android.R.color.holo_red_dark)
                        .setInActiveColorResource(android.R.color.holo_red_light))
                .addItem(new BottomNavigationItem(R.drawable.ic_profile_favorite, "文章")
                        .setActiveColorResource(android.R.color.holo_orange_dark)
                        .setInActiveColorResource(android.R.color.holo_orange_light))
                .setFirstSelectedPosition(0)
                .initialise();
        onTabSelected(0);
    }

    @Override
    public void onTabSelected(int position) {
        Log.d(TAG, "onTabSelected: " + position);
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fragments.get(position);
            if (!fragment.isAdded()) {
                ft.add(R.id.layFrame, fragment, Integer.toString(position));
            } else {
                ft.show(fragment);
            }

            ft.commitAllowingStateLoss();
            int[] resColor = {android.R.color.holo_blue_dark, android.R.color.holo_green_dark,
                    android.R.color.holo_red_dark, android.R.color.holo_orange_dark};
            int color = ContextCompat.getColor(this, resColor[position]);
            mainVM.statusbarcolor.set(color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabUnselected(int position) {
        Log.d(TAG, "onTabUnselected: " + position);
        try {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment fragment = fragments.get(position);
            ft.hide(fragment);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTabReselected(int position) {
        Log.d(TAG, "onTabReselected:  " + position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainVM.onDestory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_aboutme:
                AboutActivity.launch(MainActivity.this, binding.toolbar);
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
