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
import com.hwqgooo.douknow.databinding.ActivitySectionDetailBinding;
import com.hwqgooo.douknow.databinding.ItemSectionDetailBinding;
import com.hwqgooo.douknow.viewmodel.MainVM;
import com.hwqgooo.douknow.viewmodel.SectionDetailVM;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by weiqiang on 2016/7/28.
 */
public class SectionsDetailsActivity extends AppCompatActivity {
    final static String TAG = "SectionsDetailsActivity";
    SectionDetailVM sectionDetailVM;
    ActivitySectionDetailBinding binding;
    CompositeSubscription compositeSubscription = new CompositeSubscription();
    int dailyId;

    public static void launch(Context context, View childView,
                              int dailyId, String title, String image) {
        final Intent intent = new Intent(context, SectionsDetailsActivity.class);
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
        if (sectionDetailVM == null) {
            sectionDetailVM = new SectionDetailVM(this, dailyId);
        }
        sectionDetailVM.title.set(intent.getStringExtra("dailytitle"));
        sectionDetailVM.image.set(intent.getStringExtra("dailyimage"));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_section_detail);
        binding.setSectiondetailvm(sectionDetailVM);
//        binding.executePendingBindings();
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemSectionDetailBinding>() {
                    @Override
                    public void onItemClick(ItemSectionDetailBinding binding, int position) {
                        DailyDetailActivity.launch(SectionsDetailsActivity.this,
                                binding.itemImage,
                                sectionDetailVM.items.get(position).id,
                                sectionDetailVM.items.get(position).title,
                                sectionDetailVM.items.get(position).images == null ? "" :
                                        sectionDetailVM.items.get(position).images.get(0));
                        sectionDetailVM.setRead(sectionDetailVM.items.get(position));
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
        mActionBar.setTitle(sectionDetailVM.title.get());
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
            sectionDetailVM.onRefresh.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sectionDetailVM != null) {
            sectionDetailVM.onDestory();
        }
        compositeSubscription.unsubscribe();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
