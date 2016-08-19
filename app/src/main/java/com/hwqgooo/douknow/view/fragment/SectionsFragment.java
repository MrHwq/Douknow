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
import com.hwqgooo.douknow.databinding.FragmentSectionsBinding;
import com.hwqgooo.douknow.databinding.ItemSectionsBinding;
import com.hwqgooo.douknow.view.activity.SectionsDetailsActivity;
import com.hwqgooo.douknow.viewmodel.MainVM;
import com.hwqgooo.douknow.viewmodel.SectionsVM;

/**
 * Created by weiqiang on 2016/7/26.
 */
public class SectionsFragment extends BaseFragment {
    public static final String TAG = "SectionsFragment";
    FragmentSectionsBinding binding;
    SectionsVM sectionsVM;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout
                .fragment_sections, container, false);
        sectionsVM = SectionsVM.getInstance(context);
        binding.setSections(sectionsVM);
//        binding.executePendingBindings();
        binding.swipeRcv.recyclerview.setHasFixedSize(true);
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener<ItemSectionsBinding>() {
                    @Override
                    public void onItemClick(ItemSectionsBinding binding, int position) {
                        SectionsDetailsActivity.launch(context,
                                binding.itemImage,
                                sectionsVM.items.get(position).id,
                                sectionsVM.items.get(position).name,
                                sectionsVM.items.get(position).thumbnail);
                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onViewAppear() {
        Log.d(TAG, "onViewAppear: ");
        MainVM.getInstance(context.getApplicationContext()).toolbarTitle.set("专栏");
    }

    @Override
    public void onViewFirstAppear() {
        Log.d(TAG, "onViewFirstAppear: ");
        binding.swipeRcv.swiperefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));
        sectionsVM.onRefresh.execute();
    }

    @Override
    public void onViewDisappear() {
    }
}
