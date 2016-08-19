package com.hwqgooo.douknow.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.hwqgooo.databinding.command.webview.ViewBindingAdapter;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityDailyDetailBinding;
import com.hwqgooo.douknow.model.bean.DailyBean;
import com.hwqgooo.douknow.model.dao.DailyORM;
import com.hwqgooo.douknow.model.service.DailyDaoService;
import com.hwqgooo.douknow.viewmodel.DailyDetailVM;
import com.hwqgooo.douknow.viewmodel.MainVM;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by weiqiang on 2016/7/23.
 */
public class DailyDetailActivity extends AppCompatActivity {
    final static String TAG = "DailyDetailActivity";
    DailyDetailVM dailyDetailVM;
    ActivityDailyDetailBinding binding;
    DailyBean dailyBean;
    int dailyId;
    MyPropertyChanged myPropertyChanged = new MyPropertyChanged();
    CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static void launch(Context context, View childView, int dailyId,
                              String title, String image) {
        final Intent intent = new Intent(context, DailyDetailActivity.class);
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
        DailyDaoService.getInstance(context.getApplicationContext())
                .insertHistory(dailyId)
                .subscribeOn(Schedulers.io()).subscribe(new Subscriber<DailyORM>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(DailyORM dailyORM) {
                Log.d(TAG, "insertHistory: " + dailyORM.getDailyid());
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 允许使用transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setSharedElementEnterTransition(enterTransition());
//            getWindow().setSharedElementReturnTransition(returnTransition());
//        }
        if (dailyDetailVM == null) {
            dailyDetailVM = new DailyDetailVM(getApplicationContext());
        }
        Intent intent = getIntent();
        dailyBean = intent.getParcelableExtra("dailybean");
        if (dailyBean != null) {
            dailyId = dailyBean.getId();
        } else {
            dailyId = intent.getIntExtra("dailyid", 0);
        }
        dailyDetailVM.title.set(intent.getStringExtra("dailytitle"));
        dailyDetailVM.image.set(intent.getStringExtra("dailyimage"));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_detail);
        binding.setDailydetailvm(dailyDetailVM);
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));
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
        mActionBar.setTitle("");
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
            dailyDetailVM.loadData.addOnPropertyChangedCallback(myPropertyChanged);
            dailyDetailVM.onRefresh.execute(dailyId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dailyDetailVM != null) {
            dailyDetailVM.onDestory();
        }
        compositeSubscription.unsubscribe();
    }

    MenuItem itemCommentNum;
//    MenuItem itemPariseNum;
    MenuItem itemParise;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        itemCommentNum = menu.findItem(R.id.menu_action_comment_num);
//        itemPariseNum = menu.findItem(R.id.menu_action_parise_num);
        itemParise = menu.findItem(R.id.menu_action_parise);

        dailyDetailVM.comment.addOnPropertyChangedCallback(myPropertyChanged);
        dailyDetailVM.popularity.addOnPropertyChangedCallback(myPropertyChanged);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu: ");
//        if (dailyDetailVM.popularity.get() != 0) {
//            itemPariseNum.setVisible(true);
//            itemPariseNum.setTitle(Integer.toString(dailyDetailVM.popularity.get()));
//        } else {
//            itemPariseNum.setVisible(false);
//        }
        if (dailyDetailVM.comment.get() != 0) {
            itemCommentNum.setVisible(true);
            itemCommentNum.setTitle(Integer.toString(dailyDetailVM
                    .comment.get()));
        } else {
            itemCommentNum.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        String title;
        if (dailyBean != null) {
            title = dailyBean.getTitle();
        } else {
            title = dailyDetailVM.title.get();
        }
        intent.putExtra(Intent.EXTRA_TEXT, "来自「知乎」的分享:" + title +
                "，" + dailyDetailVM.shareUrl);
        startActivity(Intent.createChooser(intent, title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_share:
                //分享新闻
                share();
                return true;

            case R.id.menu_action_fav:
                //查看新闻推荐者
                DailyRecommendEditorsActivity.launch(DailyDetailActivity.this,
                        binding.toolbar, dailyId);
                return true;

            case R.id.menu_action_comment:
                // 查看新闻评论
                if (dailyDetailVM.comment.get() != 0) {
                    DailyCommentActivity.launch(this,
                            binding.toolbar,
                            dailyId,
                            dailyDetailVM.comment.get(),
                            dailyDetailVM.longcomments.get(),
                            dailyDetailVM.shortcomments.get());
                } else {
                    Snackbar.make(binding.coordinatorlayout, "没有评论", Snackbar.LENGTH_LONG).show();
                }
                return true;

            case R.id.menu_action_parise:
                //执行点赞动画
                AnimationUtils.loadAnimation(DailyDetailActivity.this, R.anim.anim_small);
                itemParise.setIcon(R.drawable.praised);
                Toast.makeText(DailyDetailActivity.this,
                        "点赞数:" + dailyDetailVM.popularity.get(), Toast.LENGTH_SHORT).show();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MyPropertyChanged extends Observable.OnPropertyChangedCallback {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            if (observable == dailyDetailVM.loadData) {
                if (i == BR.data) {
                    compositeSubscription.add(rx.Observable.just(dailyDetailVM.loadData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<ViewBindingAdapter.WebViewLoadData>() {
                                @Override
                                public void call(ViewBindingAdapter.WebViewLoadData
                                                         webViewLoadData) {
                                    binding.webviewdetail.loadData(dailyDetailVM.loadData.data,
                                            dailyDetailVM.loadData.mimeType,
                                            dailyDetailVM.loadData.encoding);
                                }
                            }));
                }
            } else if (observable == dailyDetailVM.comment) {
                compositeSubscription.add(rx.Observable.just(dailyDetailVM.comment.get())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d(TAG, "itemCommentNum call: " + integer);
                                itemCommentNum.setTitle(integer.toString());
                                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                                invalidateOptionsMenu();
                            }
                        }));
            } else if (observable == dailyDetailVM.popularity) {
                compositeSubscription.add(rx.Observable.just(dailyDetailVM.popularity.get())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                Log.d(TAG, "itemParise call: " + integer);
//                                itemPariseNum.setTitle(integer.toString());
                                getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
                            }
                        }));
            }
        }
    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private Transition enterTransition() {
//        ChangeBounds bounds = new ChangeBounds();
////        bounds.setDuration(2000);
//        Log.d(TAG, "enterTransition: ");
//
//        return bounds;
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private Transition returnTransition() {
//        ChangeBounds bounds = new ChangeBounds();
//        bounds.setInterpolator(new DecelerateInterpolator());
////        bounds.setDuration(2000);
//        Log.d(TAG, "returnTransition: ");
//
//        return bounds;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCompat.finishAfterTransition(this);
    }
}
