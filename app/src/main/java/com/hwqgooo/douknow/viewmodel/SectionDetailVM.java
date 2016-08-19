package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.SectionsDetails;
import com.hwqgooo.douknow.model.dao.DailyORM;
import com.hwqgooo.douknow.model.service.DailyDaoService;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/27.
 */
public class SectionDetailVM {
    public static final String TAG = "ThemesDailyDetailVM";
    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ReplyCommand onRefresh;
    public ReplyCommand onLoadMore;
    public ItemView itemView = ItemView.of(BR.item, R.layout.item_section_detail);
    public ObservableList<SectionsDetails.SectionsDetailsInfo> items = new ObservableArrayList();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> image = new ObservableField<>();
    IZhihuDailyService zhihuDailyService;
    int dailyid;
    private long timetemp;
    DailyDaoService dailyDaoService;

    public SectionDetailVM(Context context, int dailyid) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        this.dailyid = dailyid;
        onRefresh = new ReplyCommand(onRefreshAction);
        onLoadMore = new ReplyCommand(onLoadMoreAction);
        dailyDaoService = DailyDaoService.getInstance(context);
    }

    public void onDestory() {
        zhihuDailyService.onDestroy();
    }


    Func2 resetRead = new Func2<SectionsDetails, List<DailyORM>, SectionsDetails>() {
        @Override
        public SectionsDetails call(SectionsDetails sectionsDetails,
                                    List<DailyORM> dailyORMs) {
            if (sectionsDetails.stories != null) {
                for (SectionsDetails.SectionsDetailsInfo info :
                        sectionsDetails.stories) {
                    if (info.isRead == null) {
                        Log.d(TAG, "call: is null");
                        info.isRead = new ObservableBoolean(false);
                    }
                    for (DailyORM orm : dailyORMs) {
                        if (info.id == orm.getDailyid()) {
                            info.isRead.set(true);
                            break;
                        }
                    }
                }
            }
            return sectionsDetails;
        }
    };
    Action0 onRefreshAction = new Action0() {
        @Override
        public void call() {
            isRefreshing.set(true);
            Observable.zip(zhihuDailyService.getSectionsDetails(dailyid),
                    dailyDaoService.getHistory(), resetRead)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SectionsDetails>() {
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
                        public void onNext(SectionsDetails sectionsDetails) {
                            Log.d(TAG, "onNext: " + sectionsDetails);
                            if (sectionsDetails != null) {
                                List<SectionsDetails.SectionsDetailsInfo> storiesList =
                                        sectionsDetails.stories;
                                title.set(sectionsDetails.name);
                                timetemp = sectionsDetails.timestamp;
                                if (!storiesList.isEmpty()) {
                                    items.clear();
                                    items.addAll(storiesList);
                                }

                            }
                        }
                    });
        }
    };

    Action0 onLoadMoreAction = new Action0() {
        @Override
        public void call() {
            if (timetemp <= 0) {
                Log.d(TAG, "call: onLoadMore end");
                return;
            }
            Log.d(TAG, "call: onLoadMore");
            isRefreshing.set(true);
            Observable.zip(zhihuDailyService.getBeforeSectionsDetails(dailyid, timetemp),
                    dailyDaoService.getHistory(), resetRead)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SectionsDetails>() {
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
                        public void onNext(SectionsDetails sectionsDetails) {
                            if (sectionsDetails != null) {
                                Log.d(TAG, "onNext: " + sectionsDetails.timestamp);
                                List<SectionsDetails.SectionsDetailsInfo> storiesList =
                                        sectionsDetails.stories;
                                timetemp = sectionsDetails.timestamp;
                                if (!storiesList.isEmpty()) {
                                    items.addAll(storiesList);
                                }
                            }
                        }
                    });
        }
    };

    public void setRead(SectionsDetails.SectionsDetailsInfo info) {
        info.isRead.set(true);
    }
}
