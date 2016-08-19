package com.hwqgooo.douknow.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.hwqgooo.databinding.message.Messenger;
import com.hwqgooo.douknow.R;

import java.util.LinkedList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by weiqiang on 2016/7/21.
 */
public class MainVM {
    public String TAG = "MainVM";
    public static final String TOKEN_UPDATE_TITLE = "TOKEN_MainVM_TITLE";
    Context context;
    public ObservableInt statusbarcolor = new ObservableInt();
    public ObservableField<String> toolbarTitle = new ObservableField<>();

    private MainVM(Context context) {
        TAG += context;
        this.context = context;
        statusbarcolor.set(ContextCompat.getColor(context, R.color.colorPrimary));
        toolbarTitle.set("你知道吗");
        Messenger.getDefault().register(context,
                TOKEN_UPDATE_TITLE,
                String.class,
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "call: " + TOKEN_UPDATE_TITLE + "==>" + s);
                        toolbarTitle.set(s);
                    }
                });
    }

    static List<MainVM> mainVMs = new LinkedList<>();

    public static MainVM getInstance(Context context) {
        for (MainVM mainVM : mainVMs) {
            if (mainVM.context == context) {
                return mainVM;
            }
        }
        Log.d("MainVM", "getInstance: " + context);
        MainVM mainVM = new MainVM(context);
        mainVMs.add(mainVM);
        return mainVM;
    }

    public void onDestory() {
        Messenger.getDefault().unregister(context);
        this.context = null;
        mainVMs.remove(this);
    }
}
