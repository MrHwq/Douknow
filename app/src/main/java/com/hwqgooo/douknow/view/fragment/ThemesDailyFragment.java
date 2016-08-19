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
import com.hwqgooo.douknow.databinding.FragmentThemesdailyBinding;
import com.hwqgooo.douknow.databinding.ItemThemesdailyBinding;
import com.hwqgooo.douknow.view.activity.ThemesDailyDetailsActivity;
import com.hwqgooo.douknow.viewmodel.MainVM;
import com.hwqgooo.douknow.viewmodel.ThemeDailyVM;

/**
 * Created by weiqiang on 2016/7/26.
 */
public class ThemesDailyFragment extends BaseFragment {
    public static final String TAG = "ThemesDailyFragment";
    FragmentThemesdailyBinding binding;
    ThemeDailyVM themesDailyVM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout
                .fragment_themesdaily, container, false);
        themesDailyVM = ThemeDailyVM.getInstance(context.getApplicationContext());
        binding.setThemesdaily(themesDailyVM);
        binding.swipeRcv.recyclerview.setHasFixedSize(true);
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemThemesdailyBinding>() {
                    @Override
                    public void onItemClick(ItemThemesdailyBinding binding, int position) {
                        ThemesDailyDetailsActivity.launch(context,
                                binding.itemImage,
                                themesDailyVM.items.get(position).getId(),
                                themesDailyVM.items.get(position).getName(),
                                themesDailyVM.items.get(position).getThumbnail());
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewAppear() {
        Log.d(TAG, "onViewAppear: ");
        MainVM.getInstance(context.getApplicationContext()).toolbarTitle.set("主题日报");
    }

    @Override
    public void onViewFirstAppear() {
        Log.d(TAG, "onViewFirstAppear: ");
        binding.swipeRcv.swiperefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));
        themesDailyVM.onRefresh.execute();
    }

    @Override
    public void onViewDisappear() {
    }
}
