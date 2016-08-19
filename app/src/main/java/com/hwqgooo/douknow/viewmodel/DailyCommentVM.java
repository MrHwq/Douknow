package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;

import me.tatarka.bindingcollectionadapter.ItemView;
import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.DailyComment;
import com.hwqgooo.douknow.model.service.IZhihuDailyService;
import com.hwqgooo.douknow.model.service.ZhihuDailyService;
import com.hwqgooo.douknow.utils.ExceptionPrint;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by weiqiang on 2016/7/25.
 */
public class DailyCommentVM {
    public final static String TAG = "DailyCommentVM";
    public CommentData longComment;
    public CommentData shortComment;
    IZhihuDailyService zhihuDailyService;
    static DailyCommentVM dailyCommentVM;
    static int refrence;

    public static DailyCommentVM getInstance(Context context) {
        if (dailyCommentVM == null) {
            dailyCommentVM = new DailyCommentVM(context);
        }
        ++refrence;
        return dailyCommentVM;
    }

    private DailyCommentVM(Context context) {
        zhihuDailyService = ZhihuDailyService.getInstance(context);
        longComment = new CommentData(onRefreshLong);
        shortComment = new CommentData(onRefreshShort);
    }

    public void onDestory() {
        --refrence;
        if (refrence == 0) {
            Log.d(TAG, "onDestory: ");
            dailyCommentVM = null;
            zhihuDailyService.onDestroy();
            longComment.items.clear();
            shortComment.items.clear();
        }
    }

    private ReplyCommand<Integer> onRefreshLong = new ReplyCommand(new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            zhihuDailyService.getDailyLongComment(integer.intValue())
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<DailyComment, List<DailyComment.CommentInfo>>() {
                        @Override
                        public List<DailyComment.CommentInfo> call(DailyComment dailyComment) {
                            return dailyComment.comments;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<DailyComment.CommentInfo>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            ExceptionPrint.print(e, TAG);
                        }

                        @Override
                        public void onNext(List<DailyComment.CommentInfo> commentInfos) {
                            if (commentInfos.isEmpty()) {
                                return;
                            }
                            if (!longComment.items.isEmpty()) {
                                longComment.items.clear();
                            }
                            longComment.items.addAll(commentInfos);
                        }
                    });
        }
    });

    private ReplyCommand<Integer> onRefreshShort = new ReplyCommand(new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            zhihuDailyService.getDailyShortComment(integer.intValue())
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<DailyComment, List<DailyComment.CommentInfo>>() {
                        @Override
                        public List<DailyComment.CommentInfo> call(DailyComment dailyComment) {
                            return dailyComment.comments;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<DailyComment.CommentInfo>>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(List<DailyComment.CommentInfo> commentInfos) {
                            if (commentInfos.isEmpty()) {
                                return;
                            }
                            if (!shortComment.items.isEmpty()) {
                                shortComment.items.clear();
                            }
                            shortComment.items.addAll(commentInfos);
                        }
                    });
        }
    });

    public static class CommentData {
        public final ObservableList<DailyComment.CommentInfo> items = new ObservableArrayList<>();
        public final ItemView itemView = ItemView.of(BR.item, R.layout.item_comment);
        public ReplyCommand<Integer> onRefresh;

        public CommentData(ReplyCommand<Integer> onRefresh) {
            this.onRefresh = onRefresh;
        }
    }

}
