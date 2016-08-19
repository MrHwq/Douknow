package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.DailyBean;
import com.hwqgooo.douknow.model.bean.DailyListBean;
import com.hwqgooo.douknow.model.dao.DailyORM;
import com.hwqgooo.douknow.model.databindmodel.TopDailyModel;
import com.hwqgooo.douknow.model.service.DailyDaoService;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.DateUtil;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemViewSelector;
import me.tatarka.bindingcollectionadapter.collections.MergeObservableList;
import me.tatarka.bindingcollectionadapter.itemviews.ItemViewClassSelector;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/22.
 */
public class DailyVM {
    public static final String TAG = "DailyVM";
    public final DailysMerge merge;
    public final TopDailyModel topdaily;
    public final ObservableList<Object> dailyBeans;
    public String currentTime = "";
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ReplyCommand onRefresh;
    public ReplyCommand onLoadMore;

    IZhihuDailyService zhiHuDailyAPI;
    DailyDaoService dailyDaoService;
    static DailyVM dailyVM;
    Subscription subscription;

    public static DailyVM getInstance(Context context) {
        Log.d(TAG, "getInstance: ");
        if (dailyVM == null) {
            dailyVM = new DailyVM(context);
        }
        return dailyVM;
    }

    private DailyVM(Context context) {
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
        dailyDaoService = DailyDaoService.getInstance(context);
        topdaily = new TopDailyModel();
        dailyBeans = new ObservableArrayList<>();
        merge = new DailysMerge();
        merge.items.insertItem(topdaily);
        merge.items.insertList(dailyBeans);
    }

    public void onDestory() {
        Log.d(TAG, "onDestory: ");
        zhiHuDailyAPI.onDestroy();
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        dailyVM = null;
    }

    public class DailysMerge {
        public final ItemViewSelector itemView = ItemViewClassSelector.builder()
                .put(TopDailyModel.class, BR.item, R.layout.pager_gallery)
                .put(DailyBean.class, BR.item, R.layout.item_daily_bean)
                .put(String.class, BR.item, R.layout.item_daily_bean_time)
                .build();
        public MergeObservableList<Object> items = new MergeObservableList<>();
    }

    private void refresh() {
        if (isRefreshing.get()) {
            return;
        }

        Log.d(TAG, "refresh: ");
        isRefreshing.set(true);
//        if (subscription != null && subscription.isUnsubscribed()) {
//            subscription.unsubscribe();
//        }
        subscription = Observable.zip(zhiHuDailyAPI.getlatestNews(),
                dailyDaoService.getHistory(), resetRead)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        isRefreshing.set(false);
                    }
                })
                .subscribe(new Subscriber<DailyListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExceptionPrint.print(e, TAG);
                    }

                    @Override
                    public void onNext(final DailyListBean dailyListBean) {
                        if (!dailyListBean.getTop_stories().isEmpty()) {
                            topdaily.items.clear();
                            topdaily.items.addAll(dailyListBean.getTop_stories());
                            topdaily.setIndicatorCount(dailyListBean.getTop_stories().size());
                        }
                        if (!dailyListBean.getStories().isEmpty()) {
                            dailyBeans.clear();
                            dailyBeans.add("今日新闻");
                            dailyBeans.addAll(dailyListBean.getStories());
                        }

                        currentTime = dailyListBean.getDate();
                    }
                });
    }

    Func2 resetRead = new Func2<DailyListBean, List<DailyORM>, DailyListBean>() {
        @Override
        public DailyListBean call(DailyListBean dailyListBean, List<DailyORM>
                dailyORMs) {
            for (DailyBean bean : dailyListBean.getStories()) {
                for (DailyORM orm : dailyORMs) {
                    if (bean.getId() == orm.getDailyid()) {
                        bean.setRead(true);
                        break;
                    }
                }
            }
            return dailyListBean;
        }
    };

    private void loadmore() {
        if (isRefreshing.get()) {
            return;
        }
        Log.d(TAG, "loadmore: ");
        isRefreshing.set(true);
//        if (subscription != null) {
//            if (subscription.isUnsubscribed()) {
//                Log.d(TAG, "loadmore: isUnsubscribed");
//                subscription.unsubscribe();
//            } else {
//                Log.d(TAG, "loadmore: issubscribed");
//            }
//        }
        subscription = Observable.zip(zhiHuDailyAPI.getBeforeNews(currentTime),
                dailyDaoService.getHistory(), resetRead)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        isRefreshing.set(false);
                    }
                })
                .subscribe(new Subscriber<DailyListBean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ExceptionPrint.print(e, TAG);
                    }

                    @Override
                    public void onNext(DailyListBean dailyListBean) {
                        Log.d(TAG, "loadmore onNext: " + dailyListBean.getStories().size());
                        if (dailyListBean.getStories().isEmpty()) {
                            return;
                        }

                        currentTime = dailyListBean.getDate();
                        dailyBeans.add(DateUtil.formatDate(dailyListBean.getDate()));
                        dailyBeans.addAll(dailyListBean.getStories());
                    }
                });
    }

    public void setRead(DailyBean daily) {
        daily.setRead(true);
    }
}
