package com.hwqgooo.douknow.view.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by weiqiang on 2016/7/6.
 */
public abstract class BaseFragment extends Fragment {
    public static final String TAG = "BaseFragment";
    private boolean isViewFirstAppear = true;
    private boolean isViewAppear = false;
    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
        Log.d(TAG, "onDetach: ");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            if (isVisibleToUser) {
                onViewCanSee();
            }
        } else {
            return;
        }
        onViewNoSee();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (getUserVisibleHint()) {
            onViewCanSee();
            return;
        }
        onViewNoSee();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        onViewNoSee();
    }

    private void onViewCanSee() {
        if (this.isViewFirstAppear) {
            onViewFirstAppear();
            this.isViewFirstAppear = false;
        }
        if (!isViewAppear) {
            isViewAppear = true;
            onViewAppear();
        }
    }

    private void onViewNoSee() {
        if (isViewAppear) {
            isViewAppear = false;
            onViewDisappear();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    public abstract void onViewAppear();

    public abstract void onViewFirstAppear();

    public abstract void onViewDisappear();
}
