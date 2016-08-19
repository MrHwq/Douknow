package com.hwqgooo.douknow.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by weiqiang on 2016/8/6.
 */
public class DailyRecommendEditorsActivity extends AppCompatActivity {
    public static void launch(Context context,
                              View childView,
                              int dailyId) {
        Intent intent = new Intent(context, DailyRecommendEditorsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("dailyid", dailyId);
        final ActivityOptionsCompat options;
        if (Build.VERSION.SDK_INT >= 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, childView, Integer.toString(dailyId));
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(
                    childView, 0, 0, childView.getWidth(), childView.getHeight());
        }
        context.startActivity(intent, options.toBundle());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int dailyid = intent.getIntExtra("dailyid", 0);
    }
}
