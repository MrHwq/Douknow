package com.hwqgooo.douknow.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hwqgooo.databinding.utils.recyclerview.OnRcvClickListener;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityThemesDailyBinding;
import com.hwqgooo.douknow.databinding.ItemThemeDailyDetailBinding;
import com.hwqgooo.douknow.databinding.ItemThemeDailyEditorBinding;
import com.hwqgooo.douknow.viewmodel.MainVM;
import com.hwqgooo.douknow.viewmodel.ThemesDailyDetailVM;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by weiqiang on 2016/7/27.
 */
public class ThemesDailyDetailsActivity extends AppCompatActivity {
    final static String TAG = "ThemesDailyDetailsActivity";
    ThemesDailyDetailVM themesDailyDetailVM;
    ActivityThemesDailyBinding binding;
    CompositeSubscription compositeSubscription = new CompositeSubscription();
    int dailyId;

    public static void launch(Context context, View childView,
                              int dailyId, String title, String image) {
        final Intent intent = new Intent(context, ThemesDailyDetailsActivity.class);
        intent.putExtra("dailyid", dailyId);
        intent.putExtra("dailytitle", title);
        intent.putExtra("dailyimage", image);
        final ActivityOptionsCompat options;

        if (Build.VERSION.SDK_INT >= 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, childView, title);
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
        dailyId = intent.getIntExtra("dailyid", 0);
        if (themesDailyDetailVM == null) {
            themesDailyDetailVM = new ThemesDailyDetailVM(this, dailyId);
        }
        themesDailyDetailVM.title.set(intent.getStringExtra("dailytitle"));
        themesDailyDetailVM.image.set(intent.getStringExtra("dailyimage"));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_themes_daily);
        binding.setDailydetailvm(themesDailyDetailVM);
//        binding.executePendingBindings();
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));
        binding.swipeRcvEditor.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemThemeDailyEditorBinding>() {
                    @Override
                    public void onItemClick(ItemThemeDailyEditorBinding binding, int position) {
                        EditorInfoActivity.launch(ThemesDailyDetailsActivity.this,
                                binding.itemImage,
                                themesDailyDetailVM.editors.get(position).getId(),
                                themesDailyDetailVM.editors.get(position).getName());
                    }
                });
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemThemeDailyDetailBinding>() {
                    @Override
                    public void onItemClick(ItemThemeDailyDetailBinding binding, int position) {
                        DailyDetailActivity.launch(ThemesDailyDetailsActivity.this,
                                binding.itemTitle,
                                themesDailyDetailVM.items.get(position).getId(),
                                themesDailyDetailVM.items.get(position).getTitle(),
                                "");
                        themesDailyDetailVM.setRead(themesDailyDetailVM.items.get(position));
                    }
                });
        initAppbar();
    }

    void initAppbar() {
        // 初始化ToolBar
        setSupportActionBar(binding.toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.collToolbarLayout.setTitleEnabled(true);
        mActionBar.setTitle(themesDailyDetailVM.title.get());
//        binding.toolbar.setNavigationIcon(R.drawable.back);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    boolean isFirst = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
//            sectionDetailVM.loadData.addOnPropertyChangedCallback(myPropertyChanged);
            themesDailyDetailVM.onRefresh.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (themesDailyDetailVM != null) {
            themesDailyDetailVM.onDestory();
        }
        compositeSubscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
