package com.hwqgooo.douknow.viewmodel;


import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.LaunchImageBean;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import rx.Subscriber;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/21.
 */
public class EntryVM {
    public final static String TAG = "EntryVM";
    private static final String RESOLUTION = "1080*1776";
    public ObservableField<String> entryName = new ObservableField<>();
    public ObservableField<String> entryImageUrl = new ObservableField<>();
    public ObservableInt entryErrorImage = new ObservableInt();
    IZhihuDailyService zhihuDailyService;
    public ReplyCommand onRefresh = new ReplyCommand(new Action0() {
        @Override
        public void call() {
            zhihuDailyService.getLaunchImage(RESOLUTION)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Subscriber<LaunchImageBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            entryImageUrl.set("");
                            ExceptionPrint.print(e, TAG);
                        }

                        @Override
                        public void onNext(LaunchImageBean launchImageBean) {
                            entryImageUrl.set(launchImageBean.getImg());
                            entryName.set(launchImageBean.getText());
                        }
                    });
        }
    });

    public EntryVM(Context context) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        entryName.set(" ");
        entryErrorImage.set(R.drawable.default_splash);
    }

    public void onDestroy() {

    }
}
