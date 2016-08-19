package com.hwqgooo.douknow.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityDailyCommentBinding;
import com.hwqgooo.douknow.view.fragment.LongCommentFragment;
import com.hwqgooo.douknow.view.fragment.ShortCommentFragment;
import com.hwqgooo.douknow.viewmodel.DailyDetailVM;
import com.hwqgooo.douknow.viewmodel.MainVM;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by weiqiang on 2016/7/25.
 */
public class DailyCommentActivity extends AppCompatActivity {
    public static final String TAG = "DailyCommentActivity";
    ActivityDailyCommentBinding binding;
    List<Fragment> fragmentList = new LinkedList<>();
    //    ItemView itemView = ItemView.of(BR.item, R.layout.item_fragment);
//    BindingViewPagerAdapter.PageTitles<Fragment> pageTitles;
    DailyDetailVM dailyDetailVM;
    int dailyid;
    int dailycomment;
    int dailylongcomment;
    int dailyshortcomment;

    public static void launch(Context context,
                              View childView,
                              int dailyId,
                              int dailycomment,
                              int dailylongcomment,
                              int dailyshortcomment) {
        Intent intent = new Intent(context, DailyCommentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dailyid", dailyId);
        intent.putExtra("dailycomment", dailycomment);
        intent.putExtra("dailylongcomment", dailylongcomment);
        intent.putExtra("dailyshortcomment", dailyshortcomment);
        final ActivityOptionsCompat options;
        if (Build.VERSION.SDK_INT >= 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, childView, Integer.toString(dailyId));
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(
                    childView, 0, 0, childView.getWidth(), childView.getHeight());
        }
        context.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        dailyid = intent.getIntExtra("dailyid", 0);
        dailycomment = intent.getIntExtra("dailycomment", 0);
        dailylongcomment = intent.getIntExtra("dailylongcomment", 0);
        dailyshortcomment = intent.getIntExtra("dailyshortcomment", 0);
        if (dailyDetailVM == null) {
            dailyDetailVM = new DailyDetailVM(getApplicationContext());
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_comment);
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));
        binding.setTitle(Integer.toString(dailyid));

        initAppbar();
        initViewPager();
        //tablayout会检查viewpager的adapter
//        binding.executePendingBindings();
        binding.tablayout.setupWithViewPager(binding.viewpager);
    }

    void initAppbar() {
        binding.toolbar.setTitle(dailycomment + " 条点评");
        setSupportActionBar(binding.toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    void initViewPager() {
        if (dailylongcomment != 0) {
            fragmentList.add(LongCommentFragment.getInstance(dailyid));
        }
        if (dailyshortcomment != 0) {
            fragmentList.add(ShortCommentFragment.getInstance(dailyid));
        }
//        pageTitles = new BindingViewPagerAdapter.PageTitles<Fragment>() {
//            @Override
//            public CharSequence getPageTitle(int position, Fragment item) {
//                if (item instanceof LongCommentFragment) {
//                    return "长评论" + " (" + dailylongcomment + ")";
//                }
//                return "短评论" + " (" + dailyshortcomment + ")";
//            }
//        };
//        binding.setItems(fragmentList);
//        binding.setItemView(itemView);
//        binding.setPagetitles(pageTitles);
        binding.viewpager.setAdapter(new MyAdapter(getSupportFragmentManager(), fragmentList));
        if (dailylongcomment != 0) {
            binding.viewpager.setCurrentItem(0);
        } else if (dailyshortcomment != 0) {
            binding.viewpager.setCurrentItem(1);
        }
        binding.tablayout.setupWithViewPager(binding.viewpager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends FragmentPagerAdapter {
        List<Fragment> lists;

        public MyAdapter(FragmentManager fm, List<Fragment> lists) {
            super(fm);
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (lists.get(position) instanceof LongCommentFragment) {
                return "长评论" + " (" + dailylongcomment + ")";
            }
            return "短评论" + " (" + dailyshortcomment + ")";
        }
    }
}
