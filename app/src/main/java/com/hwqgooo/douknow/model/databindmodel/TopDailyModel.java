package com.hwqgooo.douknow.model.databindmodel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;

import com.hwqgooo.databinding.command.ReplyCommand;
import com.hwqgooo.douknow.BR;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.model.bean.TopDailys;

import me.tatarka.bindingcollectionadapter.ItemView;
import rx.functions.Action1;

/**
 * Created by weiqiang on 2016/8/4.
 */
public class TopDailyModel extends BaseObservable {
    public static final String TAG = "TopDailyModel";
    public final ObservableList<TopDailys> items = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(BR.item, R.layout.item_top_dailys);
    public int selectPage;
    public int indicatorCount;
    public ReplyCommand<Integer> onpageselect = new ReplyCommand<Integer>(new Action1<Integer>() {
        @Override
        public void call(Integer integer) {
            Log.d(TAG, "onpageselect call: " + integer);
            selectPage = integer.intValue();
        }
    });

    @Bindable
    public int getSelectPage() {
        return selectPage;
    }

    public void setSelectPage(int selectPage) {
        if (this.selectPage == selectPage) {
            return;
        }
        this.selectPage = selectPage;
        notifyPropertyChanged(BR.selectPage);
    }

    @Bindable
    public int getIndicatorCount() {
        return indicatorCount;
    }

    public void setIndicatorCount(int indicatorCount) {
        if (this.indicatorCount == indicatorCount) {
            return;
        }
        this.indicatorCount = indicatorCount;
        notifyPropertyChanged(BR.indicatorCount);
    }

    public String getItemTitle() {
        return items.get(selectPage).getTitle();
    }
}