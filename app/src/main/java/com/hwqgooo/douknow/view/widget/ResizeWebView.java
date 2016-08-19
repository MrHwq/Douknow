package com.hwqgooo.douknow.view.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by weiqiang on 2016/8/8.
 */
public class ResizeWebView extends WebView {
    public final static String TAG = "ResizeWebView";

    public ResizeWebView(Context context) {
        super(context);
    }

    public ResizeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ResizeWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ResizeWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean
            privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (h == 0 || h == params.height) {
            return;
        }
        params.height = h;
        setLayoutParams(params);
        Log.d(TAG, "onSizeChanged: " + w + ".." + h);
        Log.d(TAG, "onSizeChanged: " + ow + ".." + oh);
    }
}
