package com.hwqgooo.douknow.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityAboutmeBinding;
import com.hwqgooo.douknow.viewmodel.MainVM;

/**
 * Created by weiqiang on 2016/8/6.
 */
public class AboutActivity extends AppCompatActivity {
    public static void launch(Context context,
                              View childView) {
        Intent intent = new Intent(context, AboutActivity.class);
        final ActivityOptionsCompat options;
        if (Build.VERSION.SDK_INT >= 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, childView, "AboutMe");
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(
                    childView, 0, 0, childView.getWidth(), childView.getHeight());
        }
        context.startActivity(intent, options.toBundle());
    }

    ActivityAboutmeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_aboutme);
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));
        binding.title.setText("Mrhwq");
        binding.subtitle.setText("https://github.com/mrhwq");
        setupWebView("https://github.com/MrHwq");

        initAppbar();
    }

    void initAppbar() {
        // 初始化ToolBar
        setSupportActionBar(binding.toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        binding.collToolbarLayout.setTitleEnabled(true);
        mActionBar.setTitle("");
//        binding.toolbar.setNavigationIcon(R.drawable.back);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupWebView(String url) {
        binding.editorWeb.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        final WebSettings webSettings = binding.editorWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        binding.editorWeb.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        binding.editorWeb.getSettings().setBlockNetworkImage(true);
        WebViewClientBase webViewClient = new WebViewClientBase();
        binding.editorWeb.setWebViewClient(webViewClient);
        binding.editorWeb.requestFocus(View.FOCUS_DOWN);
        binding.editorWeb.getSettings().setDefaultTextEncodingName("UTF-8");
        binding.editorWeb.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult
                    result) {

                AlertDialog.Builder b2 = new AlertDialog.Builder(AboutActivity.this)
                        .setTitle(R.string.app_name).setMessage(message).setPositiveButton("确定",
                                new AlertDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                });

                b2.setCancelable(false);
                b2.create();
                b2.show();
                return true;
            }
        });
        binding.editorWeb.loadUrl(url);
    }

    public class WebViewClientBase extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.editorWeb.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            String errorHtml = "<html><body><h2>" + failingUrl + "</h2></body><ml>";
            view.loadDataWithBaseURL(null, errorHtml, "textml", "UTF-8", null);
        }
    }
}
