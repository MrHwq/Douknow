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
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hwqgooo.douknow.R;
import com.hwqgooo.douknow.databinding.ActivityEditorInfoBinding;
import com.hwqgooo.douknow.viewmodel.MainVM;

/**
 * Created by weiqiang on 2016/8/4.
 */
public class EditorInfoActivity extends AppCompatActivity {
    ActivityEditorInfoBinding binding;

    public static void launch(Context context, View childView, int id, String name) {
        final Intent intent = new Intent(context, EditorInfoActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        final ActivityOptionsCompat options;
        if (Build.VERSION.SDK_INT >= 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (Activity) context, childView, name);
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
        int id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_editor_info);
        binding.setTitle(name);
        binding.setMainvm(MainVM.getInstance(getApplicationContext()));

        initAppbar(name);
        setupWebView("http://news-at.zhihu.com/api/4/editor/" + id + "/profile-page/android");
    }

    void initAppbar(String title) {
        // 初始化ToolBar
        setSupportActionBar(binding.toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mActionBar.setTitle(title);
        binding.toolbar.setNavigationIcon(R.drawable.back);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupWebView(String url) {
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
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.d("hwqhwq", "onProgressChanged: " + newProgress);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult
                    result) {

                AlertDialog.Builder b2 = new AlertDialog.Builder(EditorInfoActivity.this)
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
            String errorHtml = "<html><body><h2>找不到网页</h2></body><ml>";
            view.loadDataWithBaseURL(null, errorHtml, "textml", "UTF-8", null);
        }
    }
}
