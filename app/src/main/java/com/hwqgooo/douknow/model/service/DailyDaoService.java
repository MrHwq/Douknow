package com.hwqgooo.douknow.model.service;

import android.content.Context;

import com.hwqgooo.douknow.model.dao.DailyORM;
import com.hwqgooo.douknow.model.dao.DaoSession;
import com.hwqgooo.douknow.view.MyApp;

import org.greenrobot.greendao.rx.RxDao;
import org.greenrobot.greendao.rx.RxQuery;

import java.util.List;

import rx.Observable;

/**
 * Created by weiqiang on 2016/8/10.
 */
public class DailyDaoService {
    public final static String TAG = "DailyDaoService";
    Context context;
    RxDao<DailyORM, Long> rxDao;
    RxQuery<DailyORM> rxQuery;

    static DailyDaoService dailyDaoService;

    public static DailyDaoService getInstance(Context context) {
        if (dailyDaoService == null) {
            dailyDaoService = new DailyDaoService(context);
        }
        return dailyDaoService;
    }

    private DailyDaoService(Context context) {
        DaoSession daoSession = ((MyApp) context.getApplicationContext()).getDaoSession();
        rxDao = daoSession.getDailyORMDao().rx();
        rxQuery = daoSession.getDailyORMDao().queryBuilder().rx();
    }

    public Observable<List<DailyORM>> getHistory() {
        return rxQuery.list();
    }

    public Observable<DailyORM> insertHistory(int dailyid) {
        DailyORM dailyORM = new DailyORM(Long.valueOf(dailyid));
        return rxDao.insertOrReplace(dailyORM);
    }
}
