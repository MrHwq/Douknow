package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.DailyTypeBean;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by weiqiang on 2016/7/22.
 */
public class ThemeDailyVM {
    public static final String TAG = "ThemeDailyVM";

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ItemView itemView = ItemView.of(BR.item, R.layout.item_themesdaily);
    public ObservableList<DailyTypeBean.SubjectDaily> items = new ObservableArrayList();
    public ReplyCommand onRefresh;
    public ReplyCommand onLoadMore;
    IZhihuDailyService zhiHuDailyAPI;

    static ThemeDailyVM dailyVM;
    static int reference = 0;

    Subscription subscription;

    public static ThemeDailyVM getInstance(Context context) {
        if (dailyVM == null) {
            dailyVM = new ThemeDailyVM(context);
        }
        ++reference;
        return dailyVM;
    }

    private ThemeDailyVM(Context context) {
        onRefresh = new ReplyCommand(new Action0() {
            @Override
            public void call() {
                refresh();
            }
        });
        onLoadMore = new ReplyCommand(new Action0() {
            @Override
            public void call() {
                loadmore();
            }
        });
        zhiHuDailyAPI = ZhihuDailyService.getInstance(context);
    }

    public void onDestory() {
        --reference;
        if (reference == 0) {
            zhiHuDailyAPI.onDestroy();
            if (subscription != null) {
                if (subscription.isUnsubscribed()) {
                    Log.d(TAG, "refresh: isUnsubscribed");
                    subscription.unsubscribe();
                } else {
                    Log.d(TAG, "refresh: issubscribed");
                }
            }
        }
    }

    private void refresh() {
        if (isRefreshing.get()) {
            return;
        }

        Log.d(TAG, "refresh: ");
        isRefreshing.set(true);
        if (subscription != null) {
            if (subscription.isUnsubscribed()) {
                Log.d(TAG, "refresh: isUnsubscribed");
                subscription.unsubscribe();
            } else {
                Log.d(TAG, "refresh: issubscribed");
            }
        }
        subscription = zhiHuDailyAPI.getDailyType()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        isRefreshing.set(false);
                    }
                })
                .subscribe(new Subscriber<DailyTypeBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExceptionPrint.print(e, TAG);
                    }

                    @Override
                    public void onNext(final DailyTypeBean dailyTypeBean) {
                        List<DailyTypeBean.SubjectDaily> others = dailyTypeBean.getOthers();
                        if (others != null) {
                            items.clear();
                            items.addAll(others);
                        }
                    }
                });
    }

    private void loadmore() {
        Log.d(TAG, "loadmore: ");
    }
}
