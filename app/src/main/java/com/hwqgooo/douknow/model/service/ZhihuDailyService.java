package com.hwqgooo.douknow.model.service;

import android.content.Context;
import android.util.Log;

import com.hwqgooo.douknow.model.bean.DailyComment;
import com.hwqgooo.douknow.model.bean.DailyDetail;
import com.hwqgooo.douknow.model.bean.DailyExtraMessage;
import com.hwqgooo.douknow.model.bean.DailyListBean;
import com.hwqgooo.douknow.model.bean.DailyRecommend;
import com.hwqgooo.douknow.model.bean.DailySections;
import com.hwqgooo.douknow.model.bean.DailyTypeBean;
import com.hwqgooo.douknow.model.bean.HotNews;
import com.hwqgooo.douknow.model.bean.LaunchImageBean;
import com.hwqgooo.douknow.model.bean.SectionsDetails;
import com.hwqgooo.douknow.model.bean.ThemesDetails;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by weiqiang on 2016/7/22.
 */
public class ZhihuDailyService implements IZhihuDailyService {
    Context context;
    public static final String TAG = "ZhihuDailyService";
    public static final String ZHIHU_DAILY_URL = "http://news-at.zhihu.com/api/4/";
    Retrofit mRetrofit;
    IZhiHuDailyAPI zhiHuDailyAPI;
    static int reference = 0;
    static ZhihuDailyService service;

    public static IZhihuDailyService getInstance(Context context) {
        if (service == null) {
            service = new ZhihuDailyService(context);
        }
        ++reference;
        return service;
    }

    private ZhihuDailyService(Context context) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ZHIHU_DAILY_URL)
                .client(CacheHttpClient.getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        zhiHuDailyAPI = mRetrofit.create(IZhiHuDailyAPI.class);
        Log.d(TAG, "ZhihuDailyService: create");
    }

    @Override
    public Observable<DailyListBean> getlatestNews() {
        return zhiHuDailyAPI.getlatestNews();
    }

    @Override
    public Observable<DailyListBean> getBeforeNews(@Path("date") String date) {
        return zhiHuDailyAPI.getBeforeNews(date);
    }

    @Override
    public Observable<DailyDetail> getNewsDetails(@Path("id") int id) {
        return zhiHuDailyAPI.getNewsDetails(id);
    }

    @Override
    public Observable<LaunchImageBean> getLaunchImage(@Path("res") String res) {
        return zhiHuDailyAPI.getLaunchImage(res);
    }

    @Override
    public Observable<DailyTypeBean> getDailyType() {
        return zhiHuDailyAPI.getDailyType();
    }

    @Override
    public Observable<ThemesDetails> getThemesDetailsById(@Path("id") int id) {
        return zhiHuDailyAPI.getThemesDetailsById(id);
    }

    @Override
    public Observable<DailyExtraMessage> getDailyExtraMessageById(@Path("id") int id) {
        return zhiHuDailyAPI.getDailyExtraMessageById(id);
    }

    @Override
    public Observable<DailyComment> getDailyLongComment(@Path("id") int id) {
        return zhiHuDailyAPI.getDailyLongComment(id);
    }

    @Override
    public Observable<DailyComment> getDailyShortComment(@Path("id") int id) {
        return zhiHuDailyAPI.getDailyShortComment(id);
    }

    @Override
    public Observable<HotNews> getHotNews() {
        return zhiHuDailyAPI.getHotNews();
    }

    @Override
    public Observable<DailySections> getZhiHuSections() {
        return zhiHuDailyAPI.getZhiHuSections();
    }

    @Override
    public Observable<SectionsDetails> getSectionsDetails(@Path("id") int id) {
        return zhiHuDailyAPI.getSectionsDetails(id);
    }

    @Override
    public Observable<SectionsDetails> getBeforeSectionsDetails(@Path("id") int id,
                                                                @Path("timestamp") long timestamp) {
        return zhiHuDailyAPI.getBeforeSectionsDetails(id, timestamp);
    }

    @Override
    public Observable<DailyRecommend> getDailyRecommendEditors(@Path("id") int id) {
        return zhiHuDailyAPI.getDailyRecommendEditors(id);
    }

    @Override
    public void onDestroy() {
        --reference;
        if (reference == 0) {
            service = null;
        }
    }
}
