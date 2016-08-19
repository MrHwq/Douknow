package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.util.Log;

import me.tatarka.bindingcollectionadapter.ItemView;
import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.DailySections;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/27.
 */
public class SectionsVM {
    public static final String TAG = "SectionsVM";
    IZhihuDailyService zhihuDailyService;
    static SectionsVM sectionsVM;
    static int refrence;

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ReplyCommand onRefresh = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            isRefreshing.set(true);
            zhihuDailyService.getZhiHuSections()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DailySections>() {
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
                        public void onNext(DailySections dailySections) {
                            List<DailySections.DailySectionsInfo> datas = dailySections.data;
                            if (datas != null && !datas.isEmpty()) {
                                items.clear();
                                items.addAll(datas);
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
    public ItemView itemView = ItemView.of(BR.item, R.layout.item_sections);
    public ObservableList<DailySections.DailySectionsInfo> items = new ObservableArrayList();


    public static SectionsVM getInstance(Context context) {
        if (sectionsVM == null) {
            sectionsVM = new SectionsVM(context);
        }
        ++refrence;
        return sectionsVM;
    }

    private SectionsVM(Context context) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
    }

    public void onDestory() {
        --refrence;
        if (refrence == 0) {
            zhihuDailyService.onDestroy();
        }
    }
}
