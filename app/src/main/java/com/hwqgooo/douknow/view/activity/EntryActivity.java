package com.hwqgooo.douknow.view.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityEntryBinding;
import com.hwqgooo.douknow.viewmodel.EntryVM;

import rx.subscriptions.CompositeSubscription;

/**
 * Created by weiqiang on 2016/6/11.
 */
public class EntryActivity extends AppCompatActivity {
    ActivityEntryBinding binding;
    EntryVM entryVM;

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        if (!hasFocus) {
//            return;
//        }
//        startAnim();
//        super.onWindowFocusChanged(hasFocus);
//    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            startAnim();
        }
    };


    private void startAnim() {
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(binding.ivLogo, "scaleX", 1f, 1.1f, 1.2f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(binding.ivLogo, "scaleY", 1f, 1.1f, 1.2f);

        AnimatorSet animatorImage = new AnimatorSet();
        animatorImage.setDuration(1500).play(animatorX).with(animatorY);
        animatorImage.start();
        animatorImage.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                startActivity(intent);
                EntryActivity.this.finish();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

//        ObjectAnimator moveDown = ObjectAnimator.ofFloat(binding.textLabel, "translationY", 200f,
//                250f);
//        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(binding.textLabel, "alpha", 0f, 0.5f);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.play(moveDown);//.with(fadeInOut);
//        animatorSet.setDuration(1500);
//        animatorSet.setStartDelay(700);
//        animatorSet.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                Intent intent = new Intent(EntryActivity.this, MainActivity.class);
//                startActivity(intent);
//                EntryActivity.this.finish();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//            }
//        });
//        animatorSet.start();
    }

    CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_entry);
        if (entryVM == null) {
            entryVM = new EntryVM(this);
            entryVM.onRefresh.execute();
        }
        binding.setEntryvm(entryVM);
//        compositeSubscription.add(
//                Observable.timer(1, TimeUnit.SECONDS)
//                        .subscribe(new Action1<Long>() {
//                            @Override
//                            public void call(Long aLong) {
//                                Intent intent = new Intent(EntryActivity.this, MainActivity
//                                        .class);
//                                startActivity(intent);
//                                EntryActivity.this.finish();
//                            }
//                        }));

        entryVM.entryImageUrl.addOnPropertyChangedCallback(
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable observable, int i) {
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
        mHandler.removeCallbacksAndMessages(null);
    }
}
