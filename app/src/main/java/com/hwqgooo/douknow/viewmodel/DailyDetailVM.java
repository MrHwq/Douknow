package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.databinding.command.webview.ViewBindingAdapter;
import com.hwqgooo.douknow.model.bean.DailyDetail;
import com.hwqgooo.douknow.model.bean.DailyExtraMessage;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;
import com.hwqgooo.douknow.utils.HtmlUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/24.
 */
public class DailyDetailVM {
    public static final String TAG = "DailyDetailVM";
    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> imageSource = new ObservableField<>();
    public ViewBindingAdapter.WebViewLoadData loadData = new ViewBindingAdapter.WebViewLoadData();
    public ObservableInt comment = new ObservableInt(0);
    public ObservableInt popularity = new ObservableInt(0);
    public ObservableInt longcomments = new ObservableInt(0);
    public ObservableInt shortcomments = new ObservableInt(0);
    public String shareUrl;

    IZhihuDailyService zhihuDailyService;
    Subscription subscription;

    public DailyDetailVM(Context context) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        loadData.setMimeType(HtmlUtil.MIME_TYPE);
        loadData.setEncoding(HtmlUtil.ENCODING);
    }

    public void onDestory() {
        zhihuDailyService.onDestroy();
        zhihuDailyService = null;
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public class DailyDetailData {
        public String image;
        public String title;
        public String imageSource;
        public List<String> css;
        public List<String> js;
        public String body;
        public String shareUrl;
        //extra
        public int comments;
        public int longComments;
        public int popularity;
        public int shortComments;
    }

    public ReplyCommand<Integer> onRefresh = new ReplyCommand(new Action1<Integer>() {
        @Override
        public void call(final Integer integer) {
            if (subscription != null) {
                if (subscription.isUnsubscribed()) {
                    Log.d(TAG, "refresh: isUnsubscribed");
                    subscription.unsubscribe();
                } else {
                    Log.d(TAG, "refresh: issubscribed");
                }
            }
            subscription = Observable.zip(zhihuDailyService.getNewsDetails(integer.intValue()),
                    zhihuDailyService.getDailyExtraMessageById(integer.intValue()),
                    new Func2<DailyDetail, DailyExtraMessage, DailyDetailData>() {
                        @Override
                        public DailyDetailData call(DailyDetail dailyDetail, DailyExtraMessage
                                dailyExtraMessage) {
                            DailyDetailData data = new DailyDetailData();
                            data.image = dailyDetail.getImage();
                            data.title = dailyDetail.getTitle();
                            data.imageSource = dailyDetail.getImage_source();
                            data.css = dailyDetail.getCss();
                            data.js = dailyDetail.getJs();
                            data.body = dailyDetail.getBody();
                            data.shareUrl = dailyDetail.getShare_url();
                            data.comments = dailyExtraMessage.comments;
                            data.longComments = dailyExtraMessage.longComments;
                            data.popularity = dailyExtraMessage.popularity;
                            data.shortComments = dailyExtraMessage.shortComments;
                            return data;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<DailyDetailData>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "onError: id: " + integer);
                            ExceptionPrint.print(e, TAG);
                        }

                        @Override
                        public void onNext(DailyDetailData data) {
                            image.set(data.image);
                            title.set(data.title);
                            imageSource.set(data.imageSource);
                            final String css = HtmlUtil.createCssTag(data.css);
                            final String js = HtmlUtil.createJsTag(data.js);
                            String htmlData = HtmlUtil.createHtmlData(data.body, css, js);
                            loadData.setData(htmlData);
                            shareUrl = data.shareUrl;
                            //extra
                            comment.set(data.comments);
                            popularity.set(data.popularity);
                            longcomments.set(data.longComments);
                            shortcomments.set(data.shortComments);
                        }
                    });
        }
    });
}
