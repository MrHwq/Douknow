package com.hwqgooo.douknow.view.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwqgooo.databinding.utils.recyclerview.OnRcvClickListener;
import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.FragmentDailysBinding;
import com.hwqgooo.douknow.databinding.ItemDailyBeanBinding;
import com.hwqgooo.douknow.model.bean.DailyBean;
import com.hwqgooo.douknow.model.bean.TopDailys;
import com.hwqgooo.douknow.model.databindmodel.TopDailyModel;
import com.hwqgooo.douknow.view.activity.DailyDetailActivity;
import com.hwqgooo.douknow.viewmodel.DailyVM;
import com.hwqgooo.douknow.viewmodel.MainVM;

/**
 * Created by weiqiang on 2016/7/21.
 */
public class DailyListFragment extends BaseFragment {
    public static final String TAG = "DailyListFragment";
    FragmentDailysBinding binding;
    DailyVM dailyVM;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
    Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dailys, container, false);
//        binding.setMainvm(MainVM.getInstance(context));
        Log.d(TAG, "onCreateView: " + savedInstanceState);
        Log.d(TAG, "onCreateView: " + dailyVM);
        if (dailyVM == null) {
            dailyVM = DailyVM.getInstance(context);
        }
        binding.setDailyvm(dailyVM);
        binding.swipeRcv.recyclerview.addOnItemTouchListener(
                new OnRcvClickListener() {
                    @Override
                    public void onItemClick(ViewDataBinding binding, int position) {
                        if (dailyVM.merge.items.get(position) instanceof TopDailyModel) {
                            TopDailyModel topDailyModel =
                                    (TopDailyModel) dailyVM.merge.items.get(position);
                            TopDailys topDailys = topDailyModel.items.get(topDailyModel.selectPage);
                            DailyDetailActivity.launch(context, binding.getRoot(),
                                    topDailys.getId(),
                                    topDailys.getTitle(),
                                    topDailys.getImage());
                        } else if (dailyVM.merge.items.get(position) instanceof DailyBean) {
                            DailyBean dailyBean = (DailyBean) dailyVM.merge.items.get(position);
                            ItemDailyBeanBinding dailyBeanBinding = (ItemDailyBeanBinding) binding;
                            DailyDetailActivity.launch(context, dailyBeanBinding.itemImage,
                                    dailyBean.getId(),
                                    dailyBean.getTitle(),
                                    dailyBean.getImages() == null
                                            ? "" : dailyBean.getImages().get(0));
                            dailyVM.setRead(dailyBean);
                        }
                    }
                });
        binding.swipeRcv.recyclerview.addOnScrollListener(new OnPinHeaderScrollListener());
        return binding.getRoot();
    }

    boolean isNeedSave = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
        isNeedSave = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + isNeedSave);
        isNeedSave = false;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: " + savedInstanceState);
    }

    @Override
    public void onViewFirstAppear() {
        binding.swipeRcv.swiperefresh.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24,
                        getResources().getDisplayMetrics()));
        dailyVM.onRefresh.execute();
    }

    String title = "首页";

    @Override
    public void onViewAppear() {
        MainVM.getInstance(context.getApplicationContext()).toolbarTitle.set(title);
    }

    @Override
    public void onViewDisappear() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + isNeedSave);
        title = "首页";
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: " + isNeedSave);
        if (!isNeedSave) {
            dailyVM.onDestory();
        }
    }

    public class OnPinHeaderScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int firstItem;
            if (layoutManager instanceof LinearLayoutManager) {
                firstItem = ((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof GridLayoutManager) {
                firstItem = ((GridLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager =
                        (StaggeredGridLayoutManager) layoutManager;
                int[] pos = new int[staggeredGridLayoutManager.getSpanCount()];
                staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(pos);
                firstItem = findMin(pos);
            } else {
                return;
            }

            title = "首页";
            //倒退寻找第一个标题
            for (int idx = firstItem; idx >= 0; --idx) {
                if (dailyVM.merge.items.get(idx) instanceof String) {
                    title = (String) dailyVM.merge.items.get(idx);
                    break;
                }
            }
            MainVM.getInstance(context.getApplicationContext()).toolbarTitle.set(title);
        }

        private int findMin(int[] pos) {
            int min = pos[0];
            for (int value : pos) {
                if (value < min) {
                    min = value;
                }
            }
            return min;
        }
    }

}
