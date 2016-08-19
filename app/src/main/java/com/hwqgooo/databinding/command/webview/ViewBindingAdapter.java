package com.hwqgooo.databinding.command.webview;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import com.hwqgooo.douknow.BR;

/**
 * Created by weiqiang on 2016/7/24.
 */
public class ViewBindingAdapter {
    public static final String TAG = "WebViewBindingAdapter";

//    @BindingAdapter(value = {"loaddata"},
//            requireAll = false)
//    public static void loadUrlImage(final WebView webView,
//                                    WebViewLoadData loaddata) {
//        if (TextUtils.isEmpty(loaddata.data)) {
//            return;
//        }
//        Log.d(TAG, "loadUrlImage: ");
//        webView.loadData(loaddata.data, loaddata.mimeType, loaddata.encoding);
//    }

    public static class WebViewLoadData extends BaseObservable {
        public String data;
        public String mimeType;
        public String encoding;

        @Bindable
        public String getData() {
            return data;
        }

        public void setData(String data) {
            if (TextUtils.equals(this.data, data)) {
                return;
            }
            this.data = data;
            notifyPropertyChanged(BR.data);
        }

        @Bindable
        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            if (TextUtils.equals(this.encoding, encoding)) {
                return;
            }
            this.encoding = encoding;
            notifyPropertyChanged(BR.encoding);
        }

        @Bindable
        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            if (TextUtils.equals(this.mimeType, mimeType)) {
                return;
            }
            this.mimeType = mimeType;
            notifyPropertyChanged(BR.mimeType);
        }
    }
}
