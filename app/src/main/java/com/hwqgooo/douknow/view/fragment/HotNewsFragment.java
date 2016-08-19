package com.hwqgooo.douknow.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwqgooo.databinding.utils.recyclerview.OnRcvClickListener;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.FragmentHotnewsBinding;
import com.hwqgooo.douknow.databinding.ItemHotnewsBinding;
import com.hwqgooo.douknow.view.activity.DailyDetailActivity;
import com.hwqgooo.douknow.viewmodel.HotNewsVM;
import com.hwqgooo.douknow.viewmodel.MainVM;

/**
 * Created by weiqiang on 2016/7/26.
 */
public class HotNewsFragment extends BaseFragment {
    public static final String TAG = "HotNewsFragment";
    FragmentHotnewsBinding binding;
    HotNewsVM hotNewsVM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout
                .fragment_hotnews, container, false);
        hotNewsVM = HotNewsVM.getInstance(context);
        binding.setHotnews(hotNewsVM);
//        binding.executePendingBindings();
        binding.swipeRcv.recyclerview.setHasFixedSize(true);
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemHotnewsBinding>() {
                    @Override
                    public void onItemClick(ItemHotnewsBinding binding, int position) {
                        DailyDetailActivity.launch(context,
                                binding.itemImage,
                                hotNewsVM.items.get(position).newsId,
                                hotNewsVM.items.get(position).title,
                                hotNewsVM.items.get(position).thumbnail);
                        hotNewsVM.setRead(hotNewsVM.items.get(position));
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewAppear() {
        Log.d(TAG, "onViewAppear: ");
        MainVM.getInstance(context.getApplicationContext()).toolbarTitle.set("热点文章");
    }

    @Override
    public void onViewFirstAppear() {
        Log.d(TAG, "onViewFirstAppear: ");
        binding.swipeRcv.swiperefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));
        hotNewsVM.onRefresh.execute();
    }

    @Override
    public void onViewDisappear() {
    }
}
