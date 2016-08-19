package com.hwqgooo.douknow.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.FragmentCommentBinding;
import com.hwqgooo.douknow.viewmodel.DailyCommentVM;

/**
 * Created by weiqiang on 2016/7/25.
 */
public class LongCommentFragment extends BaseFragment {
    public static final String TAG = "LongCommentFragment";
    FragmentCommentBinding binding;
    DailyCommentVM dailyCommentVM;
    int dailyid;

    public static LongCommentFragment getInstance(int dailyid) {
        LongCommentFragment longCommentFragment = new LongCommentFragment();
        Bundle longbundle = new Bundle();
        longbundle.putInt("dailyid", dailyid);
        longCommentFragment.setArguments(longbundle);
        return longCommentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false);
        dailyCommentVM = DailyCommentVM.getInstance(context);
        Log.d(TAG, "onCreateView: items " + dailyCommentVM.longComment.items);
        Log.d(TAG, "onCreateView: itemview " + dailyCommentVM.longComment.itemView);
        binding.setComment(dailyCommentVM.longComment);
//        binding.setVariable(BR.comment, dailyCommentVM.longComment);
//        binding.executePendingBindings();
        dailyid = getArguments().getInt("dailyid");

        return binding.getRoot();
    }

    @Override
    public void onViewAppear() {
    }

    @Override
    public void onViewFirstAppear() {
        dailyCommentVM.longComment.onRefresh.execute(dailyid);
    }

    @Override
    public void onViewDisappear() {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dailyCommentVM.onDestory();
    }
}
