package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.HotNews;
import com.hwqgooo.douknow.model.dao.DailyORM;
import com.hwqgooo.douknow.model.service.DailyDaoService;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/27.
 */
public class HotNewsVM {
    public static final String TAG = "HotNewsVM";
    static HotNewsVM hotNewsVM;
    static int refrence;

    IZhihuDailyService zhihuDailyService;
    Subscription subscription;
    DailyDaoService dailyDaoService;
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);

    Func2 resetRead = new Func2<HotNews, List<DailyORM>, HotNews>() {
        @Override
        public HotNews call(HotNews hotNews, List<DailyORM> dailyORMs) {
            if (hotNews.recent != null) {
                for (HotNews.HotNewsInfo hotNewsInfo : hotNews.recent) {
                    if (hotNewsInfo.isRead == null) {
                        hotNewsInfo.isRead = new ObservableBoolean(false);
                        Log.d(TAG, "call: isnull");
                    }
                    for (DailyORM orm : dailyORMs) {
                        if (hotNewsInfo.newsId == orm.getDailyid()) {
                            hotNewsInfo.isRead.set(true);
                            break;
                        }
                    }
                }
            }
            return hotNews;
        }
    };
    public ReplyCommand onRefresh = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            isRefreshing.set(true);
            subscription = Observable.zip(zhihuDailyService.getHotNews(),
                    dailyDaoService.getHistory(), resetRead)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HotNews>() {
                        @Override
                        public void onCompleted() {
                            isRefreshing.set(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            ExceptionPrint.print(e, TAG);
                            isRefreshing.set(false);
                        }

                        @Override
                        public void onNext(HotNews hotNews) {
                            List<HotNews.HotNewsInfo> recent = hotNews.recent;
                            if (recent != null && !recent.isEmpty()) {
                                items.clear();
                                items.addAll(recent);
                            }
                        }
                    });
        }
    });

    public ReplyCommand onLoadMore = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            Log.d(TAG, "call: onLoadMore");
        }
    });
    public ItemView itemView = ItemView.of(BR.item, R.layout.item_hotnews);
    public ObservableList<HotNews.HotNewsInfo> items = new ObservableArrayList();


    public static HotNewsVM getInstance(Context context) {
        if (hotNewsVM == null) {
            hotNewsVM = new HotNewsVM(context);
        }
        ++refrence;
        return hotNewsVM;
    }

    private HotNewsVM(Context context) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        dailyDaoService = DailyDaoService.getInstance(context);
    }

    public void onDestory() {
        --refrence;
        if (refrence == 0) {
            zhihuDailyService.onDestroy();
            hotNewsVM = null;
            if (subscription != null && subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    public void setRead(HotNews.HotNewsInfo info) {
        info.isRead.set(true);
    }
}
