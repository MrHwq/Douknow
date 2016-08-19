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
import com.hwqgooo.douknow.model.bean.Editors;
import com.hwqgooo.douknow.model.bean.Stories;
import com.hwqgooo.douknow.model.bean.ThemesDetails;
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
public class ThemesDailyDetailVM {
    public static final String TAG = "ThemesDailyDetailVM";

    IZhihuDailyService zhihuDailyService;
    int dailyid;
    Subscription subscription;

    public ObservableBoolean isRefreshing = new ObservableBoolean(false);
    public ReplyCommand onRefresh;
    public ReplyCommand onLoadMore;
    public ItemView itemView = ItemView.of(BR.item, R.layout.item_theme_daily_detail);
    public ObservableList<Stories> items = new ObservableArrayList();
    public ItemView itemViewEditor = ItemView.of(BR.item, R.layout.item_theme_daily_editor);
    public ObservableList<Editors> editors = new ObservableArrayList();

    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    DailyDaoService dailyDaoService;


    public ThemesDailyDetailVM(Context context, int dailyid) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        this.dailyid = dailyid;
        onRefresh = new ReplyCommand(onRefreshAction);
        onLoadMore = new ReplyCommand(onLoadMoreAction);
        dailyDaoService = DailyDaoService.getInstance(context);
    }

    public void onDestory() {
        zhihuDailyService.onDestroy();
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    Func2 resetRead = new Func2<ThemesDetails, List<DailyORM>, ThemesDetails>() {
        @Override
        public ThemesDetails call(ThemesDetails themesDetails, List<DailyORM>
                dailyORMs) {
            for (Stories stories : themesDetails.getStories()) {
                for (DailyORM orm : dailyORMs) {
                    if (stories.getId() == orm.getDailyid()) {
                        stories.isRead.set(true);
                        break;
                    }
                }
            }
            return themesDetails;
        }
    };

    Action0 onRefreshAction = new Action0() {
        @Override
        public void call() {
            isRefreshing.set(true);

            subscription = Observable.zip(zhihuDailyService.getThemesDetailsById(dailyid),
                    dailyDaoService.getHistory(), resetRead)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate(new Action0() {
                        @Override
                        public void call() {
                            isRefreshing.set(false);
                        }
                    })
                    .subscribe(new Subscriber<ThemesDetails>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            ExceptionPrint.print(e, TAG);
                        }

                        @Override
                        public void onNext(ThemesDetails dailyTypeBean) {
                            if (dailyTypeBean != null) {
                                List<Stories> storiesList = dailyTypeBean.getStories();
                                if (storiesList != null) {
                                    items.clear();
                                    items.addAll(storiesList);
                                }
                                List<Editors> editorsList = dailyTypeBean.getEditors();
                                if (editorsList != null) {
                                    editors.clear();
                                    editors.addAll(editorsList);
                                }
                                title.set(dailyTypeBean.getName());
                                image.set(dailyTypeBean.getBackground());
                            }
                        }
                    });
        }
    };

    Action0 onLoadMoreAction = new Action0() {
        @Override
        public void call() {
            Log.d(TAG, "call: onLoadMore");
        }
    };

    public void setRead(Stories stories) {
        stories.isRead.set(true);
    }
}
